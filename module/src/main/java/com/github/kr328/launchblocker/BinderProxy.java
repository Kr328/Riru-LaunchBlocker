package com.github.kr328.launchblocker;

import android.os.Binder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

import java.io.FileDescriptor;
import java.util.Set;

class BinderProxy extends Binder {
    private final Set<Integer> transactCodes;
    private final Binder handler;
    private final Binder fallback;

    public BinderProxy(Set<Integer> transactCodes, Binder handler, Binder fallback) {
        this.transactCodes = transactCodes;
        this.handler = handler;
        this.fallback = fallback;
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        if (transactCodes.contains(code))
            return handler.transact(code, data, reply, flags);

        return fallback.transact(code, data, reply, flags);
    }

    @Override
    public void attachInterface(IInterface owner, String descriptor) {
        fallback.attachInterface(owner, descriptor);
    }

    @Override
    public String getInterfaceDescriptor() {
        return fallback.getInterfaceDescriptor();
    }

    @Override
    public boolean pingBinder() {
        return fallback.pingBinder();
    }

    @Override
    public boolean isBinderAlive() {
        return fallback.isBinderAlive();
    }

    @Override
    public IInterface queryLocalInterface(String descriptor) {
        return fallback.queryLocalInterface(descriptor);
    }

    @Override
    public void dump(FileDescriptor fd, String[] args) {
        fallback.dump(fd, args);
    }

    @Override
    public void dumpAsync(FileDescriptor fd, String[] args) {
        fallback.dumpAsync(fd, args);
    }

    @Override
    public void linkToDeath(DeathRecipient recipient, int flags) {
        fallback.linkToDeath(recipient, flags);
    }

    @Override
    public boolean unlinkToDeath(DeathRecipient recipient, int flags) {
        return fallback.unlinkToDeath(recipient, flags);
    }
}
