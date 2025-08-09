package com.hdsc.edog.entity;

public class GpsInfo {
    private int altitude = 0;
    private int bearing = 0;
    private int gpsDate = 0;
    private int gpsFixTime = -1;
    private int gpsTimeS = 0;
    private int lat = 0;
    private int lng = 0;
    private int mapupdata = 2;
    private int satelliteCount = 0;
    private int speed = 0;

    public int getLng() {
        return this.lng;
    }

    public void setLng(int lng2) {
        this.lng = lng2;
    }

    public int getLat() {
        return this.lat;
    }

    public void setLat(int lat2) {
        this.lat = lat2;
    }

    public int getAltitude() {
        return this.altitude;
    }

    public void setAltitude(int altitude2) {
        this.altitude = altitude2;
    }

    public int getSpeed() {
        return this.speed;
    }

    public void setSpeed(int speed2) {
        this.speed = speed2;
    }

    public int getBearing() {
        return this.bearing;
    }

    public void setBearing(int bearing2) {
        this.bearing = bearing2;
    }

    public int getSatelliteCount() {
        return this.satelliteCount;
    }

    public void setSatelliteCount(int satelliteCount2) {
        this.satelliteCount = satelliteCount2;
    }

    public int getGpsDate() {
        return this.gpsDate;
    }

    public void setGpsDate(int gpsDate2) {
        this.gpsDate = gpsDate2;
    }

    public int getGpsTimeS() {
        return this.gpsTimeS;
    }

    public void setGpsTimeS(int gpsTimeS2) {
        this.gpsTimeS = gpsTimeS2;
    }

    public int getgpsFixTime() {
        return this.gpsFixTime;
    }

    public void setgpsFixTime(int gpsFixTime2) {
        this.gpsFixTime = gpsFixTime2;
    }

    public int getmapupdata() {
        return this.mapupdata;
    }

    public void setmapupdata(int mapupdata2) {
        this.mapupdata = mapupdata2;
    }
}
