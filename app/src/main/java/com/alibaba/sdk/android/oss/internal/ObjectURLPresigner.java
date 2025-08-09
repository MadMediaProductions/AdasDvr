package com.alibaba.sdk.android.oss.internal;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSCustomSignerCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.common.utils.DateUtil;
import com.alibaba.sdk.android.oss.common.utils.HttpUtil;
import com.alibaba.sdk.android.oss.common.utils.OSSUtils;
import com.fvision.camera.utils.DoCmdUtil;
import java.net.URI;

public class ObjectURLPresigner {
    private OSSCredentialProvider credentialProvider;
    private URI endpoint;

    public ObjectURLPresigner(URI endpoint2, OSSCredentialProvider credentialProvider2) {
        this.endpoint = endpoint2;
        this.credentialProvider = credentialProvider2;
    }

    public String presignConstrainedURL(String bucketName, String objectKey, long expiredTimeInSeconds) throws ClientException {
        String signature;
        String resource = "/" + bucketName + "/" + objectKey;
        String expires = String.valueOf((DateUtil.getFixedSkewedTimeMillis() / 1000) + expiredTimeInSeconds);
        OSSFederationToken token = null;
        if (this.credentialProvider instanceof OSSFederationCredentialProvider) {
            token = ((OSSFederationCredentialProvider) this.credentialProvider).getValidFederationToken();
            if (token == null) {
                throw new ClientException("Can not get a federation token!");
            }
            resource = resource + "?security-token=" + token.getSecurityToken();
        } else if (this.credentialProvider instanceof OSSStsTokenCredentialProvider) {
            token = ((OSSStsTokenCredentialProvider) this.credentialProvider).getFederationToken();
            resource = resource + "?security-token=" + token.getSecurityToken();
        }
        String contentToSign = "GET\n\n\n" + expires + DoCmdUtil.COMMAND_LINE_END + resource;
        if ((this.credentialProvider instanceof OSSFederationCredentialProvider) || (this.credentialProvider instanceof OSSStsTokenCredentialProvider)) {
            signature = OSSUtils.sign(token.getTempAK(), token.getTempSK(), contentToSign);
        } else if (this.credentialProvider instanceof OSSPlainTextAKSKCredentialProvider) {
            signature = OSSUtils.sign(((OSSPlainTextAKSKCredentialProvider) this.credentialProvider).getAccessKeyId(), ((OSSPlainTextAKSKCredentialProvider) this.credentialProvider).getAccessKeySecret(), contentToSign);
        } else if (this.credentialProvider instanceof OSSCustomSignerCredentialProvider) {
            signature = ((OSSCustomSignerCredentialProvider) this.credentialProvider).signContent(contentToSign);
        } else {
            throw new ClientException("Unknown credentialProvider!");
        }
        String accessKey = signature.split(":")[0].substring(4);
        String signature2 = signature.split(":")[1];
        String host = this.endpoint.getHost();
        if (!OSSUtils.isCname(host)) {
            host = bucketName + "." + host;
        }
        String url = this.endpoint.getScheme() + "://" + host + "/" + HttpUtil.urlEncode(objectKey, "utf-8") + "?OSSAccessKeyId=" + HttpUtil.urlEncode(accessKey, "utf-8") + "&Expires=" + expires + "&Signature=" + HttpUtil.urlEncode(signature2, "utf-8");
        if ((this.credentialProvider instanceof OSSFederationCredentialProvider) || (this.credentialProvider instanceof OSSStsTokenCredentialProvider)) {
            return url + "&security-token=" + HttpUtil.urlEncode(token.getSecurityToken(), "utf-8");
        }
        return url;
    }

    public String presignPublicURL(String bucketName, String objectKey) {
        String host = this.endpoint.getHost();
        if (!OSSUtils.isCname(host)) {
            host = bucketName + "." + host;
        }
        return this.endpoint.getScheme() + "://" + host + "/" + HttpUtil.urlEncode(objectKey, "utf-8");
    }
}
