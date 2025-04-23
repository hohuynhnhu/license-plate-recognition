import cv2
import torch
from ultralytics import YOLO
import easyocr
import re
import time

# Load YOLO model 1 lần duy nhất
model = YOLO("D:/giaothong/license-plate-recognition/.venv/runs/detect/train/weights/best.pt")

# Hàm sửa lỗi OCR phổ biến
def fix_common_ocr_mistakes(text):
    corrections = {
        'I': '1', 'L': '1', '|': '1',
        'O': '0', 'Q': '0', 'S': '5'
    }
    for wrong, right in corrections.items():
        text = text.replace(wrong, right)
    return text

# Hàm chính để nhận diện biển số
def detect_license_plate(timeout=10):
    cap = cv2.VideoCapture(0)
    best_conf = 0
    best_plate = None
    best_frame = None
    start_time = time.time()

    while time.time() - start_time < timeout:
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
                if confidence > best_conf:
                    best_conf = confidence
                    best_frame = frame.copy()
                    best_plate = frame[y1:y2, x1:x2]
                    cv2.imwrite("debug_plate.jpg", best_plate)

        cv2.imshow("Nhan Dien Bien So", frame)
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

    cap.release()
    cv2.destroyAllWindows()

    if best_plate is not None:
        # Tiền xử lý ảnh biển số
        plate_gray = cv2.cvtColor(best_plate, cv2.COLOR_BGR2GRAY)
        plate_blur = cv2.GaussianBlur(plate_gray, (3, 3), 0)
        plate_sharp = cv2.addWeighted(plate_gray, 1.5, plate_blur, -0.5, 0)
        _, thresh = cv2.threshold(plate_sharp, 0, 255, cv2.THRESH_BINARY + cv2.THRESH_OTSU)

        # OCR
        reader = easyocr.Reader(['en'], gpu=False)
        results = reader.readtext(thresh, detail=0)
        print("🧪 Kết quả OCR:", results)

        if results:
            if len(results) >= 2:
                # Sắp xếp theo độ dài (dòng ngắn thường là phần chữ, dài là số)
                res_sorted = sorted(results, key=len)
                line1_raw = res_sorted[0].upper()
                line2_raw = res_sorted[1].upper()

                line1 = fix_common_ocr_mistakes(re.sub(r'[^A-Z0-9]', '', line1_raw))
                line2 = fix_common_ocr_mistakes(re.sub(r'[^0-9]', '', line2_raw))

                # Định dạng lại dòng số: thêm dấu chấm trước 2 số cuối
                if len(line2) >= 3:
                    line2 = line2[:-2] + '.' + line2[-2:]

                full_plate = f"{line1},{line2}"
                print("🔍 Biển số quét được:", full_plate)

                if 5 <= len(full_plate) <= 15:
                    return full_plate

            else:
                # Chỉ có 1 dòng kết quả
                single_raw = results[0].upper()
                single = fix_common_ocr_mistakes(re.sub(r'[^A-Z0-9]', '', single_raw))
                print("🔍 Biển số quét được (1 dòng):", single)
                if 5 <= len(single) <= 12:
                    return single

    return None  # Nếu không có biển nào được nhận diện
