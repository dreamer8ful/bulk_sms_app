# Pre-Release Testing Checklist

**App**: Bulk SMS Sender  
**Version**: 1.1 (Build 2)  
**Test Date**: ________________  
**Tester Name**: ________________  

---

## 🎯 Testing Scope

- [ ] All features tested on multiple devices
- [ ] All Android API levels (21, 28, 34) tested
- [ ] All orientations (Portrait, Landscape) tested
- [ ] Performance acceptable
- [ ] No crashes or exceptions
- [ ] ProGuard minification verified
- [ ] All permissions working

---

## 📱 Test Devices

Fill in test results for each device:

### Device 1: ________________
- Model: ________________
- Android Version: ________________
- Date Tested: ________________
- Tester: ________________

### Device 2: ________________
- Model: ________________
- Android Version: ________________
- Date Tested: ________________
- Tester: ________________

### Device 3: ________________
- Model: ________________
- Android Version: ________________
- Date Tested: ________________
- Tester: ________________

### Emulator (Optional)
- API Level: 21, 28, 34 (test all)
- Configuration: ________________
- Date Tested: ________________

---

## 🧪 Core Functionality Tests

### SMS Sending
- [ ] Single SMS sends successfully
- [ ] Multipart SMS (long messages) sends correctly
- [ ] Multiple recipients send in sequence
- [ ] Large batch (100+ recipients) completes
- [ ] Delay slider controls send speed
- [ ] Cancel button stops sending immediately
- [ ] Progress bar updates in real-time

### Message Input
- [ ] Empty message blocked with error
- [ ] Message preview displays correctly
- [ ] Personalization with names works
- [ ] Special characters handled properly
- [ ] Very long messages supported (multipart)
- [ ] Message cleared after sending

### Phone Number Input
- [ ] Empty numbers blocked with error
- [ ] Numbers without format accepted
- [ ] +1 format accepted
- [ ] Numbers with spaces/dashes handled
- [ ] Comma/semicolon/newline separators work
- [ ] Duplicate numbers removed automatically
- [ ] Empty lines ignored
- [ ] Thousands of numbers accepted

### Import Features
- [ ] CSV import: File picker opens
- [ ] CSV import: Valid files load correctly
- [ ] CSV import: Numbers extracted properly
- [ ] CSV import: Names imported (if included)
- [ ] Contact picker: Permissions requested
- [ ] Contact picker: Contacts load completely
- [ ] Contact picker: Search/filter works
- [ ] Contact picker: Multiple selection works
- [ ] Contact picker: Selected contacts added to list

### Error Handling
- [ ] Invalid phone numbers logged
- [ ] Failed SMS logged with error code
- [ ] Failed numbers displayed at end
- [ ] Network errors handled gracefully
- [ ] Permission denial handled
- [ ] File read errors show message
- [ ] CSV format errors handled

---

## 🔐 Permissions Tests

### SMS Permission (SEND_SMS)
- [ ] Permission request shown on first send
- [ ] Permission request clear and understandable
- [ ] Permission granted: app sends SMS
- [ ] Permission denied: error message shown
- [ ] Second request if first denied
- [ ] Revoked permission detected

### Phone State Permission (READ_PHONE_STATE)
- [ ] Permission request shown
- [ ] Permission granted: SmsManager accessible
- [ ] Permission denied: error handled
- [ ] Revoked permission detected

### Contacts Permission (READ_CONTACTS)
- [ ] Permission request shown on contact picker tap
- [ ] Permission granted: contacts load
- [ ] Permission denied: error message shown
- [ ] Revoked permission: contact picker disabled

### Runtime Permissions (Android 6.0+)
- [ ] Permissions requested at runtime
- [ ] Permissions granted: functionality works
- [ ] Permissions denied: graceful error
- [ ] Permission status checked before use
- [ ] Settings app opens for permission change

---

## 🎨 UI/UX Tests

### Layout & Orientation
- [ ] Portrait orientation: all UI visible
- [ ] Landscape orientation: all UI visible
- [ ] No text overflow or cutoff
- [ ] Buttons accessible in both orientations
- [ ] ScrollView functions properly with many contacts
- [ ] RecyclerView scrolls smoothly

### Visual Design
- [ ] Material Design principles followed
- [ ] Colors consistent with app theme
- [ ] Icons clear and recognizable
- [ ] Text sizes readable
- [ ] Spacing and padding appropriate
- [ ] Status bar not overlapped

### Interactions
- [ ] Buttons respond to taps immediately
- [ ] Slider responds smoothly
- [ ] EditText keyboard appears/disappears
- [ ] No accidental double-taps
- [ ] Loading states visible
- [ ] Disabled states clear (grayed out)

### Notifications/Toasts
- [ ] Success toasts appear
- [ ] Error toasts appear
- [ ] Import success message clear
- [ ] Permission denial message helpful
- [ ] Toast messages readable
- [ ] Multiple toasts stacked properly

---

## ⚡ Performance Tests

### App Launch
- [ ] App launches within 2 seconds
- [ ] No startup crashes
- [ ] UI responsive immediately
- [ ] No ANR (Application Not Responding) errors

### Message Sending
- [ ] 10 messages: < 20 seconds
- [ ] 100 messages: < 3 minutes
- [ ] 500 messages: < 15 minutes
- [ ] No lag during sending
- [ ] Progress bar smooth
- [ ] Device responsive during sending

### Contact Loading
- [ ] 100 contacts: < 1 second
- [ ] 1000 contacts: < 2 seconds
- [ ] Search responsive (< 500ms filter)
- [ ] RecyclerView smooth scroll
- [ ] No memory leaks (checked with debugger)

### CSV Import
- [ ] 100 numbers: < 2 seconds
- [ ] 1000 numbers: < 5 seconds
- [ ] Large file parsing smooth
- [ ] No crashes with corrupted files

---

## 🔍 Code Quality Tests

### Minification
- [ ] Release APK size reasonable (< 10MB)
- [ ] Debug APK built successfully
- [ ] App runs with minification enabled
- [ ] ProGuard mapping file generated
- [ ] All classes obfuscated correctly

### Lint Warnings
- [ ] No critical lint issues
- [ ] No hardcoded strings (except test data)
- [ ] No unused resources
- [ ] All permissions justified
- [ ] Accessibility score acceptable

### Memory
- [ ] No memory leaks (tested with Profiler)
- [ ] Memory usage reasonable (< 100MB)
- [ ] Garbage collection working
- [ ] Long-running test (500+ messages) stable

### Battery/Network
- [ ] No excessive battery drain
- [ ] No unnecessary network calls
- [ ] Background operations minimal
- [ ] Appropriate wake locks used

---

## 🌐 Device Compatibility

### Minimum SDK (API 21)
- [ ] Tested on API 21 device/emulator
- [ ] Features work without crashes
- [ ] UI renders correctly
- [ ] Permissions handled properly

### Target SDK (API 34)
- [ ] Tested on API 34 device
- [ ] All features work
- [ ] Modern Android features supported

### Version Coverage
- [ ] API 21 (Android 5.0): ✓ / ✗
- [ ] API 28 (Android 9.0): ✓ / ✗
- [ ] API 31 (Android 12.0): ✓ / ✗
- [ ] API 34 (Android 14.0): ✓ / ✗

---

## 🐛 Bug & Crash Tests

### Stability
- [ ] No crashes on first launch
- [ ] No crashes during sending
- [ ] No crashes on permission denial
- [ ] No crashes with empty input
- [ ] No crashes with very large input

### Edge Cases
- [ ] Single recipient: works
- [ ] 1000 recipients: works
- [ ] Empty message: blocked
- [ ] Very long message (500+ chars): works
- [ ] Invalid phone numbers: logged
- [ ] Rapid button presses: handled
- [ ] Send during no service: error shown
- [ ] Background app: resumed properly

### Recovery
- [ ] App resumable after permission dialog
- [ ] App resumable after file picker
- [ ] App resumable after phone lock
- [ ] State preserved on orientation change
- [ ] Progress preserved if minimized

---

## 🎤 User Experience

### First Time Use
- [ ] App loads with helpful UI
- [ ] Instructions clear for new user
- [ ] Tutorial/tips helpful (if included)
- [ ] Default values reasonable
- [ ] Getting started obvious

### Workflow Smoothness
- [ ] Message → Numbers → Send is intuitive
- [ ] Import options easily discoverable
- [ ] Delay adjustment straightforward
- [ ] Progress tracking clear
- [ ] Results understandable

### Accessibility (Optional)
- [ ] Screen reader compatible
- [ ] Touch targets ≥ 48dp
- [ ] Color contrast sufficient
- [ ] Text sizes scalable
- [ ] No flashing or seizure triggers

---

## 📊 Test Results Summary

### Overall Status
- [ ] **PASS** - Ready for release
- [ ] **FAIL** - Needs fixes before release
- [ ] **CONDITIONAL PASS** - Minor issues, acceptable for release

### Critical Issues Found
```
Issue 1: ________________
Severity: High / Medium / Low
Status: Fixed / Pending

Issue 2: ________________
Severity: High / Medium / Low
Status: Fixed / Pending
```

### Recommendations
```
1. ________________
2. ________________
3. ________________
```

---

## 📝 Sign-Off

**Tested By**: ________________  
**Date**: ________________  
**Signature**: ________________  

**Manager Review**: ________________  
**Approved for Release**: [ ] Yes [ ] No  
**Comments**: ________________

---

## Appendix: Test Commands

### Build Debug
```powershell
./gradlew assembleDebug
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

### Build Release
```powershell
./gradlew bundleRelease
# or
./gradlew assembleRelease
```

### View Logs
```powershell
adb logcat | findstr bulksms
```

### Memory Profiler
- Open Android Studio
- Run → Attach Debugger
- View → Tool Windows → Profiler
- Memory tab → Check heap

### Check APK Size
```powershell
ls -la app\build\outputs\apk\release\app-release.apk
```

### Extract APK from Device
```powershell
adb shell pm path com.introsoft.bulksms
adb pull /data/app/com.introsoft.bulksms-*/base.apk
```

---

**Test Date**: ________________  
**Next Test**: ________________  
**Release Target**: 2026-07-25

