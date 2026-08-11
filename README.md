# Bulk SMS Sender (Native Kotlin, Android only)

Sends one message to a list of phone numbers using **your phone's own SIM
and carrier plan**, via Android's native `SmsManager` API directly — no
third-party plugin layer.

## Why this can't be iOS

Apple does not allow apps to send SMS programmatically without the user
manually tapping "Send" for each message in the Messages app. This is an
iOS platform restriction, not a framework limitation — so native or not,
there's no iOS version of this.

## Project structure

```
BulkSmsKotlin/
├── build.gradle.kts              (project-level)
├── settings.gradle.kts
└── app/
    ├── build.gradle.kts           (app module — dependencies, SDK versions)
    └── src/main/
        ├── AndroidManifest.xml    (SMS permissions declared here)
        ├── java/com/introsoft/bulksms/MainActivity.kt
        └── res/
            ├── layout/activity_main.xml
            └── values/{strings.xml, themes.xml}
```

## Setup

1. Install **Android Studio** (includes the Android SDK and Gradle):
   https://developer.android.com/studio
2. Open Android Studio → **Open** → select the `BulkSmsKotlin` folder.
   Let Gradle sync (it will download dependencies automatically).
3. Connect an Android phone via USB with **USB debugging** enabled
   (Settings → About phone → tap "Build number" 7 times → Developer
   options → USB debugging), or use an emulator — but a real device with
   an active SIM is required to actually send SMS.
4. Click **Run** (the green triangle) with your device selected.

No extra manifest editing needed — the `SEND_SMS` and `READ_PHONE_STATE`
permissions are already declared in `AndroidManifest.xml`. The app
requests them at runtime the first time you tap Send.

## Using the app

1. Type your message in the top box.
2. Paste your phone numbers in the bottom box — one per line (commas or
   semicolons also work). Include country code, e.g. `+15551234567`.
   Duplicates are removed automatically.
3. Adjust the delay slider if needed (default 1500ms between sends).
4. Tap **Send to all numbers**, grant the SMS permission when prompted,
   and confirm in the dialog.
5. Watch sent/failed counts update live via the progress bar. Each
   message's actual delivery status is tracked with a `BroadcastReceiver`
   tied to Android's own sent-confirmation callback — so "sent" here
   means the radio actually confirmed transmission, not just that the
   function call didn't throw.

## Things worth knowing before sending to 500 numbers

- **Pacing matters.** At the default 1.5s delay, 500 texts takes ~12-13
  minutes. If failures pile up, increase the delay — some devices/carriers
  throttle SMS sent in quick succession.
- **Long messages get split.** If your message exceeds one SMS segment
  (160 characters for plain text), the app automatically uses
  `sendMultipartTextMessage` so it's tracked as one logical send even
  though it goes out as multiple parts.
- **Failed numbers are listed** at the end so you can spot bad formatting
  — numbers should generally be in `+1XXXXXXXXXX`-style international
  format.
- **Keep the app in the foreground** while sending. It runs on Android's
  coroutine lifecycle scope tied to the Activity, so backgrounding or
  killing the app mid-run will stop the batch.
- **Carrier fair-use caps** on bundled SMS plans can still apply per hour
  or per day even though the message count is covered by your bill —
  worth checking if you'll be doing this regularly.
