package android.app;

public class ActivityThread {
    private ContextImpl mSystemContext;
    private ContextImpl mSystemUiContext;

    public static ActivityThread currentActivityThread() {
        throw new IllegalArgumentException("Stub!");
    }

    public ContextImpl getSystemContext() {
        throw new IllegalArgumentException("Stub!");
    }

    public ContextImpl getSystemUiContext() {
        throw new IllegalArgumentException("Stub!");
    }

    public Application getApplication() {
        throw new IllegalArgumentException("Stub!");
    }
}
