package com.fvision.camera.view.iface;

import com.fvision.camera.base.IBaseView;
import com.fvision.camera.bean.AdasResult;

public interface ISettingView extends IBaseView {
    void adasIsplayBack(boolean z);

    void authResult(boolean z, String str);

    void edogIsPlayBack(boolean z);

    void edogResult(boolean z, String str);

    void showResult(AdasResult adasResult);
}
