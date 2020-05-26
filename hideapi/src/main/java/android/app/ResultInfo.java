package android.app;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

public class ResultInfo implements Parcelable {
    public final int mRequestCode;

    public static final Creator<ResultInfo> CREATOR = new Creator<ResultInfo>() {
        @Override
        public ResultInfo createFromParcel(Parcel in) {
            throw new IllegalArgumentException("Stub!");
        }

        @Override
        public ResultInfo[] newArray(int size) {
            throw new IllegalArgumentException("Stub!");
        }
    };

    public ResultInfo(String resultWho, int requestCode, int resultCode, Intent data) {
        throw new IllegalArgumentException("Stub!");
    }

    protected ResultInfo(Parcel in) {
        throw new IllegalArgumentException("Stub!");
    }

    @Override
    public int describeContents() {
        throw new IllegalArgumentException("Stub!");
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        throw new IllegalArgumentException("Stub!");
    }
}
