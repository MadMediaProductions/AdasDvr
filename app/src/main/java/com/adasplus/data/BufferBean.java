package com.adasplus.data;

public class BufferBean {
    public byte[] isCanRead = new byte[1];
    public byte[] mBuffer;

    public BufferBean(int bufferSize) {
        if (bufferSize > 0) {
            this.mBuffer = new byte[bufferSize];
        }
        for (int i = 0; i < this.mBuffer.length; i++) {
            this.mBuffer[i] = 0;
        }
    }
}
