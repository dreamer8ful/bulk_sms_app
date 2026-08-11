# Implementation Plan - Prepare Project for Release

This plan outlines the steps to prepare the Bulk SMS app for a production release, focusing on build optimization, security, and Play Store readiness.

## User Review Required

> [!IMPORTANT]
> **Google Play SMS Policy**: Google Play has strict policies regarding apps that use the `SEND_SMS` permission. Unless this app is the device's **default SMS handler**, it may be rejected from the Play Store.
> - If you intend to publish on Play Store, you may need to apply for a [Permission Declaration Form](https://support.google.com/googleplay/android-developer/answer/9047303) or implement the [SMS Retriever API](https://developers.google.com/identity/sms-retriever/overview) if applicable (though not for bulk sending).
> - For "In-house" or "Ad-hoc" distribution (APK), this is not an issue.

> [!CAUTION]
> **Signing Credentials**: I will provide a template for signing configurations. **DO NOT** commit your actual keystore file or passwords to a public version control system. Use `local.properties` or environment variables for sensitive data.

## Proposed Changes

### 1. Build Optimization (R8/ProGuard)
#### [MODIFY] [app/build.gradle.kts](file:///C:/Users/lenovo/develop/bulk_sms_app/app/build.gradle.kts)
- Enable `isMinifyEnabled = true` to remove unused code and obfuscate.
- Enable `isShrinkResources = true` to remove unused resources.
- Add a placeholder `signingConfigs` block.

#### [NEW] [proguard-rules.pro](file:///C:/Users/lenovo/develop/bulk_sms_app/app/proguard-rules.pro)
- Add standard ProGuard rules for a Kotlin Android app to prevent R8 from over-optimizing required classes (like ViewBinding or ViewModel).

### 2. Manifest & Resources
#### [MODIFY] [AndroidManifest.xml](file:///C:/Users/lenovo/develop/bulk_sms_app/app/src/main/AndroidManifest.xml)
- Update `android:icon` and `android:roundIcon` to use `@mipmap/ic_launcher`.
- Ensure `android:allowBackup="true"` is intentional (recommended for most apps).

#### [NEW] [mipmap-anydpi/ic_launcher_round.xml](file:///C:/Users/lenovo/develop/bulk_sms_app/app/src/main/res/mipmap-anydpi/ic_launcher_round.xml)
- Create a round icon for better compatibility with modern launchers.

### 3. Documentation & Release Guide
#### [MODIFY] [README.md](file:///C:/Users/lenovo/develop/bulk_sms_app/README.md)
- Add a "Release Instructions" section explaining how to:
    - Generate a Signing Key (JKS).
    - Build the Release APK/Bundle.
    - Handle the SMS permission policy.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:assembleRelease` to verify that the project compiles with R8 enabled.
- Verify the size of the release APK compared to the debug APK.

### Manual Verification
- Install the release APK on a device.
- Verify that the app still functions correctly (R8 can sometimes break reflection-based code, though this app uses very little reflection).
- Verify the app icon appears correctly as a round icon where supported.
