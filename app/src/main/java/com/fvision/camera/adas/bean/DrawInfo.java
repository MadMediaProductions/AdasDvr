package com.fvision.camera.adas.bean;

import com.adasplus.data.AdasConfig;
import com.adasplus.data.FcwInfo;
import com.adasplus.data.LdwInfo;

public class DrawInfo {
    private AdasConfig config;
    private FcwInfo fcwResults;
    private LdwInfo ldwResults;
    private float speed;

    public LdwInfo getLdwResults() {
        return this.ldwResults;
    }

    public void setLdwResults(LdwInfo ldwResults2) {
        this.ldwResults = ldwResults2;
    }

    public FcwInfo getFcwResults() {
        return this.fcwResults;
    }

    public void setFcwResults(FcwInfo fcwResults2) {
        this.fcwResults = fcwResults2;
    }

    public AdasConfig getConfig() {
        return this.config;
    }

    public void setConfig(AdasConfig config2) {
        this.config = config2;
    }

    public float getSpeed() {
        return this.speed;
    }

    public void setSpeed(float speed2) {
        this.speed = speed2;
    }
}
