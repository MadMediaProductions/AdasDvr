package com.adasplus.adas;

import android.content.Context;
import android.util.Log;
import com.adasplus.adas.adas.AdasConstants;
import com.adasplus.adas.adas.BuildConfig;
import com.adasplus.adas.adas.net.RequestManager;
import com.adasplus.adas.util.Util;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

public class AdasLicenseEntry {
    public static int licenseCreate(Context context, String license) {
        int result = licenseTest(context, license);
        return result == 0 ? licenseEntry(context, license) : result;
    }

    private static int licenseTest(Context context, String license) {
        Map<String, String> params = new HashMap<>();
        String time = String.valueOf(System.currentTimeMillis());
        params.put("IMEIS", license);
        params.put(AdasConstants.STR_TIMESTAMP, time);
        params.put(AdasConstants.STR_MERCHANTID, BuildConfig.ADAS_VERSION_MERCHANTID);
        params.put(AdasConstants.STR_SIGN, Util.getStrMd5(time + Util.getStrMd5(BuildConfig.ADAS_VERSION_MERCHANTID).toLowerCase()).toLowerCase());
        if (!Util.isNetworkConnected(context)) {
            Log.e("Adas", "Cannot connect network!");
            return -2;
        }
        try {
            if (Integer.valueOf(new JSONObject(RequestManager.getInstance(context).getReponseByPostMethod(AdasConstants.IMEI_TEST_URL, params)).getString("data")).intValue() == -1) {
                return 0;
            }
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
            return -3;
        }
    }

    private static int licenseEntry(Context context, String license) {
        Map<String, String> params = new HashMap<>();
        String time = String.valueOf(System.currentTimeMillis());
        params.put("IMEIS", license);
        params.put(AdasConstants.STR_TIMESTAMP, time);
        params.put(AdasConstants.STR_MERCHANTID, BuildConfig.ADAS_VERSION_MERCHANTID);
        params.put(AdasConstants.STR_SIGN, Util.getStrMd5(time + Util.getStrMd5(BuildConfig.ADAS_VERSION_MERCHANTID).toLowerCase()).toLowerCase());
        if (!Util.isNetworkConnected(context)) {
            Log.e("Adas", "Cannot connect network!");
            return -2;
        }
        try {
            if (new JSONObject(RequestManager.getInstance(context).getReponseByPostMethod(AdasConstants.CREATE_KEY_URL, params)).getInt("resultCode") == 0) {
                return 0;
            }
            return -3;
        } catch (Exception e) {
            e.printStackTrace();
            return -3;
        }
    }
}
