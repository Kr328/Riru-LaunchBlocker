package com.github.kr328.launchblocker;

import android.app.IApplicationThread;
import android.app.ResultInfo;
import android.app.servertransaction.ActivityResultItem;
import android.app.servertransaction.ClientTransaction;
import android.content.pm.IPackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;

import java.util.Collections;

final class Compat {
    // Compat with Riru-IFWEnhance
    static IPackageManager getPackageManager() {
        return IPackageManager.Stub.asInterface(getCommonServicesLocked());
    }

    // for getPackageManager
    private static IBinder getCommonServicesLocked() {
        return ServiceManager.getService("package");
    }

    static int scheduleSendResult(IApplicationThread caller, IBinder token, ResultInfo result) throws RemoteException {
        if (result.mRequestCode < 0)
            return 1;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            ActivityResultItem item = ActivityResultItem.obtain(Collections.singletonList(result));
            ClientTransaction transaction = ClientTransaction.obtain(caller, token);

            transaction.addCallback(item);
            transaction.schedule();
        } else {
            caller.scheduleSendResult(token, Collections.singletonList(result));
        }

        return 0;
    }
}
