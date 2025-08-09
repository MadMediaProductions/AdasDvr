package com.fvision.camera.view.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.fvision.camera.R;
import com.fvision.camera.utils.LogUtils;

public class IconView extends FrameLayout {
    public static final int TYPE_BIG = 1;
    public static final int TYPE_SMALL = 0;
    private RelativeLayout bg;
    /* access modifiers changed from: private */
    public int bg_nor = -1;
    /* access modifiers changed from: private */
    public int bg_press = -1;
    private ImageView icon;
    private String icon_name = null;
    private int icon_name_id = -1;
    private LinearLayout layout;
    private Context mContext;
    /* access modifiers changed from: private */
    public OnClickListener mOnIconClick;
    private LinearLayout main;
    private TextView text;
    private int type = 0;

    public IconView(Context context) {
        super(context);
        initView(context, (AttributeSet) null);
    }

    public IconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public IconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.IconView);
            this.type = array.getInt(0, 0);
            this.bg_nor = array.getResourceId(1, -1);
            this.bg_press = array.getResourceId(2, -1);
            this.icon_name = array.getString(3);
            this.icon_name_id = array.getResourceId(3, -1);
        }
        this.mContext = context;
        if (this.type == 1) {
            this.main = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.icon_view_big, this, false);
        } else if (this.type == 0) {
            this.main = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.icon_view, this, false);
        }
        addView(this.main);
        this.layout = (LinearLayout) this.main.findViewById(R.id.layout);
        this.text = (TextView) this.main.findViewById(R.id.main_icon_text);
        this.icon = (ImageView) this.main.findViewById(R.id.main_icon_img);
        setBackgroundResource(this.bg_nor);
        this.bg = (RelativeLayout) this.main.findViewById(R.id.main_icon_layout);
        this.bg.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Log.e("IconView", "mOnIconClick == null");
                if (IconView.this.mOnIconClick != null) {
                    Log.e("IconView", "bg.setOnClickListener");
                    IconView.this.mOnIconClick.onClick(view);
                }
            }
        });
        this.bg.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case 0:
                        IconView.this.setBackgroundResource(IconView.this.bg_press);
                        Log.e("IconView", "bg.setOnTouchListener_down");
                        return false;
                    case 1:
                        IconView.this.setBackgroundResource(IconView.this.bg_nor);
                        Log.e("IconView", "bg.setOnTouchListener_up");
                        return false;
                    default:
                        return false;
                }
            }
        });
        if (!TextUtils.isEmpty(this.icon_name)) {
            setText(this.icon_name);
        }
        if (this.icon_name_id != -1) {
            setText(this.icon_name_id);
        }
    }

    public void setText(String name) {
        this.icon_name = name;
        this.text.setVisibility(8);
        this.text.setText(this.icon_name);
    }

    public void setText(int nameId) {
        this.icon_name_id = nameId;
        this.text.setVisibility(8);
        this.text.setText(this.icon_name_id);
    }

    public void setBackgroundResource(int resid) {
        if (resid != -1) {
            LogUtils.d("resid = " + resid);
            this.icon.setBackgroundResource(resid);
        }
    }

    public void setBgColor(int color) {
        this.layout.setBackgroundColor(color);
    }

    public void setOnClick(OnClickListener click) {
        this.main.setOnClickListener(click);
    }

    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
    }

    public void setOnIconClick(OnClickListener onIconClick) {
        this.mOnIconClick = onIconClick;
    }

    public void setToggleImg(int imgUpId, int imgDownId) {
        setBackgroundResource(imgUpId);
        this.bg_nor = imgUpId;
        this.bg_press = imgDownId;
    }

    public void setType(int type2) {
        this.type = type2;
    }
}
