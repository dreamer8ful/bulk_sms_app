# Release Notes - Bulk SMS Sender v1.1

**Version**: 1.1 (Build 2)
**Release Date**: July 25, 2026
**Platform**: Android 5.0+ (API 21+)

---

## ✨ What's New

### 📱 Dual SIM Support
- **SIM Selection**: Now you can choose which SIM card to use for sending messages on dual-SIM devices.
- **Auto-Detection**: The app automatically detects active SIM cards and displays carrier names (e.g., SIM 1: Safaricom, SIM 2: Airtel).

### 📊 Precision Status Tracking
- **Accurate Delivery Reports**: Completely refactored status tracking. Each recipient is now counted exactly once, even for long multi-part messages.
- **Real-time Progress**: Enhanced progress bar and status text provide a clear breakdown of Sent, Delivered, and Failed messages.

### 🎨 UI & UX Improvements
- **Accessible Actions**: The "Send" and "Cancel" buttons are now pinned to the bottom of the screen, staying above the keyboard for easier access while typing.
- **Faster Contact Search**: Optimized the contact selection list for smoother scrolling and near-instant searching, even with large address books.
- **Smart Recipient Counting**: Added a breakdown notification when sending to explain why some numbers might be skipped (e.g., duplicates or invalid formats).

### 🛠️ Core Enhancements
- **Intelligent Parsing**: Improved phone number parsing to handle "Name, Number" pairs and various formatting characters (spaces, dashes, parentheses) more reliably.
- **Production Optimization**: Enabled R8/ProGuard minification for a smaller, faster, and more secure app experience.

---

## 🐛 Bug Fixes
- Fixed an issue where the app would over-count messages when sending long texts (multi-part SMS).
- Fixed a bug that caused the "Send" button to be hidden behind the keyboard on some devices.
- Resolved a resource linking error in the contact selection UI.
- Improved cancellation logic to ensure the final status is correctly reported when stopping a batch.

---

## 🚀 How to Use the New SIM Feature
1. If you have two active SIM cards, a **"Select SIM"** section will automatically appear below the delay settings.
2. Tap the chip for the SIM you want to use.
3. Your selection will be applied to the entire batch of messages.

---

## 🔒 Privacy & Permissions
- **READ_PHONE_STATE**: This new (optional) permission is required only if you want to use the Dual SIM selection feature.
- **SEND_SMS**: Required to send messages from your device.
- **READ_CONTACTS**: Required only if you want to import recipients from your address book.

---

**Thank you for using Bulk SMS Sender!**
*If you find this app helpful, please consider leaving a review on the Play Store.*
