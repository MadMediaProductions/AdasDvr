package com.adasplus.adas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Log;
import com.adasplus.adas.adas.AdasClassLoader;
import com.adasplus.adas.adas.AdasConstants;
import com.adasplus.adas.adas.AdasService;
import com.adasplus.adas.adas.BuildConfig;
import com.adasplus.adas.adas.net.RequestManager;
import com.adasplus.adas.security.Crypto;
import com.adasplus.adas.util.FileUtils;
import com.adasplus.adas.util.LoadUtil;
import com.adasplus.adas.util.LogUtil;
import com.adasplus.adas.util.Util;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Adas {
    private static Handler mHander;
    private String content;
    /* access modifiers changed from: private */
    public String fileDirPath;
    /* access modifiers changed from: private */
    public Context mContext;
    private HandlerThread mHanderThread;
    private String mImei;
    private PrepareListener mPrepareListener;
    private Runnable mRunnable = new Runnable() {
        public void run() {
            SharedPreferences unused = Adas.this.mSharedPreference = Adas.this.mContext.getSharedPreferences(AdasConstants.AdasConfig.ADASCONFIG, 0);
            boolean init = Adas.this.mSharedPreference.getBoolean(AdasConstants.AdasConfig.ADASCONFIG_FIRSTINIT, false);
            String unused2 = Adas.this.fileDirPath = Adas.this.mContext.getFilesDir() + File.separator + AdasConstants.FILE_ADAS + File.separator;
            if (!init) {
                Adas.this.prepareAdasDir();
                Adas.this.prepareOriginFile(AdasConstants.AdasConfig.ADASCONFIG_LOAD_PATH0);
            }
            String prepareVersion = Adas.this.mSharedPreference.getString("version", (String) null);
            if (!TextUtils.isEmpty(prepareVersion) && Integer.valueOf(prepareVersion.split("_")[2].substring(0, 3)).intValue() < Integer.valueOf(AdasConstants.FILE_ORIGIN_ZIP.split("_")[2].substring(0, 3)).intValue()) {
                Adas.this.prepareOriginFile(AdasConstants.AdasConfig.ADASCONFIG_LOAD_PATH0);
            }
            Adas.this.loadAdasFile();
            if (Util.isNetworkConnected(Adas.this.mContext)) {
                Adas.this.mContext.startService(new Intent(Adas.this.mContext, AdasService.class));
            }
        }
    };
    /* access modifiers changed from: private */
    public SharedPreferences mSharedPreference;

    public interface PrepareListener {
        void onPrepare(boolean z);
    }

    public void setPrepareListener(PrepareListener listener) {
        this.mPrepareListener = listener;
    }

    public Adas(Context context) {
        this.mContext = context;
        this.mHanderThread = new HandlerThread("AdasPrepareThread");
        this.mHanderThread.start();
        mHander = new Handler(this.mHanderThread.getLooper());
    }

    public void setAdasInfo(String imei, String content2) {
        this.mImei = imei;
        this.content = content2;
    }

    /* access modifiers changed from: private */
    public void prepareOriginFile(String path) {
        try {
            ZipInputStream zis = new ZipInputStream(this.mContext.getAssets().open(AdasConstants.FILE_ORIGIN_ZIP));
            while (true) {
                ZipEntry entry = zis.getNextEntry();
                if (entry == null) {
                    zis.close();
                    SharedPreferences.Editor editor = this.mSharedPreference.edit();
                    editor.putBoolean(AdasConstants.AdasConfig.ADASCONFIG_FIRSTINIT, true);
                    editor.putString(AdasConstants.AdasConfig.ADASCONFIG_LOAD, path);
                    editor.putString("version", "adas_30.0.5_004");
                    editor.commit();
                    return;
                } else if (!entry.isDirectory()) {
                    FileUtils.copyFile(zis, new File(this.fileDirPath + path + File.separator + entry.getName()));
                } else {
                    new File(this.fileDirPath + path + File.separator + entry.getName()).mkdirs();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.logE(e.getLocalizedMessage());
        }
    }

    /* access modifiers changed from: private */
    public void prepareAdasDir() {
        File file = new File(this.fileDirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        File file2 = new File(this.fileDirPath + AdasConstants.FILE_BACKUP);
        if (!file2.exists()) {
            file2.mkdirs();
        }
        File file3 = new File(this.fileDirPath + AdasConstants.AdasConfig.ADASCONFIG_LOAD_PATH0);
        if (!file3.exists()) {
            file3.mkdirs();
        }
        File file4 = new File(this.fileDirPath + AdasConstants.AdasConfig.ADASCONFIG_LOAD_PATH1);
        if (!file4.exists()) {
            file4.mkdirs();
        }
        File file5 = new File(this.fileDirPath + AdasConstants.AdasConfig.ADASCONFIG_LOAD_PATH0 + File.separator + AdasConstants.FILE_DEX);
        if (!file5.exists()) {
            file5.mkdirs();
        }
        File file6 = new File(this.fileDirPath + AdasConstants.AdasConfig.ADASCONFIG_LOAD_PATH1 + File.separator + AdasConstants.FILE_DEX);
        if (!file6.exists()) {
            file6.mkdirs();
        }
    }

    /* access modifiers changed from: private */
    public void loadAdasFile() {
        prepareImei();
        String loadPath = this.mSharedPreference.getString(AdasConstants.AdasConfig.ADASCONFIG_LOAD, AdasConstants.AdasConfig.ADASCONFIG_LOAD_PATH0);
        HashMap<String, String> array = new HashMap<>();
        String loadPathDst = loadPath.equals(AdasConstants.AdasConfig.ADASCONFIG_LOAD_PATH0) ? AdasConstants.AdasConfig.ADASCONFIG_LOAD_PATH0 : AdasConstants.AdasConfig.ADASCONFIG_LOAD_PATH1;
        try {
            if (!FileUtils.verifyTotalFile(new File(this.fileDirPath + loadPathDst + File.separator + AdasConstants.FILE_MANIFEST), array) || !FileUtils.verifyItemFile(array, this.fileDirPath + loadPathDst + File.separator + AdasConstants.FILE_JAR) || !FileUtils.verifyItemFile(array, this.fileDirPath + loadPathDst + File.separator + Build.CPU_ABI + File.separator + AdasConstants.LIB_SENSOR) || !FileUtils.verifyItemFile(array, this.fileDirPath + loadPathDst + File.separator + Build.CPU_ABI + File.separator + AdasConstants.LIB_ADAS) || !FileUtils.verifyItemFile(array, this.fileDirPath + loadPathDst + File.separator + AdasConstants.FILE_DAT1) || !FileUtils.verifyItemFile(array, this.fileDirPath + loadPathDst + File.separator + AdasConstants.FILE_DAT2)) {
                LogUtil.logE("Verify file fail!");
                FileUtils.clearDir(new File(this.fileDirPath + loadPathDst));
                prepareOriginFile(loadPathDst);
                if (Util.isNetworkConnected(this.mContext)) {
                    uploadErrorVersion(false);
                }
            }
            AdasClassLoader classLoader = new AdasClassLoader(this.fileDirPath + loadPathDst + File.separator + AdasConstants.FILE_JAR, this.fileDirPath + loadPathDst + File.separator + AdasConstants.FILE_DEX, (String) null, this.mContext.getClassLoader());
            LogUtil.logE("cpu:" + Build.CPU_ABI);
            FileUtils.copyFile(this.fileDirPath + loadPathDst + File.separator + AdasConstants.FILE_DAT1, this.fileDirPath + AdasConstants.FILE_DAT1);
            FileUtils.copyFile(this.fileDirPath + loadPathDst + File.separator + AdasConstants.FILE_DAT2, this.fileDirPath + AdasConstants.FILE_DAT2);
            LoadUtil.inject(classLoader, this.mContext);
            System.load(this.fileDirPath + loadPathDst + File.separator + Build.CPU_ABI + File.separator + AdasConstants.LIB_SENSOR);
            System.load(this.fileDirPath + loadPathDst + File.separator + Build.CPU_ABI + File.separator + AdasConstants.LIB_ADAS);
            LogUtil.logE(loadPath + " " + this.mSharedPreference.getString("version", "adas_30.0.5_004"));
            this.mPrepareListener.onPrepare(true);
        } catch (Exception e) {
            e.printStackTrace();
            if (Util.isNetworkConnected(this.mContext)) {
                uploadErrorVersion(false);
            }
        }
    }

    private void uploadErrorVersion(boolean success) {
        String version = this.mSharedPreference.getString("version", "adas_30.0.5_004");
        String uuid = FileUtils.getFileString(new File(this.mContext.getFilesDir(), "/adas/Uuid"));
        if (TextUtils.isEmpty(uuid) || !uuid.contains(",")) {
            LogUtil.logE("Cannot find uuid!");
            return;
        }
        String uuid2 = uuid.split(",")[0];
        Map<String, String> params = new HashMap<>();
        params.put("current_verison", version);
        params.put(AdasConstants.STR_UUID, uuid2);
        params.put(AdasConstants.STR_MERCHAINT_ID, BuildConfig.ADAS_VERSION_MERCHANTID);
        String time = String.valueOf(System.currentTimeMillis());
        params.put(AdasConstants.STR_TIMESTAMP, time);
        params.put(AdasConstants.STR_SIGN, Util.getStrMd5(time + Util.getStrMd5(BuildConfig.ADAS_VERSION_MERCHANTID).toLowerCase()).toLowerCase());
        params.put("updata_flag", success ? String.valueOf(0) : String.valueOf(1));
        RequestManager.getInstance(this.mContext).getReponseByPostMethod(AdasConstants.UPDATE_APP_VERSION_URL, params);
    }

    public void install() {
        if (this.mPrepareListener == null) {
            throw new NullPointerException("Preparelistener must be not empty!");
        } else if (mHander != null) {
            mHander.post(this.mRunnable);
        }
    }

    public void release() {
        if (mHander != null) {
            mHander.removeCallbacks(this.mRunnable);
            mHander = null;
        }
        if (this.mHanderThread != null) {
            if (Build.VERSION.SDK_INT >= 18) {
                this.mHanderThread.quitSafely();
            }
            this.mHanderThread = null;
        }
    }

    private void prepareImei() {
        try {
            FileWriter fos = new FileWriter(new File(this.mContext.getFilesDir(), "adas/imei"));
            fos.write(this.mImei);
            fos.close();
            if (!TextUtils.isEmpty(this.content)) {
                Log.e("Adas", "Cipher:" + this.content);
                String plain = Crypto.decode(this.content.getBytes());
                if (TextUtils.isEmpty(plain) || !plain.contains("_")) {
                    Log.e("Adas", "Cannot get adas info from DVR1!!!!");
                    return;
                }
                String[] param = plain.split("_");
                saveUuid(param[0]);
                saveSecretKey(param[1]);
                return;
            }
            Log.e("Adas", "Cannot get adas info from DVR2!!!!");
        } catch (Exception e) {
            Log.e("Adas", "Cannot get adas info from DVR3!!!!");
            e.printStackTrace();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:0x0029 A[SYNTHETIC, Splitter:B:15:0x0029] */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0035 A[SYNTHETIC, Splitter:B:21:0x0035] */
    /* JADX WARNING: Removed duplicated region for block: B:32:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void saveUuid(String r7) {
        /*
            r6 = this;
            java.io.File r1 = new java.io.File
            android.content.Context r4 = r6.mContext
            java.io.File r4 = r4.getFilesDir()
            java.lang.String r5 = "adas/Uuid"
            r1.<init>(r4, r5)
            r2 = 0
            java.io.FileWriter r3 = new java.io.FileWriter     // Catch:{ Exception -> 0x0023 }
            r3.<init>(r1)     // Catch:{ Exception -> 0x0023 }
            r3.write(r7)     // Catch:{ Exception -> 0x0041, all -> 0x003e }
            if (r3 == 0) goto L_0x001b
            r3.close()     // Catch:{ Exception -> 0x001d }
        L_0x001b:
            r2 = r3
        L_0x001c:
            return
        L_0x001d:
            r0 = move-exception
            r0.printStackTrace()
            r2 = r3
            goto L_0x001c
        L_0x0023:
            r0 = move-exception
        L_0x0024:
            r0.printStackTrace()     // Catch:{ all -> 0x0032 }
            if (r2 == 0) goto L_0x001c
            r2.close()     // Catch:{ Exception -> 0x002d }
            goto L_0x001c
        L_0x002d:
            r0 = move-exception
            r0.printStackTrace()
            goto L_0x001c
        L_0x0032:
            r4 = move-exception
        L_0x0033:
            if (r2 == 0) goto L_0x0038
            r2.close()     // Catch:{ Exception -> 0x0039 }
        L_0x0038:
            throw r4
        L_0x0039:
            r0 = move-exception
            r0.printStackTrace()
            goto L_0x0038
        L_0x003e:
            r4 = move-exception
            r2 = r3
            goto L_0x0033
        L_0x0041:
            r0 = move-exception
            r2 = r3
            goto L_0x0024
        */
        throw new UnsupportedOperationException("Method not decompiled: com.adasplus.adas.Adas.saveUuid(java.lang.String):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:0x0029 A[SYNTHETIC, Splitter:B:15:0x0029] */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0035 A[SYNTHETIC, Splitter:B:21:0x0035] */
    /* JADX WARNING: Removed duplicated region for block: B:32:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void saveSecretKey(String r7) {
        /*
            r6 = this;
            java.io.File r1 = new java.io.File
            android.content.Context r4 = r6.mContext
            java.io.File r4 = r4.getFilesDir()
            java.lang.String r5 = "adas/SecretKey"
            r1.<init>(r4, r5)
            r2 = 0
            java.io.FileWriter r3 = new java.io.FileWriter     // Catch:{ Exception -> 0x0023 }
            r3.<init>(r1)     // Catch:{ Exception -> 0x0023 }
            r3.write(r7)     // Catch:{ Exception -> 0x0041, all -> 0x003e }
            if (r3 == 0) goto L_0x001b
            r3.close()     // Catch:{ Exception -> 0x001d }
        L_0x001b:
            r2 = r3
        L_0x001c:
            return
        L_0x001d:
            r0 = move-exception
            r0.printStackTrace()
            r2 = r3
            goto L_0x001c
        L_0x0023:
            r0 = move-exception
        L_0x0024:
            r0.printStackTrace()     // Catch:{ all -> 0x0032 }
            if (r2 == 0) goto L_0x001c
            r2.close()     // Catch:{ Exception -> 0x002d }
            goto L_0x001c
        L_0x002d:
            r0 = move-exception
            r0.printStackTrace()
            goto L_0x001c
        L_0x0032:
            r4 = move-exception
        L_0x0033:
            if (r2 == 0) goto L_0x0038
            r2.close()     // Catch:{ Exception -> 0x0039 }
        L_0x0038:
            throw r4
        L_0x0039:
            r0 = move-exception
            r0.printStackTrace()
            goto L_0x0038
        L_0x003e:
            r4 = move-exception
            r2 = r3
            goto L_0x0033
        L_0x0041:
            r0 = move-exception
            r2 = r3
            goto L_0x0024
        */
        throw new UnsupportedOperationException("Method not decompiled: com.adasplus.adas.Adas.saveSecretKey(java.lang.String):void");
    }

    public String getAdasInfo() {
        StringBuffer sb = new StringBuffer();
        String uuid = readUuid();
        String secretkey = readSecretKey();
        try {
            if (TextUtils.isEmpty(uuid) || TextUtils.isEmpty(secretkey)) {
                Log.e("Adas", "File error!");
                return null;
            }
            sb.append(uuid).append("_").append(secretkey);
            return Crypto.encode(sb.toString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:29:0x004c A[SYNTHETIC, Splitter:B:29:0x004c] */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0051 A[Catch:{ Exception -> 0x0055 }] */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x005d A[SYNTHETIC, Splitter:B:37:0x005d] */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x0062 A[Catch:{ Exception -> 0x0066 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private String readUuid() {
        /*
            r9 = this;
            r4 = 0
            r0 = 0
            java.io.File r3 = new java.io.File     // Catch:{ Exception -> 0x0046 }
            android.content.Context r7 = r9.mContext     // Catch:{ Exception -> 0x0046 }
            java.io.File r7 = r7.getFilesDir()     // Catch:{ Exception -> 0x0046 }
            java.lang.String r8 = "adas/Uuid"
            r3.<init>(r7, r8)     // Catch:{ Exception -> 0x0046 }
            java.io.FileReader r5 = new java.io.FileReader     // Catch:{ Exception -> 0x0046 }
            r5.<init>(r3)     // Catch:{ Exception -> 0x0046 }
            java.io.BufferedReader r1 = new java.io.BufferedReader     // Catch:{ Exception -> 0x0072, all -> 0x006b }
            r1.<init>(r5)     // Catch:{ Exception -> 0x0072, all -> 0x006b }
            java.lang.String r6 = r1.readLine()     // Catch:{ Exception -> 0x0075, all -> 0x006e }
            if (r6 == 0) goto L_0x0031
            if (r5 == 0) goto L_0x0024
            r5.close()     // Catch:{ Exception -> 0x002c }
        L_0x0024:
            if (r1 == 0) goto L_0x0029
            r1.close()     // Catch:{ Exception -> 0x002c }
        L_0x0029:
            r0 = r1
            r4 = r5
        L_0x002b:
            return r6
        L_0x002c:
            r2 = move-exception
            r2.printStackTrace()
            goto L_0x0029
        L_0x0031:
            if (r5 == 0) goto L_0x0036
            r5.close()     // Catch:{ Exception -> 0x003f }
        L_0x0036:
            if (r1 == 0) goto L_0x003b
            r1.close()     // Catch:{ Exception -> 0x003f }
        L_0x003b:
            r0 = r1
            r4 = r5
        L_0x003d:
            r6 = 0
            goto L_0x002b
        L_0x003f:
            r2 = move-exception
            r2.printStackTrace()
            r0 = r1
            r4 = r5
            goto L_0x003d
        L_0x0046:
            r2 = move-exception
        L_0x0047:
            r2.printStackTrace()     // Catch:{ all -> 0x005a }
            if (r4 == 0) goto L_0x004f
            r4.close()     // Catch:{ Exception -> 0x0055 }
        L_0x004f:
            if (r0 == 0) goto L_0x003d
            r0.close()     // Catch:{ Exception -> 0x0055 }
            goto L_0x003d
        L_0x0055:
            r2 = move-exception
            r2.printStackTrace()
            goto L_0x003d
        L_0x005a:
            r7 = move-exception
        L_0x005b:
            if (r4 == 0) goto L_0x0060
            r4.close()     // Catch:{ Exception -> 0x0066 }
        L_0x0060:
            if (r0 == 0) goto L_0x0065
            r0.close()     // Catch:{ Exception -> 0x0066 }
        L_0x0065:
            throw r7
        L_0x0066:
            r2 = move-exception
            r2.printStackTrace()
            goto L_0x0065
        L_0x006b:
            r7 = move-exception
            r4 = r5
            goto L_0x005b
        L_0x006e:
            r7 = move-exception
            r0 = r1
            r4 = r5
            goto L_0x005b
        L_0x0072:
            r2 = move-exception
            r4 = r5
            goto L_0x0047
        L_0x0075:
            r2 = move-exception
            r0 = r1
            r4 = r5
            goto L_0x0047
        */
        throw new UnsupportedOperationException("Method not decompiled: com.adasplus.adas.Adas.readUuid():java.lang.String");
    }

    /* JADX WARNING: Removed duplicated region for block: B:29:0x004c A[SYNTHETIC, Splitter:B:29:0x004c] */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0051 A[Catch:{ Exception -> 0x0055 }] */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x005d A[SYNTHETIC, Splitter:B:37:0x005d] */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x0062 A[Catch:{ Exception -> 0x0066 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private String readSecretKey() {
        /*
            r9 = this;
            r4 = 0
            r0 = 0
            java.io.File r3 = new java.io.File     // Catch:{ Exception -> 0x0046 }
            android.content.Context r7 = r9.mContext     // Catch:{ Exception -> 0x0046 }
            java.io.File r7 = r7.getFilesDir()     // Catch:{ Exception -> 0x0046 }
            java.lang.String r8 = "adas/SecretKey"
            r3.<init>(r7, r8)     // Catch:{ Exception -> 0x0046 }
            java.io.FileReader r5 = new java.io.FileReader     // Catch:{ Exception -> 0x0046 }
            r5.<init>(r3)     // Catch:{ Exception -> 0x0046 }
            java.io.BufferedReader r1 = new java.io.BufferedReader     // Catch:{ Exception -> 0x0072, all -> 0x006b }
            r1.<init>(r5)     // Catch:{ Exception -> 0x0072, all -> 0x006b }
            java.lang.String r6 = r1.readLine()     // Catch:{ Exception -> 0x0075, all -> 0x006e }
            if (r6 == 0) goto L_0x0031
            if (r5 == 0) goto L_0x0024
            r5.close()     // Catch:{ Exception -> 0x002c }
        L_0x0024:
            if (r1 == 0) goto L_0x0029
            r1.close()     // Catch:{ Exception -> 0x002c }
        L_0x0029:
            r0 = r1
            r4 = r5
        L_0x002b:
            return r6
        L_0x002c:
            r2 = move-exception
            r2.printStackTrace()
            goto L_0x0029
        L_0x0031:
            if (r5 == 0) goto L_0x0036
            r5.close()     // Catch:{ Exception -> 0x003f }
        L_0x0036:
            if (r1 == 0) goto L_0x003b
            r1.close()     // Catch:{ Exception -> 0x003f }
        L_0x003b:
            r0 = r1
            r4 = r5
        L_0x003d:
            r6 = 0
            goto L_0x002b
        L_0x003f:
            r2 = move-exception
            r2.printStackTrace()
            r0 = r1
            r4 = r5
            goto L_0x003d
        L_0x0046:
            r2 = move-exception
        L_0x0047:
            r2.printStackTrace()     // Catch:{ all -> 0x005a }
            if (r4 == 0) goto L_0x004f
            r4.close()     // Catch:{ Exception -> 0x0055 }
        L_0x004f:
            if (r0 == 0) goto L_0x003d
            r0.close()     // Catch:{ Exception -> 0x0055 }
            goto L_0x003d
        L_0x0055:
            r2 = move-exception
            r2.printStackTrace()
            goto L_0x003d
        L_0x005a:
            r7 = move-exception
        L_0x005b:
            if (r4 == 0) goto L_0x0060
            r4.close()     // Catch:{ Exception -> 0x0066 }
        L_0x0060:
            if (r0 == 0) goto L_0x0065
            r0.close()     // Catch:{ Exception -> 0x0066 }
        L_0x0065:
            throw r7
        L_0x0066:
            r2 = move-exception
            r2.printStackTrace()
            goto L_0x0065
        L_0x006b:
            r7 = move-exception
            r4 = r5
            goto L_0x005b
        L_0x006e:
            r7 = move-exception
            r0 = r1
            r4 = r5
            goto L_0x005b
        L_0x0072:
            r2 = move-exception
            r4 = r5
            goto L_0x0047
        L_0x0075:
            r2 = move-exception
            r0 = r1
            r4 = r5
            goto L_0x0047
        */
        throw new UnsupportedOperationException("Method not decompiled: com.adasplus.adas.Adas.readSecretKey():java.lang.String");
    }
}
