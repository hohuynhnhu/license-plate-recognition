import cv2
import torch
from ultralytics import YOLO
import winsound
import easyocr
import re
import time

def fix_common_ocr_mistakes(text):
    corrections = {
        'I': '1',
        'L': '1',
        '|': '1',
        'O': '0',
        'Q': '0',
        'S': '5',
    }
    for wrong, right in corrections.items():
        text = text.replace(wrong, right)
    return text

def detect_license_plate():
    model = YOLO("D:/giaothong/license-plate-recognition/.venv/runs/detect/train/weights/best.pt")
    cap = cv2.VideoCapture(0)

    recognized_plates = []
    best_conf = 0
    best_frame = None
    best_plate = None
    last_ocr_time = time.time()

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

        current_time = time.time()
        if best_conf > 0.8 and best_plate is not None and (current_time - last_ocr_time >= 15):
            last_ocr_time = current_time

            # Xử lý ảnh biển số
            plate_gray = cv2.cvtColor(best_plate, cv2.COLOR_BGR2GRAY)
            plate_blur = cv2.GaussianBlur(plate_gray, (3, 3), 0)
            plate_sharp = cv2.addWeighted(plate_gray, 1.5, plate_blur, -0.5, 0)
            _, thresh = cv2.threshold(plate_sharp, 0, 255, cv2.THRESH_BINARY + cv2.THRESH_OTSU)

            cv2.imwrite(".venv/bien_so_xe.jpg", best_frame)
            cv2.imwrite(".venv/bien_so_xe_da_cat.jpg", thresh)
            print("📸 Đã lưu ảnh biển số rõ nhất! (tự động sau 15s)")

            winsound.Beep(1000, 500)

            # OCR
            reader = easyocr.Reader(['en'], gpu=False)
            ocr_results = reader.readtext(thresh, detail=0)
            print("🧪 Kết quả OCR:", ocr_results)

            processed_texts = []
            for text in ocr_results:
                filtered = re.sub(r'[^A-Z0-9]', '', text.upper())
                corrected = fix_common_ocr_mistakes(filtered)
                if 2 <= len(corrected) <= 8:
                    processed_texts.append(corrected)

            if processed_texts:
                # Giả định dòng đầu là chữ, dòng sau là số
                if len(processed_texts) >= 2:
                    line1 = processed_texts[0]
                    line2 = processed_texts[1]
                    if len(line2) >= 3:
                        line2 = line2[:-2] + '.' + line2[-2:]
                    full_plate = f"{line1},{line2}"
                else:
                    full_plate = processed_texts[0]

                print("✅ Biển số quét được:", full_plate)
                if 5 <= len(full_plate) <= 15:
                    recognized_plates.append(full_plate)
                    return full_plate

            best_conf = 0  # reset

        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

    cap.release()
    cv2.destroyAllWindows()
    return recognized_plates[0] if recognized_plates else None
