package com.github.kr328.launchblocker;

import android.app.ActivityThread;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.ResolveInfo;
import android.os.RemoteException;
import android.util.Log;

import java.util.List;

final class ActivityInterceptor {
    private static final IPackageManager packageManager = Compat.getPackageManager();
    private static final Cache<String, Class<Object>> cache = new Cache<>();

    static boolean interceptActivity(ActivityRequest request) throws RemoteException {
        String callingPackage = request.getCallingPackage();
        Intent intent = request.getIntent();

        if (Constants.SOURCE_WHITELIST.contains(callingPackage))
            return false;
        if (intent.hasCategory(Intent.CATEGORY_LAUNCHER))
            return false;
        if (intent.getComponent() != null && callingPackage.equals(intent.getComponent().getPackageName()))
            return false;
        if (intent.getPackage() != null && callingPackage.equals(intent.getPackage()))
            return false;

        List<ResolveInfo> activities = packageManager
                .queryIntentActivities(intent, request.getResolvedType(), 0, request.getUserId())
                .getList();

        if (activities.size() != 1)
            return false;

        ResolveInfo target = activities.get(0);

        if (Constants.TARGET_WHITELIST.contains(target.activityInfo.applicationInfo.packageName))
            return false;

        String key = generateKey(callingPackage, target);

        Class<Object> o = cache.get(key);
        if (o != null) {
            cache.put(key, Object.class, System.currentTimeMillis() + Constants.DEFAULT_ACTION_EXPIRED);
            return false;
        }

        try {
            Context context = ActivityThread.currentActivityThread().getSystemContext();

            ApplicationInfo sourceInfo = context.getPackageManager().getApplicationInfo(callingPackage, 0);
            ApplicationInfo targetInfo = target.activityInfo.applicationInfo;

            if ((sourceInfo.flags & targetInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0)
                return false;

            CharSequence sourceLabel = sourceInfo.loadLabel(context.getPackageManager());
            CharSequence targetLabel = targetInfo.loadLabel(context.getPackageManager());

            DialogUtils.popupRequest(
                    sourceLabel,
                    targetLabel,
                    () -> runUpstreamAndSaveState(key, request),
                    () -> runCancelAndSaveState(key, request));

            Log.d(Constants.TAG, "Intercept " + sourceInfo.packageName + " -> " + targetInfo.packageName);
        } catch (Exception e) {
            Log.w(Constants.TAG, "Query source/target failure", e);
        }

        return true;
    }

    private static void runUpstreamAndSaveState(String key, ActivityRequest request) {
        request.runUpstream();

        cache.put(key, Object.class, System.currentTimeMillis() + Constants.DEFAULT_ACTION_EXPIRED);
    }

    private static void runCancelAndSaveState(String key, ActivityRequest request) {
        request.runCancel();

        cache.remove(key);
    }

    private static String generateKey(String callingPackage, ResolveInfo target) {
        return callingPackage + "#" + target.activityInfo.packageName + "#" + target.activityInfo.name;
    }
}
