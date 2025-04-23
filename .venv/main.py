from nhan_dien import detect_license_plate
from firebase_hander import get_field_from_all_docs
from firebase import firebase

def normalize_plate(plate):
    if not plate:
        return None
    return plate.replace(" ", "").replace("-", "").upper()

firebase = firebase.FirebaseApplication('https://tramxeuth-default-rtdb.firebaseio.com', None)

# 🔍 Lấy biển số từ camera
bien_so_quet = detect_license_plate()
bien_so_quet = normalize_plate(bien_so_quet)
print("🔍 Biển số quét được:", bien_so_quet)

# 📂 Lấy danh sách biển số đã đăng ký từ Firestore
collection_name = "thongtindangky"
field_name = "biensoxe"
ds_bien_so_raw = get_field_from_all_docs(collection_name, field_name)
ds_bien_so = [normalize_plate(val) for val in ds_bien_so_raw if val]

print("📂 Danh sách từ Firestore:", ds_bien_so)

# ✅ So sánh biển số
hop_le = bien_so_quet in ds_bien_so
print("✅ Hợp lệ!" if hop_le else "❌ Không hợp lệ!")

# 📥 Ghi kết quả lên Realtime Database
data = {
    'biensoxe': bien_so_quet,
    'mssv': "123123123",
    'thoigian': 10,
    'trangthai': hop_le
}
firebase.put('/biensotrongbai', 'xe1', data)
firebase.put('/biensotrongbai', 'trangthaicong', False)

print("📥 Đã ghi kết quả vào Realtime Database!")
