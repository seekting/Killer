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
-keep class com.wang.avi.** { *; }
-keep class com.wang.avi.indicators.** { *; }
#-keep class com.seekting.killer.model.BarControl** { *; }
#-keep class com.seekting.killer.model.PersonControl** { *; }

# keep classes for @JavaScriptInterface
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable,*Annotation*,Signature,Exceptions,InnerClasses,EnclosingMethod




-keep class android.content.pm.IPackageInstallObserver {
  *;
}


#For milink
-keep class com.milink.api.v1.aidl.**{*;}

#For duokan video player
-keep class com.duokan.** {
  *;
}

-keep class io.vov.vitamio.** {
*;
}

-keep class com.google.android.exoplayer.** {
*;
}


-keep class android.chromium.** {
 *;
}
-keep class com.google.common.** {
 *;
}







-keep class sun.misc.Unsafe { *; }


#for retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-dontwarn okhttp3.**
-keep class okhttp3.** { *;}
-dontwarn okio.**

-keep public class pl.droidsonroids.gif.GifIOException{<init>(int, java.lang.String);}

-keep class * implements com.sina.weibo.sdk.api.BaseMediaObject { *; }


-keep class com.ut.** {*;}
-dontwarn com.ut.**
-keep class com.ta.** {*;}
-dontwarn com.ta.**

-dontwarn com.google.common.base.**
-dontwarn org.chromium.**
-dontwarn com.umeng.**
-dontwarn com.milink.api.v1.**
-dontwarn io.vov.vitamio.**

-dontwarn org.apache.log4j.**
-dontwarn com.android.org.**
-dontwarn android.support.v4.**
-dontwarn com.google.android.exoplayer2.**

# translation
-dontwarn com.google.android.gms.**
-dontwarn com.google.ipc.**

-keepnames class com.google.** {*;}

# translation
#ad
-keep class com.facebook.** { *;}
-keep class com.google.android.gms.** { *;}

#add Sensors Analytics keep
-dontwarn com.sensorsdata.analytics.android.**
-keep class com.sensorsdata.analytics.android.** {*;}
-keep class **.R$* { <fields>;}
-keep public class * extends android.content.ContentProvider
-keepnames class * extends android.view.View

-keep class * extends android.app.Fragment {
 public void setUserVisibleHint(boolean);
 public void onHiddenChanged(boolean);
 public void onResume();
 public void onPause();
}
-keep class android.support.v4.app.Fragment {
 public void setUserVisibleHint(boolean);
 public void onHiddenChanged(boolean);
 public void onResume();
 public void onPause();
}
-keep class * extends android.support.v4.app.Fragment {
 public void setUserVisibleHint(boolean);
 public void onHiddenChanged(boolean);
 public void onResume();
 public void onPause();
}

# DataBinding
-dontwarn android.databinding.**
-keep class android.databinding.** { *; }
-keep class com.seekting.killer.databinding.** {
    <fields>;
    <methods>;
}
# for market sdk
-dontwarn android.graphics.**

-keep class oauth.signpost.** { *; }
-keep class com.google.** { *; }


-keep class micloud.*** { *; }
-keep class cn.kuaipan.*** { *; }

-keep class * extends java.lang.Exception
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**


#androidx

-keep class com.google.android.material.** {*;}
-keep class androidx.** {*;}
-keep public class * extends androidx.**
-keep interface androidx.** {*;}
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-dontwarn androidx.**
