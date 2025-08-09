package com.hdsc.edog.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import com.hdsc.edog.entity.HttpResult;
import com.hdsc.edog.entity.LogTag;
import com.hdsc.edog.entity.VersionInfo;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class HttpRequestManager {
    private static final int HTTP_TIMEOUT = 5000;
    public static final int MSG_UPDATE_VERSION_FAIL = 2;
    public static final int MSG_UPDATE_VERSION_SUCC = 1;
    public static final int TIME_OUT = 0;
    private static HttpRequestManager instance;
    private ExecutorService executorService;
    /* access modifiers changed from: private */
    public String mstrIP;

    private HttpRequestManager() {
        this.mstrIP = "http://www.dzgsj.com/apk/apkver.xml";
        this.executorService = null;
        this.executorService = Executors.newFixedThreadPool(5);
    }

    public static HttpRequestManager getInstance() {
        if (instance == null) {
            instance = new HttpRequestManager();
        }
        return instance;
    }

    public void updateVersion(final Context context, final Handler handler) {
        new Thread(new Runnable() {
            public void run() {
                HttpRequestManager.this.updateVersionParams(context, new HttpReqCallback() {
                    public void httpResult(HttpResult hr) {
                        if (hr.mnRet == 0) {
                            VersionInfo info = HttpRequestManager.this.paserUpdateVersion(hr.mstrRet);
                            Message msg = new Message();
                            msg.what = 1;
                            msg.obj = info;
                            handler.sendMessage(msg);
                            return;
                        }
                        handler.sendEmptyMessage(2);
                    }
                });
            }
        }).start();
    }

    /* access modifiers changed from: private */
    public void updateVersionParams(Context context, final HttpReqCallback hrc) {
        if (!checkNetwork(context)) {
            return;
        }
        if (this.executorService == null) {
            LogTag.e("HttpRequestManager:", "executorService == null");
            return;
        }
        try {
            if (this.executorService.submit(new Runnable() {
                public void run() {
                    HttpRequestManager.this.RequestHttpGet(HttpRequestManager.this.mstrIP, hrc);
                }
            }).get() == null) {
                LogTag.d("HttpRequestManager", "executorService finish!!!!!");
            }
        } catch (Exception e) {
            LogTag.e("HttpRequestManager", e.getCause().getMessage());
        }
    }

    /* access modifiers changed from: private */
    public VersionInfo paserUpdateVersion(String strData) {
        if (strData == null) {
            return null;
        }
        final VersionInfo obj = new VersionInfo();
        InputStream inputStream = new ByteArrayInputStream(strData.getBytes());
        XMLReader reader = null;
        try {
            reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e2) {
            e2.printStackTrace();
        }
        RootElement rootElement = new RootElement("root");
        rootElement.getChild("versionNo").setEndTextElementListener(new EndTextElementListener() {
            public void end(String vip) {
                obj.versionNo = vip;
            }
        });
        rootElement.getChild("downloadUrlA").setEndTextElementListener(new EndTextElementListener() {
            public void end(String vip) {
                obj.downloadUrl = vip.trim();
            }
        });
        if (reader == null) {
            return obj;
        }
        reader.setContentHandler(rootElement.getContentHandler());
        try {
            reader.parse(new InputSource(inputStream));
            return obj;
        } catch (IOException e3) {
            e3.printStackTrace();
            return obj;
        } catch (SAXException e4) {
            e4.printStackTrace();
            return obj;
        }
    }

    public void shutDownThreadPool() {
        if (this.executorService != null) {
            this.executorService.shutdownNow();
            this.executorService = null;
            instance = null;
        }
    }

    public boolean checkNetwork(Context context) {
        NetworkInfo info = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        if (info != null) {
            return info.isAvailable();
        }
        return false;
    }

    /* JADX WARNING: type inference failed for: r13v4, types: [java.net.URLConnection] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00a5  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void RequestHttpGet(String r19, HttpReqCallback r20) {
        /*
            r18 = this;
            long r2 = java.lang.System.currentTimeMillis()
            r11 = 0
            r5 = 0
            com.hdsc.edog.entity.HttpResult r6 = new com.hdsc.edog.entity.HttpResult
            r6.<init>()
            java.net.URL r12 = new java.net.URL     // Catch:{ Exception -> 0x00ac }
            r0 = r19
            r12.<init>(r0)     // Catch:{ Exception -> 0x00ac }
            java.net.URLConnection r13 = r12.openConnection()     // Catch:{ Exception -> 0x006c, all -> 0x00a1 }
            r0 = r13
            java.net.HttpURLConnection r0 = (java.net.HttpURLConnection) r0     // Catch:{ Exception -> 0x006c, all -> 0x00a1 }
            r5 = r0
            r13 = 1
            r5.setDoInput(r13)     // Catch:{ Exception -> 0x006c, all -> 0x00a1 }
            r13 = 0
            r5.setUseCaches(r13)     // Catch:{ Exception -> 0x006c, all -> 0x00a1 }
            r13 = 5000(0x1388, float:7.006E-42)
            r5.setConnectTimeout(r13)     // Catch:{ Exception -> 0x006c, all -> 0x00a1 }
            r13 = 5000(0x1388, float:7.006E-42)
            r5.setReadTimeout(r13)     // Catch:{ Exception -> 0x006c, all -> 0x00a1 }
            java.lang.String r13 = "GET"
            r5.setRequestMethod(r13)     // Catch:{ Exception -> 0x006c, all -> 0x00a1 }
            int r8 = r5.getResponseCode()     // Catch:{ Exception -> 0x006c, all -> 0x00a1 }
            r13 = 200(0xc8, float:2.8E-43)
            if (r13 != r8) goto L_0x009d
            java.lang.StringBuffer r10 = new java.lang.StringBuffer     // Catch:{ Exception -> 0x006c, all -> 0x00a1 }
            r10.<init>()     // Catch:{ Exception -> 0x006c, all -> 0x00a1 }
            java.io.BufferedReader r9 = new java.io.BufferedReader     // Catch:{ Exception -> 0x006c, all -> 0x00a1 }
            java.io.InputStreamReader r13 = new java.io.InputStreamReader     // Catch:{ Exception -> 0x006c, all -> 0x00a1 }
            java.io.InputStream r14 = r5.getInputStream()     // Catch:{ Exception -> 0x006c, all -> 0x00a1 }
            java.lang.String r15 = "UTF-8"
            r13.<init>(r14, r15)     // Catch:{ Exception -> 0x006c, all -> 0x00a1 }
            r9.<init>(r13)     // Catch:{ Exception -> 0x006c, all -> 0x00a1 }
        L_0x004e:
            java.lang.String r7 = r9.readLine()     // Catch:{ Exception -> 0x006c, all -> 0x00a1 }
            if (r7 == 0) goto L_0x0080
            java.lang.StringBuffer r13 = r10.append(r7)     // Catch:{ Exception -> 0x006c, all -> 0x00a1 }
            java.lang.String r14 = "\n"
            r13.append(r14)     // Catch:{ Exception -> 0x006c, all -> 0x00a1 }
            long r14 = com.hdsc.edog.service.TuzhiService.GPRSTOTAL     // Catch:{ Exception -> 0x006c, all -> 0x00a1 }
            byte[] r13 = r7.getBytes()     // Catch:{ Exception -> 0x006c, all -> 0x00a1 }
            int r13 = r13.length     // Catch:{ Exception -> 0x006c, all -> 0x00a1 }
            long r0 = (long) r13     // Catch:{ Exception -> 0x006c, all -> 0x00a1 }
            r16 = r0
            long r14 = r14 + r16
            com.hdsc.edog.service.TuzhiService.GPRSTOTAL = r14     // Catch:{ Exception -> 0x006c, all -> 0x00a1 }
            goto L_0x004e
        L_0x006c:
            r4 = move-exception
            r11 = r12
        L_0x006e:
            r4.printStackTrace()     // Catch:{ all -> 0x00aa }
            r13 = -1
            r6.mnRet = r13     // Catch:{ all -> 0x00aa }
            if (r5 == 0) goto L_0x0079
            r5.disconnect()
        L_0x0079:
            r11 = 0
        L_0x007a:
            r0 = r20
            r0.httpResult(r6)
            return
        L_0x0080:
            r9.close()     // Catch:{ Exception -> 0x006c, all -> 0x00a1 }
            java.lang.String r13 = r10.toString()     // Catch:{ Exception -> 0x006c, all -> 0x00a1 }
            r6.mstrRet = r13     // Catch:{ Exception -> 0x006c, all -> 0x00a1 }
            r13 = 0
            r6.mnRet = r13     // Catch:{ Exception -> 0x006c, all -> 0x00a1 }
            long r14 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x006c, all -> 0x00a1 }
            long r14 = r14 - r2
            r0 = r19
            com.hdsc.edog.entity.LogTag.netTimeLog(r0, r14)     // Catch:{ Exception -> 0x006c, all -> 0x00a1 }
        L_0x0096:
            if (r5 == 0) goto L_0x009b
            r5.disconnect()
        L_0x009b:
            r11 = 0
            goto L_0x007a
        L_0x009d:
            r13 = -1
            r6.mnRet = r13     // Catch:{ Exception -> 0x006c, all -> 0x00a1 }
            goto L_0x0096
        L_0x00a1:
            r13 = move-exception
            r11 = r12
        L_0x00a3:
            if (r5 == 0) goto L_0x00a8
            r5.disconnect()
        L_0x00a8:
            r11 = 0
            throw r13
        L_0x00aa:
            r13 = move-exception
            goto L_0x00a3
        L_0x00ac:
            r4 = move-exception
            goto L_0x006e
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hdsc.edog.net.HttpRequestManager.RequestHttpGet(java.lang.String, com.hdsc.edog.net.HttpReqCallback):void");
    }
}
