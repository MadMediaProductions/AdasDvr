package com.fvision.camera.view.customview;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import com.fvision.camera.R;
import com.fvision.camera.utils.DensityUtils;
import com.fvision.camera.utils.Uview;

public class SettingLayout extends LinearLayout {
    private Context mContext;
    private SparseArray<View> mItemViews;

    public interface IClickListener {
        void click(SettingLayout settingLayout, Item item);
    }

    public enum SEP {
        NO,
        FILL,
        AFTERICON
    }

    public SettingLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        this.mItemViews = new SparseArray<>();
    }

    public SettingLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public SettingLayout addItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("item为空");
        } else if (this.mItemViews.get(item.titleStrId) != null) {
            throw new IllegalArgumentException("此Item项已存在，请不要重复添加");
        } else {
            View v = LayoutInflater.from(this.mContext).inflate(R.layout.item_setting_layout, (ViewGroup) null);
            addView(v, new LayoutParams(-1, DensityUtils.dp2px(this.mContext, 48.0f)));
            this.mItemViews.put(item.titleStrId, v);
            refreshItem(item);
            return this;
        }
    }

    public SettingLayout refreshItem(final Item item) {
        if (item == null) {
            throw new NullPointerException("Item为空");
        }
        View v = getItemView(item);
        if (v == null) {
            throw new IllegalArgumentException("尚未添加此Item, 请先通过addItem方法添加");
        }
        if (item.iconResId != 0) {
            ImageView ivIcon = (ImageView) v.findViewById(R.id.iv_set_icon);
            ivIcon.setVisibility(0);
            ivIcon.setImageResource(item.iconResId);
        }
        TextView tvTitle = (TextView) v.findViewById(R.id.tv_set_title);
        tvTitle.setText(item.titleStrId);
        if (!TextUtils.isEmpty(item.contentStr)) {
            TextView tvContent = (TextView) v.findViewById(R.id.tv_set_content);
            tvContent.setVisibility(0);
            tvContent.setText(item.contentStr);
        }
        if (!TextUtils.isEmpty(item.unitStr)) {
            TextView tvUnit = (TextView) v.findViewById(R.id.tv_set_unit);
            tvUnit.setVisibility(0);
            tvUnit.setText(item.unitStr);
        }
        if (item.hasToRight) {
            ((ImageView) v.findViewById(R.id.iv_set_to_right)).setVisibility(0);
        }
        if (item.isShowCheck) {
            CheckBox cb = (CheckBox) v.findViewById(R.id.cb);
            cb.setVisibility(0);
            cb.setChecked(item.isCheck);
            if (item.listener != null) {
                cb.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        item.listener.click(SettingLayout.this, item);
                    }
                });
            }
        }
        v.setEnabled(item.isEnable);
        ((CheckBox) v.findViewById(R.id.cb)).setEnabled(item.isEnable);
        tvTitle.setTextColor(item.isEnable ? -1 : -7829368);
        View vDivider = v.findViewById(R.id.v_set_divider);
        if (item.sep == SEP.FILL) {
            vDivider.setVisibility(0);
        } else if (item.sep == SEP.AFTERICON) {
            vDivider.setVisibility(0);
            ((MarginLayoutParams) vDivider.getLayoutParams()).leftMargin = DensityUtils.dp2px(this.mContext, 48.0f);
        }
        if (item.listener != null) {
            Uview.clickEffectByAlphaWithBg(new OnClickListener() {
                public void onClick(View v) {
                    item.listener.click(SettingLayout.this, item);
                }
            }, v);
        }
        return this;
    }

    public SettingLayout addCustomView(View view) {
        addView(view);
        return this;
    }

    public SettingLayout addSpace(int spaceHeight) {
        Space space = new Space(this.mContext);
        space.setLayoutParams(new ViewGroup.LayoutParams(-1, spaceHeight));
        addView(space);
        return this;
    }

    public SettingLayout addHeadTips(int strId) {
        return addHeadTips(this.mContext.getResources().getString(strId));
    }

    public SettingLayout addHeadTips(String tips) {
        TextView tvTips = new TextView(this.mContext);
        LayoutParams params = new LayoutParams(-1, -2);
        params.leftMargin = DensityUtils.dp2px(this.mContext, 16.0f);
        params.bottomMargin = DensityUtils.dp2px(this.mContext, 8.0f);
        tvTips.setLayoutParams(params);
        tvTips.setText(tips);
        tvTips.setTextColor(-7829368);
        tvTips.setTextSize(18.0f);
        addView(tvTips);
        return this;
    }

    public View getItemView(Item item) {
        return getItemView(item.titleStrId);
    }

    public View getItemView(int titleStrId) {
        return this.mItemViews.get(titleStrId);
    }

    public static class Item {
        public String contentStr;
        public boolean hasToRight;
        public int iconResId;
        public boolean isCheck;
        public boolean isEnable;
        public boolean isShowCheck;
        public IClickListener listener;
        public SEP sep;
        public int titleStrId;
        public String unitStr;

        public Item(int titleStrId2) {
            this.titleStrId = titleStrId2;
        }

        public Item(int titleStrId2, int iconResId2, String contentStr2, String unitStr2, boolean hasToRight2, SEP sep2, boolean isCheck2, boolean isShowCheck2, boolean isEnable2, IClickListener listener2) {
            this.iconResId = iconResId2;
            this.titleStrId = titleStrId2;
            this.contentStr = contentStr2;
            this.unitStr = unitStr2;
            this.hasToRight = hasToRight2;
            this.sep = sep2;
            this.listener = listener2;
            this.isCheck = isCheck2;
            this.isShowCheck = isShowCheck2;
            this.isEnable = isEnable2;
        }
    }
}
