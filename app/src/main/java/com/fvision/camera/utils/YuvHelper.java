package com.fvision.camera.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class YuvHelper {
    public static byte[] getBytesFromFile(File file) {
        int numRead;
        byte[] bytes = null;
        try {
            InputStream is = new FileInputStream(file);
            long length = file.length();
            if (length > 2147483647L) {
                throw new IOException("File is to large " + file.getName());
            }
            bytes = new byte[((int) length)];
            int offset = 0;
            while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }
            if (offset < bytes.length) {
                throw new IOException("Could not completely read file " + file.getName());
            }
            is.close();
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] inputStreamTobyte(InputStream inStream) throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        while (true) {
            int rc = inStream.read(buff, 0, 100);
            if (rc <= 0) {
                return swapStream.toByteArray();
            }
            swapStream.write(buff, 0, rc);
        }
    }
}
