# Signing Configuration Guide

## Overview
To release an app on Google Play Store, you need to sign your APK/AAB with a private key. This guide walks you through the setup.

## Step 1: Generate a Keystore (One-time Setup)

### Option A: Using Windows PowerShell
```powershell
# Navigate to the app directory
cd C:\Users\lenovo\develop\bulk_sms_app\app

# Generate keystore with RSA 2048-bit key, valid for 10000 days (~27 years)
keytool -genkey -v -keystore key.keystore -keyalg RSA -keysize 2048 -validity 10000 -alias release_key
```

### Option B: Using Android Studio
1. Open Android Studio
2. Navigate to **Build** → **Generate Signed Bundle/APK**
3. Select your module (app)
4. Click **Create new...** to create a new keystore
5. Fill in the form and save to `app/key.keystore`

## Step 2: Configure Gradle for Signing

### Option A: Using Environment Variables (Recommended for CI/CD)

Set environment variables on your system:
- `KEYSTORE_PASSWORD`: Your keystore password
- `KEY_ALIAS`: The key alias (default: `release_key`)
- `KEY_PASSWORD`: Your key password

```powershell
# Set environment variables (Windows PowerShell)
[Environment]::SetEnvironmentVariable("KEYSTORE_PASSWORD", "your_password", "User")
[Environment]::SetEnvironmentVariable("KEY_ALIAS", "release_key", "User")
[Environment]::SetEnvironmentVariable("KEY_PASSWORD", "your_password", "User")

# Verify they're set
Get-ChildItem Env: | Select-Object Name, Value | Where-Object { $_.Name -like "*KEY*" }
```

### Option B: Using gradle.properties (Local Development)

Create `gradle.properties` in the `app/` directory (or root directory):
```properties
KEYSTORE_PATH=./app/key.keystore
KEYSTORE_PASSWORD=your_keystore_password
KEY_ALIAS=release_key
KEY_PASSWORD=your_key_password
```

Then update `app/build.gradle.kts`:
```kotlin
signingConfigs {
    create("release") {
        val keystorePath = project.properties["KEYSTORE_PATH"]?.toString()
        val keystorePass = project.properties["KEYSTORE_PASSWORD"]?.toString()
        val keyAlias = project.properties["KEY_ALIAS"]?.toString()
        val keyPass = project.properties["KEY_PASSWORD"]?.toString()
        
        if (!keystorePath.isNullOrEmpty()) {
            storeFile = file(keystorePath)
            storePassword = keystorePass
            keyAlias = keyAlias
            keyPassword = keyPass
        }
    }
}
```

**⚠️ IMPORTANT**: Do NOT commit `gradle.properties` with passwords to version control!
Add it to `.gitignore`:
```
gradle.properties
*.keystore
```

## Step 3: Build Release APK/AAB

### Build Release APK
```powershell
cd C:\Users\lenovo\develop\bulk_sms_app
./gradlew.bat assembleRelease
# Output: app\build\outputs\apk\release\app-release.apk
```

### Build Release AAB (Recommended for Play Store)
```powershell
cd C:\Users\lenovo\develop\bulk_sms_app
./gradlew.bat bundleRelease
# Output: app\build\outputs\bundle\release\app-release.aab
```

## Step 4: Verify Signing

### View Keystore Contents
```powershell
keytool -list -v -keystore app/key.keystore
```

### Verify APK Signature
```powershell
jarsigner -verify -verbose app/build/outputs/apk/release/app-release.apk
```

### View Certificate Details
```powershell
keytool -printcert -jarfile app/build/outputs/apk/release/app-release.apk
```

## Step 5: Upload to Google Play Console

1. Go to [Google Play Console](https://play.google.com/console)
2. Create a new application or update existing one
3. Navigate to **Release** → **Production**
4. Upload the signed AAB file
5. Complete store listing information
6. Submit for review

## Troubleshooting

### "Keystore file not found"
- Ensure `key.keystore` is in the `app/` directory
- Check the path in signingConfig

### "Invalid password"
- Verify keystore password is correct
- Run: `keytool -list -keystore app/key.keystore` to test

### "Alias not found"
- List aliases: `keytool -list -v -keystore app/key.keystore`
- Use the correct alias from the output

### Build fails with signing error
- Check environment variables are set correctly
- Or update gradle.properties with correct values
- Ensure file permissions are correct

## Security Best Practices

1. **Never commit the keystore file** to version control
2. **Never hardcode passwords** in build scripts
3. **Keep passwords secure** - consider using a password manager
4. **Use the same keystore** for all app updates (required by Play Store)
5. **Back up your keystore** in a secure location
6. **Sign your app locally**, not in CI/CD pipelines if possible

## Useful Commands Reference

```powershell
# Generate keystore
keytool -genkey -v -keystore key.keystore -keyalg RSA -keysize 2048 -validity 10000 -alias release_key

# List keystore keys
keytool -list -v -keystore key.keystore

# Import existing key
keytool -importkeystore -srckeystore old.keystore -destkeystore new.keystore

# Build release
./gradlew.bat bundleRelease

# Verify APK
jarsigner -verify -verbose app/build/outputs/apk/release/app-release.apk
```

## Next Steps
- See RELEASE_CHECKLIST.md for full release preparation
- Update app version in build.gradle.kts before each release
- Create release notes for Google Play Store

