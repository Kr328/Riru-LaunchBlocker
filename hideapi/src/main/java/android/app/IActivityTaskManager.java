package android.app;

import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IActivityTaskManager extends IInterface {
    int startActivity(IApplicationThread caller, String callingPackage,
                      Intent intent, String resolvedType, IBinder resultTo, String resultWho,
                      int requestCode, int flags, ProfilerInfo profilerInfo,
                      Bundle options) throws RemoteException;

    int startActivityAsUser(IApplicationThread caller, String callingPackage,
                            Intent intent, String resolvedType, IBinder resultTo, String resultWho,
                            int requestCode, int flags, ProfilerInfo profilerInfo,
                            Bundle options, int userId) throws RemoteException;

    abstract class Stub extends Binder implements IActivityTaskManager {
        static final int TRANSACTION_startActivity = 0;
        static final int TRANSACTION_startActivityAsUser = 0;

        public static IActivityTaskManager asInterface(IBinder binder) {
            throw new IllegalArgumentException("Stub!");
        }

        @Override
        public IBinder asBinder() {
            throw new IllegalArgumentException("Stub!");
        }
    }
}