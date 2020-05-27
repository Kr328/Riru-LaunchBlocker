package com.github.kr328.launchblocker;

import android.app.ActivityThread;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.WindowManager;

final class DialogUtils {
    private static Handler handler;

    public static synchronized void popupRequest(CharSequence source, CharSequence target, Runnable callback, Runnable fallback) {
        if (handler == null)
            initialize();

        try {
            handler.post(() -> {
                try {
                    I18n.TextProvider textProvider = I18n.getCurrent();

                    Dialog dialog = new AlertDialog.Builder(ActivityThread.currentActivityThread().getSystemUiContext())
                            .setTitle(textProvider.getText(I18n.TEXT_DIALOG_TITLE))
                            .setMessage(Html.fromHtml(String.format(textProvider.getText(I18n.TEXT_DIALOG_CONTENT).toString(), source, target), Html.FROM_HTML_MODE_COMPACT))
                            .setPositiveButton(textProvider.getText(I18n.TEXT_DIALOG_ALLOW), (d, w) -> callback.run())
                            .setNegativeButton(textProvider.getText(I18n.TEXT_DIALOG_DENY), (d, w) -> fallback.run())
                            .setOnCancelListener((d) -> fallback.run())
                            .create();

                    dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG);

                    dialog.show();
                } catch (Exception e) {
                    Log.w(Constants.TAG, "Create dialog failure", e);
                }
            });
        } catch (Exception e) {
            Log.w(Constants.TAG, "Post failure", e);
        }
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
