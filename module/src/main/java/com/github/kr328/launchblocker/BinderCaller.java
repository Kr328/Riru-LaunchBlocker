package com.github.kr328.launchblocker;

import android.os.RemoteException;

interface BinderCaller {
    int invoke() throws RemoteException;
}
