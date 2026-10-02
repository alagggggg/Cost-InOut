# Thu Chi Android Wrapper

Dự án Android WebView hoàn chỉnh để đóng gói ứng dụng Thu Chi thành APK.

## Đã bổ sung

- WebViewAssetLoader với origin ổn định `https://appassets.androidplatform.net`.
- JavaScript và DOM Storage.
- Chặn điều hướng ra ngoài ứng dụng.
- Tắt truy cập `file://` và truy cập tệp rộng.
- Nhập JSON qua `onShowFileChooser()` và Android file picker.
- Xuất JSON qua `ACTION_CREATE_DOCUMENT` bằng AndroidBridge.
- Xử lý nút/cử chỉ Back của Android.
- Lưu và phục hồi trạng thái WebView khi Activity được tạo lại.
- Sao lưu dữ liệu ứng dụng khi Android Backup được bật.
- `adjustResize` cho bàn phím.

## Cách dùng

1. Đổi tên file HTML mới nhất thành `index.html`.
2. Chép file vào `app/src/main/assets/index.html`.
3. Áp dụng đoạn mã trong `HTML_ANDROID_PATCH.md`.
4. Mở dự án bằng Android Studio dùng JDK 17.
5. Chọn Sync Project with Gradle Files.
6. Build > Generate App Bundles or APKs > Generate APKs.

## Lưu ý dữ liệu

Luôn giữ nguyên `applicationId = com.quan.thuchi` và URL AssetLoader để localStorage không đổi origin khi cập nhật APK.


## Widget 4×2 Thu chi nhanh

- 4 nút Tiền vào và 4 nút Tiền ra.
- Tên loại sự kiện được nhập khi thêm widget vào màn hình chính.
- Nhấn nút sẽ mở ứng dụng, chọn sẵn loại giao dịch và sự kiện, đặt con trỏ tại ô số tiền.
- Nút MỞ ↗ mở thẳng ứng dụng.
- Muốn thay cấu hình: xóa widget và thêm lại, sau đó nhập 8 tên mới.
