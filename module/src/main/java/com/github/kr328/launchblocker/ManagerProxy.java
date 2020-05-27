package com.github.kr328.launchblocker;

import android.annotation.SuppressLint;
import android.os.IBinder;
import android.os.IPermissionController;
import android.os.IServiceManager;
import android.os.RemoteException;
import android.os.ServiceManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@SuppressLint("PrivateApi")
public class ManagerProxy implements IServiceManager {
    private static ManagerProxy instance;

    private IServiceManager upstream;
    private Callback callback;

    private ManagerProxy(IServiceManager upstream, Callback callback) {
        this.upstream = upstream;
        this.callback = callback;
    }

    static synchronized void install(Callback callback) throws ReflectiveOperationException {
        if (callback == null)
            return;

        if (instance == null) {
            instance = new ManagerProxy(getUpstreamIServiceManager(), new Callback());
            setDefaultServiceManager(instance);
        }

        instance.callback = callback;
    }

    private static IServiceManager getUpstreamIServiceManager() throws ReflectiveOperationException {
        Method m = ServiceManager.class.getDeclaredMethod("getIServiceManager");
        m.setAccessible(true);

        Object o = m.invoke(null);

        if (!(o instanceof IServiceManager))
            throw new NoSuchMethodException("getIServiceManager failure");

        return (IServiceManager) o;
    }

    private static void setDefaultServiceManager(IServiceManager serviceManager) throws ReflectiveOperationException {
        Field field = ServiceManager.class.getDeclaredField("sServiceManager");
        field.setAccessible(true);
        field.set(null, serviceManager);
    }

    private IServiceManager getUpstream() {
        return upstream;
    }

    // Pie
    @Override
    public IBinder getService(String name) throws RemoteException {
        return callback.getService(name, getUpstream().getService(name));
    }

    @Override
    public IBinder checkService(String name) throws RemoteException {
        return callback.checkService(name, getUpstream().checkService(name));
    }

    @Override
    public void addService(String name, IBinder service, boolean allowIsolated, int dumpFlags) throws RemoteException {
        getUpstream().addService(name, callback.addService(name, service), allowIsolated, dumpFlags);
    }

    @Override
    public String[] listServices(int dumpFlags) throws RemoteException {
        return getUpstream().listServices(dumpFlags);
    }

    @Override
    public void setPermissionController(IPermissionController controller) throws RemoteException {
        getUpstream().setPermissionController(controller);
    }

    // Oreo
    @Override
    public void addService(String name, IBinder service, boolean allowIsolated) throws RemoteException {
        getUpstream().addService(name, callback.addService(name, service), allowIsolated);
    }

    @Override
    public String[] listServices() throws RemoteException {
        return getUpstream().listServices();
    }

    @Override
    public IBinder asBinder() {
        return getUpstream().asBinder();
    }

    public static class Callback {
        public IBinder addService(String name, IBinder service) {
            return service;
        }

        public IBinder getService(String name, IBinder service) {
            return service;
        }

        public IBinder checkService(String name, IBinder service) {
            return service;
        }
    }
}
