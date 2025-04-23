from firebase_admin import firestore
import firebase_admin

# Khởi tạo Firebase App (nếu chưa có)
if not firebase_admin._apps:
    cred = firebase_admin.credentials.Certificate(".venv/serviceAccountKey.json")
    firebase_admin.initialize_app(cred)

db = firestore.client()


def get_field_from_all_docs(collection_name, field_name):
    """
    Lấy giá trị của một field từ tất cả documents trong collection

    Args:
        collection_name (str): Tên collection
        field_name (str): Tên field cần lấy

    Returns:
        list: Danh sách các giá trị của field
    """
    field_values = []

    # Lấy tất cả documents trong collection
    docs = db.collection(collection_name).stream()

    for doc in docs:
        doc_data = doc.to_dict()
        if field_name in doc_data:
            field_values.append(doc_data[field_name])
        else:
            print(f"Document {doc.id} không có field {field_name}")

    return field_values


# Sử dụng hàm
collection_name = "thongtindangky"  # Thay bằng tên collection của bạn
field_name = "biensoxe"  # Thay bằng tên field bạn muốn lấy

values = get_field_from_all_docs(collection_name, field_name)
print(f"Các giá trị của field {field_name}:")
for value in values:
    print(value)