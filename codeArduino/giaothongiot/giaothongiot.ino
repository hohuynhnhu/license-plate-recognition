#include <WiFi.h>
#include <FirebaseESP32.h>
#include <Wire.h>
#include <LiquidCrystal_I2C.h>
#include <ESP32Servo.h>

// WiFi thông tin
#define WIFI_SSID "50k nha"
#define WIFI_PASSWORD "123456788"

// Firebase thông tin
#define FIREBASE_HOST "tramxeuth-default-rtdb.firebaseio.com"
#define FIREBASE_AUTH "06uoSkZdrjmCGCHDntgeV7NHpCKKliS93SC6heUI"

// Firebase và WiFi cấu hình
FirebaseData fbTrangThaiCong;
FirebaseData fbAutoOpen;
FirebaseData fbCanhBao;
FirebaseAuth auth;
FirebaseConfig config;

// LCD
LiquidCrystal_I2C lcd(0x27, 16, 2);

// Servo
Servo myServo;
const int servoPin = 18;

// Buzzer
const int buzzerPin = 26;
bool baoDong = false;
unsigned long timeBuzzer = 0;

// Cờ trạng thái servo
bool activeSG = false;

// Biến lưu góc servo
int angle = 0;

// Button điều khiển SG
const int btn = 25;
bool lastBtnState = HIGH;
unsigned long lastDebounceTime = 0;
const unsigned long debounceDelay = 200; // debounce 200ms

// cảm biến rung
const int sw = 34;
bool lastShake = false;

// Biến quản lý hẹn giờ auto-close
unsigned long moCongTime = 0;   // thời điểm mở cổng
bool dangMoCong = false;        // flag đang mở cổng nhờ Firebase
bool autoCloseEnabled = false;  // có tự đóng hay không

void setup() {
  Serial.begin(115200);

  // WiFi
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Kết nối WiFi");
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("\n WiFi OK");

  // Firebase
  config.host = FIREBASE_HOST;
  config.signer.tokens.legacy_token = FIREBASE_AUTH;
  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);

  // LCD
  lcd.init();
  lcd.backlight();
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("Firebase + Servo");

  // Servo
  myServo.attach(servoPin);
  myServo.write(angle);

  // Button + cảm biến rung
  pinMode(btn, INPUT_PULLUP);
  pinMode(sw, INPUT_PULLUP);

  // Buzzer
  pinMode(buzzerPin, OUTPUT);
  digitalWrite(buzzerPin, LOW);
}

void loop() {
  bool trangThai = false;

  // Đọc trạng thái cổng từ Firebase
  if (Firebase.getBool(fbTrangThaiCong, "/trangthaicong")) {
    trangThai = fbTrangThaiCong.boolData();
    Serial.print("Trạng thái cổng: ");
    Serial.println(trangThai ? "Đang mở" : "Đang đóng");

    if (trangThai == true && !dangMoCong) {  
      // chỉ xử lý khi mới nhận lệnh mở
      activeSG = true;
      myServo.write(90);
      angle = 90;
      lcd.clear();
      lcd.setCursor(0, 0);
      lcd.print("Cong dang mo");
      Serial.println("Mở cổng (servo 90)");

      // Đọc AutoOpen
      autoCloseEnabled = false;
      if (Firebase.getBool(fbAutoOpen, "/AutoOpen")) {
        autoCloseEnabled = fbAutoOpen.boolData();
        Serial.print("AutoOpen: ");
        Serial.println(autoCloseEnabled);
      } else {
        Serial.println("Lỗi đọc AutoOpen: " + fbAutoOpen.errorReason());
      }

      // Nếu bật AutoOpen thì bắt đầu đếm thời gian
      if (autoCloseEnabled) {
        moCongTime = millis();
        dangMoCong = true;
      }
      activeSG = false;
    }
  } else {
    Serial.println("Lỗi đọc trạng thái cổng: " + fbTrangThaiCong.errorReason());
    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print("Firebase Error");
  }

  // Kiểm tra đóng cổng sau 10s (non-blocking)
  if (dangMoCong && autoCloseEnabled) {
    if (millis() - moCongTime >= 10000) {
      myServo.write(0);
      angle = 0;
      lcd.clear();
      lcd.setCursor(0, 0);
      lcd.print("Cong da dong");
      Serial.println("Đóng cổng (servo 0)");

      // Cập nhật Firebase
      if (Firebase.setBool(fbAutoOpen, "/trangthaicong", false)) {
        Serial.println("Đã cập nhật trạng thái cổng thành FALSE");
      } else {
        Serial.println("Lỗi cập nhật trạng thái cổng: " + fbAutoOpen.errorReason());
      }
      dangMoCong = false;  // reset flag
    }
  }

  // Nút nhấn điều khiển servo (debounce bằng millis)
  bool btnState = digitalRead(btn);
  if (btnState == LOW && lastBtnState == HIGH && (millis() - lastDebounceTime > debounceDelay)) {
    lastDebounceTime = millis();
    Serial.println("Nút nhấn được bấm → Đảo trạng thái servo");
    activeSG = true;
    if (angle == 0) {
      angle = 90;
    } else {
      angle = 0;
    }
    myServo.write(angle);
    activeSG = false;
  }
  lastBtnState = btnState;

  // kiểm tra rung
  bool shake = digitalRead(sw);
  if (!activeSG && shake == LOW && lastShake == HIGH) { // LOW = có rung nếu dùng INPUT_PULLUP
    Serial.println("Ngoại lực tác động vào servo!");
    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print("CO TAC DONG");

    // Buzzer cảnh báo non-blocking
    baoDong = true;
    timeBuzzer = millis();

    // Gửi cảnh báo lên Firebase
    if (Firebase.setBool(fbCanhBao, "/CanhBaoRung", true)) {
      Serial.println("Đã gửi CanhBao = TRUE lên Firebase");
    } else {
      Serial.println("Lỗi gửi CanhBao: " + fbCanhBao.errorReason());
    }
  }
  lastShake = shake;

  // Xử lý buzzer không chặn loop
  if (baoDong) {
    if (millis() - timeBuzzer < 3000) {
      digitalWrite(buzzerPin, HIGH);
    } else {
      digitalWrite(buzzerPin, LOW);
      baoDong = false;
    }
  }
}
