package com.github.kr328.launchblocker;

import android.app.ActivityThread;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;

final class DialogUtils {
    private static Handler handler;

    static synchronized void popupRequest(CharSequence source, CharSequence target, Runnable callback, Runnable fallback) {
        if (handler == null)
            initialize();

        handler.post(() -> {
            try {
                I18n i18n = I18n.getCurrent();

                Dialog dialog = new AlertDialog.Builder(ActivityThread.currentActivityThread().getSystemUiContext())
                        .setTitle(i18n.getText(I18n.TEXT_DIALOG_TITLE))
                        .setMessage(i18n.getText(I18n.TEXT_DIALOG_CONTENT, source, target))
                        .setPositiveButton(i18n.getText(I18n.TEXT_DIALOG_ALLOW), (d, w) -> callback.run())
                        .setNegativeButton(i18n.getText(I18n.TEXT_DIALOG_DENY), (d, w) -> fallback.run())
                        .setOnCancelListener((d) -> fallback.run())
                        .create();

                dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG);

                dialog.show();
            } catch (Exception e) {
                Log.w(Constants.TAG, "Create dialog failure", e);
            }
        });
    }

    private static void initialize() {
        Object sync = new Object();

        synchronized (sync) {
            new Thread(() -> {
                Looper.prepare();

                synchronized (sync) {
                    handler = new Handler();

                    sync.notifyAll();
                }

                Looper.loop();
            }).start();

            try {
                sync.wait();
            } catch (InterruptedException ignored) {
            }
        }

        Log.d(Constants.TAG, "Looper created");
    }
}
