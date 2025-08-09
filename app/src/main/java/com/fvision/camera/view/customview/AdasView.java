package com.fvision.camera.view.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

public class AdasView extends View {
    private Bitmap adasBmp;
    private int centerX;
    private int centerY;

    public AdasView(Context context) {
        super(context);
    }

    public AdasView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdasView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawAdasBmp(canvas);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.centerX = widthMeasureSpec / 2;
        this.centerY = heightMeasureSpec / 2;
    }

    public void setBitmap(Bitmap bitmap) {
        this.adasBmp = bitmap;
        invalidate();
    }

    private void drawAdasBmp(Canvas canvas) {
        if (this.adasBmp != null) {
            drawImg(canvas, new BitmapDrawable(this.adasBmp), (float) (getWidth() / 2), (float) (getHeight() / 2), 1080.0f, 720.0f);
        }
    }

    private void drawImg(Canvas canvas, Drawable img, float x, float y, float width, float height) {
        int left = (int) (x - (width / 2.0f));
        int top = (int) (y - (height / 2.0f));
        img.setBounds(left, top, (int) (((float) left) + width), (int) (((float) top) + height));
        img.draw(canvas);
    }
}
