package com.alibaba.sdk.android.oss.internal;

import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.common.OSSHeaders;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.utils.DateUtil;
import com.alibaba.sdk.android.oss.common.utils.HttpHeaders;
import com.alibaba.sdk.android.oss.model.AbortMultipartUploadResult;
import com.alibaba.sdk.android.oss.model.AppendObjectResult;
import com.alibaba.sdk.android.oss.model.CompleteMultipartUploadResult;
import com.alibaba.sdk.android.oss.model.CopyObjectResult;
import com.alibaba.sdk.android.oss.model.CreateBucketResult;
import com.alibaba.sdk.android.oss.model.DeleteBucketResult;
import com.alibaba.sdk.android.oss.model.DeleteObjectResult;
import com.alibaba.sdk.android.oss.model.GetBucketACLResult;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.HeadObjectResult;
import com.alibaba.sdk.android.oss.model.InitiateMultipartUploadResult;
import com.alibaba.sdk.android.oss.model.ListObjectsResult;
import com.alibaba.sdk.android.oss.model.ListPartsResult;
import com.alibaba.sdk.android.oss.model.OSSObjectSummary;
import com.alibaba.sdk.android.oss.model.ObjectMetadata;
import com.alibaba.sdk.android.oss.model.PartSummary;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.alibaba.sdk.android.oss.model.UploadPartResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import okhttp3.Headers;
import okhttp3.Response;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public final class ResponseParsers {
    public static final DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();

    public static final class PutObjectReponseParser implements ResponseParser<PutObjectResult> {
        public PutObjectResult parse(Response response) throws IOException {
            try {
                PutObjectResult result = new PutObjectResult();
                result.setRequestId(response.header(OSSHeaders.OSS_HEADER_REQUEST_ID));
                result.setStatusCode(response.code());
                result.setResponseHeader(ResponseParsers.parseResponseHeader(response));
                result.setETag(ResponseParsers.trimQuotes(response.header(HttpHeaders.ETAG)));
                if (response.body().contentLength() > 0) {
                    result.setServerCallbackReturnBody(response.body().string());
                }
                return result;
            } finally {
                ResponseParsers.safeCloseResponse(response);
            }
        }
    }

    public static final class AppendObjectResponseParser implements ResponseParser<AppendObjectResult> {
        public AppendObjectResult parse(Response response) throws IOException {
            try {
                AppendObjectResult result = new AppendObjectResult();
                result.setRequestId(response.header(OSSHeaders.OSS_HEADER_REQUEST_ID));
                result.setStatusCode(response.code());
                result.setResponseHeader(ResponseParsers.parseResponseHeader(response));
                String nextPosition = response.header(OSSHeaders.OSS_NEXT_APPEND_POSITION);
                if (nextPosition != null) {
                    result.setNextPosition(Long.valueOf(nextPosition));
                }
                result.setObjectCRC64(response.header(OSSHeaders.OSS_HASH_CRC64_ECMA));
                return result;
            } finally {
                ResponseParsers.safeCloseResponse(response);
            }
        }
    }

    public static final class HeadObjectResponseParser implements ResponseParser<HeadObjectResult> {
        public HeadObjectResult parse(Response response) throws IOException {
            HeadObjectResult result = new HeadObjectResult();
            try {
                result.setRequestId(response.header(OSSHeaders.OSS_HEADER_REQUEST_ID));
                result.setStatusCode(response.code());
                result.setResponseHeader(ResponseParsers.parseResponseHeader(response));
                result.setMetadata(ResponseParsers.parseObjectMetadata(result.getResponseHeader()));
                return result;
            } finally {
                ResponseParsers.safeCloseResponse(response);
            }
        }
    }

    public static final class GetObjectResponseParser implements ResponseParser<GetObjectResult> {
        public GetObjectResult parse(Response response) throws IOException {
            GetObjectResult result = new GetObjectResult();
            result.setRequestId(response.header(OSSHeaders.OSS_HEADER_REQUEST_ID));
            result.setStatusCode(response.code());
            result.setResponseHeader(ResponseParsers.parseResponseHeader(response));
            result.setMetadata(ResponseParsers.parseObjectMetadata(result.getResponseHeader()));
            result.setContentLength(response.body().contentLength());
            result.setObjectContent(response.body().byteStream());
            return result;
        }
    }

    public static final class CopyObjectResponseParser implements ResponseParser<CopyObjectResult> {
        public CopyObjectResult parse(Response response) throws IOException {
            try {
                CopyObjectResult result = ResponseParsers.parseCopyObjectResponseXML(response.body().byteStream());
                result.setRequestId(response.header(OSSHeaders.OSS_HEADER_REQUEST_ID));
                result.setStatusCode(response.code());
                result.setResponseHeader(ResponseParsers.parseResponseHeader(response));
                ResponseParsers.safeCloseResponse(response);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                throw new IOException(e.getMessage(), e);
            } catch (Throwable th) {
                ResponseParsers.safeCloseResponse(response);
                throw th;
            }
        }
    }

    public static final class CreateBucketResponseParser implements ResponseParser<CreateBucketResult> {
        public CreateBucketResult parse(Response response) throws IOException {
            try {
                CreateBucketResult result = new CreateBucketResult();
                result.setRequestId(response.header(OSSHeaders.OSS_HEADER_REQUEST_ID));
                result.setStatusCode(response.code());
                result.setResponseHeader(ResponseParsers.parseResponseHeader(response));
                ResponseParsers.safeCloseResponse(response);
                return result;
            } catch (Exception e) {
                throw new IOException(e.getMessage(), e);
            } catch (Throwable th) {
                ResponseParsers.safeCloseResponse(response);
                throw th;
            }
        }
    }

    public static final class DeleteBucketResponseParser implements ResponseParser<DeleteBucketResult> {
        public DeleteBucketResult parse(Response response) throws IOException {
            try {
                DeleteBucketResult result = new DeleteBucketResult();
                result.setRequestId(response.header(OSSHeaders.OSS_HEADER_REQUEST_ID));
                result.setStatusCode(response.code());
                result.setResponseHeader(ResponseParsers.parseResponseHeader(response));
                return result;
            } finally {
                ResponseParsers.safeCloseResponse(response);
            }
        }
    }

    public static final class GetBucketACLResponseParser implements ResponseParser<GetBucketACLResult> {
        public GetBucketACLResult parse(Response response) throws IOException {
            try {
                GetBucketACLResult result = ResponseParsers.parseGetBucketACLResponse(response.body().byteStream());
                result.setRequestId(response.header(OSSHeaders.OSS_HEADER_REQUEST_ID));
                result.setStatusCode(response.code());
                result.setResponseHeader(ResponseParsers.parseResponseHeader(response));
                ResponseParsers.safeCloseResponse(response);
                return result;
            } catch (Exception e) {
                throw new IOException(e.getMessage(), e);
            } catch (Throwable th) {
                ResponseParsers.safeCloseResponse(response);
                throw th;
            }
        }
    }

    public static final class DeleteObjectResponseParser implements ResponseParser<DeleteObjectResult> {
        public DeleteObjectResult parse(Response response) throws IOException {
            DeleteObjectResult result = new DeleteObjectResult();
            try {
                result.setRequestId(response.header(OSSHeaders.OSS_HEADER_REQUEST_ID));
                result.setStatusCode(response.code());
                result.setResponseHeader(ResponseParsers.parseResponseHeader(response));
                return result;
            } finally {
                ResponseParsers.safeCloseResponse(response);
            }
        }
    }

    public static final class ListObjectsResponseParser implements ResponseParser<ListObjectsResult> {
        public ListObjectsResult parse(Response response) throws IOException {
            try {
                ListObjectsResult result = ResponseParsers.parseObjectListResponse(response.body().byteStream());
                result.setRequestId(response.header(OSSHeaders.OSS_HEADER_REQUEST_ID));
                result.setStatusCode(response.code());
                result.setResponseHeader(ResponseParsers.parseResponseHeader(response));
                ResponseParsers.safeCloseResponse(response);
                return result;
            } catch (Exception e) {
                throw new IOException(e.getMessage(), e);
            } catch (Throwable th) {
                ResponseParsers.safeCloseResponse(response);
                throw th;
            }
        }
    }

    public static final class InitMultipartResponseParser implements ResponseParser<InitiateMultipartUploadResult> {
        public InitiateMultipartUploadResult parse(Response response) throws IOException {
            try {
                InitiateMultipartUploadResult result = ResponseParsers.parseInitMultipartResponseXML(response.body().byteStream());
                result.setRequestId(response.header(OSSHeaders.OSS_HEADER_REQUEST_ID));
                result.setStatusCode(response.code());
                result.setResponseHeader(ResponseParsers.parseResponseHeader(response));
                ResponseParsers.safeCloseResponse(response);
                return result;
            } catch (Exception e) {
                throw new IOException(e.getMessage(), e);
            } catch (Throwable th) {
                ResponseParsers.safeCloseResponse(response);
                throw th;
            }
        }
    }

    public static final class UploadPartResponseParser implements ResponseParser<UploadPartResult> {
        public UploadPartResult parse(Response response) throws IOException {
            try {
                UploadPartResult result = new UploadPartResult();
                result.setRequestId(response.header(OSSHeaders.OSS_HEADER_REQUEST_ID));
                result.setStatusCode(response.code());
                result.setResponseHeader(ResponseParsers.parseResponseHeader(response));
                result.setETag(ResponseParsers.trimQuotes(response.header(HttpHeaders.ETAG)));
                return result;
            } finally {
                ResponseParsers.safeCloseResponse(response);
            }
        }
    }

    public static final class AbortMultipartUploadResponseParser implements ResponseParser<AbortMultipartUploadResult> {
        public AbortMultipartUploadResult parse(Response response) throws IOException {
            try {
                AbortMultipartUploadResult result = new AbortMultipartUploadResult();
                result.setRequestId(response.header(OSSHeaders.OSS_HEADER_REQUEST_ID));
                result.setStatusCode(response.code());
                result.setResponseHeader(ResponseParsers.parseResponseHeader(response));
                return result;
            } finally {
                ResponseParsers.safeCloseResponse(response);
            }
        }
    }

    public static final class CompleteMultipartUploadResponseParser implements ResponseParser<CompleteMultipartUploadResult> {
        public CompleteMultipartUploadResult parse(Response response) throws IOException {
            try {
                CompleteMultipartUploadResult result = new CompleteMultipartUploadResult();
                if (response.header("Content-Type").equals("application/xml")) {
                    result = ResponseParsers.parseCompleteMultipartUploadResponseXML(response.body().byteStream());
                } else if (response.body() != null) {
                    result.setServerCallbackReturnBody(response.body().string());
                }
                result.setRequestId(response.header(OSSHeaders.OSS_HEADER_REQUEST_ID));
                result.setStatusCode(response.code());
                result.setResponseHeader(ResponseParsers.parseResponseHeader(response));
                ResponseParsers.safeCloseResponse(response);
                return result;
            } catch (Exception e) {
                throw new IOException(e.getMessage(), e);
            } catch (Throwable th) {
                ResponseParsers.safeCloseResponse(response);
                throw th;
            }
        }
    }

    public static final class ListPartsResponseParser implements ResponseParser<ListPartsResult> {
        public ListPartsResult parse(Response response) throws IOException {
            try {
                ListPartsResult result = ResponseParsers.parseListPartsResponseXML(response.body().byteStream());
                result.setRequestId(response.header(OSSHeaders.OSS_HEADER_REQUEST_ID));
                result.setStatusCode(response.code());
                result.setResponseHeader(ResponseParsers.parseResponseHeader(response));
                ResponseParsers.safeCloseResponse(response);
                return result;
            } catch (Exception e) {
                throw new IOException(e.getMessage(), e);
            } catch (Throwable th) {
                ResponseParsers.safeCloseResponse(response);
                throw th;
            }
        }
    }

    /* access modifiers changed from: private */
    public static CopyObjectResult parseCopyObjectResponseXML(InputStream in) throws ParseException, ParserConfigurationException, IOException, SAXException {
        CopyObjectResult result = new CopyObjectResult();
        Element element = domFactory.newDocumentBuilder().parse(in).getDocumentElement();
        OSSLog.logD("[item] - " + element.getNodeName());
        NodeList list = element.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node item = list.item(i);
            String name = item.getNodeName();
            if (name != null) {
                if (name.equals("LastModified")) {
                    result.setLastModified(DateUtil.parseIso8601Date(checkChildNotNullAndGetValue(item)));
                } else if (name.equals(HttpHeaders.ETAG)) {
                    result.setEtag(checkChildNotNullAndGetValue(item));
                }
            }
        }
        return result;
    }

    /* access modifiers changed from: private */
    public static ListPartsResult parseListPartsResponseXML(InputStream in) throws ParserConfigurationException, IOException, SAXException, ParseException {
        String size;
        ListPartsResult result = new ListPartsResult();
        Element element = domFactory.newDocumentBuilder().parse(in).getDocumentElement();
        OSSLog.logD("[parseObjectListResponse] - " + element.getNodeName());
        List<PartSummary> partEtagList = new ArrayList<>();
        NodeList list = element.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node item = list.item(i);
            String name = item.getNodeName();
            if (name != null) {
                if (name.equals("Bucket")) {
                    result.setBucketName(checkChildNotNullAndGetValue(item));
                } else if (name.equals("Key")) {
                    result.setKey(checkChildNotNullAndGetValue(item));
                } else if (name.equals("UploadId")) {
                    result.setUploadId(checkChildNotNullAndGetValue(item));
                } else if (name.equals("PartNumberMarker")) {
                    String partNumberMarker = checkChildNotNullAndGetValue(item);
                    if (partNumberMarker != null) {
                        result.setPartNumberMarker(Integer.valueOf(partNumberMarker).intValue());
                    }
                } else if (name.equals("NextPartNumberMarker")) {
                    String nextPartNumberMarker = checkChildNotNullAndGetValue(item);
                    if (nextPartNumberMarker != null) {
                        result.setNextPartNumberMarker(Integer.valueOf(nextPartNumberMarker).intValue());
                    }
                } else if (name.equals("MaxParts")) {
                    String maxParts = checkChildNotNullAndGetValue(item);
                    if (maxParts != null) {
                        result.setMaxParts(Integer.valueOf(maxParts).intValue());
                    }
                } else if (name.equals("IsTruncated")) {
                    String isTruncated = checkChildNotNullAndGetValue(item);
                    if (isTruncated != null) {
                        result.setTruncated(Boolean.valueOf(isTruncated).booleanValue());
                    }
                } else if (name.equals("Part")) {
                    NodeList partNodeList = item.getChildNodes();
                    PartSummary partSummary = new PartSummary();
                    for (int k = 0; k < partNodeList.getLength(); k++) {
                        Node partItem = partNodeList.item(k);
                        String partItemName = partItem.getNodeName();
                        if (partItemName != null) {
                            if (partItemName.equals("PartNumber")) {
                                String partNumber = checkChildNotNullAndGetValue(partItem);
                                if (partNumber != null) {
                                    partSummary.setPartNumber(Integer.valueOf(partNumber).intValue());
                                }
                            } else if (partItemName.equals("LastModified")) {
                                partSummary.setLastModified(DateUtil.parseIso8601Date(checkChildNotNullAndGetValue(partItem)));
                            } else if (partItemName.equals(HttpHeaders.ETAG)) {
                                partSummary.setETag(checkChildNotNullAndGetValue(partItem));
                            } else if (partItemName.equals("Size") && (size = checkChildNotNullAndGetValue(partItem)) != null) {
                                partSummary.setSize((long) Integer.valueOf(size).intValue());
                            }
                        }
                    }
                    partEtagList.add(partSummary);
                }
            }
        }
        result.setParts(partEtagList);
        return result;
    }

    /* access modifiers changed from: private */
    public static CompleteMultipartUploadResult parseCompleteMultipartUploadResponseXML(InputStream in) throws ParserConfigurationException, IOException, SAXException {
        CompleteMultipartUploadResult result = new CompleteMultipartUploadResult();
        Element element = domFactory.newDocumentBuilder().parse(in).getDocumentElement();
        OSSLog.logD("[item] - " + element.getNodeName());
        NodeList list = element.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node item = list.item(i);
            String name = item.getNodeName();
            if (name != null) {
                if (name.equalsIgnoreCase(HttpHeaders.LOCATION)) {
                    result.setLocation(checkChildNotNullAndGetValue(item));
                } else if (name.equalsIgnoreCase("Bucket")) {
                    result.setBucketName(checkChildNotNullAndGetValue(item));
                } else if (name.equalsIgnoreCase("Key")) {
                    result.setObjectKey(checkChildNotNullAndGetValue(item));
                } else if (name.equalsIgnoreCase(HttpHeaders.ETAG)) {
                    result.setETag(checkChildNotNullAndGetValue(item));
                }
            }
        }
        return result;
    }

    /* access modifiers changed from: private */
    public static InitiateMultipartUploadResult parseInitMultipartResponseXML(InputStream in) throws IOException, SAXException, ParserConfigurationException {
        InitiateMultipartUploadResult result = new InitiateMultipartUploadResult();
        Element element = domFactory.newDocumentBuilder().parse(in).getDocumentElement();
        OSSLog.logD("[item] - " + element.getNodeName());
        NodeList list = element.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node item = list.item(i);
            String name = item.getNodeName();
            if (name != null) {
                if (name.equalsIgnoreCase("UploadId")) {
                    result.setUploadId(checkChildNotNullAndGetValue(item));
                } else if (name.equalsIgnoreCase("Bucket")) {
                    result.setBucketName(checkChildNotNullAndGetValue(item));
                } else if (name.equalsIgnoreCase("Key")) {
                    result.setObjectKey(checkChildNotNullAndGetValue(item));
                }
            }
        }
        return result;
    }

    private static OSSObjectSummary parseObjectSummaryXML(NodeList list) throws ParseException {
        OSSObjectSummary object = new OSSObjectSummary();
        for (int i = 0; i < list.getLength(); i++) {
            Node item = list.item(i);
            String name = item.getNodeName();
            if (name != null) {
                if (name.equals("Key")) {
                    object.setKey(checkChildNotNullAndGetValue(item));
                } else if (name.equals("LastModified")) {
                    object.setLastModified(DateUtil.parseIso8601Date(checkChildNotNullAndGetValue(item)));
                } else if (name.equals("Size")) {
                    String size = checkChildNotNullAndGetValue(item);
                    if (size != null) {
                        object.setSize((long) Integer.valueOf(size).intValue());
                    }
                } else if (name.equals(HttpHeaders.ETAG)) {
                    object.setETag(checkChildNotNullAndGetValue(item));
                } else if (name.equals("Type")) {
                    object.setType(checkChildNotNullAndGetValue(item));
                } else if (name.equals("StorageClass")) {
                    object.setStorageClass(checkChildNotNullAndGetValue(item));
                }
            }
        }
        return object;
    }

    private static String parseCommonPrefixXML(NodeList list) {
        for (int i = 0; i < list.getLength(); i++) {
            Node item = list.item(i);
            String name = item.getNodeName();
            if (name != null && name.equals("Prefix")) {
                return checkChildNotNullAndGetValue(item);
            }
        }
        return "";
    }

    /* access modifiers changed from: private */
    public static GetBucketACLResult parseGetBucketACLResponse(InputStream in) throws ParserConfigurationException, IOException, SAXException, ParseException {
        GetBucketACLResult result = new GetBucketACLResult();
        Element element = domFactory.newDocumentBuilder().parse(in).getDocumentElement();
        OSSLog.logD("[parseGetBucketACLResponse - " + element.getNodeName());
        NodeList list = element.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node item = list.item(i);
            String name = item.getNodeName();
            if (name != null) {
                if (name.equals("Owner")) {
                    NodeList ownerList = item.getChildNodes();
                    for (int j = 0; j < ownerList.getLength(); j++) {
                        Node ownerItem = ownerList.item(j);
                        String ownerName = ownerItem.getNodeName();
                        if (ownerName != null) {
                            if (ownerName.equals("ID")) {
                                result.setBucketOwnerID(checkChildNotNullAndGetValue(ownerItem));
                            } else if (ownerName.equals("DisplayName")) {
                                result.setBucketOwner(checkChildNotNullAndGetValue(ownerItem));
                            }
                        }
                    }
                } else if (name.equals("AccessControlList")) {
                    NodeList aclList = item.getChildNodes();
                    for (int k = 0; k < aclList.getLength(); k++) {
                        Node aclItem = aclList.item(k);
                        String aclName = aclItem.getNodeName();
                        if (aclName != null && aclName.equals("Grant")) {
                            result.setBucketACL(checkChildNotNullAndGetValue(aclItem));
                        }
                    }
                }
            }
        }
        return result;
    }

    /* access modifiers changed from: private */
    public static ListObjectsResult parseObjectListResponse(InputStream in) throws ParserConfigurationException, IOException, SAXException, ParseException {
        String prefix;
        ListObjectsResult result = new ListObjectsResult();
        Element element = domFactory.newDocumentBuilder().parse(in).getDocumentElement();
        OSSLog.logD("[parseObjectListResponse] - " + element.getNodeName());
        NodeList list = element.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node item = list.item(i);
            String name = item.getNodeName();
            if (name != null) {
                if (name.equals("Name")) {
                    result.setBucketName(checkChildNotNullAndGetValue(item));
                } else if (name.equals("Prefix")) {
                    result.setPrefix(checkChildNotNullAndGetValue(item));
                } else if (name.equals("Marker")) {
                    result.setMarker(checkChildNotNullAndGetValue(item));
                } else if (name.equals("Delimiter")) {
                    result.setDelimiter(checkChildNotNullAndGetValue(item));
                } else if (name.equals("EncodingType")) {
                    result.setEncodingType(checkChildNotNullAndGetValue(item));
                } else if (name.equals("MaxKeys")) {
                    String maxKeys = checkChildNotNullAndGetValue(item);
                    if (maxKeys != null) {
                        result.setMaxKeys(Integer.valueOf(maxKeys).intValue());
                    }
                } else if (name.equals("NextMarker")) {
                    result.setNextMarker(checkChildNotNullAndGetValue(item));
                } else if (name.equals("IsTruncated")) {
                    String isTruncated = checkChildNotNullAndGetValue(item);
                    if (isTruncated != null) {
                        result.setTruncated(Boolean.valueOf(isTruncated).booleanValue());
                    }
                } else if (name.equals("Contents")) {
                    if (item.getChildNodes() != null) {
                        result.getObjectSummaries().add(parseObjectSummaryXML(item.getChildNodes()));
                    }
                } else if (!(!name.equals("CommonPrefixes") || item.getChildNodes() == null || (prefix = parseCommonPrefixXML(item.getChildNodes())) == null)) {
                    result.getCommonPrefixes().add(prefix);
                }
            }
        }
        return result;
    }

    public static String trimQuotes(String s) {
        if (s == null) {
            return null;
        }
        String s2 = s.trim();
        if (s2.startsWith("\"")) {
            s2 = s2.substring(1);
        }
        if (s2.endsWith("\"")) {
            return s2.substring(0, s2.length() - 1);
        }
        return s2;
    }

    public static ObjectMetadata parseObjectMetadata(Map<String, String> headers) throws IOException {
        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            for (String key : headers.keySet()) {
                if (key.indexOf(OSSHeaders.OSS_USER_METADATA_PREFIX) >= 0) {
                    objectMetadata.addUserMetadata(key, headers.get(key));
                } else if (key.equals(HttpHeaders.LAST_MODIFIED) || key.equals(HttpHeaders.DATE)) {
                    objectMetadata.setHeader(key, DateUtil.parseRfc822Date(headers.get(key)));
                } else if (key.equals(HttpHeaders.CONTENT_LENGTH)) {
                    objectMetadata.setHeader(key, Long.valueOf(headers.get(key)));
                } else if (key.equals(HttpHeaders.ETAG)) {
                    objectMetadata.setHeader(key, trimQuotes(headers.get(key)));
                } else {
                    objectMetadata.setHeader(key, headers.get(key));
                }
            }
            return objectMetadata;
        } catch (ParseException pe) {
            throw new IOException(pe.getMessage(), pe);
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    public static Map<String, String> parseResponseHeader(Response response) {
        Map<String, String> result = new HashMap<>();
        Headers headers = response.headers();
        for (int i = 0; i < headers.size(); i++) {
            result.put(headers.name(i), headers.value(i));
        }
        return result;
    }

    public static ServiceException parseResponseErrorXML(Response response, boolean isHeadRequest) throws IOException {
        int statusCode = response.code();
        String requestId = response.header(OSSHeaders.OSS_HEADER_REQUEST_ID);
        String code = null;
        String message = null;
        String hostId = null;
        String errorMessage = null;
        if (!isHeadRequest) {
            try {
                errorMessage = response.body().string();
                NodeList list = domFactory.newDocumentBuilder().parse(new InputSource(new StringReader(errorMessage))).getDocumentElement().getChildNodes();
                for (int i = 0; i < list.getLength(); i++) {
                    Node item = list.item(i);
                    String name = item.getNodeName();
                    if (name != null) {
                        if (name.equals("Code")) {
                            code = checkChildNotNullAndGetValue(item);
                        }
                        if (name.equals("Message")) {
                            message = checkChildNotNullAndGetValue(item);
                        }
                        if (name.equals("RequestId")) {
                            requestId = checkChildNotNullAndGetValue(item);
                        }
                        if (name.equals("HostId")) {
                            hostId = checkChildNotNullAndGetValue(item);
                        }
                    }
                }
                response.body().close();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e2) {
                e2.printStackTrace();
            }
        }
        return new ServiceException(statusCode, message, code, requestId, hostId, errorMessage);
    }

    public static String checkChildNotNullAndGetValue(Node item) {
        if (item.getFirstChild() != null) {
            return item.getFirstChild().getNodeValue();
        }
        return null;
    }

    public static void safeCloseResponse(Response response) {
        try {
            response.body().close();
        } catch (Exception e) {
        }
    }
}
