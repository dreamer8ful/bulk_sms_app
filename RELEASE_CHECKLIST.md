# Bulk SMS App - Release Preparation Checklist

## Pre-Release Checklist

### 1. Code Quality
- [x] ProGuard rules configured (`proguard-rules.pro`)
- [x] Minification enabled for release builds
- [x] Resource shrinking enabled
- [x] No debug logging in code
- [x] All permissions properly declared in AndroidManifest.xml

### 2. Version Updates
- [x] Version Code: 2
- [x] Version Name: 1.1
- [ ] Update CHANGELOG.md with release notes
- [ ] Tag release in git: `git tag v1.1`

### 3. App Configuration

#### Application ID
- Current: `com.introsoft.bulksms`
- **TODO**: Change to your official package name (e.g., `com.yourcompany.bulksms`)
  - Update in `app/build.gradle.kts` line 11
  - Update in `app/src/main/AndroidManifest.xml` if needed

#### App Icon & Branding
- [x] App label set to "Bulk SMS Sender" in AndroidManifest.xml
- [ ] Replace app icon at `app/src/main/res/drawable/ic_launcher.xml`
- [ ] Ensure launcher icon meets Play Store guidelines (512x512px recommended)
- [ ] Create feature graphics if required

### 4. Signing Configuration

#### Generate Keystore (if not already created)
```powershell
# Run this command to generate a signing key
keytool -genkey -v -keystore key.keystore -keyalg RSA -keysize 2048 -validity 10000 -alias release_key

# You will be prompted for:
# - Keystore password
# - Key password
# - Certificate information (name, organization, etc.)
```

#### Configure Signing
1. Place `key.keystore` in the `app/` directory
2. Set environment variables (or add to gradle.properties):
   ```
   KEYSTORE_PASSWORD=your_keystore_password
   KEY_ALIAS=release_key
   KEY_PASSWORD=your_key_password
   ```

Alternative: Add to `gradle.properties` (local, not committed):
```properties
KEYSTORE_PATH=./app/key.keystore
KEYSTORE_PASSWORD=your_keystore_password
KEY_ALIAS=release_key
KEY_PASSWORD=your_key_password
```

Then update `app/build.gradle.kts` signingConfig block if using gradle.properties.

### 5. Testing

- [ ] Test on multiple Android devices (API 21+ as per minSdk)
- [ ] Test on API 21 (minimum), API 34 (target), and latest available
- [ ] Test SMS sending functionality thoroughly
- [ ] Test contact picker feature
- [ ] Test CSV import functionality
- [ ] Test permission requests on Android 6.0+ (runtime permissions)
- [ ] Verify UI is not broken after ProGuard minification
- [ ] Test on both portrait and landscape orientations
- [ ] Test with various message lengths (including multipart SMS)
- [ ] Test with long recipient lists (100+, 500+)
- [ ] Verify crash reporting (if using Firebase/Crashlytics)

### 6. Build Process

#### Debug Build (Testing)
```powershell
# From workspace root directory
./gradlew.bat assembleDebug
# APK will be at: app/build/outputs/apk/debug/app-debug.apk
```

#### Release Build (AAB - Google Play Store)
```powershell
# From workspace root directory
./gradlew.bat bundleRelease
# AAB will be at: app/build/outputs/bundle/release/app-release.aab
```

#### Release Build (APK)
```powershell
# From workspace root directory
./gradlew.bat assembleRelease
# APK will be at: app/build/outputs/apk/release/app-release.apk
```

### 7. Play Store / Distribution

#### Prepare Store Listing
- [ ] App name and short description
- [ ] Full description (80-4000 characters)
- [ ] Screenshots (2-8 required):
  - Main screen
  - Message input
  - SMS sending progress
  - Results/delivery status
- [ ] Feature graphic (1024x500px)
- [ ] Privacy policy URL
- [ ] Contact email
- [ ] Website URL (if applicable)
- [ ] Category selection (Productivity/Utilities)
- [ ] Content rating questionnaire
- [ ] Target audience

#### Permissions Justification (for review)
Document why each permission is needed:
- **SEND_SMS**: Core functionality - sends bulk SMS messages
- **READ_PHONE_STATE**: Needed to access SmsManager and phone status
- **READ_CONTACTS**: Optional feature to import contacts from device

#### Content Rating
- Answer content rating form on Play Console

### 8. Security & Compliance

- [ ] Enable ProGuard minification verification
- [ ] Review AndroidManifest.xml for any exposed components
- [ ] Ensure no sensitive data logged
- [ ] Verify no hardcoded passwords/API keys
- [ ] Check for any analytics or tracking (if intentional)
- [ ] Privacy policy is compliant with data collection
- [ ] GDPR compliant if targeting EU users

### 9. Build Verification

After building release APK/AAB:
```powershell
# Verify signatures
keytool -verify -verbose -certs -keystore key.keystore -file app/build/outputs/apk/release/app-release.apk
```

### 10. Final Submission

- [ ] Verify all build output files are present
- [ ] Confirm minification is working (check mapping.txt)
- [ ] Test release build on actual device
- [ ] Create release notes/changelog
- [ ] Submit to Google Play Store (or other store)
- [ ] Monitor for crashes and user feedback post-release

## Build Commands Quick Reference

```powershell
# Clean and build
./gradlew.bat clean build

# Build release AAB (recommended for Play Store)
./gradlew.bat bundleRelease

# Build release APK
./gradlew.bat assembleRelease

# Run on connected device
./gradlew.bat installRelease

# Check ProGuard mapping
type app/build/outputs/mapping/release/mapping.txt
```

## Troubleshooting

### Minification Issues
If the app crashes after enabling minification:
1. Check `app/build/outputs/mapping/release/mapping.txt` for obfuscation mappings
2. Enable ProGuard verbose output in build.gradle.kts
3. Gradually disable minification for specific classes in proguard-rules.pro
4. Use Lint to identify kept classes

### Signing Issues
```powershell
# List keystore contents
keytool -list -v -keystore key.keystore

# Verify APK signature
jarsigner -verify -verbose app/build/outputs/apk/release/app-release.apk
```

### Permission Issues
- Verify AndroidManifest.xml has correct permission declarations
- Test on device with Android 6.0+ for runtime permissions
- Check app logs: `adb logcat | findstr bulksms`

## Resources

- [Android App Release Documentation](https://developer.android.com/studio/publish)
- [Google Play Store Publishing Guide](https://play.google.com/console/about/guides/)
- [ProGuard Configuration Guide](https://www.guardsquare.com/proguard/manual/usage)
- [R8/ProGuard Rules](https://developer.android.com/build/shrink-code)

