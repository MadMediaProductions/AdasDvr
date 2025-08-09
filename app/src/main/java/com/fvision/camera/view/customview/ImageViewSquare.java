package com.fvision.camera.view.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ImageViewSquare extends ImageView {
    public ImageViewSquare(Context context) {
        super(context);
    }

    public ImageViewSquare(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, heightMeasureSpec);
    }
}
