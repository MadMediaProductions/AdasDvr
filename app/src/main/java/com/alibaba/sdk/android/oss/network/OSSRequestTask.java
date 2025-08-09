package com.alibaba.sdk.android.oss.network;

import android.support.v4.media.session.PlaybackStateCompat;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.utils.DateUtil;
import com.alibaba.sdk.android.oss.common.utils.HttpHeaders;
import com.alibaba.sdk.android.oss.common.utils.OSSUtils;
import com.alibaba.sdk.android.oss.internal.OSSRetryHandler;
import com.alibaba.sdk.android.oss.internal.OSSRetryType;
import com.alibaba.sdk.android.oss.internal.RequestMessage;
import com.alibaba.sdk.android.oss.internal.ResponseParser;
import com.alibaba.sdk.android.oss.internal.ResponseParsers;
import com.alibaba.sdk.android.oss.model.OSSResult;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public class OSSRequestTask<T extends OSSResult> implements Callable<T> {
    private OkHttpClient client;
    /* access modifiers changed from: private */
    public ExecutionContext context;
    private int currentRetryCount = 0;
    private RequestMessage message;
    private ResponseParser<T> responseParser;
    private OSSRetryHandler retryHandler;

    class ProgressTouchableRequestBody extends RequestBody {
        private static final int SEGMENT_SIZE = 2048;
        private BufferedSink bufferedSink;
        private OSSProgressCallback callback;
        private long contentLength;
        private String contentType;
        private byte[] data;
        private File file;
        private InputStream inputStream;

        public ProgressTouchableRequestBody(File file2, String contentType2, OSSProgressCallback callback2) {
            this.file = file2;
            this.contentType = contentType2;
            this.contentLength = file2.length();
            this.callback = callback2;
        }

        public ProgressTouchableRequestBody(byte[] data2, String contentType2, OSSProgressCallback callback2) {
            this.data = data2;
            this.contentType = contentType2;
            this.contentLength = (long) data2.length;
            this.callback = callback2;
        }

        public ProgressTouchableRequestBody(InputStream input, long contentLength2, String contentType2, OSSProgressCallback callback2) {
            this.inputStream = input;
            this.contentType = contentType2;
            this.contentLength = contentLength2;
            this.callback = callback2;
        }

        public MediaType contentType() {
            return MediaType.parse(this.contentType);
        }

        public long contentLength() throws IOException {
            return this.contentLength;
        }

        public void writeTo(BufferedSink sink) throws IOException {
            Source source = null;
            if (this.file != null) {
                source = Okio.source(this.file);
            } else if (this.data != null) {
                source = Okio.source((InputStream) new ByteArrayInputStream(this.data));
            } else if (this.inputStream != null) {
                source = Okio.source(this.inputStream);
            }
            long total = 0;
            while (total < this.contentLength) {
                long read = source.read(sink.buffer(), Math.min(this.contentLength - total, PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH));
                if (read == -1) {
                    break;
                }
                total += read;
                sink.flush();
                if (this.callback != null) {
                    this.callback.onProgress(OSSRequestTask.this.context.getRequest(), total, this.contentLength);
                }
            }
            if (source != null) {
                source.close();
            }
        }
    }

    public OSSRequestTask(RequestMessage message2, ResponseParser parser, ExecutionContext context2, int maxRetry) {
        this.responseParser = parser;
        this.message = message2;
        this.context = context2;
        this.client = context2.getClient();
        this.retryHandler = new OSSRetryHandler(maxRetry);
    }

    public T call() throws Exception {
        Request request = null;
        Response response = null;
        Exception exception = null;
        try {
            OSSLog.logD("[call] - ");
            OSSUtils.ensureRequestValid(this.context.getRequest(), this.message);
            OSSUtils.signRequest(this.message);
            if (this.context.getCancellationHandler().isCancelled()) {
                throw new InterruptedIOException("This task is cancelled!");
            }
            Request.Builder requestBuilder = new Request.Builder().url(this.message.buildCanonicalURL());
            for (String key : this.message.getHeaders().keySet()) {
                requestBuilder = requestBuilder.addHeader(key, this.message.getHeaders().get(key));
            }
            String contentType = this.message.getHeaders().get("Content-Type");
            switch (this.message.getMethod()) {
                case POST:
                case PUT:
                    OSSUtils.assertTrue(contentType != null, "Content type can't be null when upload!");
                    if (this.message.getUploadData() == null) {
                        if (this.message.getUploadFilePath() == null) {
                            if (this.message.getUploadInputStream() == null) {
                                requestBuilder = requestBuilder.method(this.message.getMethod().toString(), RequestBody.create((MediaType) null, new byte[0]));
                                break;
                            } else {
                                requestBuilder = requestBuilder.method(this.message.getMethod().toString(), new ProgressTouchableRequestBody(this.message.getUploadInputStream(), this.message.getReadStreamLength(), contentType, this.context.getProgressCallback()));
                                break;
                            }
                        } else {
                            requestBuilder = requestBuilder.method(this.message.getMethod().toString(), new ProgressTouchableRequestBody(new File(this.message.getUploadFilePath()), contentType, this.context.getProgressCallback()));
                            break;
                        }
                    } else {
                        requestBuilder = requestBuilder.method(this.message.getMethod().toString(), new ProgressTouchableRequestBody(this.message.getUploadData(), contentType, this.context.getProgressCallback()));
                        break;
                    }
                case GET:
                    requestBuilder = requestBuilder.get();
                    break;
                case HEAD:
                    requestBuilder = requestBuilder.head();
                    break;
                case DELETE:
                    requestBuilder = requestBuilder.delete();
                    break;
            }
            request = requestBuilder.build();
            if (OSSLog.isEnableLog()) {
                OSSLog.logD("request url: " + request.url());
                Map<String, List<String>> headerMap = request.headers().toMultimap();
                for (String key2 : headerMap.keySet()) {
                    OSSLog.logD("requestHeader " + key2 + ": " + ((String) headerMap.get(key2).get(0)));
                }
            }
            Call call = this.client.newCall(request);
            this.context.getCancellationHandler().setCall(call);
            response = call.execute();
            if (OSSLog.isEnableLog()) {
                OSSLog.logD("response code: " + response.code() + " for url: " + request.url());
                Map<String, List<String>> headerMap2 = response.headers().toMultimap();
                for (String key3 : headerMap2.keySet()) {
                    OSSLog.logD("responseHeader " + key3 + ": " + ((String) headerMap2.get(key3).get(0)));
                }
            }
            if (exception == null && (response.code() == 203 || response.code() >= 300)) {
                try {
                    exception = ResponseParsers.parseResponseErrorXML(response, request.method().equals("HEAD"));
                } catch (IOException e) {
                    exception = new ClientException(e.getMessage(), e);
                }
            } else if (exception == null) {
                try {
                    T result = (OSSResult) this.responseParser.parse(response);
                    if (this.context.getCompletedCallback() == null) {
                        return result;
                    }
                    this.context.getCompletedCallback().onSuccess(this.context.getRequest(), result);
                    return result;
                } catch (IOException e2) {
                    exception = new ClientException(e2.getMessage(), e2);
                }
            }
            OSSRetryType retryType = this.retryHandler.shouldRetry(exception, this.currentRetryCount);
            OSSLog.logE("[run] - retry, retry type: " + retryType);
            if (retryType == OSSRetryType.OSSRetryTypeShouldRetry) {
                this.currentRetryCount++;
                return call();
            } else if (retryType == OSSRetryType.OSSRetryTypeShouldFixedTimeSkewedAndRetry) {
                String responseDateString = response.header(HttpHeaders.DATE);
                this.message.getHeaders().put(HttpHeaders.DATE, responseDateString);
                DateUtil.setCurrentServerTime(DateUtil.parseRfc822Date(responseDateString).getTime());
                this.currentRetryCount++;
                return call();
            } else {
                if (exception instanceof ClientException) {
                    if (this.context.getCompletedCallback() != null) {
                        this.context.getCompletedCallback().onFailure(this.context.getRequest(), (ClientException) exception, (ServiceException) null);
                    }
                } else if (this.context.getCompletedCallback() != null) {
                    this.context.getCompletedCallback().onFailure(this.context.getRequest(), (ClientException) null, (ServiceException) exception);
                }
                throw exception;
            }
        } catch (Exception e3) {
            OSSLog.logE("Encounter local execpiton: " + e3.toString());
            if (OSSLog.isEnableLog()) {
                e3.printStackTrace();
            }
            exception = new ClientException(e3.getMessage(), e3);
        }
    }
}
