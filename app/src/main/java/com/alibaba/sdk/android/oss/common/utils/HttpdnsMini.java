package com.alibaba.sdk.android.oss.common.utils;

import com.alibaba.sdk.android.oss.common.OSSLog;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.JSONArray;
import org.json.JSONObject;

public class HttpdnsMini {
    private static final int EMPTY_RESULT_HOST_TTL = 30;
    private static final int MAX_HOLD_HOST_NUM = 100;
    private static final int MAX_THREAD_NUM = 5;
    private static final int RESOLVE_TIMEOUT_IN_SEC = 10;
    private static final String SERVER_HOST = "httpdns.aliyuncs.com";
    private static final String SERVER_IP = "140.205.143.143";
    private static final String TAG = "HttpDnsMini";
    private static final int THRESHOLD_DEGRADE_HOST = 5;
    /* access modifiers changed from: private */
    public static AtomicInteger globalNetworkError = new AtomicInteger(0);
    private static HttpdnsMini instance = new HttpdnsMini();
    /* access modifiers changed from: private */
    public ConcurrentMap<String, HostObject> hostManager = new ConcurrentHashMap();
    private ExecutorService pool = Executors.newFixedThreadPool(5);

    class HostObject {
        private String hostName;
        private String ip;
        private long queryTime;
        private long ttl;

        HostObject() {
        }

        public String toString() {
            return "HostObject [hostName=" + this.hostName + ", ip=" + this.ip + ", ttl=" + this.ttl + ", queryTime=" + this.queryTime + "]";
        }

        public boolean isExpired() {
            return getQueryTime() + this.ttl < System.currentTimeMillis() / 1000;
        }

        public boolean isStillAvailable() {
            return (getQueryTime() + this.ttl) + 600 > System.currentTimeMillis() / 1000;
        }

        public String getIp() {
            return this.ip;
        }

        public void setIp(String ip2) {
            this.ip = ip2;
        }

        public void setHostName(String hostName2) {
            this.hostName = hostName2;
        }

        public String getHostName() {
            return this.hostName;
        }

        public long getTtl() {
            return this.ttl;
        }

        public void setTtl(long ttl2) {
            this.ttl = ttl2;
        }

        public long getQueryTime() {
            return this.queryTime;
        }

        public void setQueryTime(long queryTime2) {
            this.queryTime = queryTime2;
        }
    }

    class QueryHostTask implements Callable<String> {
        private boolean hasRetryed = false;
        private String hostName;

        public QueryHostTask(String hostToQuery) {
            this.hostName = hostToQuery;
        }

        public String call() {
            String ip;
            String chooseServerAddress = HttpdnsMini.SERVER_IP;
            if (HttpdnsMini.globalNetworkError.get() > 5) {
                chooseServerAddress = HttpdnsMini.SERVER_HOST;
            }
            String resolveUrl = "http://" + chooseServerAddress + "/d?host=" + this.hostName;
            OSSLog.logD("[httpdnsmini] - buildUrl: " + resolveUrl);
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(resolveUrl).openConnection();
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                if (conn.getResponseCode() != 200) {
                    OSSLog.logE("[httpdnsmini] - responseCodeNot 200, but: " + conn.getResponseCode());
                } else {
                    HttpdnsMini.globalNetworkError.decrementAndGet();
                    BufferedReader streamReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    StringBuilder sb = new StringBuilder();
                    while (true) {
                        String line = streamReader.readLine();
                        if (line == null) {
                            break;
                        }
                        sb.append(line);
                    }
                    JSONObject json = new JSONObject(sb.toString());
                    String host = json.getString("host");
                    long ttl = json.getLong("ttl");
                    JSONArray ips = json.getJSONArray("ips");
                    if (host != null) {
                        if (ttl == 0) {
                            ttl = 30;
                        }
                        HostObject hostObject = new HostObject();
                        if (ips == null) {
                            ip = null;
                        } else {
                            ip = ips.getString(0);
                        }
                        OSSLog.logD("[httpdnsmini] - resolve host:" + host + " ip:" + ip + " ttl:" + ttl);
                        hostObject.setHostName(host);
                        hostObject.setTtl(ttl);
                        hostObject.setIp(ip);
                        hostObject.setQueryTime(System.currentTimeMillis() / 1000);
                        if (HttpdnsMini.this.hostManager.size() >= 100) {
                            return ip;
                        }
                        HttpdnsMini.this.hostManager.put(this.hostName, hostObject);
                        return ip;
                    }
                }
            } catch (Exception e) {
                HttpdnsMini.globalNetworkError.incrementAndGet();
                if (OSSLog.isEnableLog()) {
                    e.printStackTrace();
                }
            }
            if (this.hasRetryed) {
                return null;
            }
            this.hasRetryed = true;
            return call();
        }
    }

    private HttpdnsMini() {
    }

    public static HttpdnsMini getInstance() {
        return instance;
    }

    public String getIpByHost(String hostName) {
        HostObject host = (HostObject) this.hostManager.get(hostName);
        if (host != null && !host.isExpired()) {
            return host.getIp();
        }
        OSSLog.logD("[httpdnsmini] - refresh host: " + hostName);
        try {
            return this.pool.submit(new QueryHostTask(hostName)).get();
        } catch (Exception e) {
            if (OSSLog.isEnableLog()) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public String getIpByHostAsync(String hostName) {
        HostObject host = (HostObject) this.hostManager.get(hostName);
        if (host == null || host.isExpired()) {
            OSSLog.logD("[httpdnsmini] - refresh host: " + hostName);
            this.pool.submit(new QueryHostTask(hostName));
        }
        if (host == null || !host.isStillAvailable()) {
            return null;
        }
        return host.getIp();
    }
}
