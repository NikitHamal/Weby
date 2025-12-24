# Weby ProGuard Rules

# Keep data classes for Room and Gson
-keepclassmembers class com.officialcodingconvention.weby.data.** { *; }
-keepclassmembers class com.officialcodingconvention.weby.domain.model.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# Compose
-dontwarn androidx.compose.**

# Coil
-dontwarn coil.**

# Keep crash handler
-keep class com.officialcodingconvention.weby.CrashActivity { *; }
-keep class com.officialcodingconvention.weby.WebyApplication { *; }
