# Quick Start: Releasing Bulk SMS App

## TL;DR - 5 Steps to Release

### Step 1: Generate Signing Key (One-time)
```powershell
cd app
keytool -genkey -v -keystore key.keystore -keyalg RSA -keysize 2048 -validity 10000 -alias release_key
```
Follow prompts and remember your passwords!

### Step 2: Configure Signing
Set environment variables:
```powershell
[Environment]::SetEnvironmentVariable("KEYSTORE_PASSWORD", "your_password", "User")
[Environment]::SetEnvironmentVariable("KEY_ALIAS", "release_key", "User")
[Environment]::SetEnvironmentVariable("KEY_PASSWORD", "your_password", "User")
```

Or create `gradle.properties` in app directory:
```properties
KEYSTORE_PATH=./app/key.keystore
KEYSTORE_PASSWORD=your_password
KEY_ALIAS=release_key
KEY_PASSWORD=your_password
```

### Step 3: Build Release
```powershell
./gradlew bundleRelease
# For Play Store: app\build\outputs\bundle\release\app-release.aab
```

### Step 4: Test
- Install on multiple test devices
- Verify all features work
- Check UI isn't broken after minification

### Step 5: Upload to Play Store
- Go to Google Play Console
- Upload the AAB file
- Complete store listing
- Submit for review

## Full Guides

- **Complete Checklist**: See `RELEASE_CHECKLIST.md`
- **Detailed Signing Setup**: See `SIGNING_GUIDE.md`
- **Release Summary**: See `RELEASE_SUMMARY.md`

## What's Changed in This Version

✅ **v1.1 (Build 2) Changes:**
- Code minification enabled
- Resource shrinking enabled
- ProGuard rules configured
- Signing configuration added
- Release documentation added

## Files Created

1. `proguard-rules.pro` - Code obfuscation rules
2. `RELEASE_CHECKLIST.md` - Full release checklist
3. `SIGNING_GUIDE.md` - Signing setup guide
4. `RELEASE_SUMMARY.md` - Detailed release summary
5. `.gitignore` - Security configuration

## Current App Info

- **App Name**: Bulk SMS Sender
- **Package**: com.introsoft.bulksms (update as needed)
- **Min SDK**: 21 (Android 5.0)
- **Target SDK**: 34 (Android 14)
- **Version**: 1.1
- **Build**: 2

## Before You Start

- [ ] Install Java (JDK 11 or later)
- [ ] Set JAVA_HOME environment variable
- [ ] Have Android SDK installed
- [ ] Prepare app screenshots for store
- [ ] Write privacy policy (if needed)

## Common Issues

**"JAVA_HOME not set"**
- Download Java: https://www.oracle.com/java/technologies/downloads/
- Set JAVA_HOME to Java installation path

**"Keystore file not found"**
- Run Step 1 to create keystore
- Ensure key.keystore is in app/ directory

**"Invalid signing config"**
- Check environment variables or gradle.properties
- Verify keystore password is correct

**"ProGuard broke the app"**
- Check proguard-rules.pro for missing -keep rules
- Run: `./gradlew bundleDebug` to test debug version first

## Next: Update Package Name (Recommended)

Before releasing, change from example package to yours:

**File**: `app/build.gradle.kts` line 11
```kotlin
// Change from:
applicationId = "com.introsoft.bulksms"
// To:
applicationId = "com.yourcompany.bulksms"
```

Then rebuild.

## Help & Support

- **Android Studio Docs**: https://developer.android.com/studio/publish
- **Google Play Console**: https://play.google.com/console
- **ProGuard/R8**: https://developer.android.com/build/shrink-code

---

**Status**: Ready to build! Follow the TL;DR steps above.

