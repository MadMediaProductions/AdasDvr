package com.fvision.camera.http;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.fvision.camera.R;
import com.fvision.camera.utils.LogUtils;
import com.fvision.camera.utils.SharedPreferencesUtil;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import org.json.JSONObject;

public class HttpRequest {
    static String adasCodeUrl = "/watch/getadascode&";
    static String baseUrl = "http://www.huiying616.com/watch/index.php?r=api";
    static RequestIFace mLintener;
    static SanctionIFace mSanctionIFace;
    static String sanctionUrl = "/package/get-sanction&";
    static String updateApk = "/package/check-update&";

    public interface RequestIFace {
        void onFail(int i, String str);

        void onSuccess(int i, String str, String str2, String str3);
    }

    public interface SanctionIFace {
        void onFail(int i, String str);

        void onSuccess(boolean z);
    }

    public static void requestGet(HashMap<String, String> paramsMap, RequestIFace lintener, Context context) {
        mLintener = lintener;
        try {
            StringBuilder tempParams = new StringBuilder();
            int pos = 0;
            for (String key : paramsMap.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                tempParams.append(String.format("%s=%s", new Object[]{key, URLEncoder.encode(paramsMap.get(key), "utf-8")}));
                pos++;
            }
            String requestUrl = baseUrl + updateApk + tempParams.toString();
            LogUtils.d("requestUrl " + requestUrl);
            HttpURLConnection urlConn = (HttpURLConnection) new URL(requestUrl).openConnection();
            urlConn.setConnectTimeout(5000);
            urlConn.setReadTimeout(5000);
            urlConn.setUseCaches(true);
            urlConn.setRequestMethod("GET");
            urlConn.setRequestProperty("Content-Type", "application/json");
            urlConn.addRequestProperty("Connection", "Keep-Alive");
            urlConn.connect();
            if (urlConn.getResponseCode() == 200) {
                String result = streamToString(urlConn.getInputStream());
                Log.e("zoulequan", "Get方式请求成功，result--->" + result);
                JSONObject json = new JSONObject(result);
                boolean status = json.getBoolean("status");
                JSONObject data = json.getJSONObject("data");
                if (status) {
                    int version_code = data.getInt("version_code");
                    String version = data.getString("version");
                    String app_desc = data.getString("app_desc");
                    String path = data.getString("path");
                    LogUtils.d(" status " + status + " version_code " + version_code + " version " + version + " app_desc " + app_desc + " path " + path);
                    if (mLintener != null) {
                        mLintener.onSuccess(version_code, path, app_desc, version);
                    }
                } else {
                    int code = data.getInt("code");
                    String error = data.getString("message");
                    if (error.contains("SocketTimeoutException")) {
                        error = context.getResources().getString(R.string.network_connection_timeout);
                    } else if (error.contains("NullPointerException")) {
                        error = context.getResources().getString(R.string.network_error);
                    } else if (error.startsWith("java.lang.SocketTimeoutException")) {
                        error = context.getResources().getString(R.string.network_connection_timeout);
                    } else if (error.startsWith("java.lang.NullPointerException")) {
                        error = context.getResources().getString(R.string.network_error);
                    }
                    if (mLintener != null) {
                        mLintener.onFail(code, error);
                    }
                }
            } else {
                if (mLintener != null) {
                    mLintener.onFail(-1, "get request fail");
                }
                Log.e("zoulequan", "Get方式请求失败");
            }
            urlConn.disconnect();
        } catch (Exception e) {
            if (mLintener != null) {
                mLintener.onFail(50, e.toString());
            }
            Log.e("ContentValues", e.toString());
        }
    }

    public static void requestSanction(HashMap<String, String> paramsMap, SanctionIFace lintener) {
        boolean z;
        mSanctionIFace = lintener;
        try {
            StringBuilder tempParams = new StringBuilder();
            int pos = 0;
            for (String key : paramsMap.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                tempParams.append(String.format("%s=%s", new Object[]{key, URLEncoder.encode(paramsMap.get(key), "utf-8")}));
                pos++;
            }
            String requestUrl = baseUrl + sanctionUrl + tempParams.toString();
            LogUtils.d("requestUrl " + requestUrl);
            HttpURLConnection urlConn = (HttpURLConnection) new URL(requestUrl).openConnection();
            urlConn.setConnectTimeout(5000);
            urlConn.setReadTimeout(5000);
            urlConn.setUseCaches(true);
            urlConn.setRequestMethod("GET");
            urlConn.setRequestProperty("Content-Type", "application/json");
            urlConn.addRequestProperty("Connection", "Keep-Alive");
            urlConn.connect();
            if (urlConn.getResponseCode() == 200) {
                String result = streamToString(urlConn.getInputStream());
                LogUtils.e("zoulequan", "制裁请求成功，result--->" + result);
                JSONObject json = new JSONObject(result);
                boolean status = json.getBoolean("status");
                JSONObject data = json.getJSONObject("data");
                if (status) {
                    int is_sanction = data.getInt(SharedPreferencesUtil.IS_SANCTION);
                    if (mSanctionIFace != null) {
                        SanctionIFace sanctionIFace = mSanctionIFace;
                        if (is_sanction == 1) {
                            z = true;
                        } else {
                            z = false;
                        }
                        sanctionIFace.onSuccess(z);
                    }
                } else {
                    int code = data.getInt("code");
                    String error = data.getString("message");
                    if (mSanctionIFace != null) {
                        mSanctionIFace.onFail(code, error);
                    }
                }
            } else {
                if (mSanctionIFace != null) {
                    mSanctionIFace.onFail(-1, "get request fail");
                }
                Log.e("zoulequan", "Get方式请求失败");
            }
            urlConn.disconnect();
        } catch (Exception e) {
            if (mSanctionIFace != null) {
                mSanctionIFace.onFail(50, e.toString());
            }
            Log.e("ContentValues", e.toString());
        }
    }

    public static void javaHttpGet(HashMap<String, String> paramsMap) {
        StringBuilder tempParams = new StringBuilder();
        int pos = 0;
        for (String key : paramsMap.keySet()) {
            if (pos > 0) {
                tempParams.append("&");
            }
            try {
                tempParams.append(String.format("%s=%s", new Object[]{key, URLEncoder.encode(paramsMap.get(key), "utf-8")}));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            pos++;
        }
        try {
            HttpURLConnection urlConnect = (HttpURLConnection) new URL("http://apicloud.mob.com/v1/mobile/address/query?" + tempParams.toString()).openConnection();
            urlConnect.setConnectTimeout(10000);
            urlConnect.connect();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(urlConnect.getInputStream()));
            String resultData = null;
            while (true) {
                String inputLine = buffer.readLine();
                if (inputLine != null) {
                    resultData = resultData + inputLine;
                } else {
                    LogUtils.d("resultData " + resultData);
                    return;
                }
            }
        } catch (MalformedURLException e2) {
            e2.printStackTrace();
        } catch (IOException e3) {
            e3.printStackTrace();
        }
    }

    private void requestPost(HashMap<String, String> paramsMap) {
        try {
            StringBuilder tempParams = new StringBuilder();
            int pos = 0;
            for (String key : paramsMap.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                tempParams.append(String.format("%s=%s", new Object[]{key, URLEncoder.encode(paramsMap.get(key), "utf-8")}));
                pos++;
            }
            byte[] postData = tempParams.toString().getBytes();
            HttpURLConnection urlConn = (HttpURLConnection) new URL("https://xxx.com/getUsers").openConnection();
            urlConn.setConnectTimeout(5000);
            urlConn.setReadTimeout(5000);
            urlConn.setDoOutput(true);
            urlConn.setDoInput(true);
            urlConn.setUseCaches(false);
            urlConn.setRequestMethod("POST");
            urlConn.setInstanceFollowRedirects(true);
            urlConn.setRequestProperty("Content-Type", "application/json");
            urlConn.connect();
            DataOutputStream dos = new DataOutputStream(urlConn.getOutputStream());
            dos.write(postData);
            dos.flush();
            dos.close();
            if (urlConn.getResponseCode() == 200) {
                Log.e("ContentValues", "Post方式请求成功，result--->" + streamToString(urlConn.getInputStream()));
            } else {
                Log.e("ContentValues", "Post方式请求失败");
            }
            urlConn.disconnect();
        } catch (Exception e) {
            Log.e("ContentValues", e.toString());
        }
    }

    public static String streamToString(InputStream is) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            while (true) {
                int len = is.read(buffer);
                if (len != -1) {
                    baos.write(buffer, 0, len);
                } else {
                    baos.close();
                    is.close();
                    return new String(baos.toByteArray());
                }
            }
        } catch (Exception e) {
            Log.e("ContentValues", e.toString());
            return null;
        }
    }

    private void downloadFile(String fileUrl) {
        try {
            HttpURLConnection urlConn = (HttpURLConnection) new URL(fileUrl).openConnection();
            urlConn.setConnectTimeout(5000);
            urlConn.setReadTimeout(5000);
            urlConn.setUseCaches(true);
            urlConn.setRequestMethod("GET");
            urlConn.setRequestProperty("Content-Type", "application/json");
            urlConn.addRequestProperty("Connection", "Keep-Alive");
            urlConn.connect();
            if (urlConn.getResponseCode() == 200) {
                FileOutputStream fos = new FileOutputStream(new File(""));
                byte[] buffer = new byte[1024];
                InputStream inputStream = urlConn.getInputStream();
                while (true) {
                    int len = inputStream.read(buffer);
                    if (len == -1) {
                        break;
                    }
                    fos.write(buffer, 0, len);
                }
            } else {
                Log.e("ContentValues", "文件下载失败");
            }
            urlConn.disconnect();
        } catch (Exception e) {
            Log.e("ContentValues", e.toString());
        }
    }

    public static void getADASCode(HashMap<String, String> paramsMap, RequestIFace lintener) {
        mLintener = lintener;
        try {
            StringBuilder tempParams = new StringBuilder();
            int pos = 0;
            for (String key : paramsMap.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                tempParams.append(String.format("%s=%s", new Object[]{key, URLEncoder.encode(paramsMap.get(key), "utf-8")}));
                pos++;
            }
            LogUtils.d("requestUrl " + adasCodeUrl);
            HttpURLConnection urlConn = (HttpURLConnection) new URL(baseUrl + adasCodeUrl + tempParams.toString()).openConnection();
            urlConn.setConnectTimeout(5000);
            urlConn.setReadTimeout(5000);
            urlConn.setUseCaches(true);
            urlConn.setRequestMethod("GET");
            urlConn.setRequestProperty("Content-Type", "application/json");
            urlConn.addRequestProperty("Connection", "Keep-Alive");
            urlConn.connect();
            if (urlConn.getResponseCode() == 200) {
                String result = streamToString(urlConn.getInputStream());
                Log.e("zoulequan", "Get方式请求成功，result--->" + result);
                JSONObject json = new JSONObject(result);
                boolean status = json.getBoolean("status");
                JSONObject data = json.getJSONObject("data");
                if (status) {
                    String adasCode = data.getString("code");
                    LogUtils.d(" status " + status + " adasCode " + adasCode);
                    if (mLintener != null) {
                        mLintener.onSuccess(-1, adasCode, adasCode, adasCode);
                    }
                } else {
                    int code = data.getInt("code");
                    String error = data.getString("message");
                    if (mLintener != null) {
                        mLintener.onFail(code, error);
                    }
                }
            } else {
                if (mLintener != null) {
                    mLintener.onFail(-1, "get request fail");
                }
                Log.e("zoulequan", "Get方式请求失败");
            }
            urlConn.disconnect();
        } catch (Exception e) {
            if (mLintener != null) {
                if (TextUtils.isEmpty("")) {
                    mLintener.onFail(53, e.toString());
                } else {
                    mLintener.onFail(52, "");
                }
            }
            Log.e("ContentValues", e.toString());
        }
    }
}
