package com.fvision.camera.adas.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.adasplus.data.AdasConfig;
import com.adasplus.data.FcwInfo;
import com.adasplus.data.LdwInfo;
import com.adasplus.data.RectInt;
import com.fvision.camera.R;
import com.fvision.camera.adas.bean.DrawInfo;

public class SVDrawRectangle1 extends SurfaceView implements SurfaceHolder.Callback {
    public static final int MSG_DRAW = 1;
    public static final int MSG_SETCHECK = 2;
    private static volatile boolean mIsDraw = true;
    private static volatile Rect mLandRect;
    private static volatile Rect mPortRect;
    /* access modifiers changed from: private */
    public static volatile float sConfigX;
    /* access modifiers changed from: private */
    public static volatile float sConfigY;
    /* access modifiers changed from: private */
    public float countX = -1.0f;
    /* access modifiers changed from: private */
    public float countY = -1.0f;
    private float[] dst;
    private boolean isLand = false;
    private boolean isPort = false;
    private boolean lock = false;
    private Paint mBitmapPaint;
    private Bitmap mBlueCarBitmap;
    private int mBtnBottom;
    private int mBtnLeft;
    private Paint mBtnPaint;
    private int mBtnRight;
    private int mBtnTop;
    private HandlerThread mDrawThread;
    private DrawHandler mHandler;
    private int mHeight;
    private int mLdwCount;
    private boolean mLdwFlag;
    private Bitmap mLine;
    /* access modifiers changed from: private */
    public IAdasConfigListener mListener;
    private final Object mLock = new Object();
    private Path mPath;
    private Matrix mPocilyMatrix;
    private RectF mRectF;
    private Bitmap mRedCarBitmap;
    private int mScanIndex;
    private float mStartX;
    private float mStartY;
    private Rect mTextRect;
    private int mWidth;
    private int m_stg_cnt = 0;
    private float maxBottom;
    private float maxLeft;
    private float maxRight;
    private float maxTop;
    private float offX;
    private float offY;
    /* access modifiers changed from: private */
    public int offsetX = 80;
    /* access modifiers changed from: private */
    public int offsetY = 45;
    private Paint p;
    private Paint p_text;
    private SurfaceHolder sh = getHolder();
    private float[] src;
    /* access modifiers changed from: private */
    public float taupaintX;
    /* access modifiers changed from: private */
    public float taupaintY;
    private int xCenter;

    public interface IAdasConfigListener {
        void setAdasConfigXY(float f, float f2);
    }

    public SVDrawRectangle1(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.sh.addCallback(this);
    }

    public void surfaceChanged(SurfaceHolder arg0, int arg1, int w, int h) {
        this.mWidth = w;
        this.mHeight = h;
        this.taupaintX = ((float) w) / 800.0f;
        this.taupaintY = ((float) h) / 450.0f;
        this.xCenter = w / 2;
        this.mBtnLeft = (w / 2) - (h / 10);
        this.mBtnTop = 10;
        this.mBtnRight = (w / 2) + (h / 10);
        this.mBtnBottom = h / 10;
        this.maxLeft = (float) (w / 3);
        this.maxTop = (float) ((h / 2) - (h / 8));
        this.maxRight = (float) (w - (w / 3));
        this.maxBottom = (float) ((h / 2) + (h / 6));
        this.offX = ((float) (this.offsetX + 640)) * this.taupaintX;
        this.offY = ((float) (this.offsetY + 360)) * this.taupaintY;
    }

    public void surfaceCreated(SurfaceHolder arg0) {
        this.sh = getHolder();
        this.sh.addCallback(this);
        mIsDraw = true;
        this.p = new Paint();
        this.p_text = new Paint();
        this.p_text.setAntiAlias(true);
        this.p_text.setStrokeWidth(3.0f);
        this.p_text.setTextAlign(Paint.Align.CENTER);
        this.mTextRect = new Rect();
        this.mBtnPaint = new Paint();
        this.mBtnPaint.setStyle(Paint.Style.FILL);
        this.mBtnPaint.setColor(-16711936);
        mLandRect = new Rect();
        mPortRect = new Rect();
        this.mPath = new Path();
        this.mRectF = new RectF();
        this.mBitmapPaint = new Paint();
        this.mBitmapPaint.setAntiAlias(true);
        this.mBitmapPaint.setFilterBitmap(true);
        this.mBitmapPaint.setDither(true);
        this.mDrawThread = new HandlerThread("SVDrawThread");
        this.mDrawThread.start();
        this.mHandler = new DrawHandler(this.mDrawThread.getLooper());
        this.mLdwCount = 0;
        this.mScanIndex = 0;
        if (this.mLine == null) {
            this.mLine = BitmapFactory.decodeResource(getResources(), R.drawable.ldw_line);
        }
        if (this.mBlueCarBitmap == null) {
            this.mBlueCarBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.blue_car);
        }
        if (this.mRedCarBitmap == null) {
            this.mRedCarBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.red_car);
        }
        this.mLdwFlag = false;
        this.mPocilyMatrix = new Matrix();
    }

    public void surfaceDestroyed(SurfaceHolder arg0) {
        if (this.mHandler != null) {
            this.mHandler.removeMessages(1);
            this.mHandler.removeMessages(2);
            if (Build.VERSION.SDK_INT >= 18) {
                this.mDrawThread.quitSafely();
            }
            this.mDrawThread = null;
            this.mHandler = null;
        }
        mIsDraw = false;
        if (this.mLine != null && !this.mLine.isRecycled()) {
            this.mLine.recycle();
            this.mLine = null;
        }
        if (this.mBlueCarBitmap != null && !this.mBlueCarBitmap.isRecycled()) {
            this.mBlueCarBitmap.recycle();
            this.mBlueCarBitmap = null;
        }
        if (this.mRedCarBitmap != null && !this.mRedCarBitmap.isRecycled()) {
            this.mRedCarBitmap.recycle();
            this.mRedCarBitmap = null;
        }
    }

    /* access modifiers changed from: private */
    public void setCheckpoint() {
        Canvas canvas = this.sh.lockCanvas();
        if (canvas != null) {
            synchronized (this.sh) {
                this.p.setStrokeWidth(2.0f);
                canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                Paint.FontMetrics fontMetrics = this.p_text.getFontMetrics();
                int centerY = (int) ((((float) this.mTextRect.centerY()) - (fontMetrics.top / 2.0f)) - (fontMetrics.bottom / 2.0f));
                this.p_text.setColor(-16711936);
                this.p.setColor(-16711936);
                this.p_text.setTextSize(60.0f);
                if (sConfigX == this.maxLeft || sConfigX == this.maxRight) {
                    this.p.setColor(Color.argb(255, 252, 25, 25));
                } else {
                    this.p.setColor(-16711936);
                }
                canvas.drawText("请对齐车头中心", sConfigX, (float) (this.mHeight - 30), this.p_text);
                canvas.drawLine(sConfigX, 0.0f, sConfigX, (float) this.mHeight, this.p);
                mPortRect.set(((int) sConfigX) - 30, 0, ((int) sConfigX) + 30, this.mHeight);
                if (sConfigY == this.maxTop || sConfigY == this.maxBottom) {
                    this.p.setColor(Color.argb(255, 252, 25, 25));
                } else {
                    this.p.setColor(-16711936);
                }
                canvas.drawLine(0.0f, sConfigY, (float) this.mWidth, sConfigY, this.p);
                canvas.drawText("请对齐地平线", (float) (this.mWidth / 5), sConfigY - 10.0f, this.p_text);
                mLandRect.set(0, ((int) sConfigY) - 30, this.mWidth, ((int) sConfigY) + 30);
            }
            this.sh.unlockCanvasAndPost(canvas);
        }
    }

    public Handler getHandler() {
        return this.mHandler;
    }

    public void processResult(DrawInfo drawInfo) {
        if (this.mHandler != null) {
            if (this.countX == -1.0f) {
                this.countX = drawInfo.getConfig().getX();
                this.countY = drawInfo.getConfig().getY();
            }
            Message message = this.mHandler.obtainMessage();
            message.what = 1;
            message.obj = drawInfo;
            this.mHandler.sendMessage(message);
        }
    }

    public void drawCheckLine() {
        if (this.mHandler != null) {
            Message message = this.mHandler.obtainMessage();
            message.what = 2;
            this.mHandler.sendMessage(message);
        }
    }

    public void setCheckpointXY() {
        if (this.mHandler != null) {
            Message message = this.mHandler.obtainMessage();
            message.what = 153;
            this.mHandler.sendMessage(message);
        }
    }

    public boolean isDraw() {
        return mIsDraw;
    }

    public void setIsDraw(boolean draw) {
        mIsDraw = draw;
    }

    /* access modifiers changed from: private */
    public void drawResult(DrawInfo drawInfo) {
        int y_end;
        LdwInfo ldwResults = drawInfo.getLdwResults();
        FcwInfo fcwResults = drawInfo.getFcwResults();
        AdasConfig config = drawInfo.getConfig();
        if (((int) this.countX) != ((int) config.getX())) {
            this.countX = -1.0f;
            return;
        }
        Canvas canvas = this.sh.lockCanvas();
        if (canvas != null) {
            synchronized (this.sh) {
                canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                this.p.setAntiAlias(true);
                this.p.setColor(-16711936);
                this.p.setStyle(Paint.Style.STROKE);
                this.p_text.setTextSize(25.0f);
                this.p_text.setStrokeWidth(1.0f);
                this.p_text.setTextAlign(Paint.Align.CENTER);
                this.p_text.setAntiAlias(true);
                this.p.setStrokeWidth(2.0f);
                Paint.FontMetrics fontMetrics = this.p_text.getFontMetrics();
                int centerY = (int) ((((float) this.mTextRect.centerY()) - (fontMetrics.top / 2.0f)) - (fontMetrics.bottom / 2.0f));
                config.setY((config.getY() + ((float) this.offsetY)) * this.taupaintY);
                config.setX((config.getX() + ((float) this.offsetX)) * this.taupaintX);
                sConfigX = config.getX();
                sConfigY = config.getY();
                if (config.getIsCalibCredible() == 0) {
                    this.p.setStrokeWidth(5.0f);
                    canvas.drawLine(config.getX() - 150.0f, config.getY(), config.getX() + 150.0f, config.getY(), this.p);
                    canvas.drawLine(config.getX(), config.getY() - 8.0f, config.getX(), config.getY() + 8.0f, this.p);
                    canvas.drawLine(config.getX() - 150.0f, config.getY() - 3.0f, config.getX() - 150.0f, config.getY() + 3.0f, this.p);
                    canvas.drawLine(config.getX() + 150.0f, config.getY() - 3.0f, config.getX() + 150.0f, config.getY() + 3.0f, this.p);
                    this.mTextRect.set(this.mBtnLeft, this.mBtnTop, this.mBtnRight, this.mBtnBottom);
                    this.p.setColor(-16711936);
                    canvas.drawLines(new float[]{config.getX() - 75.0f, config.getY() - 75.0f, config.getX() - 55.0f, config.getY() - 75.0f, config.getX() - 75.0f, config.getY() - 75.0f, config.getX() - 75.0f, config.getY() - 55.0f, config.getX() + 75.0f, config.getY() - 75.0f, config.getX() + 55.0f, config.getY() - 75.0f, config.getX() + 75.0f, config.getY() - 75.0f, config.getX() + 75.0f, config.getY() - 55.0f, config.getX() - 75.0f, config.getY() + 75.0f, config.getX() - 55.0f, config.getY() + 75.0f, config.getX() - 75.0f, config.getY() + 75.0f, config.getX() - 75.0f, config.getY() + 55.0f, config.getX() + 75.0f, config.getY() + 75.0f, config.getX() + 55.0f, config.getY() + 75.0f, config.getX() + 75.0f, config.getY() + 75.0f, config.getX() + 75.0f, config.getY() + 55.0f}, this.p);
                    this.p.setStrokeWidth(2.0f);
                    if (this.mScanIndex < 150) {
                        this.mScanIndex += 5;
                    } else {
                        this.mScanIndex = 0;
                    }
                    canvas.drawLine(config.getX() - 75.0f, (config.getY() - 75.0f) + ((float) this.mScanIndex), config.getX() + 75.0f, (config.getY() - 75.0f) + ((float) this.mScanIndex), this.p);
                } else {
                    this.mScanIndex = 0;
                }
                this.p.setStrokeWidth(3.0f);
                if (ldwResults != null) {
                    if (ldwResults.getLeft().getIsCredible() == 1 || ldwResults.getRight().getIsCredible() == 1) {
                        Point[] pointsLeft = ldwResults.getLeft().getPoints();
                        Point[] pointsRight = ldwResults.getRight().getPoints();
                        int y_start = pointsLeft[0].y < pointsRight[0].y ? pointsLeft[0].y : pointsRight[0].y;
                        if (pointsLeft[1].y > pointsRight[1].y) {
                            y_end = pointsLeft[1].y;
                        } else {
                            y_end = pointsRight[1].y;
                        }
                        int i = y_end - y_start;
                        float ltagc = 0.0f;
                        if (pointsLeft[1].x - pointsLeft[0].x != 0) {
                            ltagc = (float) (((pointsLeft[1].x * pointsLeft[0].y) - (pointsLeft[0].x * pointsLeft[1].y)) / (pointsLeft[1].x - pointsLeft[0].x));
                        }
                        float ltaga = 1.0f;
                        if (pointsLeft[0].x != 0) {
                            ltaga = (((float) pointsLeft[0].y) - ltagc) / ((float) pointsLeft[0].x);
                        }
                        int ltagx = pointsLeft[1].x;
                        if (ltaga != 0.0f) {
                            ltagx = (int) ((((float) y_start) - ltagc) / ltaga);
                        }
                        float rtagc = 0.0f;
                        if (pointsRight[1].x - pointsRight[0].x != 0) {
                            rtagc = (float) (((pointsRight[1].x * pointsRight[0].y) - (pointsRight[0].x * pointsRight[1].y)) / (pointsRight[1].x - pointsRight[0].x));
                        }
                        float rtaga = 1.0f;
                        if (pointsRight[0].x != 0) {
                            rtaga = (((float) pointsRight[0].y) - rtagc) / ((float) pointsRight[0].x);
                        }
                        int rtagx = pointsRight[1].x;
                        if (rtaga != 0.0f) {
                            rtagx = (int) ((((float) y_start) - rtagc) / rtaga);
                        }
                        int leftDownX = (int) (((float) (pointsLeft[1].x + this.offsetX)) * this.taupaintX);
                        int leftDownY = (int) (((float) (pointsLeft[1].y + this.offsetY)) * this.taupaintY);
                        int leftUpX = (int) (((float) (this.offsetX + ltagx)) * this.taupaintX);
                        int leftUpY = (int) (((float) (this.offsetY + y_start)) * this.taupaintY);
                        int rightDownX = (int) (((float) (pointsRight[1].x + this.offsetX)) * this.taupaintX);
                        int rightDownY = (int) (((float) (pointsRight[1].y + this.offsetY)) * this.taupaintY);
                        int rightUpX = (int) (((float) (this.offsetX + rtagx)) * this.taupaintX);
                        if (ldwResults.getState() == 1 || ldwResults.getState() == 2) {
                            this.p.setShader(new LinearGradient((float) leftDownX, (float) leftDownY, (float) leftDownX, (float) leftUpY, Color.parseColor("#FF041B"), 0, Shader.TileMode.CLAMP));
                        } else {
                            this.p.setShader(new LinearGradient((float) leftDownX, (float) leftDownY, (float) leftDownX, (float) leftUpY, Color.parseColor("#05E2FF"), 0, Shader.TileMode.CLAMP));
                        }
                        this.mPath = new Path();
                        this.mPath.moveTo((float) leftDownX, (float) leftDownY);
                        this.mPath.lineTo((float) leftUpX, (float) leftUpY);
                        this.mPath.lineTo((float) rightUpX, (float) leftUpY);
                        this.mPath.lineTo((float) rightDownX, (float) rightDownY);
                        this.p.setStyle(Paint.Style.FILL);
                        canvas.drawPath(this.mPath, this.p);
                        this.p.setShader((Shader) null);
                        this.p.setStyle(Paint.Style.STROKE);
                        if (ldwResults.getState() == 1) {
                            this.p.setColor(-609276);
                            this.p.setShader(new LinearGradient((float) leftDownX, (float) leftDownY, (float) leftDownX, (float) leftUpY, Color.parseColor("#FF041B"), 0, Shader.TileMode.CLAMP));
                        } else {
                            this.p.setColor(-1);
                            this.p.setShader(new LinearGradient((float) leftDownX, (float) leftDownY, (float) leftDownX, (float) leftUpY, -1, ViewCompat.MEASURED_SIZE_MASK, Shader.TileMode.CLAMP));
                        }
                        canvas.drawLine((float) leftUpX, (float) leftUpY, (float) leftDownX, (float) leftDownY, this.p);
                        if (ldwResults.getState() == 2) {
                            this.p.setColor(-609276);
                            this.p.setShader(new LinearGradient((float) rightDownX, (float) rightDownY, (float) rightDownX, (float) leftUpY, Color.parseColor("#FF041B"), 0, Shader.TileMode.CLAMP));
                        } else {
                            this.p.setColor(-1);
                            this.p.setShader(new LinearGradient((float) rightDownX, (float) rightDownY, (float) rightDownX, (float) leftUpY, -1, ViewCompat.MEASURED_SIZE_MASK, Shader.TileMode.CLAMP));
                        }
                        canvas.drawLine((float) rightUpX, (float) leftUpY, (float) rightDownX, (float) rightDownY, this.p);
                        this.p.setPathEffect((PathEffect) null);
                        this.p.setShader((Shader) null);
                        if (ldwResults.getLeft().getIsCredible() == 1 && ldwResults.getRight().getIsCredible() == 1) {
                            this.mLdwCount++;
                        }
                    } else if (this.mLdwCount < 150 || config.getIsCalibCredible() == 0) {
                        if (this.mLine != null) {
                            this.src = new float[]{0.0f, 0.0f, (float) this.mLine.getWidth(), 0.0f, (float) this.mLine.getWidth(), (float) this.mLine.getHeight(), 0.0f, (float) this.mLine.getHeight()};
                            this.dst = new float[]{(config.getX() - (this.offX / 20.0f)) - 20.0f, config.getY() + (this.offY / 18.0f), config.getX() - (this.offX / 20.0f), config.getY() + (this.offY / 18.0f), config.getX() - (this.offX / 5.0f), config.getY() + (this.offY / 4.0f), (config.getX() - (this.offX / 5.0f)) - 50.0f, config.getY() + (this.offY / 4.0f)};
                            this.mPocilyMatrix.setPolyToPoly(this.src, 0, this.dst, 0, this.src.length >> 1);
                            canvas.drawBitmap(this.mLine, this.mPocilyMatrix, this.p);
                            this.dst = new float[]{config.getX() + (this.offX / 15.0f), config.getY() + (this.offY / 18.0f), config.getX() + (this.offX / 15.0f) + 20.0f, config.getY() + (this.offY / 18.0f), config.getX() + (this.offX / 4.5f) + 50.0f, config.getY() + (this.offY / 4.0f), config.getX() + (this.offX / 4.5f), config.getY() + (this.offY / 4.0f)};
                            this.mPocilyMatrix.setPolyToPoly(this.src, 0, this.dst, 0, this.src.length >> 1);
                            canvas.drawBitmap(this.mLine, this.mPocilyMatrix, this.p);
                            this.p.setStrokeWidth(3.0f);
                        } else {
                            return;
                        }
                    }
                }
                this.p.setAlpha(255);
                if (fcwResults != null) {
                    int carState = fcwResults.getState();
                    int i2 = 0;
                    while (i2 < fcwResults.getCarNum()) {
                        this.p_text.setColor(Color.parseColor("#05E2FF"));
                        int car_dis = (int) fcwResults.getCar()[i2].getDis();
                        RectInt carRectInt = fcwResults.getCar()[i2].getCarRect();
                        carRectInt.setY((int) (((double) (((float) (carRectInt.getY() + this.offsetY)) * this.taupaintY)) - (((double) (((float) carRectInt.getW()) * this.taupaintX)) * 0.08d)));
                        carRectInt.setX((int) (((double) (((float) (carRectInt.getX() + this.offsetX)) * this.taupaintX)) - (((double) (((float) carRectInt.getH()) * this.taupaintY)) * 0.08d)));
                        carRectInt.setW((int) (((double) (((float) carRectInt.getW()) * this.taupaintX)) * 1.16d));
                        carRectInt.setH((int) (((double) (((float) carRectInt.getH()) * this.taupaintY)) * 1.16d));
                        if (carState != 0 && i2 == 0) {
                            this.p_text.setColor(Color.parseColor("#FF041B"));
                        }
                        Bitmap bitmap = getCarBitmap(carRectInt, carState != 0 && i2 == 0);
                        if (bitmap != null) {
                            canvas.drawBitmap(bitmap, (float) carRectInt.getX(), (float) carRectInt.getY(), this.mBitmapPaint);
                        }
                        if (config.getIsCalibCredible() == 1) {
                            String text = car_dis + "m";
                            this.p_text.setTextSize((float) (carRectInt.getW() / 3));
                            float textWidth = this.p_text.measureText(text);
                            if (textWidth <= ((float) carRectInt.getW())) {
                                this.mTextRect.set(carRectInt.getX(), carRectInt.getY() - (carRectInt.getW() / 10), carRectInt.getX() + carRectInt.getW(), carRectInt.getY() - (carRectInt.getW() / 10));
                            } else {
                                int offsetX2 = (int) (((float) carRectInt.getX()) - ((textWidth / 2.0f) - ((float) (carRectInt.getW() / 2))));
                                this.mTextRect.set(offsetX2, carRectInt.getY() - (carRectInt.getW() / 10), (int) (((float) offsetX2) + textWidth), carRectInt.getY() - (carRectInt.getW() / 10));
                            }
                            Paint.FontMetrics fontMetrics2 = this.p_text.getFontMetrics();
                            canvas.drawText(text, (float) this.mTextRect.centerX(), (float) (((int) ((((float) this.mTextRect.centerY()) - (fontMetrics2.top / 2.0f)) - (fontMetrics2.bottom / 2.0f))) - 5), this.p_text);
                        }
                        i2++;
                    }
                }
                this.sh.unlockCanvasAndPost(canvas);
            }
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case 0:
                this.mStartX = event.getX();
                this.mStartY = event.getY();
                if (!mLandRect.contains((int) this.mStartX, (int) this.mStartY)) {
                    if (mPortRect.contains((int) this.mStartX, (int) this.mStartY)) {
                        this.isPort = true;
                        break;
                    }
                } else {
                    this.isLand = true;
                    break;
                }
                break;
            case 1:
                this.isPort = false;
                this.isLand = false;
                break;
            case 2:
                if (!mIsDraw) {
                    if (!this.isLand) {
                        if (this.isPort) {
                            if (event.getX() < this.maxRight) {
                                if (event.getX() > this.maxLeft) {
                                    sConfigX = event.getX();
                                    break;
                                } else {
                                    sConfigX = this.maxLeft;
                                    break;
                                }
                            } else {
                                sConfigX = this.maxRight;
                                break;
                            }
                        }
                    } else if (event.getY() < this.maxBottom) {
                        if (event.getY() > this.maxTop) {
                            sConfigY = event.getY();
                            break;
                        } else {
                            sConfigY = this.maxTop;
                            break;
                        }
                    } else {
                        sConfigY = this.maxBottom;
                        break;
                    }
                }
                break;
        }
        return true;
    }

    class DrawHandler extends Handler {
        public DrawHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    SVDrawRectangle1.this.drawResult((DrawInfo) msg.obj);
                    return;
                case 2:
                    SVDrawRectangle1.this.setCheckpoint();
                    return;
                case 153:
                    float unused = SVDrawRectangle1.sConfigX = (SVDrawRectangle1.sConfigX / SVDrawRectangle1.this.taupaintX) - ((float) SVDrawRectangle1.this.offsetX);
                    float unused2 = SVDrawRectangle1.sConfigY = (SVDrawRectangle1.sConfigY / SVDrawRectangle1.this.taupaintY) - ((float) SVDrawRectangle1.this.offsetY);
                    float unused3 = SVDrawRectangle1.this.countX = SVDrawRectangle1.sConfigX;
                    float unused4 = SVDrawRectangle1.this.countY = SVDrawRectangle1.sConfigY;
                    if (SVDrawRectangle1.this.mListener != null) {
                        SVDrawRectangle1.this.mListener.setAdasConfigXY(SVDrawRectangle1.sConfigX, SVDrawRectangle1.sConfigY);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    }

    private Bitmap getCarBitmap(RectInt carRectInt, boolean flag) {
        if (this.mRedCarBitmap != null && this.mRedCarBitmap.isRecycled()) {
            this.mRedCarBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.red_car);
        }
        if (this.mBlueCarBitmap != null && this.mBlueCarBitmap.isRecycled()) {
            this.mBlueCarBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.blue_car);
        }
        Bitmap bitmap = flag ? this.mRedCarBitmap : this.mBlueCarBitmap;
        this.mRectF.set((float) carRectInt.getX(), (float) carRectInt.getY(), (float) (carRectInt.getX() + carRectInt.getW()), (float) (carRectInt.getY() + carRectInt.getH()));
        Matrix matrix = new Matrix();
        matrix.postScale(this.mRectF.width() / ((float) bitmap.getWidth()), this.mRectF.height() / ((float) bitmap.getHeight()));
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public void setListener(IAdasConfigListener listener) {
        this.mListener = listener;
    }
}
