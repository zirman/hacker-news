# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#-dontobfuscate

# Keep `Companion` object fields of serializable classes.
# This avoids serializer lookup through `getDeclaredClasses` as done for named companion objects.
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}

# Keep `serializer()` on companion objects (both default and named) of serializable classes.
-if @kotlinx.serialization.Serializable class ** {
    static **$* *;
}
-keepclassmembers class <2>$<3> {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep `INSTANCE.serializer()` of serializable objects.
-if @kotlinx.serialization.Serializable class ** {
    public static ** INSTANCE;
}
-keepclassmembers class <1> {
    public static <1> INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}

# @Serializable and @Polymorphic are used at runtime for polymorphic serialization.
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault

# Don't print notes about potential mistakes or omissions in the configuration for kotlinx-serialization classes
# See also https://github.com/Kotlin/kotlinx.serialization/issues/1900
-dontnote kotlinx.serialization.**

# Serialization core uses `java.lang.ClassValue` for caching inside these specified classes.
# If there is no `java.lang.ClassValue` (for example, in Android), then R8/ProGuard will print a warning.
# However, since in this case they will not be used, we can disable these warnings
-dontwarn kotlinx.serialization.internal.ClassValueReferences

# Fixes Ktor submitForm()
-keepclassmembers class io.ktor.http.** { *; }

#-keep class * extends com.google.protobuf.GeneratedMessageLite { *; }

-dontwarn java.net.http.HttpClient$Builder
-dontwarn java.net.http.HttpClient$Redirect
-dontwarn java.net.http.HttpClient$Version
-dontwarn java.net.http.HttpClient
-dontwarn java.net.http.HttpConnectTimeoutException
-dontwarn java.net.http.HttpRequest$BodyPublisher
-dontwarn java.net.http.HttpRequest$BodyPublishers
-dontwarn java.net.http.HttpRequest$Builder
-dontwarn java.net.http.HttpRequest
-dontwarn java.net.http.HttpResponse$BodyHandler
-dontwarn java.net.http.HttpResponse
-dontwarn java.net.http.HttpTimeoutException
-dontwarn java.net.http.WebSocket$Builder
-dontwarn java.net.http.WebSocket$Listener
-dontwarn java.net.http.WebSocket
-dontwarn java.net.http.WebSocketHandshakeException

# prevent some name obfuscation to prevent runtime errors
-keepnames class androidx.lifecycle.** { *; }
-keepnames class com.mohamedrejeb.ksoup.html.parser.** { *; }
#-keeppackagenames
#-whyareyoukeeping
-assumenosideeffects class android.util.Log {
     public static boolean isLoggable(java.lang.String, int);
     public static int v(...);
     public static int d(...);
     public static int i(...);
}
