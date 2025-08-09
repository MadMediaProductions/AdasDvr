package org.apache.http.entity.mime.content;

import com.alibaba.sdk.android.oss.common.OSSConstants;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.http.entity.mime.MIME;

public class ByteArrayBody extends AbstractContentBody {
    private final byte[] data;
    private final String filename;

    public ByteArrayBody(byte[] data2, String mimeType, String filename2) {
        super(mimeType);
        if (data2 == null) {
            throw new IllegalArgumentException("byte[] may not be null");
        }
        this.data = data2;
        this.filename = filename2;
    }

    public ByteArrayBody(byte[] data2, String filename2) {
        this(data2, OSSConstants.DEFAULT_OBJECT_CONTENT_TYPE, filename2);
    }

    public String getFilename() {
        return this.filename;
    }

    public void writeTo(OutputStream out) throws IOException {
        out.write(this.data);
    }

    public String getCharset() {
        return null;
    }

    public String getTransferEncoding() {
        return MIME.ENC_BINARY;
    }

    public long getContentLength() {
        return (long) this.data.length;
    }
}
