#include <jni.h>
#include <sys/types.h>
#include <android/log.h>

#define TAG "LaunchBlocker"
#define EXPORT __attribute__((visibility("default"))) __attribute__((used))

static void load_and_invoke_inject(JNIEnv *env) {
    jclass cClassLoader = (*env)->FindClass(env, "java/lang/ClassLoader");
    jclass cDexClassLoader = (*env)->FindClass(env, "dalvik/system/DexClassLoader");

    jmethodID mGetSystemClassLoader =
            (*env)->GetStaticMethodID(env, cClassLoader, "getSystemClassLoader", "()Ljava/lang/ClassLoader;");
    jmethodID mDexClassLoaderInit =
            (*env)->GetMethodID(env, cDexClassLoader, "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/ClassLoader;)V");
    jmethodID mFindClass =
            (*env)->GetMethodID(env, cClassLoader, "loadClass", "(Ljava/lang/String;)Ljava/lang/Class;");

    jstring sDexPath = (*env)->NewStringUTF(env, "/system/framework/boot-launch-blocker.jar");
    jstring sOdexPath = (*env)->NewStringUTF(env, "/data/dalvik-cache");
    jstring sInjectorClass = (*env)->NewStringUTF(env, "com/github/kr328/launchblocker/Injector");

    jobject oSystemClassLoader = (*env)->CallStaticObjectMethod(env, cClassLoader,
                                                                mGetSystemClassLoader);
    jobject oClassLoader = (*env)->NewObject(env, cDexClassLoader, mDexClassLoaderInit, sDexPath,
                                             sOdexPath, NULL, oSystemClassLoader);

    jclass cInjector = (*env)->CallObjectMethod(env, oClassLoader, mFindClass, sInjectorClass);
    jmethodID mInject = (*env)->GetStaticMethodID(env, cInjector, "inject", "()V");

    (*env)->CallStaticVoidMethod(env, cInjector, mInject);

    if ((*env)->ExceptionCheck(env)) {
        (*env)->ExceptionDescribe(env);
        (*env)->ExceptionClear(env);
        __android_log_print(ANDROID_LOG_ERROR, TAG, "Inject dex failure");
    }
}

EXPORT int nativeForkSystemServerPost(JNIEnv *env, jclass clazz, jint res) {
    if (res == 0) {
        load_and_invoke_inject(env);
    }

    return 0;
}
