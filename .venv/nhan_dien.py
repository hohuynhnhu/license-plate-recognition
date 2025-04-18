import cv2
import torch
from ultralytics import YOLO
import winsound
import easyocr
import os
import csv

import re

# Load YOLOv8 model
model = YOLO("D:/train_bien_so_xe/.venv/runs/detect/train/weights/best.pt")

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
#chỉnh lại độ tương phản sao cho nhận dạng hơn 90%
    if best_conf > 0.8 and best_plate is not None and not ocr_done:
        # Lưu ảnh gốc biển số
        cv2.imwrite(".venv/bien_so_xe.jpg", best_frame)

        # Tiền xử lý ảnh biển số
        plate_gray = cv2.cvtColor(best_plate, cv2.COLOR_BGR2GRAY)
        plate_blur = cv2.GaussianBlur(plate_gray, (3, 3), 0)
        plate_equalized = cv2.equalizeHist(plate_blur)
        plate_sharp = cv2.addWeighted(plate_gray, 1.5, plate_blur, -0.5, 0)

        # Threshold để phân biệt chữ
        _, thresh = cv2.threshold(plate_sharp, 0, 255, cv2.THRESH_BINARY + cv2.THRESH_OTSU)

        plate_path = ".venv/bien_so_xe_da_cat.jpg"
        cv2.imwrite(plate_path, thresh)

        print("📸 Đã lưu ảnh biển số rõ nhất!")
        winsound.Beep(1000, 500)

        # OCR
        reader = easyocr.Reader(['en'], gpu=False)
        ocr_results = reader.readtext(thresh, detail=0)

        print("Kết quả OCR (thô):", ocr_results)


        def fix_common_ocr_mistakes(text):
            corrections = {
                'I': '1',
                'L': '1',
                '|': '1',
                'O': '0',
                'Q': '0',
                'S': '5',
                # Nếu có thêm trường hợp nào bạn thấy hay nhầm thì bổ sung vào đây
            }
            for wrong, right in corrections.items():
                text = text.replace(wrong, right)
            return text


        # Hậu xử lý: loại bỏ mọi ký tự trừ chữ cái, số, dấu gạch ngang (-), và dấu chấm (.)
        processed_texts = []
        for text in ocr_results:
            filtered = re.sub(r'[^A-Z0-9\-\.]', '', text.upper())
            corrected = fix_common_ocr_mistakes(filtered)
            if 5 <= len(corrected) <= 12:
                processed_texts.append(corrected)

        with open(csv_file, mode='a', newline='', encoding='utf-8') as file:
            plate_text = ', '.join(processed_texts)
            file.write(f"{plate_text}\n")

        print("💾 Đã lưu vào file bien_so.csv!")

        ocr_done = True
        best_conf = 0

    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

cap.release()
cv2.destroyAllWindows()
