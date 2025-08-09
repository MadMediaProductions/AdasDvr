package com.alibaba.sdk.android.oss.common.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public class HttpUtil {
    static final /* synthetic */ boolean $assertionsDisabled = (!HttpUtil.class.desiredAssertionStatus());
    private static final String ISO_8859_1_CHARSET = "iso-8859-1";
    private static final String JAVA_CHARSET = "utf-8";

    public static String urlEncode(String value, String encoding) {
        if (value == null) {
            return "";
        }
        try {
            return URLEncoder.encode(value, encoding).replace("+", "%20").replace("*", "%2A").replace("%7E", "~").replace("%2F", "/");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("failed to encode url!", e);
        }
    }

    public static String paramToQueryString(Map<String, String> params, String charset) throws UnsupportedEncodingException {
        if (params == null || params.size() == 0) {
            return null;
        }
        StringBuilder paramString = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> p : params.entrySet()) {
            String key = p.getKey();
            String val = p.getValue();
            if (!first) {
                paramString.append("&");
            }
            paramString.append(key);
            if (val != null) {
                paramString.append("=").append(urlEncode(val, charset));
            }
            first = false;
        }
        return paramString.toString();
    }

    public static void convertHeaderCharsetFromIso88591(Map<String, String> headers) {
        convertHeaderCharset(headers, ISO_8859_1_CHARSET, "utf-8");
    }

    public static void convertHeaderCharsetToIso88591(Map<String, String> headers) {
        convertHeaderCharset(headers, "utf-8", ISO_8859_1_CHARSET);
    }

    private static void convertHeaderCharset(Map<String, String> headers, String fromCharset, String toCharset) {
        if ($assertionsDisabled || headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                if (header.getValue() != null) {
                    try {
                        header.setValue(new String(header.getValue().getBytes(fromCharset), toCharset));
                    } catch (UnsupportedEncodingException e) {
                        throw new AssertionError("Invalid charset name.");
                    }
                }
            }
            return;
        }
        throw new AssertionError();
    }
}
