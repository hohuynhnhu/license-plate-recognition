import cv2
from pyzbar.pyzbar import decode
import requests
import time

FIREBASE_URL = "https://tramxeuth-default-rtdb.firebaseio.com/"  # ← sửa lại đúng Realtime DB của bạn

def upload_to_firebase(data):
    timestamp = int(time.time())
    path = f"{FIREBASE_URL}/qrcodes/{timestamp}.json"
    response = requests.put(path, json={"data": data})
    print(f" Đã gửi lên Firebase: {response.status_code}, {response.text}")

def scan_qr():
    cap = cv2.VideoCapture(0)
    scanned_data = set()

    print("Đang quét QR... Bấm 'q' để thoát.")
    while True:
        ret, frame = cap.read()
        if not ret:
            break

        for barcode in decode(frame):
            qr_data = barcode.data.decode("utf-8")
            (x, y, w, h) = barcode.rect
            cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 255, 0), 2)
            cv2.putText(frame, qr_data, (x, y - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.9, (0, 255, 0), 2)

            if qr_data not in scanned_data:
                print("QR:", qr_data)
                upload_to_firebase(qr_data)
                scanned_data.add(qr_data)

        cv2.imshow("Quét mã QR", frame)
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

    cap.release()
    cv2.destroyAllWindows()

if __name__ == "__main__":
    scan_qr()
