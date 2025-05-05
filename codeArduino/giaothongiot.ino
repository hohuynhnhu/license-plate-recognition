#include <WiFi.h>
#include <FirebaseESP32.h>
#include <Wire.h>
#include <LiquidCrystal_I2C.h>
#include <ESP32Servo.h>

// WiFi thông tin
#define WIFI_SSID "UT HCMC"
#define WIFI_PASSWORD "SVUT@2018"

// Firebase thông tin
#define FIREBASE_HOST "tramxeuth-default-rtdb.firebaseio.com"
#define FIREBASE_AUTH "06uoSkZdrjmCGCHDntgeV7NHpCKKliS93SC6heUI"

// Firebase và WiFi cấu hình
FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;

// LCD
LiquidCrystal_I2C lcd(0x27, 16, 2);

// Servo
Servo myServo;
const int servoPin = 18;

// Biến lưu góc servo
int angle = 0;

void setup() {
  Serial.begin(115200);

  // WiFi
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Kết nối WiFi");
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("\n✅ WiFi OK");

  // Firebase
  config.host = FIREBASE_HOST;
  config.signer.tokens.legacy_token = FIREBASE_AUTH;
  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);

  // LCD
  lcd.begin();
  lcd.backlight();
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("Firebase + Servo");

  // Servo
  myServo.attach(servoPin);
  myServo.write(angle);
}

void loop() {
  bool trangThai = false;

  // Đọc trạng thái cổng từ Firebase
  if (Firebase.getBool(fbdo, "/trangthaicong")) {
    trangThai = fbdo.boolData();
    Serial.print("Trạng thái cổng: ");
    Serial.println(trangThai ? "Đang mở" : "Đang đóng");

    if (trangThai == true) {
      // Mở cổng
      myServo.write(90);
      lcd.clear();
      lcd.setCursor(0, 0);
      lcd.print("Cong dang mo");
      Serial.println("🟢 Mở cổng (servo 90)");

      // Đợi 10 giây
      delay(10000);

      // Đóng cổng lại
      myServo.write(0);
      lcd.clear();
      lcd.setCursor(0, 0);
      lcd.print("Cong da dong");
      Serial.println("🔴 Đóng cổng (servo 0)");

      // Cập nhật trạng thái cổng về false
      if (Firebase.setBool(fbdo, "/trangthaicong", false)) {
        Serial.println("✅ Đã cập nhật trạng thái cổng thành FALSE");
      } else {
        Serial.println("❌ Lỗi cập nhật trạng thái cổng: " + fbdo.errorReason());
      }
    }

  } else {
    Serial.println("❌ Lỗi đọc trạng thái cổng: " + fbdo.errorReason());
    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print("Firebase Error");
  }

  delay(1000); // Kiểm tra lại mỗi giây
}


