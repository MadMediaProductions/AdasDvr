package com.hdsc.edog.utils;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.hdsc.edog.service.TuzhiService;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.opencv.features2d.FeatureDetector;

public class UpDownManager extends BroadcastReceiver {
    static long CHECKING_INTERVAL = 600000;
    public static boolean Enupdata = false;
    static final String UPLOAD_URL = "http://suanda.ilinkn.com:8588/ALogin.aspx";
    public static long mLastUpdateTime;
    public static long mLastUploadTime;
    static UpDownManager sInstance;
    final String TAG = "UpDownManager";
    AlertDialog mAlertDialog;
    Context mContext;
    DataUpdateListener mDataUpdateListener;
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 99:
                    if (UpDownManager.this.mAlertDialog == null) {
                        UpDownManager.this.mAlertDialog = new AlertDialog.Builder(UpDownManager.this.mContext).setIcon(17301514).setTitle("已更新增量电子狗数据").setPositiveButton("确定", (DialogInterface.OnClickListener) null).create();
                        UpDownManager.this.mAlertDialog.getWindow().setType(FeatureDetector.PYRAMID_SIFT);
                    } else {
                        UpDownManager.this.mAlertDialog.setTitle("已更新增量电子狗数据：");
                    }
                    if (!UpDownManager.this.mAlertDialog.isShowing()) {
                        UpDownManager.this.mAlertDialog.show();
                    }
                    sendEmptyMessageDelayed(101, 10000);
                    return;
                case 100:
                    if (UpDownManager.this.mAlertDialog == null) {
                        UpDownManager.this.mAlertDialog = new AlertDialog.Builder(UpDownManager.this.mContext).setIcon(17301514).setTitle("已更新电子狗数据").setPositiveButton("确定", (DialogInterface.OnClickListener) null).create();
                        UpDownManager.this.mAlertDialog.getWindow().setType(FeatureDetector.PYRAMID_SIFT);
                    } else {
                        UpDownManager.this.mAlertDialog.setTitle("已更新电子狗数据：");
                    }
                    if (!UpDownManager.this.mAlertDialog.isShowing()) {
                        UpDownManager.this.mAlertDialog.show();
                    }
                    sendEmptyMessageDelayed(101, 30000);
                    return;
                case 101:
                    if (UpDownManager.this.mAlertDialog.isShowing()) {
                        UpDownManager.this.mAlertDialog.dismiss();
                        return;
                    }
                    return;
                case 102:
                    Log.e("UpDownManager", "msg.what 102 = " + msg.what);
                    if (UpDownManager.this.mAlertDialog == null) {
                        UpDownManager.this.mAlertDialog = new AlertDialog.Builder(UpDownManager.this.mContext).setIcon(17301514).setTitle("等待更新电子狗数据,请不要退出 。。。").setPositiveButton("确定", (DialogInterface.OnClickListener) null).create();
                        UpDownManager.this.mAlertDialog.getWindow().setType(FeatureDetector.PYRAMID_SIFT);
                    } else {
                        UpDownManager.this.mAlertDialog.setTitle("等待更新电子狗数据，请不要退出 。。。");
                    }
                    if (!UpDownManager.this.mAlertDialog.isShowing()) {
                        UpDownManager.this.mAlertDialog.show();
                    }
                    sendEmptyMessageDelayed(101, 10000);
                    return;
                case 103:
                    Log.e("UpDownManager", "msg.what 103 = " + msg.what);
                    String title = (String) msg.obj;
                    if (UpDownManager.this.mAlertDialog == null) {
                        UpDownManager.this.mAlertDialog = new AlertDialog.Builder(UpDownManager.this.mContext).setIcon(17301514).setTitle(title).setPositiveButton("确定", (DialogInterface.OnClickListener) null).create();
                        UpDownManager.this.mAlertDialog.getWindow().setType(FeatureDetector.PYRAMID_SIFT);
                    } else {
                        UpDownManager.this.mAlertDialog.setTitle(title);
                    }
                    if (!UpDownManager.this.mAlertDialog.isShowing()) {
                        UpDownManager.this.mAlertDialog.show();
                    }
                    sendEmptyMessageDelayed(101, 50000);
                    return;
                default:
                    return;
            }
        }
    };
    String mMyDir = null;

    public interface DataUpdateListener {
        void onUpdateFinish(boolean z, boolean z2);
    }

    public static UpDownManager getInstance() {
        return sInstance;
    }

    public void setListener(DataUpdateListener l) {
        this.mDataUpdateListener = l;
    }

    public void AddUpinit(Context ctx, String myDir) {
        sInstance = this;
        this.mContext = ctx;
        this.mMyDir = myDir;
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        this.mContext.registerReceiver(this, filter);
    }

    private void doCheck() {
        new Thread(new Runnable() {
            public void run() {
                Log.e("checkMapData", "checkMapData  AAA");
            }
        }).start();
    }

    public void NETdoCheck() {
        Log.e("copyFile", "copyFile - checkNetwork  AAAA ");
        if (isConnected(this.mContext)) {
            Log.e("copyFile", "copyFile - checkNetwork  BBBB ");
            long curNow = System.currentTimeMillis();
            Log.e("curNow", " mLastUpdateTime =" + String.valueOf(mLastUpdateTime));
            if (curNow - mLastUpdateTime >= CHECKING_INTERVAL || mLastUpdateTime == 0) {
                Log.e("copyFile", "copyFile - checkNetwork  CCCCCC ");
                doCheck();
                mLastUpdateTime = System.currentTimeMillis();
                Log.e("copyFile", "copyFile - checkNetwork  EEEE ");
            }
            if (curNow - mLastUploadTime >= 3600000 || mLastUploadTime == 0) {
                mLastUploadTime = System.currentTimeMillis();
                uploadFile(UPLOAD_URL, "useradd.txt");
            }
        }
    }

    public void BaseDataCheck(boolean BCOPY) {
        if (TuzhiService.Use_Mapver < 1 || TuzhiService.Use_Mapver > 10000 || BCOPY) {
            try {
                if (this.mMyDir != null) {
                    File rootFile = new File(this.mMyDir);
                    if (!rootFile.exists()) {
                        rootFile.mkdirs();
                    }
                    ToolUtils.copyBaseFile(this.mContext, this.mMyDir);
                }
                TuzhiService.gpsInfo.setmapupdata(2);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            Enupdata = true;
        }
    }

    public String getBaseMap() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/JLG/map03apk.bin";
    }

    public String getAddMap() {
        if (this.mMyDir == null) {
            return null;
        }
        String path = this.mMyDir + "/addmap.bin";
        if (!new File(path).exists()) {
            return null;
        }
        return path;
    }

    private void checkMapData() {
        boolean succeed = false;
        try {
            InputStream input = ((HttpURLConnection) new URL("http://www.dzgsj.com/apk/apkver.xml").openConnection()).getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuffer sb = new StringBuffer();
            while (true) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }
                sb.append(line);
            }
            input.close();
            Log.d("UpDownManager", sb.toString());
            SharedPreferences sp = this.mContext.getSharedPreferences("datainfo", 0);
            Document doc = Jsoup.parse(sb.toString());
            Elements dus = doc.select("root").select("downloadUrl");
            Iterator it = dus.iterator();
            while (it.hasNext()) {
                Log.d("UpDownManager", ((Element) it.next()).text());
            }
            Elements mv = doc.select("root").select("mapver");
            if (mv != null && mv.size() == 1) {
                Log.d("UpDownManager", ((Element) mv.get(0)).text());
                int mapver = Integer.valueOf(((Element) mv.get(0)).text()).intValue();
                Log.e("TAG", " mapver =" + String.valueOf(mapver));
                Log.e("TAG", " mapver Use_Mapver =" + String.valueOf(TuzhiService.Use_Mapver));
                if (mapver > TuzhiService.Use_Mapver && TuzhiService.Use_Mapver > 2) {
                    this.mHandler.sendEmptyMessage(102);
                }
            }
            Elements av = doc.select("root").select("addver");
            if (av != null && av.size() == 1) {
                Log.d("UpDownManager", ((Element) av.get(0)).text());
                int addver = Integer.valueOf(((Element) av.get(0)).text()).intValue();
                if (addver == TuzhiService.Use_Addver || addver != 0) {
                }
            }
            Elements nv = doc.select("root").select("newsver");
            if (nv != null && nv.size() == 1) {
                String newsver = ((Element) nv.get(0)).text();
                Log.e("UpDownManager", "newsver=" + newsver);
                if (!sp.getString("newsver", "").equals(newsver)) {
                    Message msg = new Message();
                    msg.what = 103;
                    msg.obj = ((Element) dus.get(2)).text();
                    this.mHandler.sendMessage(msg);
                    sp.edit().putString("newsver", newsver).apply();
                }
            }
            succeed = true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        if (this.mDataUpdateListener != null) {
            this.mDataUpdateListener.onUpdateFinish(succeed, false);
        }
    }

    /* access modifiers changed from: package-private */
    public String getHumanTime(long sendTime) {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(sendTime));
    }

    public void uploadFileData() {
        if (isConnected(this.mContext)) {
            uploadFile(UPLOAD_URL, "useradd.txt");
        }
    }

    public void addUserData(String newData) {
        File upperDir = new File(this.mMyDir);
        if (!upperDir.exists()) {
            upperDir.mkdir();
        }
        File file = new File(this.mMyDir + "/useradd.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream fout = new FileOutputStream(file, true);
            fout.write(newData.getBytes());
            fout.close();
        } catch (FileNotFoundException e2) {
            e2.printStackTrace();
        } catch (IOException e3) {
            e3.printStackTrace();
        }
    }

    /* access modifiers changed from: package-private */
    public void uploadFile(String url, final String fileName) {
        new Thread(new Runnable() {
            public void run() {
                File upperDir = new File(UpDownManager.this.mMyDir);
                if (!upperDir.exists()) {
                    upperDir.mkdir();
                }
                File file = new File(UpDownManager.this.mMyDir + "/" + fileName);
                if (file.exists()) {
                    file.renameTo(new File(UpDownManager.this.mMyDir + "/" + UpDownManager.this.getHumanTime(System.currentTimeMillis()) + Build.SERIAL + ".txt"));
                } else {
                    UpDownManager.this.uploadRetry();
                }
            }
        }).start();
    }

    /* access modifiers changed from: package-private */
    public void uploadRetry() {
        File[] list = new File(this.mMyDir).listFiles();
        if (list != null) {
            for (File file : list) {
            }
        }
    }

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
            if (Enupdata) {
                NETdoCheck();
            }
            Log.e("copyFile", "copyFile - checkNetwork  DDDD ");
        }
    }

    static boolean isConnected(Context context) {
        NetworkInfo info;
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService("connectivity");
            if (connectivity == null || (info = connectivity.getActiveNetworkInfo()) == null || !info.isConnected()) {
                return false;
            }
            return true;
        } catch (Exception e) {
        }
    }
}
