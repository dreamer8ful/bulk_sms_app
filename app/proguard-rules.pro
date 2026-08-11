# ProGuard rules for Bulk SMS App

# Android Framework Rules
-keep public class android.** { *; }
-keep public class androidx.** { *; }
-keep interface androidx.** { *; }

# Keep view constructors for layout inflation
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

# Keep all Activities, Services, BroadcastReceivers, and ContentProviders
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# Keep ViewModel classes
-keep public class * extends androidx.lifecycle.ViewModel {
    public <init>(...);
}

# Keep LiveData and MutableLiveData
-keep class androidx.lifecycle.LiveData { *; }
-keep class androidx.lifecycle.MutableLiveData { *; }

# Keep our app classes
-keep class com.introsoft.bulksms.** { *; }
-keep interface com.introsoft.bulksms.** { *; }

# Keep data classes and their fields
-keepclassmembers class com.introsoft.bulksms.** {
    *** get*();
    void set*(***);
}

# Keep enum constructors
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep Parcelable classes
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep Serializable classes
-keep class * implements java.io.Serializable { *; }

# Keep annotations
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.annotation.Annotation { *; }

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep inner classes
-keep class **.R
-keep class **.R$* {
    <fields>;
}

# Suppress warnings
-dontwarn android.**
-dontwarn androidx.**
-dontwarn java.**
-dontwarn javax.**
-dontwarn sun.**
-dontwarn com.google.android.material.**

