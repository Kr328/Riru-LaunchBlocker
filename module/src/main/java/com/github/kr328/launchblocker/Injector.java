package com.github.kr328.launchblocker;

import android.app.IActivityManager;
import android.app.IActivityTaskManager;
import android.app.IApplicationThread;
import android.app.ProfilerInfo;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

final class Injector {
    public static void inject() {
        Log.i(Constants.TAG, "Injected");

        try {
            hijack();
        } catch (ReflectiveOperationException e) {
            Log.e(Constants.TAG, "Hijack failure", e);
        }
    }

    private static void hijack() throws ReflectiveOperationException {
        ManagerProxy.install(new ManagerProxy.Callback() {
            @Override
            public IBinder addService(String name, IBinder service) {
                switch (name) {
                    case "activity":
                        return new BinderProxy(queryActivityTransactCodes(), new ActivityProxy(IActivityManager.Stub.asInterface(service)), (Binder) service);
                    case "activity_task":
                        return new BinderProxy(queryTaskTransactCodes(), new TaskProxy(IActivityTaskManager.Stub.asInterface(service)), (Binder) service);
                }

                return service;
            }
        });
    }

    private static Set<Integer> queryActivityTransactCodes() {
        try {
            Field startActivity = IActivityManager.Stub.class.getDeclaredField("TRANSACTION_startActivity");
            Field startActivityAsUser = IActivityManager.Stub.class.getDeclaredField("TRANSACTION_startActivityAsUser");

            startActivity.setAccessible(true);
            startActivityAsUser.setAccessible(true);

            IActivityManager.class.getMethod("startActivity",
                    IApplicationThread.class, String.class,
                    Intent.class, String.class,
                    IBinder.class, String.class, int.class, int.class,
                    ProfilerInfo.class, Bundle.class);
            IActivityManager.class.getMethod("startActivityAsUser", IApplicationThread.class, String.class,
                    Intent.class, String.class,
                    IBinder.class, String.class, int.class, int.class,
                    ProfilerInfo.class, Bundle.class, int.class);

            Object startActivityCode = startActivity.get(null);
            Object startActivityAsUserCode = startActivityAsUser.get(null);

            if (!(startActivityCode instanceof Integer) || !(startActivityAsUserCode instanceof Integer))
                throw new NoSuchFieldException("No such transact code");

            return new TreeSet<>(Arrays.asList((int) startActivityCode, (int) startActivityAsUserCode));
        } catch (ReflectiveOperationException e) {
            Log.i(Constants.TAG, "Query transact codes failure", e);
        }

        return Collections.emptySet();
    }

    private static Set<Integer> queryTaskTransactCodes() {
        try {
            Field startActivity = IActivityTaskManager.Stub.class.getDeclaredField("TRANSACTION_startActivity");
            Field startActivityAsUser = IActivityTaskManager.Stub.class.getDeclaredField("TRANSACTION_startActivityAsUser");

            startActivity.setAccessible(true);
            startActivityAsUser.setAccessible(true);

            IActivityTaskManager.class.getMethod("startActivity",
                    IApplicationThread.class, String.class,
                    Intent.class, String.class,
                    IBinder.class, String.class, int.class, int.class,
                    ProfilerInfo.class, Bundle.class);
            IActivityTaskManager.class.getMethod("startActivityAsUser", IApplicationThread.class, String.class,
                    Intent.class, String.class,
                    IBinder.class, String.class, int.class, int.class,
                    ProfilerInfo.class, Bundle.class, int.class);

            Object startActivityCode = startActivity.get(null);
            Object startActivityAsUserCode = startActivityAsUser.get(null);

            if (!(startActivityCode instanceof Integer) || !(startActivityAsUserCode instanceof Integer))
                throw new NoSuchFieldException("No such transact code");

            return new TreeSet<>(Arrays.asList((int) startActivityCode, (int) startActivityAsUserCode));
        } catch (ReflectiveOperationException e) {
            Log.i(Constants.TAG, "Query transact codes failure", e);
        }

        return Collections.emptySet();
    }
}
