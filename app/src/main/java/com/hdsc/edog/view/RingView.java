package com.hdsc.edog.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import com.fvision.camera.R;
import com.fvision.camera.ui.MainActivity;

public class RingView extends View {
    Bitmap bigPointerBmp;
    private Context context;
    private int currentSpeed = 0;
    private Paint mPaint;

    public RingView(Context context2, AttributeSet attrs, int defStyleAttr) {
        super(context2, attrs, defStyleAttr);
        init(context2);
    }

    public RingView(Context context2, AttributeSet attrs) {
        super(context2, attrs);
        init(context2);
    }

    public RingView(Context context2) {
        super(context2);
        init(context2);
    }

    /* access modifiers changed from: package-private */
    public void init(Context context2) {
        this.context = context2;
        this.bigPointerBmp = BitmapFactory.decodeResource(getResources(), R.mipmap.edog_ring_arrows);
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.mPaint.setStyle(Paint.Style.STROKE);
    }

    /* access modifiers changed from: protected */
    @SuppressLint({"DrawAllocation"})
    public void onDraw(Canvas canvas) {
        canvas.save();
        float ringWidth = dip2px(this.context, 8.0f);
        float paddingX = MainActivity.xdpi * 0.16830708f;
        float paddingY = MainActivity.ydpi * 0.16830708f;
        if (MainActivity.width == 1800 && MainActivity.height == 1080) {
            paddingX = (MainActivity.xdpi * 0.16830708f) + 32.0f;
            paddingY = (MainActivity.ydpi * 0.16830708f) + 28.0f;
        }
        float cx = (float) (getWidth() / 4);
        float cy = (float) (getHeight() / 2);
        float radiusX = cy - paddingX;
        float radiusY = cy - paddingY;
        new RectF(cx - radiusX, cy - radiusY, cx + radiusX, cy + radiusY);
        this.mPaint.setStrokeWidth(ringWidth);
        this.mPaint.setARGB(255, 255, 0, 0);
        canvas.rotate((float) (this.currentSpeed - 120), cx, cy);
        float fx = MainActivity.xdpi * 0.061023623f;
        float fy = MainActivity.ydpi * 0.061023623f;
        if (MainActivity.width == 1800 && MainActivity.height == 1080) {
            fx = (MainActivity.xdpi * 0.061023623f) + 32.0f;
            fy = (MainActivity.ydpi * 0.061023623f) + 28.0f;
        }
        float rx = cy - fx;
        float ry = cy - fy;
        RectF rect3 = new RectF(cx - rx, (cy - ry) + 15.0f, cx + rx, cy + ry);
        canvas.drawBitmap(this.bigPointerBmp, (Rect) null, rect3, this.mPaint);
        canvas.restore();
        super.onDraw(canvas);
    }

    public void restoreRingView(int currentSpeed2) {
        this.currentSpeed = currentSpeed2;
        postInvalidate();
    }

    public static float px2dip(Context context2, float pxValue) {
        return (pxValue / context2.getResources().getDisplayMetrics().density) + 0.5f;
    }

    public static float dip2px(Context context2, float dpValue) {
        return (dpValue * context2.getResources().getDisplayMetrics().density) + 0.5f;
    }
}
