from nhan_dien import detect_license_plate
from firebase_hander import get_field_from_all_docs
from firebase import firebase

def normalize_plate(plate):
    if not plate:
        return None
    return (plate.replace(".", "").upper())
# .replace(" ", "").replace("-", "")
firebase = firebase.FirebaseApplication('https://tramxeuth-default-rtdb.firebaseio.com', None)

# 🔍 Lấy biển số từ camera
plates = detect_license_plate()  # trả về danh sách
bien_so_quet = normalize_plate(plates) if plates else None
# bien_so_firebase = bien_so_quet.replace(',', '_').replace('.', '_')

print("🔍 Biển số quét được:", bien_so_quet)

# 📂 Lấy danh sách biển số đã đăng ký từ Firestore
collection_name = "thongtindangky"
field_name = "biensoxe"
ds_bien_so_raw = get_field_from_all_docs(collection_name, field_name)
ds_bien_so = [normalize_plate(val) for val in ds_bien_so_raw if val]

print("📂 Danh sách từ Firestore:", ds_bien_so)

# ✅ So sánh biển số
hop_le = bien_so_quet in ds_bien_so
if hop_le:

    # 2. Ghi trạng thái tổng thể
    firebase.put('/', 'trangthaicong', hop_le)
    firebase.put('/biensotrongbai', bien_so_quet, {'trangthai': hop_le, 'canhbao': False})



    print("📥 Đã ghi kết quả vào Realtime Database!")
else:
    print("khong nam trong danh sach")

firebase.put('/', 'trangthaicong', False)
