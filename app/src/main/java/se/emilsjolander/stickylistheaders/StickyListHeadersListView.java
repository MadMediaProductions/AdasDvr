package se.emilsjolander.stickylistheaders;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import com.fvision.camera.R;
import se.emilsjolander.stickylistheaders.AdapterWrapper;
import se.emilsjolander.stickylistheaders.WrapperViewList;

public class StickyListHeadersListView extends FrameLayout {
    private AdapterWrapper mAdapter;
    private boolean mAreHeadersSticky;
    /* access modifiers changed from: private */
    public boolean mClippingToPadding;
    private AdapterWrapperDataSetObserver mDataSetObserver;
    private Drawable mDivider;
    private int mDividerHeight;
    private float mDownY;
    /* access modifiers changed from: private */
    public View mHeader;
    /* access modifiers changed from: private */
    public Long mHeaderId;
    private Integer mHeaderOffset;
    private boolean mHeaderOwnsTouch;
    /* access modifiers changed from: private */
    public Integer mHeaderPosition;
    private boolean mIsDrawingListUnderStickyHeader;
    /* access modifiers changed from: private */
    public WrapperViewList mList;
    /* access modifiers changed from: private */
    public OnHeaderClickListener mOnHeaderClickListener;
    /* access modifiers changed from: private */
    public AbsListView.OnScrollListener mOnScrollListenerDelegate;
    private OnStickyHeaderChangedListener mOnStickyHeaderChangedListener;
    private OnStickyHeaderOffsetChangedListener mOnStickyHeaderOffsetChangedListener;
    private int mPaddingBottom;
    private int mPaddingLeft;
    private int mPaddingRight;
    /* access modifiers changed from: private */
    public int mPaddingTop;
    private int mStickyHeaderTopOffset;
    private float mTouchSlop;

    public interface OnHeaderClickListener {
        void onHeaderClick(StickyListHeadersListView stickyListHeadersListView, View view, int i, long j, boolean z);
    }

    public interface OnStickyHeaderChangedListener {
        void onStickyHeaderChanged(StickyListHeadersListView stickyListHeadersListView, View view, int i, long j);
    }

    public interface OnStickyHeaderOffsetChangedListener {
        void onStickyHeaderOffsetChanged(StickyListHeadersListView stickyListHeadersListView, View view, int i);
    }

    public StickyListHeadersListView(Context context) {
        this(context, (AttributeSet) null);
    }

    public StickyListHeadersListView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.stickyListHeadersListViewStyle);
    }

    @TargetApi(11)
    public StickyListHeadersListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mAreHeadersSticky = true;
        this.mClippingToPadding = true;
        this.mIsDrawingListUnderStickyHeader = true;
        this.mStickyHeaderTopOffset = 0;
        this.mPaddingLeft = 0;
        this.mPaddingTop = 0;
        this.mPaddingRight = 0;
        this.mPaddingBottom = 0;
        this.mTouchSlop = (float) ViewConfiguration.get(getContext()).getScaledTouchSlop();
        this.mList = new WrapperViewList(context);
        this.mDivider = this.mList.getDivider();
        this.mDividerHeight = this.mList.getDividerHeight();
        this.mList.setDivider((Drawable) null);
        this.mList.setDividerHeight(0);
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.StickyListHeadersListView, defStyle, 0);
            try {
                int padding = a.getDimensionPixelSize(1, 0);
                this.mPaddingLeft = a.getDimensionPixelSize(2, padding);
                this.mPaddingTop = a.getDimensionPixelSize(3, padding);
                this.mPaddingRight = a.getDimensionPixelSize(4, padding);
                this.mPaddingBottom = a.getDimensionPixelSize(5, padding);
                setPadding(this.mPaddingLeft, this.mPaddingTop, this.mPaddingRight, this.mPaddingBottom);
                this.mClippingToPadding = a.getBoolean(8, true);
                super.setClipToPadding(true);
                this.mList.setClipToPadding(this.mClippingToPadding);
                int scrollBars = a.getInt(6, 512);
                this.mList.setVerticalScrollBarEnabled((scrollBars & 512) != 0);
                this.mList.setHorizontalScrollBarEnabled((scrollBars & 256) != 0);
                if (Build.VERSION.SDK_INT >= 9) {
                    this.mList.setOverScrollMode(a.getInt(19, 0));
                }
                this.mList.setFadingEdgeLength(a.getDimensionPixelSize(7, this.mList.getVerticalFadingEdgeLength()));
                int fadingEdge = a.getInt(21, 0);
                if (fadingEdge == 4096) {
                    this.mList.setVerticalFadingEdgeEnabled(false);
                    this.mList.setHorizontalFadingEdgeEnabled(true);
                } else if (fadingEdge == 8192) {
                    this.mList.setVerticalFadingEdgeEnabled(true);
                    this.mList.setHorizontalFadingEdgeEnabled(false);
                } else {
                    this.mList.setVerticalFadingEdgeEnabled(false);
                    this.mList.setHorizontalFadingEdgeEnabled(false);
                }
                this.mList.setCacheColorHint(a.getColor(14, this.mList.getCacheColorHint()));
                if (Build.VERSION.SDK_INT >= 11) {
                    this.mList.setChoiceMode(a.getInt(17, this.mList.getChoiceMode()));
                }
                this.mList.setDrawSelectorOnTop(a.getBoolean(10, false));
                this.mList.setFastScrollEnabled(a.getBoolean(18, this.mList.isFastScrollEnabled()));
                if (Build.VERSION.SDK_INT >= 11) {
                    this.mList.setFastScrollAlwaysVisible(a.getBoolean(20, this.mList.isFastScrollAlwaysVisible()));
                }
                this.mList.setScrollBarStyle(a.getInt(0, 0));
                if (a.hasValue(9)) {
                    this.mList.setSelector(a.getDrawable(9));
                }
                this.mList.setScrollingCacheEnabled(a.getBoolean(12, this.mList.isScrollingCacheEnabled()));
                if (a.hasValue(15)) {
                    this.mDivider = a.getDrawable(15);
                }
                this.mList.setStackFromBottom(a.getBoolean(11, false));
                this.mDividerHeight = a.getDimensionPixelSize(16, this.mDividerHeight);
                this.mList.setTranscriptMode(a.getInt(13, 0));
                this.mAreHeadersSticky = a.getBoolean(23, true);
                this.mIsDrawingListUnderStickyHeader = a.getBoolean(24, true);
            } finally {
                a.recycle();
            }
        }
        this.mList.setLifeCycleListener(new WrapperViewListLifeCycleListener());
        this.mList.setOnScrollListener(new WrapperListScrollListener());
        addView(this.mList);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureHeader(this.mHeader);
    }

    private void ensureHeaderHasCorrectLayoutParams(View header) {
        ViewGroup.LayoutParams lp = header.getLayoutParams();
        if (lp == null) {
            header.setLayoutParams(new LayoutParams(-1, -2));
        } else if (lp.height == -1 || lp.width == -2) {
            lp.height = -2;
            lp.width = -1;
            header.setLayoutParams(lp);
        }
    }

    private void measureHeader(View header) {
        if (header != null) {
            measureChild(header, MeasureSpec.makeMeasureSpec((getMeasuredWidth() - this.mPaddingLeft) - this.mPaddingRight, 1073741824), MeasureSpec.makeMeasureSpec(0, 0));
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        this.mList.layout(0, 0, this.mList.getMeasuredWidth(), getHeight());
        if (this.mHeader != null) {
            int headerTop = ((MarginLayoutParams) this.mHeader.getLayoutParams()).topMargin;
            this.mHeader.layout(this.mPaddingLeft, headerTop, this.mHeader.getMeasuredWidth() + this.mPaddingLeft, this.mHeader.getMeasuredHeight() + headerTop);
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        if (this.mList.getVisibility() == 0 || this.mList.getAnimation() != null) {
            drawChild(canvas, this.mList, 0);
        }
    }

    /* access modifiers changed from: private */
    public void clearHeader() {
        if (this.mHeader != null) {
            removeView(this.mHeader);
            this.mHeader = null;
            this.mHeaderId = null;
            this.mHeaderPosition = null;
            this.mHeaderOffset = null;
            this.mList.setTopClippingLength(0);
            updateHeaderVisibilities();
        }
    }

    /* access modifiers changed from: private */
    public void updateOrClearHeader(int firstVisiblePosition) {
        boolean doesListHaveChildren;
        boolean isFirstViewBelowTop;
        boolean isHeaderPositionOutsideAdapterRange = false;
        int adapterCount = this.mAdapter == null ? 0 : this.mAdapter.getCount();
        if (adapterCount != 0 && this.mAreHeadersSticky) {
            int headerPosition = firstVisiblePosition - this.mList.getHeaderViewsCount();
            if (this.mList.getChildCount() > 0 && this.mList.getChildAt(0).getBottom() < stickyHeaderTop()) {
                headerPosition++;
            }
            if (this.mList.getChildCount() != 0) {
                doesListHaveChildren = true;
            } else {
                doesListHaveChildren = false;
            }
            if (!doesListHaveChildren || this.mList.getFirstVisiblePosition() != 0 || this.mList.getChildAt(0).getTop() < stickyHeaderTop()) {
                isFirstViewBelowTop = false;
            } else {
                isFirstViewBelowTop = true;
            }
            if (headerPosition > adapterCount - 1 || headerPosition < 0) {
                isHeaderPositionOutsideAdapterRange = true;
            }
            if (!doesListHaveChildren || isHeaderPositionOutsideAdapterRange || isFirstViewBelowTop) {
                clearHeader();
            } else {
                updateHeader(headerPosition);
            }
        }
    }

    private void updateHeader(int headerPosition) {
        View child;
        if (this.mHeaderPosition == null || this.mHeaderPosition.intValue() != headerPosition) {
            this.mHeaderPosition = Integer.valueOf(headerPosition);
            long headerId = this.mAdapter.getHeaderId(headerPosition);
            if (this.mHeaderId == null || this.mHeaderId.longValue() != headerId) {
                this.mHeaderId = Long.valueOf(headerId);
                View header = this.mAdapter.getHeaderView(this.mHeaderPosition.intValue(), this.mHeader, this);
                if (this.mHeader != header) {
                    if (header == null) {
                        throw new NullPointerException("header may not be null");
                    }
                    swapHeader(header);
                }
                ensureHeaderHasCorrectLayoutParams(this.mHeader);
                measureHeader(this.mHeader);
                if (this.mOnStickyHeaderChangedListener != null) {
                    this.mOnStickyHeaderChangedListener.onStickyHeaderChanged(this, this.mHeader, headerPosition, this.mHeaderId.longValue());
                }
                this.mHeaderOffset = null;
            }
        }
        int headerOffset = stickyHeaderTop();
        int i = 0;
        while (true) {
            if (i >= this.mList.getChildCount()) {
                break;
            }
            child = this.mList.getChildAt(i);
            boolean doesChildHaveHeader = (child instanceof WrapperView) && ((WrapperView) child).hasHeader();
            boolean isChildFooter = this.mList.containsFooterView(child);
            if (child.getTop() < stickyHeaderTop() || (!doesChildHaveHeader && !isChildFooter)) {
                i++;
            }
        }
        headerOffset = Math.min(child.getTop() - this.mHeader.getMeasuredHeight(), headerOffset);
        setHeaderOffet(headerOffset);
        if (!this.mIsDrawingListUnderStickyHeader) {
            this.mList.setTopClippingLength(this.mHeader.getMeasuredHeight() + this.mHeaderOffset.intValue());
        }
        updateHeaderVisibilities();
    }

    private void swapHeader(View newHeader) {
        if (this.mHeader != null) {
            removeView(this.mHeader);
        }
        this.mHeader = newHeader;
        addView(this.mHeader);
        if (this.mOnHeaderClickListener != null) {
            this.mHeader.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    StickyListHeadersListView.this.mOnHeaderClickListener.onHeaderClick(StickyListHeadersListView.this, StickyListHeadersListView.this.mHeader, StickyListHeadersListView.this.mHeaderPosition.intValue(), StickyListHeadersListView.this.mHeaderId.longValue(), true);
                }
            });
        }
        this.mHeader.setClickable(true);
    }

    private void updateHeaderVisibilities() {
        int top = stickyHeaderTop();
        int childCount = this.mList.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = this.mList.getChildAt(i);
            if (child instanceof WrapperView) {
                WrapperView wrapperViewChild = (WrapperView) child;
                if (wrapperViewChild.hasHeader()) {
                    View childHeader = wrapperViewChild.mHeader;
                    if (wrapperViewChild.getTop() < top) {
                        if (childHeader.getVisibility() != 4) {
                            childHeader.setVisibility(4);
                        }
                    } else if (childHeader.getVisibility() != 0) {
                        childHeader.setVisibility(0);
                    }
                }
            }
        }
    }

    @SuppressLint({"NewApi"})
    private void setHeaderOffet(int offset) {
        if (this.mHeaderOffset == null || this.mHeaderOffset.intValue() != offset) {
            this.mHeaderOffset = Integer.valueOf(offset);
            if (Build.VERSION.SDK_INT >= 11) {
                this.mHeader.setTranslationY((float) this.mHeaderOffset.intValue());
            } else {
                MarginLayoutParams params = (MarginLayoutParams) this.mHeader.getLayoutParams();
                params.topMargin = this.mHeaderOffset.intValue();
                this.mHeader.setLayoutParams(params);
            }
            if (this.mOnStickyHeaderOffsetChangedListener != null) {
                this.mOnStickyHeaderOffsetChangedListener.onStickyHeaderOffsetChanged(this, this.mHeader, -this.mHeaderOffset.intValue());
            }
        }
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean z;
        if ((ev.getAction() & 255) == 0) {
            this.mDownY = ev.getY();
            if (this.mHeader == null || this.mDownY > ((float) (this.mHeader.getHeight() + this.mHeaderOffset.intValue()))) {
                z = false;
            } else {
                z = true;
            }
            this.mHeaderOwnsTouch = z;
        }
        if (!this.mHeaderOwnsTouch) {
            return this.mList.dispatchTouchEvent(ev);
        }
        if (this.mHeader != null && Math.abs(this.mDownY - ev.getY()) <= this.mTouchSlop) {
            return this.mHeader.dispatchTouchEvent(ev);
        }
        if (this.mHeader != null) {
            MotionEvent cancelEvent = MotionEvent.obtain(ev);
            cancelEvent.setAction(3);
            this.mHeader.dispatchTouchEvent(cancelEvent);
            cancelEvent.recycle();
        }
        MotionEvent downEvent = MotionEvent.obtain(ev.getDownTime(), ev.getEventTime(), ev.getAction(), ev.getX(), this.mDownY, ev.getMetaState());
        downEvent.setAction(0);
        boolean handled = this.mList.dispatchTouchEvent(downEvent);
        downEvent.recycle();
        this.mHeaderOwnsTouch = false;
        return handled;
    }

    private class AdapterWrapperDataSetObserver extends DataSetObserver {
        private AdapterWrapperDataSetObserver() {
        }

        public void onChanged() {
            StickyListHeadersListView.this.clearHeader();
        }

        public void onInvalidated() {
            StickyListHeadersListView.this.clearHeader();
        }
    }

    private class WrapperListScrollListener implements AbsListView.OnScrollListener {
        private WrapperListScrollListener() {
        }

        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (StickyListHeadersListView.this.mOnScrollListenerDelegate != null) {
                StickyListHeadersListView.this.mOnScrollListenerDelegate.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
            StickyListHeadersListView.this.updateOrClearHeader(StickyListHeadersListView.this.mList.getFixedFirstVisibleItem());
        }

        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (StickyListHeadersListView.this.mOnScrollListenerDelegate != null) {
                StickyListHeadersListView.this.mOnScrollListenerDelegate.onScrollStateChanged(view, scrollState);
            }
        }
    }

    private class WrapperViewListLifeCycleListener implements WrapperViewList.LifeCycleListener {
        private WrapperViewListLifeCycleListener() {
        }

        public void onDispatchDrawOccurred(Canvas canvas) {
            if (Build.VERSION.SDK_INT < 8) {
                StickyListHeadersListView.this.updateOrClearHeader(StickyListHeadersListView.this.mList.getFixedFirstVisibleItem());
            }
            if (StickyListHeadersListView.this.mHeader == null) {
                return;
            }
            if (StickyListHeadersListView.this.mClippingToPadding) {
                canvas.save();
                canvas.clipRect(0, StickyListHeadersListView.this.mPaddingTop, StickyListHeadersListView.this.getRight(), StickyListHeadersListView.this.getBottom());
                boolean unused = StickyListHeadersListView.this.drawChild(canvas, StickyListHeadersListView.this.mHeader, 0);
                canvas.restore();
                return;
            }
            boolean unused2 = StickyListHeadersListView.this.drawChild(canvas, StickyListHeadersListView.this.mHeader, 0);
        }
    }

    private class AdapterWrapperHeaderClickHandler implements AdapterWrapper.OnHeaderClickListener {
        private AdapterWrapperHeaderClickHandler() {
        }

        public void onHeaderClick(View header, int itemPosition, long headerId) {
            StickyListHeadersListView.this.mOnHeaderClickListener.onHeaderClick(StickyListHeadersListView.this, header, itemPosition, headerId, false);
        }
    }

    private boolean isStartOfSection(int position) {
        return position == 0 || this.mAdapter.getHeaderId(position) != this.mAdapter.getHeaderId(position + -1);
    }

    public int getHeaderOverlap(int position) {
        if (isStartOfSection(Math.max(0, position - getHeaderViewsCount()))) {
            return 0;
        }
        View header = this.mAdapter.getHeaderView(position, (View) null, this.mList);
        if (header == null) {
            throw new NullPointerException("header may not be null");
        }
        ensureHeaderHasCorrectLayoutParams(header);
        measureHeader(header);
        return header.getMeasuredHeight();
    }

    private int stickyHeaderTop() {
        return (this.mClippingToPadding ? this.mPaddingTop : 0) + this.mStickyHeaderTopOffset;
    }

    public void setAreHeadersSticky(boolean areHeadersSticky) {
        this.mAreHeadersSticky = areHeadersSticky;
        if (!areHeadersSticky) {
            clearHeader();
        } else {
            updateOrClearHeader(this.mList.getFixedFirstVisibleItem());
        }
        this.mList.invalidate();
    }

    public boolean areHeadersSticky() {
        return this.mAreHeadersSticky;
    }

    @Deprecated
    public boolean getAreHeadersSticky() {
        return areHeadersSticky();
    }

    public void setStickyHeaderTopOffset(int stickyHeaderTopOffset) {
        this.mStickyHeaderTopOffset = stickyHeaderTopOffset;
        updateOrClearHeader(this.mList.getFixedFirstVisibleItem());
    }

    public int getStickyHeaderTopOffset() {
        return this.mStickyHeaderTopOffset;
    }

    public void setDrawingListUnderStickyHeader(boolean drawingListUnderStickyHeader) {
        this.mIsDrawingListUnderStickyHeader = drawingListUnderStickyHeader;
        this.mList.setTopClippingLength(0);
    }

    public boolean isDrawingListUnderStickyHeader() {
        return this.mIsDrawingListUnderStickyHeader;
    }

    public void setOnHeaderClickListener(OnHeaderClickListener listener) {
        this.mOnHeaderClickListener = listener;
        if (this.mAdapter == null) {
            return;
        }
        if (this.mOnHeaderClickListener != null) {
            this.mAdapter.setOnHeaderClickListener(new AdapterWrapperHeaderClickHandler());
            if (this.mHeader != null) {
                this.mHeader.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        StickyListHeadersListView.this.mOnHeaderClickListener.onHeaderClick(StickyListHeadersListView.this, StickyListHeadersListView.this.mHeader, StickyListHeadersListView.this.mHeaderPosition.intValue(), StickyListHeadersListView.this.mHeaderId.longValue(), true);
                    }
                });
                return;
            }
            return;
        }
        this.mAdapter.setOnHeaderClickListener((AdapterWrapper.OnHeaderClickListener) null);
    }

    public void setOnStickyHeaderOffsetChangedListener(OnStickyHeaderOffsetChangedListener listener) {
        this.mOnStickyHeaderOffsetChangedListener = listener;
    }

    public void setOnStickyHeaderChangedListener(OnStickyHeaderChangedListener listener) {
        this.mOnStickyHeaderChangedListener = listener;
    }

    public View getListChildAt(int index) {
        return this.mList.getChildAt(index);
    }

    public int getListChildCount() {
        return this.mList.getChildCount();
    }

    public ListView getWrappedList() {
        return this.mList;
    }

    private boolean requireSdkVersion(int versionCode) {
        if (Build.VERSION.SDK_INT >= versionCode) {
            return true;
        }
        Log.e("StickyListHeaders", "Api lvl must be at least " + versionCode + " to call this method");
        return false;
    }

    public void setAdapter(StickyListHeadersAdapter adapter) {
        if (adapter == null) {
            if (this.mAdapter instanceof SectionIndexerAdapterWrapper) {
                ((SectionIndexerAdapterWrapper) this.mAdapter).mSectionIndexerDelegate = null;
            }
            if (this.mAdapter != null) {
                this.mAdapter.mDelegate = null;
            }
            this.mList.setAdapter((ListAdapter) null);
            clearHeader();
            return;
        }
        if (this.mAdapter != null) {
            this.mAdapter.unregisterDataSetObserver(this.mDataSetObserver);
        }
        if (adapter instanceof SectionIndexer) {
            this.mAdapter = new SectionIndexerAdapterWrapper(getContext(), adapter);
        } else {
            this.mAdapter = new AdapterWrapper(getContext(), adapter);
        }
        this.mDataSetObserver = new AdapterWrapperDataSetObserver();
        this.mAdapter.registerDataSetObserver(this.mDataSetObserver);
        if (this.mOnHeaderClickListener != null) {
            this.mAdapter.setOnHeaderClickListener(new AdapterWrapperHeaderClickHandler());
        } else {
            this.mAdapter.setOnHeaderClickListener((AdapterWrapper.OnHeaderClickListener) null);
        }
        this.mAdapter.setDivider(this.mDivider, this.mDividerHeight);
        this.mList.setAdapter(this.mAdapter);
        clearHeader();
    }

    public StickyListHeadersAdapter getAdapter() {
        if (this.mAdapter == null) {
            return null;
        }
        return this.mAdapter.mDelegate;
    }

    public void setDivider(Drawable divider) {
        this.mDivider = divider;
        if (this.mAdapter != null) {
            this.mAdapter.setDivider(this.mDivider, this.mDividerHeight);
        }
    }

    public void setDividerHeight(int dividerHeight) {
        this.mDividerHeight = dividerHeight;
        if (this.mAdapter != null) {
            this.mAdapter.setDivider(this.mDivider, this.mDividerHeight);
        }
    }

    public Drawable getDivider() {
        return this.mDivider;
    }

    public int getDividerHeight() {
        return this.mDividerHeight;
    }

    public void setOnScrollListener(AbsListView.OnScrollListener onScrollListener) {
        this.mOnScrollListenerDelegate = onScrollListener;
    }

    public void setOnTouchListener(final OnTouchListener l) {
        if (l != null) {
            this.mList.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    return l.onTouch(StickyListHeadersListView.this, event);
                }
            });
        } else {
            this.mList.setOnTouchListener((OnTouchListener) null);
        }
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        this.mList.setOnItemClickListener(listener);
    }

    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener) {
        this.mList.setOnItemLongClickListener(listener);
    }

    public void addHeaderView(View v, Object data, boolean isSelectable) {
        this.mList.addHeaderView(v, data, isSelectable);
    }

    public void addHeaderView(View v) {
        this.mList.addHeaderView(v);
    }

    public void removeHeaderView(View v) {
        this.mList.removeHeaderView(v);
    }

    public int getHeaderViewsCount() {
        return this.mList.getHeaderViewsCount();
    }

    public void addFooterView(View v, Object data, boolean isSelectable) {
        this.mList.addFooterView(v, data, isSelectable);
    }

    public void addFooterView(View v) {
        this.mList.addFooterView(v);
    }

    public void removeFooterView(View v) {
        this.mList.removeFooterView(v);
    }

    public int getFooterViewsCount() {
        return this.mList.getFooterViewsCount();
    }

    public void setEmptyView(View v) {
        this.mList.setEmptyView(v);
    }

    public View getEmptyView() {
        return this.mList.getEmptyView();
    }

    public boolean isVerticalScrollBarEnabled() {
        return this.mList.isVerticalScrollBarEnabled();
    }

    public boolean isHorizontalScrollBarEnabled() {
        return this.mList.isHorizontalScrollBarEnabled();
    }

    public void setVerticalScrollBarEnabled(boolean verticalScrollBarEnabled) {
        this.mList.setVerticalScrollBarEnabled(verticalScrollBarEnabled);
    }

    public void setHorizontalScrollBarEnabled(boolean horizontalScrollBarEnabled) {
        this.mList.setHorizontalScrollBarEnabled(horizontalScrollBarEnabled);
    }

    @TargetApi(9)
    public int getOverScrollMode() {
        if (requireSdkVersion(9)) {
            return this.mList.getOverScrollMode();
        }
        return 0;
    }

    @TargetApi(9)
    public void setOverScrollMode(int mode) {
        if (requireSdkVersion(9) && this.mList != null) {
            this.mList.setOverScrollMode(mode);
        }
    }

    @TargetApi(8)
    public void smoothScrollBy(int distance, int duration) {
        if (requireSdkVersion(8)) {
            this.mList.smoothScrollBy(distance, duration);
        }
    }

    @TargetApi(11)
    public void smoothScrollByOffset(int offset) {
        if (requireSdkVersion(11)) {
            this.mList.smoothScrollByOffset(offset);
        }
    }

    @SuppressLint({"NewApi"})
    @TargetApi(8)
    public void smoothScrollToPosition(int position) {
        int i = 0;
        if (!requireSdkVersion(8)) {
            return;
        }
        if (Build.VERSION.SDK_INT < 11) {
            this.mList.smoothScrollToPosition(position);
            return;
        }
        int offset = this.mAdapter == null ? 0 : getHeaderOverlap(position);
        if (!this.mClippingToPadding) {
            i = this.mPaddingTop;
        }
        this.mList.smoothScrollToPositionFromTop(position, offset - i);
    }

    @TargetApi(8)
    public void smoothScrollToPosition(int position, int boundPosition) {
        if (requireSdkVersion(8)) {
            this.mList.smoothScrollToPosition(position, boundPosition);
        }
    }

    @TargetApi(11)
    public void smoothScrollToPositionFromTop(int position, int offset) {
        int i = 0;
        if (requireSdkVersion(11)) {
            int offset2 = offset + (this.mAdapter == null ? 0 : getHeaderOverlap(position));
            if (!this.mClippingToPadding) {
                i = this.mPaddingTop;
            }
            this.mList.smoothScrollToPositionFromTop(position, offset2 - i);
        }
    }

    @TargetApi(11)
    public void smoothScrollToPositionFromTop(int position, int offset, int duration) {
        int i = 0;
        if (requireSdkVersion(11)) {
            int offset2 = offset + (this.mAdapter == null ? 0 : getHeaderOverlap(position));
            if (!this.mClippingToPadding) {
                i = this.mPaddingTop;
            }
            this.mList.smoothScrollToPositionFromTop(position, offset2 - i, duration);
        }
    }

    public void setSelection(int position) {
        setSelectionFromTop(position, 0);
    }

    public void setSelectionAfterHeaderView() {
        this.mList.setSelectionAfterHeaderView();
    }

    public void setSelectionFromTop(int position, int y) {
        int i = 0;
        int y2 = y + (this.mAdapter == null ? 0 : getHeaderOverlap(position));
        if (!this.mClippingToPadding) {
            i = this.mPaddingTop;
        }
        this.mList.setSelectionFromTop(position, y2 - i);
    }

    public void setSelector(Drawable sel) {
        this.mList.setSelector(sel);
    }

    public void setSelector(int resID) {
        this.mList.setSelector(resID);
    }

    public int getFirstVisiblePosition() {
        return this.mList.getFirstVisiblePosition();
    }

    public int getLastVisiblePosition() {
        return this.mList.getLastVisiblePosition();
    }

    @TargetApi(11)
    public void setChoiceMode(int choiceMode) {
        this.mList.setChoiceMode(choiceMode);
    }

    @TargetApi(11)
    public void setItemChecked(int position, boolean value) {
        this.mList.setItemChecked(position, value);
    }

    @TargetApi(11)
    public int getCheckedItemCount() {
        if (requireSdkVersion(11)) {
            return this.mList.getCheckedItemCount();
        }
        return 0;
    }

    @TargetApi(8)
    public long[] getCheckedItemIds() {
        if (requireSdkVersion(8)) {
            return this.mList.getCheckedItemIds();
        }
        return null;
    }

    @TargetApi(11)
    public int getCheckedItemPosition() {
        return this.mList.getCheckedItemPosition();
    }

    @TargetApi(11)
    public SparseBooleanArray getCheckedItemPositions() {
        return this.mList.getCheckedItemPositions();
    }

    public int getCount() {
        return this.mList.getCount();
    }

    public Object getItemAtPosition(int position) {
        return this.mList.getItemAtPosition(position);
    }

    public long getItemIdAtPosition(int position) {
        return this.mList.getItemIdAtPosition(position);
    }

    public void setOnCreateContextMenuListener(OnCreateContextMenuListener l) {
        this.mList.setOnCreateContextMenuListener(l);
    }

    public boolean showContextMenu() {
        return this.mList.showContextMenu();
    }

    public void invalidateViews() {
        this.mList.invalidateViews();
    }

    public void setClipToPadding(boolean clipToPadding) {
        if (this.mList != null) {
            this.mList.setClipToPadding(clipToPadding);
        }
        this.mClippingToPadding = clipToPadding;
    }

    public void setPadding(int left, int top, int right, int bottom) {
        this.mPaddingLeft = left;
        this.mPaddingTop = top;
        this.mPaddingRight = right;
        this.mPaddingBottom = bottom;
        if (this.mList != null) {
            this.mList.setPadding(left, top, right, bottom);
        }
        super.setPadding(0, 0, 0, 0);
        requestLayout();
    }

    /* access modifiers changed from: protected */
    public void recomputePadding() {
        setPadding(this.mPaddingLeft, this.mPaddingTop, this.mPaddingRight, this.mPaddingBottom);
    }

    public int getPaddingLeft() {
        return this.mPaddingLeft;
    }

    public int getPaddingTop() {
        return this.mPaddingTop;
    }

    public int getPaddingRight() {
        return this.mPaddingRight;
    }

    public int getPaddingBottom() {
        return this.mPaddingBottom;
    }

    public void setFastScrollEnabled(boolean fastScrollEnabled) {
        this.mList.setFastScrollEnabled(fastScrollEnabled);
    }

    @TargetApi(11)
    public void setFastScrollAlwaysVisible(boolean alwaysVisible) {
        if (requireSdkVersion(11)) {
            this.mList.setFastScrollAlwaysVisible(alwaysVisible);
        }
    }

    @TargetApi(11)
    public boolean isFastScrollAlwaysVisible() {
        if (Build.VERSION.SDK_INT < 11) {
            return false;
        }
        return this.mList.isFastScrollAlwaysVisible();
    }

    public void setScrollBarStyle(int style) {
        this.mList.setScrollBarStyle(style);
    }

    public int getScrollBarStyle() {
        return this.mList.getScrollBarStyle();
    }

    public int getPositionForView(View view) {
        return this.mList.getPositionForView(view);
    }

    @TargetApi(11)
    public void setMultiChoiceModeListener(AbsListView.MultiChoiceModeListener listener) {
        if (requireSdkVersion(11)) {
            this.mList.setMultiChoiceModeListener(listener);
        }
    }

    public Parcelable onSaveInstanceState() {
        if (super.onSaveInstanceState() == BaseSavedState.EMPTY_STATE) {
            return this.mList.onSaveInstanceState();
        }
        throw new IllegalStateException("Handling non empty state of parent class is not implemented");
    }

    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(BaseSavedState.EMPTY_STATE);
        this.mList.onRestoreInstanceState(state);
    }

    @TargetApi(14)
    public boolean canScrollVertically(int direction) {
        return this.mList.canScrollVertically(direction);
    }

    public void setTranscriptMode(int mode) {
        this.mList.setTranscriptMode(mode);
    }

    public void setBlockLayoutChildren(boolean blockLayoutChildren) {
        this.mList.setBlockLayoutChildren(blockLayoutChildren);
    }

    public void setStackFromBottom(boolean stackFromBottom) {
        this.mList.setStackFromBottom(stackFromBottom);
    }

    public boolean isStackFromBottom() {
        return this.mList.isStackFromBottom();
    }
}
