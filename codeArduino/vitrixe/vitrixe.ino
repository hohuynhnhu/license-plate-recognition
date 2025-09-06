const int chanTrig = 9;  
const int chanEcho = 10;  

// Chân LED
const int chanDen = 13;  

long thoiGian;  
int khoangCach;  

void setup() {
  pinMode(chanTrig, OUTPUT);  
  pinMode(chanEcho, INPUT);  
  pinMode(chanDen, OUTPUT);  
  Serial.begin(9600);  
}

void loop() {
  // Gửi xung trigger
  digitalWrite(chanTrig, LOW);  
  delayMicroseconds(2);  
  digitalWrite(chanTrig, HIGH);  
  delayMicroseconds(10);  
  digitalWrite(chanTrig, LOW);  

  // Đọc tín hiệu echo
  thoiGian = pulseIn(chanEcho, HIGH);  
  khoangCach = thoiGian * 0.034 / 2;  // Tính khoảng cách cm

  Serial.print("Khoang cach: ");  
  Serial.print(khoangCach);  
  Serial.println(" cm");  

  // Nếu không có vật cản (khoảng cách lớn hơn ngưỡng) → bật đèn
  if (khoangCach > 20 || khoangCach <= 0) {  
    digitalWrite(chanDen, HIGH);  
  } else {  
    digitalWrite(chanDen, LOW);  
  }

  delay(200);  
}