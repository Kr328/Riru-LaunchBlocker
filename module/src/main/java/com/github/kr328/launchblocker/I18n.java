package com.github.kr328.launchblocker;

import android.os.SystemProperties;
import android.text.Html;

abstract class I18n {
    static final String TEXT_DIALOG_TITLE = "launch_application";
    static final String TEXT_DIALOG_CONTENT = "launch_application_content";
    static final String TEXT_DIALOG_ALLOW = "accept";
    static final String TEXT_DIALOG_DENY = "deny";

    private static Languages languages = new Languages();

    static I18n getCurrent() {
        switch (SystemProperties.get("persist.sys.locale", "").toLowerCase()) {
            case "zh-cn":
            case "zh":
            case "zh-tw":
            case "zh-hk":
                return languages.zh;
            default:
                return languages.def;
        }
    }

    abstract CharSequence getText(String resId, Object... formatArgs);

    private static class Languages {
        final Zh zh = new Zh();
        final Default def = new Default();

        private static class Default extends I18n {
            @Override
            public CharSequence getText(String resId, Object... args) {
                switch (resId) {
                    case TEXT_DIALOG_TITLE:
                        return "Launch Application";
                    case TEXT_DIALOG_CONTENT:
                        return Html.fromHtml(String.format("<strong>%s</strong> launching <strong>%s</strong>", args), Html.FROM_HTML_MODE_COMPACT);
                    case TEXT_DIALOG_ALLOW:
                        return "Allow";
                    case TEXT_DIALOG_DENY:
                        return "Deny";
                }

                return resId;
            }
        }

        private static class Zh extends I18n {
            @Override
            public CharSequence getText(String resId, Object... args) {
                switch (resId) {
                    case TEXT_DIALOG_TITLE:
                        return "\u542f\u52a8\u5e94\u7528";
                    case TEXT_DIALOG_CONTENT:
                        return Html.fromHtml(String.format("<strong>%s</strong> \u5c1d\u8bd5\u542f\u52a8 <strong>%s</strong>", args), Html.FROM_HTML_MODE_COMPACT);
                    case TEXT_DIALOG_ALLOW:
                        return "\u5141\u8bb8";
                    case TEXT_DIALOG_DENY:
                        return "\u62d2\u7edd";
                }

                return resId;
            }
        }
    }
}
