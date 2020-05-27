package com.github.kr328.launchblocker;

import android.app.Activity;
import android.app.IActivityTaskManager;
import android.app.IApplicationThread;
import android.app.ProfilerInfo;
import android.app.ResultInfo;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

class TaskProxy extends IActivityTaskManager.Stub {
    private final IActivityTaskManager upstream;

    TaskProxy(IActivityTaskManager upstream) {
        this.upstream = upstream;
    }

    @Override
    public int startActivity(IApplicationThread caller, String callingPackage, Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode, int flags, ProfilerInfo profilerInfo, Bundle options) throws RemoteException {
        BinderCaller upstream = () ->
                this.upstream.startActivity(caller, callingPackage, intent, resolvedType, resultTo, resultWho, requestCode, flags, profilerInfo, options);
        BinderCaller cancel = () ->
                Compat.scheduleSendResult(caller, resultTo, new ResultInfo(resultWho, requestCode, Activity.RESULT_OK, new Intent()));

        ActivityRequest request = ActivityRequest.snapshot(callingPackage, intent, resolvedType, 0, upstream, cancel);

        if (ActivityInterceptor.interceptActivity(request))
            return 0;

        return upstream.invoke();
    }

    @Override
    public int startActivityAsUser(IApplicationThread caller, String callingPackage, Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode, int flags, ProfilerInfo profilerInfo, Bundle options, int userId) throws RemoteException {
        BinderCaller upstream = () ->
                this.upstream.startActivityAsUser(caller, callingPackage, intent, resolvedType, resultTo, resultWho, requestCode, flags, profilerInfo, options, userId);
        BinderCaller cancel = () ->
                Compat.scheduleSendResult(caller, resultTo, new ResultInfo(resultWho, requestCode, Activity.RESULT_OK, new Intent()));

        ActivityRequest request = ActivityRequest.snapshot(callingPackage, intent, resolvedType, userId, upstream, cancel);

        if (ActivityInterceptor.interceptActivity(request))
            return 0;

        return upstream.invoke();
    }
}
