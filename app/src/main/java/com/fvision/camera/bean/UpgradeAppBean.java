package com.fvision.camera.bean;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class UpgradeAppBean implements Serializable {
    @SerializedName("app_desc")
    private String app_desc;
    @SerializedName("create_at")
    private int create_at;
    @SerializedName("name")
    private String name;
    @SerializedName("packagename")
    private String packagename;
    @SerializedName("path")
    private String path;
    @SerializedName("size")
    private int size;
    @SerializedName("version")
    private String version;
    @SerializedName("version_code")
    private int version_code;

    public int getVersion_code() {
        return this.version_code;
    }

    public String getName() {
        return this.name;
    }

    public String getPackagename() {
        return this.packagename;
    }

    public String getVersion() {
        return this.version;
    }

    public String getDesc() {
        return this.app_desc;
    }

    public int getSize() {
        return this.size;
    }

    public int getCreate_at() {
        return this.create_at;
    }

    public String getPath() {
        return this.path;
    }

    public void setVersion_code(int version_code2) {
        this.version_code = version_code2;
    }

    public void setName(String name2) {
        this.name = name2;
    }

    public void setPackagename(String packagename2) {
        this.packagename = packagename2;
    }

    public void setVersion(String version2) {
        this.version = version2;
    }

    public void setDesc(String desc) {
        this.app_desc = desc;
    }

    public void setSize(int size2) {
        this.size = size2;
    }

    public void setCreate_at(int create_at2) {
        this.create_at = create_at2;
    }

    public void setPath(String path2) {
        this.path = path2;
    }
}
