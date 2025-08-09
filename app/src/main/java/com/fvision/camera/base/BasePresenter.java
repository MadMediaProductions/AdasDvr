package com.fvision.camera.base;

import android.os.Bundle;
import com.fvision.camera.listener.LifeCycleListener;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

public class BasePresenter<V, T> implements LifeCycleListener {
    protected T mActivity;
    protected Reference<T> mActivityRef;
    protected V mView;
    protected Reference<V> mViewRef;

    public BasePresenter(V view, T activity) {
        attachView(view);
        attachActivity(activity);
        setListener(activity);
    }

    private void setListener(T activity) {
        if (getActivity() != null && (activity instanceof BaseActivity)) {
            ((BaseActivity) getActivity()).setOnLifeCycleListener(this);
        }
    }

    private void attachView(V view) {
        this.mViewRef = new WeakReference(view);
        this.mView = this.mViewRef.get();
    }

    private void attachActivity(T activity) {
        this.mActivityRef = new WeakReference(activity);
        this.mActivity = this.mActivityRef.get();
    }

    private void detachView() {
        if (isViewAttached()) {
            this.mViewRef.clear();
            this.mViewRef = null;
        }
    }

    private void detachActivity() {
        if (isActivityAttached()) {
            this.mActivityRef.clear();
            this.mActivityRef = null;
        }
    }

    public V getView() {
        if (this.mViewRef == null) {
            return null;
        }
        return this.mViewRef.get();
    }

    public T getActivity() {
        if (this.mActivityRef == null) {
            return null;
        }
        return this.mActivityRef.get();
    }

    public boolean isViewAttached() {
        return (this.mViewRef == null || this.mViewRef.get() == null) ? false : true;
    }

    public boolean isActivityAttached() {
        return (this.mActivityRef == null || this.mActivityRef.get() == null) ? false : true;
    }

    public void onCreate(Bundle savedInstanceState) {
    }

    public void onStart() {
    }

    public void onRestart() {
    }

    public void onResume() {
    }

    public void onPause() {
    }

    public void onStop() {
    }

    public void onDestroy() {
        detachView();
        detachActivity();
    }
}
