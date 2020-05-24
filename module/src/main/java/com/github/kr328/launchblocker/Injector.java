package com.github.kr328.launchblocker;

import android.util.Log;

final class Injector {
    public static void inject() {
        Log.i(Constants.TAG,"Injected");

        hijack();
    }

    private static void hijack() {

    }
}
