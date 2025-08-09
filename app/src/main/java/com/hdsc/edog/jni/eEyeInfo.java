package com.hdsc.edog.jni;

import android.os.Build;
import com.hdsc.edog.service.TuzhiService;

public class eEyeInfo {
    public int Alm_AlmType;
    public int Alm_Lat;
    public int Alm_Lat_S = 0;
    public int Alm_Lng;
    public int Alm_Lng_S = 0;
    public int Alm_Speed;
    public int Alm_TAType;
    public int Alm_dir;
    public int m_AlmType;
    public int m_Lat;
    public int m_Lat_S = 0;
    public int m_Lng;
    public int m_Lng_S = 0;
    public int m_Speed;
    public int m_TAType;
    public int m_dir;
    public int m_errorRange;
    public String m_siteType;

    public String toString() {
        return "900000," + Integer.toString(this.m_TAType) + "," + Integer.toString(this.m_dir) + "," + Integer.toString(this.m_Lat) + "," + Integer.toString(this.m_Lng) + "," + Integer.toString(this.m_Lat_S) + "," + Integer.toString(this.m_Lng_S) + "," + Integer.toString(this.m_Speed) + "," + Integer.toHexString(this.m_AlmType).toUpperCase() + ",0," + Integer.toString(this.m_errorRange) + "," + this.m_siteType + "," + Build.SERIAL + "," + String.valueOf(TuzhiService.Use_Mapver);
    }
}
