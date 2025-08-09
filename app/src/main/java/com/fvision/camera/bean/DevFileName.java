package com.fvision.camera.bean;

public class DevFileName {
    private String cmd;
    private String packageName;
    private String preView;

    public DevFileName(String pre, String cmmd, String pkg) {
        this.preView = pre;
        this.cmd = cmmd;
        this.packageName = pkg;
    }

    public String getPreView() {
        return this.preView;
    }

    public String getCmd() {
        return this.cmd;
    }

    public void setPreView(String preView2) {
        this.preView = preView2;
    }

    public void setCmd(String cmd2) {
        this.cmd = cmd2;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public void setPackageName(String packageName2) {
        this.packageName = packageName2;
    }
}
