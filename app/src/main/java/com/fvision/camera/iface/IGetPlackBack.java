package com.fvision.camera.iface;

import com.fvision.camera.bean.FileBean;
import java.util.List;

public interface IGetPlackBack {
    void onFail(int i, String str);

    void onProgress(float f);

    void onSuccess(List<FileBean> list);
}
