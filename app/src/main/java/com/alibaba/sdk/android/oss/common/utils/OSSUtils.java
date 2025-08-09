package com.alibaba.sdk.android.oss.common.utils;

import android.util.Base64;
import android.util.Pair;
import com.alibaba.sdk.android.oss.common.OSSConstants;
import com.alibaba.sdk.android.oss.common.OSSHeaders;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.RequestParameters;
import com.alibaba.sdk.android.oss.common.auth.HmacSHA1Signature;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSCustomSignerCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.RequestMessage;
import com.alibaba.sdk.android.oss.model.CopyObjectRequest;
import com.alibaba.sdk.android.oss.model.CreateBucketRequest;
import com.alibaba.sdk.android.oss.model.DeleteBucketRequest;
import com.alibaba.sdk.android.oss.model.GetBucketACLRequest;
import com.alibaba.sdk.android.oss.model.ListObjectsRequest;
import com.alibaba.sdk.android.oss.model.OSSRequest;
import com.alibaba.sdk.android.oss.model.ObjectMetadata;
import com.alibaba.sdk.android.oss.model.PartETag;
import com.fvision.camera.utils.DoCmdUtil;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;

public class OSSUtils {
    private static final List<String> SIGNED_PARAMTERS = Arrays.asList(new String[]{RequestParameters.SUBRESOURCE_ACL, RequestParameters.SUBRESOURCE_UPLOADS, RequestParameters.SUBRESOURCE_LOCATION, RequestParameters.SUBRESOURCE_CORS, RequestParameters.SUBRESOURCE_LOGGING, RequestParameters.SUBRESOURCE_WEBSITE, RequestParameters.SUBRESOURCE_REFERER, RequestParameters.SUBRESOURCE_LIFECYCLE, RequestParameters.SUBRESOURCE_DELETE, RequestParameters.SUBRESOURCE_APPEND, RequestParameters.UPLOAD_ID, RequestParameters.PART_NUMBER, RequestParameters.SECURITY_TOKEN, RequestParameters.POSITION, RequestParameters.RESPONSE_HEADER_CACHE_CONTROL, RequestParameters.RESPONSE_HEADER_CONTENT_DISPOSITION, RequestParameters.RESPONSE_HEADER_CONTENT_ENCODING, RequestParameters.RESPONSE_HEADER_CONTENT_LANGUAGE, RequestParameters.RESPONSE_HEADER_CONTENT_TYPE, RequestParameters.RESPONSE_HEADER_EXPIRES});

    public static void populateRequestMetadata(Map<String, String> headers, ObjectMetadata metadata) {
        if (metadata != null) {
            Map<String, Object> rawMetadata = metadata.getRawMetadata();
            if (rawMetadata != null) {
                for (Map.Entry<String, Object> entry : rawMetadata.entrySet()) {
                    headers.put(entry.getKey(), entry.getValue().toString());
                }
            }
            Map<String, String> userMetadata = metadata.getUserMetadata();
            if (userMetadata != null) {
                for (Map.Entry<String, String> entry2 : userMetadata.entrySet()) {
                    String key = entry2.getKey();
                    String value = entry2.getValue();
                    if (key != null) {
                        key = key.trim();
                    }
                    if (value != null) {
                        value = value.trim();
                    }
                    headers.put(key, value);
                }
            }
        }
    }

    public static void populateListObjectsRequestParameters(ListObjectsRequest listObjectsRequest, Map<String, String> params) {
        if (listObjectsRequest.getPrefix() != null) {
            params.put(RequestParameters.PREFIX, listObjectsRequest.getPrefix());
        }
        if (listObjectsRequest.getMarker() != null) {
            params.put(RequestParameters.MARKER, listObjectsRequest.getMarker());
        }
        if (listObjectsRequest.getDelimiter() != null) {
            params.put(RequestParameters.DELIMITER, listObjectsRequest.getDelimiter());
        }
        if (listObjectsRequest.getMaxKeys() != null) {
            params.put(RequestParameters.MAX_KEYS, Integer.toString(listObjectsRequest.getMaxKeys().intValue()));
        }
        if (listObjectsRequest.getEncodingType() != null) {
            params.put(RequestParameters.ENCODING_TYPE, listObjectsRequest.getEncodingType());
        }
    }

    private enum MetadataDirective {
        COPY("COPY"),
        REPLACE("REPLACE");
        
        private final String directiveAsString;

        private MetadataDirective(String directiveAsString2) {
            this.directiveAsString = directiveAsString2;
        }

        public String toString() {
            return this.directiveAsString;
        }
    }

    public static void populateCopyObjectHeaders(CopyObjectRequest copyObjectRequest, Map<String, String> headers) {
        headers.put(OSSHeaders.COPY_OBJECT_SOURCE, "/" + copyObjectRequest.getSourceBucketName() + "/" + HttpUtil.urlEncode(copyObjectRequest.getSourceKey(), "utf-8"));
        addDateHeader(headers, OSSHeaders.COPY_OBJECT_SOURCE_IF_MODIFIED_SINCE, copyObjectRequest.getModifiedSinceConstraint());
        addDateHeader(headers, OSSHeaders.COPY_OBJECT_SOURCE_IF_UNMODIFIED_SINCE, copyObjectRequest.getUnmodifiedSinceConstraint());
        addStringListHeader(headers, OSSHeaders.COPY_OBJECT_SOURCE_IF_MATCH, copyObjectRequest.getMatchingETagConstraints());
        addStringListHeader(headers, OSSHeaders.COPY_OBJECT_SOURCE_IF_NONE_MATCH, copyObjectRequest.getNonmatchingEtagConstraints());
        addHeader(headers, OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION, copyObjectRequest.getServerSideEncryption());
        ObjectMetadata newObjectMetadata = copyObjectRequest.getNewObjectMetadata();
        if (newObjectMetadata != null) {
            headers.put(OSSHeaders.COPY_OBJECT_METADATA_DIRECTIVE, MetadataDirective.REPLACE.toString());
            populateRequestMetadata(headers, newObjectMetadata);
        }
        removeHeader(headers, HttpHeaders.CONTENT_LENGTH);
    }

    public static String buildXMLFromPartEtagList(List<PartETag> partETagList) {
        StringBuilder builder = new StringBuilder();
        builder.append("<CompleteMultipartUpload>\n");
        for (PartETag partETag : partETagList) {
            builder.append("<Part>\n");
            builder.append("<PartNumber>" + partETag.getPartNumber() + "</PartNumber>\n");
            builder.append("<ETag>" + partETag.getETag() + "</ETag>\n");
            builder.append("</Part>\n");
        }
        builder.append("</CompleteMultipartUpload>\n");
        return builder.toString();
    }

    public static void addHeader(Map<String, String> headers, String header, String value) {
        if (value != null) {
            headers.put(header, value);
        }
    }

    public static void addDateHeader(Map<String, String> headers, String header, Date value) {
        if (value != null) {
            headers.put(header, DateUtil.formatRfc822Date(value));
        }
    }

    public static void addStringListHeader(Map<String, String> headers, String header, List<String> values) {
        if (values != null && !values.isEmpty()) {
            headers.put(header, join(values));
        }
    }

    public static void removeHeader(Map<String, String> headers, String header) {
        if (header != null && headers.containsKey(header)) {
            headers.remove(header);
        }
    }

    public static String join(List<String> strings) {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (String s : strings) {
            if (!first) {
                result.append(", ");
            }
            result.append(s);
            first = false;
        }
        return result.toString();
    }

    public static boolean isEmptyString(String str) {
        return str == null || str.length() == 0;
    }

    public static String buildCanonicalizedResource(String bucketName, String objectKey, Map<String, String> parameters) {
        String resourcePath;
        if (bucketName == null && objectKey == null) {
            resourcePath = "/";
        } else if (objectKey == null) {
            resourcePath = "/" + bucketName + "/";
        } else {
            resourcePath = "/" + bucketName + "/" + objectKey;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(resourcePath);
        if (parameters != null) {
            String[] parameterNames = (String[]) parameters.keySet().toArray(new String[parameters.size()]);
            Arrays.sort(parameterNames);
            char separater = '?';
            for (String paramName : parameterNames) {
                if (SIGNED_PARAMTERS.contains(paramName)) {
                    builder.append(separater);
                    builder.append(paramName);
                    String paramValue = parameters.get(paramName);
                    if (!isEmptyString(paramValue)) {
                        builder.append("=").append(paramValue);
                    }
                    separater = '&';
                }
            }
        }
        return builder.toString();
    }

    public static String paramToQueryString(Map<String, String> params, String charset) {
        if (params == null || params.isEmpty()) {
            return null;
        }
        StringBuilder paramString = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> p : params.entrySet()) {
            String key = p.getKey();
            String value = p.getValue();
            if (!first) {
                paramString.append("&");
            }
            paramString.append(HttpUtil.urlEncode(key, charset));
            if (!isEmptyString(value)) {
                paramString.append("=").append(HttpUtil.urlEncode(value, charset));
            }
            first = false;
        }
        return paramString.toString();
    }

    public static String populateMapToBase64JsonString(Map<String, String> map) {
        return Base64.encodeToString(new JSONObject(map).toString().getBytes(), 2);
    }

    public static String sign(String accessKey, String screctKey, String content) {
        try {
            return "OSS " + accessKey + ":" + new HmacSHA1Signature().computeSignature(screctKey, content).trim();
        } catch (Exception e) {
            throw new IllegalStateException("Compute signature failed!", e);
        }
    }

    public static boolean isCname(String host) {
        for (String suffix : OSSConstants.DEFAULT_CNAME_EXCLUDE_LIST) {
            if (host.toLowerCase().endsWith(suffix)) {
                return false;
            }
        }
        return true;
    }

    public static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.length() == 0;
    }

    public static boolean validateBucketName(String bucketName) {
        if (bucketName == null) {
            return false;
        }
        return bucketName.matches("^[a-z0-9][a-z0-9_\\-]{2,62}$");
    }

    public static void ensureBucketNameValid(String bucketName) {
        if (!validateBucketName(bucketName)) {
            throw new IllegalArgumentException("The bucket name is invalid. \nA bucket name must: \n1) be comprised of lower-case characters, numbers or dash(-); \n2) start with lower case or numbers; \n3) be between 3-63 characters long. ");
        }
    }

    public static boolean validateObjectKey(String objectKey) {
        if (objectKey == null || objectKey.length() <= 0 || objectKey.length() > 1023) {
            return false;
        }
        try {
            byte[] bytes = objectKey.getBytes("utf-8");
            char[] keyChars = objectKey.toCharArray();
            char beginKeyChar = keyChars[0];
            if (beginKeyChar == '/' || beginKeyChar == '\\') {
                return false;
            }
            for (char keyChar : keyChars) {
                if (keyChar != 9 && keyChar < ' ') {
                    return false;
                }
            }
            return true;
        } catch (UnsupportedEncodingException e) {
            return false;
        }
    }

    public static void ensureObjectKeyValid(String objectKey) {
        if (!validateObjectKey(objectKey)) {
            throw new IllegalArgumentException("The object key is invalid. \nAn object name should be: \n1) between 1 - 1023 bytes long when encoded as UTF-8 \n2) cannot contain LF or CR or unsupported chars in XML1.0, \n3) cannot begin with \"/\" or \"\\\".");
        }
    }

    public static boolean doesRequestNeedObjectKey(OSSRequest request) {
        if ((request instanceof ListObjectsRequest) || (request instanceof CreateBucketRequest) || (request instanceof DeleteBucketRequest) || (request instanceof GetBucketACLRequest)) {
            return false;
        }
        return true;
    }

    public static void ensureRequestValid(OSSRequest request, RequestMessage message) {
        ensureBucketNameValid(message.getBucketName());
        if (doesRequestNeedObjectKey(request)) {
            ensureObjectKeyValid(message.getObjectKey());
        }
        if (request instanceof CopyObjectRequest) {
            ensureObjectKeyValid(((CopyObjectRequest) request).getDestinationKey());
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:8:0x001e, code lost:
        r0 = r2.getMimeTypeFromExtension(r7.substring(r7.lastIndexOf(46) + 1));
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static String determineContentType(String r5, String r6, String r7) {
        /*
            r4 = 46
            if (r5 == 0) goto L_0x0006
            r0 = r5
        L_0x0005:
            return r0
        L_0x0006:
            android.webkit.MimeTypeMap r2 = android.webkit.MimeTypeMap.getSingleton()
            if (r6 == 0) goto L_0x001c
            int r3 = r6.lastIndexOf(r4)
            int r3 = r3 + 1
            java.lang.String r1 = r6.substring(r3)
            java.lang.String r0 = r2.getMimeTypeFromExtension(r1)
            if (r0 != 0) goto L_0x0005
        L_0x001c:
            if (r7 == 0) goto L_0x002e
            int r3 = r7.lastIndexOf(r4)
            int r3 = r3 + 1
            java.lang.String r1 = r7.substring(r3)
            java.lang.String r0 = r2.getMimeTypeFromExtension(r1)
            if (r0 != 0) goto L_0x0005
        L_0x002e:
            java.lang.String r0 = "application/octet-stream"
            goto L_0x0005
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.sdk.android.oss.common.utils.OSSUtils.determineContentType(java.lang.String, java.lang.String, java.lang.String):java.lang.String");
    }

    public static void signRequest(RequestMessage message) throws IOException {
        if (message.isAuthorizationRequired()) {
            if (message.getCredentialProvider() == null) {
                throw new IllegalStateException("当前CredentialProvider为空！！！\n1. 请检查您是否在初始化OSSService时设置CredentialProvider;\n2. 如果您bucket为公共权限，请确认获取到Bucket后已经调用Bucket中接口声明ACL;");
            }
            OSSCredentialProvider credentialProvider = message.getCredentialProvider();
            OSSFederationToken federationToken = null;
            if (credentialProvider instanceof OSSFederationCredentialProvider) {
                federationToken = ((OSSFederationCredentialProvider) credentialProvider).getValidFederationToken();
                if (federationToken == null) {
                    OSSLog.logE("Can't get a federation token");
                    throw new IOException("Can't get a federation token");
                }
                message.getHeaders().put(OSSHeaders.OSS_SECURITY_TOKEN, federationToken.getSecurityToken());
            } else if (credentialProvider instanceof OSSStsTokenCredentialProvider) {
                federationToken = ((OSSStsTokenCredentialProvider) credentialProvider).getFederationToken();
                message.getHeaders().put(OSSHeaders.OSS_SECURITY_TOKEN, federationToken.getSecurityToken());
            }
            String method = message.getMethod().toString();
            String contentMD5 = message.getHeaders().get(HttpHeaders.CONTENT_MD5);
            if (contentMD5 == null) {
                contentMD5 = "";
            }
            String contentType = message.getHeaders().get("Content-Type");
            if (contentType == null) {
                contentType = "";
            }
            String dateString = message.getHeaders().get(HttpHeaders.DATE);
            List<Pair<String, String>> list = new ArrayList<>();
            for (String key : message.getHeaders().keySet()) {
                if (key.toLowerCase().startsWith(OSSHeaders.OSS_PREFIX)) {
                    list.add(new Pair(key.toLowerCase(), message.getHeaders().get(key)));
                }
            }
            Collections.sort(list, new Comparator<Pair<String, String>>() {
                public int compare(Pair<String, String> lhs, Pair<String, String> rhs) {
                    return ((String) lhs.first).compareTo((String) rhs.first);
                }
            });
            StringBuilder sb = new StringBuilder();
            Pair<String, String> previous = null;
            for (Pair<String, String> curr : list) {
                if (previous == null) {
                    sb.append(((String) curr.first) + ":" + ((String) curr.second));
                } else if (((String) previous.first).equals(curr.first)) {
                    sb.append("," + ((String) curr.second));
                } else {
                    sb.append(DoCmdUtil.COMMAND_LINE_END + ((String) curr.first) + ":" + ((String) curr.second));
                }
                previous = curr;
            }
            String canonicalizedHeader = sb.toString();
            if (!isEmptyString(canonicalizedHeader)) {
                canonicalizedHeader = canonicalizedHeader.trim() + DoCmdUtil.COMMAND_LINE_END;
            }
            String contentToSign = String.format("%s\n%s\n%s\n%s\n%s%s", new Object[]{method, contentMD5, contentType, dateString, canonicalizedHeader, buildCanonicalizedResource(message.getBucketName(), message.getObjectKey(), message.getParameters())});
            String signature = "---initValue---";
            if ((credentialProvider instanceof OSSFederationCredentialProvider) || (credentialProvider instanceof OSSStsTokenCredentialProvider)) {
                signature = sign(federationToken.getTempAK(), federationToken.getTempSK(), contentToSign);
            } else if (credentialProvider instanceof OSSPlainTextAKSKCredentialProvider) {
                signature = sign(((OSSPlainTextAKSKCredentialProvider) credentialProvider).getAccessKeyId(), ((OSSPlainTextAKSKCredentialProvider) credentialProvider).getAccessKeySecret(), contentToSign);
            } else if (credentialProvider instanceof OSSCustomSignerCredentialProvider) {
                signature = ((OSSCustomSignerCredentialProvider) credentialProvider).signContent(contentToSign);
            }
            OSSLog.logD("signed content: " + contentToSign.replaceAll(DoCmdUtil.COMMAND_LINE_END, "@") + "   ---------   signature: " + signature);
            message.getHeaders().put(HttpHeaders.AUTHORIZATION, signature);
        }
    }
}
