package com.github.kr328.launchblocker;

import android.content.Intent;
import android.os.Binder;
import android.util.Log;

final class ActivityRequest {
    private final long identify = Binder.clearCallingIdentity();
    private final String callingPackage;
    private final Intent intent;
    private final String resolvedType;
    private final int userId;
    private final BinderCaller upstream;
    private final BinderCaller cancel;

    private ActivityRequest(String callingPackage, Intent intent, String resolvedType, int userId, BinderCaller upstream, BinderCaller cancel) {
        Binder.restoreCallingIdentity(identify);

        this.callingPackage = callingPackage;
        this.intent = intent;
        this.resolvedType = resolvedType;
        this.userId = userId;
        this.upstream = upstream;
        this.cancel = cancel;
    }

    static ActivityRequest snapshot(String callingPackage, Intent intent, String resolvedType, int userId, BinderCaller upstream, BinderCaller cancel) {
        return new ActivityRequest(callingPackage, intent, resolvedType, userId, upstream, cancel);
    }

    String getCallingPackage() {
        return callingPackage;
    }

    Intent getIntent() {
        return intent;
    }

    String getResolvedType() {
        return resolvedType;
    }

    int getUserId() {
        return userId;
    }

    void runUpstream() {
        new Thread(() -> {
            long original = Binder.clearCallingIdentity();
            Binder.restoreCallingIdentity(identify);

            try {
                upstream.invoke();
            } catch (Exception e) {
                Log.w(Constants.TAG, "Replay request failure", e);
            }

            Binder.restoreCallingIdentity(original);
        }).start();
    }

    void runCancel() {
        new Thread(() -> {
            long original = Binder.clearCallingIdentity();
            Binder.restoreCallingIdentity(identify);

            try {
                cancel.invoke();
            } catch (Exception e) {
                Log.w(Constants.TAG, "Return cancel failure", e);
            }

            Binder.restoreCallingIdentity(original);
        }).start();
    }
}
