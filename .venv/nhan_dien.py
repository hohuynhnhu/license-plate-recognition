import cv2
import torch
from ultralytics import YOLO
import winsound
import easyocr
import os
import csv
from datetime import datetime

# Load YOLOv8 model
model = YOLO("E:/CDHTGTTM/codeGit/license-plate-recognition/.venv/runs/detect/train/weights/best.pt")

# Open camera
cap = cv2.VideoCapture(0)

best_conf = 0
best_frame = None
best_plate = None
ocr_done = False

# Create CSV file if not exists
csv_file = ".venv/bien_so.csv"
if not os.path.exists(csv_file):
    with open(csv_file, mode='w', newline='', encoding='utf-8') as file:
        writer = csv.writer(file)
        writer.writerow(["Thời gian", "Biển số"])

while True:
    ret, frame = cap.read()
    if not ret:
        break

    results = model(frame)

    for r in results:
        if r.boxes is None:
            continue
        for box in r.boxes:
            confidence = box.conf[0].item()

            x1, y1, x2, y2 = map(int, box.xyxy[0])
            cv2.rectangle(frame, (x1, y1), (x2, y2), (0, 255, 0), 2)

            if confidence > best_conf:
                best_conf = confidence
                best_frame = frame.copy()
                best_plate = frame[y1:y2, x1:x2]

    cv2.imshow("Nhan Dien Bien So", frame)

    if best_conf > 0.7 and best_plate is not None and not ocr_done:
        # Lưu ảnh và xử lý OCR
        cv2.imwrite(".venv/bien_so_xe.jpg", best_frame)
        plate_gray = cv2.cvtColor(best_plate, cv2.COLOR_BGR2GRAY)
        plate_contrast = cv2.convertScaleAbs(plate_gray, alpha=1, beta=-1)

        plate_path = ".venv/bien_so_xe_da_cat.jpg"
        cv2.imwrite(plate_path, plate_contrast)

        print("📸 Đã lưu ảnh biển số rõ nhất!")
        winsound.Beep(1000, 500)

        # OCR
        img = cv2.imread(plate_path)
        gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
        sharp = cv2.GaussianBlur(gray, (0, 0), 3)
        sharp = cv2.addWeighted(gray, 1.5, sharp, -0.5, 0)
        _, thresh = cv2.threshold(sharp, 0, 255, cv2.THRESH_BINARY + cv2.THRESH_OTSU)

        reader = easyocr.Reader(['en'], gpu=False)
        ocr_results = reader.readtext(thresh, detail=0)

        print("Kết quả OCR (thô):", ocr_results)

        # Ghi kết quả vào CSV
        with open(csv_file, mode='a', newline='', encoding='utf-8') as file:
            writer = csv.writer(file)
            now = datetime.now().strftime("%Y-%m-%d %H:%M")
            plate_text = ', '.join(ocr_results)
            writer.writerow([now, plate_text])

        print("💾 Đã lưu vào file bien_so.csv!")

        ocr_done = True
        best_conf = 0

    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

cap.release()
cv2.destroyAllWindows()
