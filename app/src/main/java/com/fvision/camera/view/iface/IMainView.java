package com.fvision.camera.view.iface;

import com.fvision.camera.base.IBaseView;
import java.util.ArrayList;

public interface IMainView extends IBaseView {
    void adasSoundPlay(int i);

    void edogSoundPlay(ArrayList<Integer> arrayList);

    void resrefhView();

    void usbStateChange(String str, int i);
}
