package com.adasplus.adas.adas;

import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.MemoryFile;
import android.os.Message;
import android.util.Log;
import com.adasplus.adas.AdasInterfaceImp;
import com.adasplus.adas.util.ReflectUtils;
import com.adasplus.data.BufferBean;
import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AdasManager {
    private static final int MEMORY_SIZE = 3133441;
    private static final int MSG_CLOSE_BUFFER = 1;
    private static final int MSG_READ_BUFFER = 0;
    private boolean isLooper = true;
    private boolean isSetShareFD = false;
    private boolean isStart = false;
    private AdasInterfaceImp mAdasImpl = null;
    private BufferBean mBufferBean = null;
    private int mBufferSize = 0;
    private HandlerThread mHandlerThread;
    private int mHeight;
    private MemoryFile mMemoryFile;
    public PreviewCallback mPreviewCallback;
    private ShareBufferHandler mShareBufferHandler;
    private int mWidth;

    public interface PreviewCallback {
        void OnPreview(byte[] bArr);
    }

    public void setAdasImple(AdasInterfaceImp impl) {
        this.mAdasImpl = impl;
    }

    public void setPreviewCallback(PreviewCallback callback) {
        this.mPreviewCallback = callback;
    }

    public void setBufferSize(int bufferSize) {
        this.mBufferSize = bufferSize;
        if (this.mBufferSize <= 0) {
            this.mBufferSize = 1382400;
        }
        this.mBufferBean = new BufferBean(this.mBufferSize);
    }

    public void setWidthAndHeight(int width, int height) {
        this.mBufferSize = (int) (((double) (width * height)) * 1.5d);
        if (this.mBufferSize <= 0) {
            this.mBufferSize = 1382400;
        }
        this.mBufferBean = new BufferBean(this.mBufferSize);
    }

    public void setHeight(int height) {
        this.mHeight = height;
    }

    public void setWidth(int width) {
        this.mWidth = width;
    }

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public AdasManager() {
        try {
            this.mMemoryFile = new MemoryFile("AdasMemory", MEMORY_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void prepare(Camera camera) {
        try {
            this.mHandlerThread = new HandlerThread("AdasThread");
            this.mHandlerThread.start();
            this.mShareBufferHandler = new ShareBufferHandler(this.mHandlerThread.getLooper());
            if (this.mMemoryFile == null) {
                try {
                    this.mMemoryFile = new MemoryFile("AdasMemory", MEMORY_SIZE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            this.mMemoryFile.writeBytes(this.mBufferBean.mBuffer, 0, 0, this.mBufferBean.mBuffer.length);
            try {
                Method getFileDescriptorMethod = this.mMemoryFile.getClass().getDeclaredMethod("getFileDescriptor", new Class[0]);
                if (getFileDescriptorMethod == null) {
                    return;
                }
                if (ReflectUtils.setShareFD(camera, (FileDescriptor) getFileDescriptorMethod.invoke(this.mMemoryFile, new Object[0])) != -1) {
                    this.isSetShareFD = true;
                } else {
                    Log.i("Adas", "setShareFD fail!");
                }
            } catch (NoSuchMethodException e2) {
                e2.printStackTrace();
            } catch (InvocationTargetException e3) {
                e3.printStackTrace();
            } catch (IllegalAccessException e4) {
                e4.printStackTrace();
            }
        } catch (IOException e5) {
            e5.printStackTrace();
        }
    }

    public void start() {
        if (this.mShareBufferHandler == null || this.mHandlerThread == null || !this.isSetShareFD) {
            throw new RuntimeException("When you start, you should invoke prepare method.");
        } else if (!this.isStart) {
            readShareBuffer();
            this.isStart = true;
        }
    }

    public void stop() {
        stopReadShareBuffer();
        if (this.mShareBufferHandler != null) {
            this.mShareBufferHandler.removeMessages(0);
            this.mShareBufferHandler.removeMessages(1);
            this.mShareBufferHandler = null;
        }
        if (this.mHandlerThread != null) {
            this.mHandlerThread.quitSafely();
            this.mHandlerThread = null;
        }
        if (this.mAdasImpl != null) {
            this.mAdasImpl = null;
        }
        this.isSetShareFD = false;
        this.isStart = false;
    }

    /* access modifiers changed from: private */
    public void readShareBufferMsg() {
        try {
            if (this.mMemoryFile != null) {
                this.mMemoryFile.readBytes(this.mBufferBean.isCanRead, 0, 0, 1);
                if (this.mBufferBean.isCanRead[0] == 1) {
                    this.mMemoryFile.readBytes(this.mBufferBean.mBuffer, 1, 0, this.mBufferBean.mBuffer.length);
                    this.mBufferBean.isCanRead[0] = 0;
                    if (this.mPreviewCallback != null) {
                        this.mPreviewCallback.OnPreview(this.mBufferBean.mBuffer);
                    } else {
                        process(this.mBufferBean.mBuffer);
                    }
                    this.mMemoryFile.writeBytes(this.mBufferBean.isCanRead, 0, 0, 1);
                }
                if (this.isLooper && this.mShareBufferHandler != null) {
                    this.mShareBufferHandler.sendEmptyMessageDelayed(0, 70);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readShareBuffer() {
        this.isLooper = true;
        if (this.mShareBufferHandler != null && this.isSetShareFD) {
            this.mShareBufferHandler.sendEmptyMessage(0);
        }
    }

    private void stopReadShareBuffer() {
        this.isLooper = true;
        if (this.mShareBufferHandler != null && this.isSetShareFD) {
            this.mShareBufferHandler.sendEmptyMessage(1);
        }
    }

    private void process(byte[] data) {
    }

    public class ShareBufferHandler extends Handler {
        public ShareBufferHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    AdasManager.this.readShareBufferMsg();
                    return;
                case 1:
                    AdasManager.this.closeShareBuffer();
                    return;
                default:
                    return;
            }
        }
    }

    public void closeShareBuffer() {
        this.isLooper = false;
        if (this.mMemoryFile != null) {
            this.mMemoryFile.close();
            this.mMemoryFile = null;
        }
    }
}
