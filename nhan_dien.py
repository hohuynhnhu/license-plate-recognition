import cv2
import torch
from ultralytics import YOLO
import winsound  # Thêm thư viện winsound để phát âm thanh

# Tải model YOLOv8 đã huấn luyện
model = YOLO("D:/train_bien_so_xe/runs/detect/train/weights/best.pt")

# Mở camera
cap = cv2.VideoCapture(0)  # 0 là webcam mặc định

best_conf = 0  # Lưu confidence cao nhất
best_frame = None  # Ảnh có biển số rõ nhất
best_plate = None  # Ảnh biển số rõ nhất

while True:
    ret, frame = cap.read()  # Đọc ảnh từ camera
    if not ret:
        break

    results = model(frame)  # Nhận diện biển số

    for r in results:
        for box in r.boxes:
            confidence = box.conf[0].item()  # Lấy độ tin cậy (confidence score)

            # Nếu phát hiện biển số xe và confidence cao hơn ảnh cũ, lưu ảnh mới
            if confidence > best_conf:
                best_conf = confidence
                best_frame = frame.copy()

                # Cắt biển số từ ảnh gốc
                x1, y1, x2, y2 = map(int, box.xyxy[0])
                best_plate = frame[y1:y2, x1:x2]

            # Vẽ khung biển số lên ảnh
            x1, y1, x2, y2 = map(int, box.xyxy[0])
            cv2.rectangle(frame, (x1, y1), (x2, y2), (0, 255, 0), 2)

    cv2.imshow("Nhan Dien Bien So", frame)  # Hiển thị ảnh từ camera

    # Nếu confidence tốt nhất vượt ngưỡng, tự động lưu ảnh và xử lý biển số
    if best_conf > 0.7 and best_plate is not None:
        # Lưu ảnh gốc có biển số
        cv2.imwrite("bien_so_xe.jpg", best_frame)

        # Chuyển biển số sang đen trắng
        plate_gray = cv2.cvtColor(best_plate, cv2.COLOR_BGR2GRAY)

        # Tăng độ tương phản cho ảnh biển số
        alpha = 2.0  # Tăng độ tương phản
        beta = 0     # Điều chỉnh độ sáng (0 là không thay đổi)

        # Áp dụng tăng độ tương phản
        plate_contrast = cv2.convertScaleAbs(plate_gray, alpha=alpha, beta=beta)

        # Lưu ảnh biển số đen trắng đã tăng độ tương phản
        cv2.imwrite("bien_so_xe_bw_contrast.jpg", plate_contrast)

        print("📸 Ảnh biển số rõ nhất đã được lưu!")
        print("🔳 Ảnh biển số đen trắng đã tăng độ tương phản và lưu thành công!")

        # Phát âm thanh khi chụp được ảnh
        winsound.Beep(1000, 500)  # Phát âm thanh tần số 1000Hz, kéo dài 500ms

        best_conf = 0  # Reset để lưu ảnh mới nếu có biển số rõ hơn

    # Thoát nếu nhấn 'q'
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

cap.release()
cv2.destroyAllWindows()
