package android.app;

import android.os.IBinder;
import android.os.IInterface;

import java.util.List;

public interface IApplicationThread extends IInterface {
    void scheduleSendResult(IBinder token, List<ResultInfo> results);
}
