package com.fvision.camera.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.fvision.camera.App;
import com.fvision.camera.R;
import com.fvision.camera.util.LogUtils;
import java.io.File;
import java.util.ArrayList;

public class PhotoActivity extends Activity implements View.OnClickListener {
    public static final String KEY_CURRENT_PHOTO = "current_photo";
    private LinearLayout back;
    private String currentPhoto;
    private int currentPos = 0;
    private ImageView imageView;
    private ArrayList<File> images;
    private Button next;
    private Button pre;
    private TextView title;
    private RelativeLayout titleLyaout;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.activity_photo);
        this.currentPhoto = getIntent().getStringExtra(KEY_CURRENT_PHOTO);
        LogUtils.d("currentPhoto " + this.currentPhoto);
        init();
        showImage(this.currentPhoto);
    }

    private void moveCurrentPos() {
        if (this.currentPhoto != null) {
            int i = 0;
            while (true) {
                if (i >= this.images.size()) {
                    break;
                } else if (this.images.get(i).getPath().equals(this.currentPhoto)) {
                    this.currentPos = i;
                    break;
                } else {
                    i++;
                }
            }
            showImage(this.currentPos);
        }
    }

    private void init() {
        this.pre = (Button) findViewById(R.id.pre);
        this.pre.setOnClickListener(this);
        this.next = (Button) findViewById(R.id.next);
        this.next.setOnClickListener(this);
        this.imageView = (ImageView) findViewById(R.id.imageview);
        this.imageView.setOnClickListener(this);
        this.titleLyaout = (RelativeLayout) findViewById(R.id.title_layout);
        this.titleLyaout.setBackgroundResource(R.color.colorTransparentBlack);
        this.title = (TextView) findViewById(R.id.id_tv_sample_title);
        this.title.setText(this.currentPhoto);
        this.back = (LinearLayout) findViewById(R.id.id_ll_sample_back);
        this.back.setOnClickListener(this);
    }

    public void onClick(View v) {
        int i = 0;
        switch (v.getId()) {
            case R.id.imageview:
                RelativeLayout relativeLayout = this.titleLyaout;
                if (this.titleLyaout.getVisibility() == 0) {
                    i = 8;
                }
                relativeLayout.setVisibility(i);
                return;
            case R.id.pre:
                this.currentPos--;
                if (this.currentPos < 0) {
                    this.currentPos = 0;
                    Toast.makeText(this, "已经是第一张了", 0).show();
                    return;
                }
                showImage(this.currentPos);
                return;
            case R.id.next:
                this.currentPos++;
                if (this.currentPos >= this.images.size()) {
                    this.currentPos = this.images.size() - 1;
                    Toast.makeText(this, "已经是最后一张了", 0).show();
                    return;
                }
                showImage(this.currentPos);
                return;
            case R.id.id_ll_sample_back:
                Log.e("photoActivity", "finish");
                finish();
                return;
            default:
                return;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x0036 A[SYNTHETIC, Splitter:B:23:0x0036] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.graphics.Bitmap getBMP(File r7) {
        /*
            r6 = this;
            r2 = 0
            r0 = 0
            java.io.BufferedInputStream r3 = new java.io.BufferedInputStream     // Catch:{ FileNotFoundException -> 0x001d }
            java.io.FileInputStream r4 = new java.io.FileInputStream     // Catch:{ FileNotFoundException -> 0x001d }
            r4.<init>(r7)     // Catch:{ FileNotFoundException -> 0x001d }
            r3.<init>(r4)     // Catch:{ FileNotFoundException -> 0x001d }
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeStream(r3)     // Catch:{ FileNotFoundException -> 0x0042, all -> 0x003f }
            if (r3 == 0) goto L_0x0045
            r3.close()     // Catch:{ IOException -> 0x0017 }
            r2 = r3
        L_0x0016:
            return r0
        L_0x0017:
            r1 = move-exception
            r1.printStackTrace()
            r2 = r3
            goto L_0x0016
        L_0x001d:
            r1 = move-exception
        L_0x001e:
            java.lang.String r4 = "程序异常！"
            r5 = 0
            android.widget.Toast r4 = android.widget.Toast.makeText(r6, r4, r5)     // Catch:{ all -> 0x0033 }
            r4.show()     // Catch:{ all -> 0x0033 }
            if (r2 == 0) goto L_0x0016
            r2.close()     // Catch:{ IOException -> 0x002e }
            goto L_0x0016
        L_0x002e:
            r1 = move-exception
            r1.printStackTrace()
            goto L_0x0016
        L_0x0033:
            r4 = move-exception
        L_0x0034:
            if (r2 == 0) goto L_0x0039
            r2.close()     // Catch:{ IOException -> 0x003a }
        L_0x0039:
            throw r4
        L_0x003a:
            r1 = move-exception
            r1.printStackTrace()
            goto L_0x0039
        L_0x003f:
            r4 = move-exception
            r2 = r3
            goto L_0x0034
        L_0x0042:
            r1 = move-exception
            r2 = r3
            goto L_0x001e
        L_0x0045:
            r2 = r3
            goto L_0x0016
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fvision.camera.view.activity.PhotoActivity.getBMP(java.io.File):android.graphics.Bitmap");
    }

    private ArrayList<File> getPath() {
        ArrayList<File> al = new ArrayList<>();
        for (File file : new File(App.JPG_PATH).listFiles()) {
            if (file.exists() && file.isFile() && isImage(file)) {
                al.add(file);
                LogUtils.d("file " + file.getName());
            }
        }
        return al;
    }

    private boolean isImage(File file) {
        String[] strs = {".jpg", ".JPG"};
        if (0 < strs.length) {
            return file.getName().endsWith(strs[0]);
        }
        LogUtils.d("not image " + file.getName());
        return false;
    }

    private void showImage(int i) {
        this.imageView.setImageBitmap(getBMP(this.images.get(i)));
    }

    private void showImage(String filePath) {
        this.imageView.setImageBitmap(getBMP(new File(filePath)));
    }
}
