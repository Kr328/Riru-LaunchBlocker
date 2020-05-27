package com.github.kr328.launchblocker;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

class Constants {
    static final String TAG = "LaunchBlocker";

    static final long DEFAULT_ACTION_EXPIRED = 60 * 1000;

    static final Set<String> SOURCE_WHITELIST = new HashSet<>(Arrays.asList("android", "com.android.systemui", "com.android.shell"));
    static final Set<String> TARGET_WHITELIST = new HashSet<>(Arrays.asList("android", "com.android.systemui", "com.android.permissioncontroller"));
}
