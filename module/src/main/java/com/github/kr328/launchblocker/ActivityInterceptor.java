package com.github.kr328.launchblocker;

import android.app.ActivityThread;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.ResolveInfo;
import android.os.RemoteException;
import android.util.Log;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class ActivityInterceptor {
    private static final Set<String> whitelist = new HashSet<>(Arrays.asList("android", "com.android.systemui", "com.android.shell"));
    private static final IPackageManager packageManager = Compat.getPackageManager();

    static boolean interceptActivity(ActivityRequest request) throws RemoteException {
        String callingPackage = request.getCallingPackage();
        Intent intent = request.getIntent();

        if (whitelist.contains(callingPackage))
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
                    request::runUpstream,
                    request::runCancel);
        } catch (Exception e) {
            Log.w(Constants.TAG, "Query source/target failure", e);
        }

        return true;
    }
}
