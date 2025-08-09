package com.alibaba.sdk.android.oss.common.utils;

public class VersionInfoUtils {
    private static String userAgent = null;
    private static String version = null;

    public static String getUserAgent() {
        if (userAgent == null) {
            userAgent = "aliyun-sdk-android/" + getVersion() + "/" + getDefaultUserAgent();
        }
        return userAgent;
    }

    public static String getVersion() {
        return "2.2.0";
    }

    public static String getDefaultUserAgent() {
        String result = System.getProperty("http.agent");
        if (OSSUtils.isEmptyString(result)) {
            result = "(" + System.getProperty("os.name") + "/" + System.getProperty("os.version") + "/" + System.getProperty("os.arch") + ";" + System.getProperty("java.version") + ")";
        }
        return result.replaceAll("[^\\p{ASCII}]", "?");
    }
}
