package com.alibaba.sdk.android.oss.common.auth;

public class OSSPlainTextAKSKCredentialProvider extends OSSCredentialProvider {
    private String accessKeyId;
    private String accessKeySecret;

    public OSSPlainTextAKSKCredentialProvider(String accessKeyId2, String accessKeySecret2) {
        this.accessKeyId = accessKeyId2.trim();
        this.accessKeySecret = accessKeySecret2.trim();
    }

    public String getAccessKeyId() {
        return this.accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId2) {
        this.accessKeyId = accessKeyId2;
    }

    public String getAccessKeySecret() {
        return this.accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret2) {
        this.accessKeySecret = accessKeySecret2;
    }
}
