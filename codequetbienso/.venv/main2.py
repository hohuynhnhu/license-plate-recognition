from nhan_dien_ra import detect_license_plate
from firebase_service import FirebaseService
import time
def normalize_plate(plate):
    if not plate:
        return None
    return (plate.replace(".", "").upper())


plate = detect_license_plate()
bien_so_quet = normalize_plate(plate)


print("Biển số quét được:", bien_so_quet)

#  Lấy danh sách biển số từ Firebase
firebase_service = FirebaseService()
ds_bien_so = firebase_service.get_all_license_plates()

print("Danh sách biển số trong DB:", ds_bien_so)

#  So sánh
if bien_so_quet:
    if bien_so_quet in ds_bien_so:
        bien_so_data = firebase_service.get_license_plate_data(bien_so_quet)
        if bien_so_data:
            trangthai = bien_so_data.get('trangthai')  # Lấy giá trị 'trangthai' từ dữ liệu
            if trangthai is False:
                print("Biển số có 'trangthai' = False.")

                #  Cập nhật trangthaicong = True
                firebase_service.update_license_plate_field(True)
                firebase_service.delete_license_plate(bien_so_quet)
            else:
                firebase_service.update_canhbao(bien_so_quet,True)
                time.sleep(10)
                bien_so_data = firebase_service.get_license_plate_data(bien_so_quet)
                if bien_so_data:
                    trangthai = bien_so_data.get('trangthai')  # Lấy giá trị 'trangthai' từ dữ liệu
                    print("Cap nhat trang thai sau 10s: ",trangthai)
                    if trangthai is False:
                        print("Biển số có 'trangthai' = False.")

                        # Cập nhật trangthaicong = True
                        firebase_service.update_license_plate_field(True)
                        firebase_service.delete_license_plate(bien_so_quet)


        else:
            print("Không lấy được dữ liệu của biển số.")
    else:
        print("Biển số không có trong DB.")
else:
    print(" Không quét được biển số.")

