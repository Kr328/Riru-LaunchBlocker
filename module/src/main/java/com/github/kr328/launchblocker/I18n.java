package com.github.kr328.launchblocker;

import android.os.SystemProperties;

final class I18n {
    static final String TEXT_DIALOG_TITLE = "launch_application";
    static final String TEXT_DIALOG_CONTENT = "launch_application_content";
    static final String TEXT_DIALOG_ALLOW = "accept";
    static final String TEXT_DIALOG_DENY = "deny";

    private static final Zh zh = new Zh();
    private static final Default def = new Default();

    static TextProvider getCurrent() {
        switch (SystemProperties.get("persist.sys.locale", "").toLowerCase()) {
            case "zh-cn":
            case "zh":
            case "zh-tw":
            case "zh-hk":
                return zh;
            default:
                return def;
        }
    }

    interface TextProvider {
        CharSequence getText(String resId);
    }

    private static class Default implements TextProvider {
        @Override
        public CharSequence getText(String resId) {
            switch (resId) {
                case TEXT_DIALOG_TITLE:
                    return "Launch Application";
                case TEXT_DIALOG_CONTENT:
                    return "<strong>%s</strong> launching <strong>%s</strong>";
                case TEXT_DIALOG_ALLOW:
                    return "Allow";
                case TEXT_DIALOG_DENY:
                    return "Deny";
            }

            return resId;
        }
    }

    private static class Zh implements TextProvider {
        @Override
        public CharSequence getText(String resId) {
            switch (resId) {
                case TEXT_DIALOG_TITLE:
                    return "\u542f\u52a8\u5e94\u7528";
                case TEXT_DIALOG_CONTENT:
                    return "<strong>%s</strong> \u5c1d\u8bd5\u542f\u52a8 <strong>%s</strong>";
                case TEXT_DIALOG_ALLOW:
                    return "\u5141\u8bb8";
                case TEXT_DIALOG_DENY:
                    return "\u62d2\u7edd";
            }

            return resId;
        }
    }
}
