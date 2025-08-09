package com.adasplus.adas.adas.net;

import android.content.Context;
import android.util.Log;
import com.adasplus.adas.util.Util;
import com.alibaba.sdk.android.oss.common.OSSConstants;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RequestManager {
    private static RequestManager sRequestManager;
    private Context mContext;
    private OkHttpClient mOkHttpClient;

    public interface ImageListener {
        void onError();

        void onSuccess(byte[] bArr);
    }

    private RequestManager(Context context) {
        this.mContext = context;
        this.mOkHttpClient = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).retryOnConnectionFailure(true).cache(new Cache(new File(context.getCacheDir(), "HttpCache"), 16777216)).cookieJar(new CookieJar() {
            private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();

            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                this.cookieStore.put(url, cookies);
            }

            public List<Cookie> loadForRequest(HttpUrl url) {
                List<Cookie> cookies = this.cookieStore.get(url);
                return cookies != null ? cookies : new ArrayList<>();
            }
        }).build();
    }

    public static RequestManager getInstance(Context context) {
        if (sRequestManager == null) {
            synchronized (RequestManager.class) {
                if (sRequestManager == null) {
                    sRequestManager = new RequestManager(context);
                }
            }
        }
        return sRequestManager;
    }

    public void loadImage(String url, final ImageListener listener) {
        this.mOkHttpClient.newCall(new Request.Builder().url(url).build()).enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                listener.onError();
            }

            public void onResponse(Call call, Response response) throws IOException {
                InputStream inputStream = response.body().byteStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                while (true) {
                    int ret = inputStream.read(buffer);
                    if (ret != -1) {
                        baos.write(buffer, 0, ret);
                    } else {
                        baos.flush();
                        listener.onSuccess(baos.toByteArray());
                        baos.close();
                        return;
                    }
                }
            }
        });
    }

    public void getResponseByGetMethod(String url, final IReponseListener<String> listener, Map<String, String> params) {
        if (!Util.isNetworkConnected(this.mContext)) {
            listener.connectNetworkFail("");
            return;
        }
        listener.beforeRequest();
        getCallByGetParams(url, params).enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                listener.onFail(e.getMessage());
                listener.afterRequest();
            }

            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Result r = new Result();
                r.setData(result);
                listener.onSuccess(r);
                listener.afterRequest();
            }
        });
    }

    public void getReponseByPostMethod(String url, final IReponseListener<String> listener, Map<String, String> params) {
        if (!Util.isNetworkConnected(this.mContext)) {
            listener.connectNetworkFail("");
            return;
        }
        listener.beforeRequest();
        getCallByPostParams(url, params).enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                listener.onFail(e.getMessage());
                listener.afterRequest();
            }

            public void onResponse(Call call, Response response) throws IOException {
                listener.afterRequest();
                String result = response.body().string();
                Result r = new Result();
                r.setData(result);
                listener.onSuccess(r);
            }
        });
    }

    public String getReponseByPostMethod(String url, Map<String, String> params) {
        Log.i("Debug", params.toString());
        try {
            return getCallByPostParams(url, params).execute().body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public InputStream getInputStream(String url, Map<String, String> params) throws IOException {
        Call call = getCallByPostParams(url, params);
        Log.i("Debug", params.toString());
        return call.execute().body().byteStream();
    }

    private Request buildMutlipartFormRequest(String url, Map<String, File> fileParams, Map<String, String> stringParams) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (stringParams != null && stringParams.size() > 0) {
            for (Map.Entry<String, String> entry : stringParams.entrySet()) {
                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + entry.getKey() + "\""), RequestBody.create((MediaType) null, entry.getValue()));
            }
        }
        if (fileParams != null && fileParams.size() > 0) {
            for (Map.Entry<String, File> entry2 : fileParams.entrySet()) {
                File file = entry2.getValue();
                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + entry2.getKey() + "\"; filename=\"" + file.getName() + "\""), RequestBody.create(MediaType.parse(getMimeType(file.getName())), file));
            }
        }
        return new Request.Builder().url(url).post(builder.build()).build();
    }

    private String getMimeType(String path) {
        String contentTypeFor = URLConnection.getFileNameMap().getContentTypeFor(path);
        return contentTypeFor == null ? OSSConstants.DEFAULT_OBJECT_CONTENT_TYPE : contentTypeFor;
    }

    private Call getCallByPostParams(String url, Map<String, String> params) {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        if (params != null && params.size() > 0) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                formBodyBuilder.add(entry.getKey(), entry.getValue());
            }
        }
        return this.mOkHttpClient.newCall(new Request.Builder().url(url).post(formBodyBuilder.build()).build());
    }

    private Call getCallByGetParams(String url, Map<String, String> params) {
        StringBuffer sb = new StringBuffer();
        sb.append(url);
        if (params != null && params.size() > 0) {
            sb.append("?");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        return this.mOkHttpClient.newCall(new Request.Builder().url(sb.toString()).build());
    }
}
