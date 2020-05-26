package android.app.servertransaction;

import android.app.IApplicationThread;
import android.os.IBinder;
import android.os.RemoteException;

public class ClientTransaction {
    public static ClientTransaction obtain(IApplicationThread client, IBinder activityToken) {
        throw new IllegalArgumentException("Stub!");
    }

    public void addCallback(ClientTransactionItem activityCallback) {
        throw new IllegalArgumentException("Stub!");
    }

    public void schedule() throws RemoteException {
        throw new IllegalArgumentException("Stub!");
    }
}