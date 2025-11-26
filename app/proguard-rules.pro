# Android IDE ProGuard Rules

# Keep Kotlin metadata
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# Keep Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# Keep Kotlin compiler classes (necessário para compilação)
-keep class org.jetbrains.kotlin.** { *; }
-keep class kotlin.** { *; }
-dontwarn org.jetbrains.kotlin.**

# Keep D8/R8 classes
-keep class com.android.tools.r8.** { *; }
-dontwarn com.android.tools.r8.**

# Keep APK Signer
-keep class com.android.apksig.** { *; }

# Keep Sora Editor
-keep class io.github.rosemoe.sora.** { *; }
-keep class org.eclipse.tm4e.** { *; }

# Keep Gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Keep data classes
-keepclassmembers class com.androidide.model.** {
    <fields>;
    <init>(...);
}

-keepclassmembers class com.androidide.project.Project {
    <fields>;
    <init>(...);
}

# Keep Parcelable
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator CREATOR;
}

# Keep R classes
-keepclassmembers class **.R$* {
    public static <fields>;
}

# Remove logging in release
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
}

# Optimize
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification
