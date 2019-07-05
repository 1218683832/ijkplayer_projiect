package com.i61.dengta.videolibray.video.v2;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * <p>
 * 描 述：通过Acitvity去查询界面布局中对应的view
 * <p>
 * <p>
 * 修订历史：
 * <p>
 * 查询View性能改造
 */
public class LayoutQuery {
    /**
     * 打印日志的TAG
     */
    private static final String TAG = LayoutQuery.class.getSimpleName();

    private Context mContext;
    private Activity mActivity;
    private View mRootView;
    private View mView;
    /**
     * 缓存View
     */
    private SparseArray<View> mViewCache;

    /**
     * 拓展播放器view的方法使用
     */
    public LayoutQuery(Activity activity, View rootView) {
        this.mContext = activity;
        this.mActivity = activity;
        this.mRootView = rootView;
        this.mViewCache = new SparseArray<>();
    }

    /**
     * 通过查询器获取View
     *
     * @param id
     * @return
     */
    public View getView(int id) {
        id(id);
        View v = mView;
        return v;
    }

    public LayoutQuery id(int id) {
        View v = mViewCache.get(id);
        if (v == null) {
            if (mRootView == null) {
                v = mActivity.findViewById(id);
            } else {
                v = mRootView.findViewById(id);
            }
            mViewCache.put(id, v);
        }
        mView = v;
        return this;
    }

    public LayoutQuery image(int resId) {
        if (mView instanceof ImageView) {
            ((ImageView) mView).setImageResource(resId);
        }
        return this;
    }

    public LayoutQuery visible() {
        if (mView != null) {
            mView.setVisibility(View.VISIBLE);
        }
        return this;
    }

    public LayoutQuery gone() {
        if (mView != null) {
            mView.setVisibility(View.GONE);
        }
        return this;
    }

    public LayoutQuery invisible() {
        if (mView != null) {
            mView.setVisibility(View.INVISIBLE);
        }
        return this;
    }

    public LayoutQuery clicked(View.OnClickListener handler) {
        if (mView != null) {
            mView.setOnClickListener(handler);
        }
        return this;
    }

    public LayoutQuery text(CharSequence text) {
        if (mView != null && mView instanceof TextView) {
            ((TextView) mView).setText(text);
        }
        return this;
    }

    public LayoutQuery visibility(int visible) {
        if (mView != null) {
            mView.setVisibility(visible);
        }
        return this;
    }

    private void size(boolean width, int n, boolean dip) {
        if (mView != null) {
            ViewGroup.LayoutParams lp = mView.getLayoutParams();
            if (n > 0 && dip) {
                n = dip2pixel(mContext, n);
            }
            if (width) {
                lp.width = n;
            } else {
                lp.height = n;
            }
            mView.setLayoutParams(lp);

        }

    }

    public LayoutQuery height(int height, boolean dip) {
        size(false, height, dip);
        return this;
    }

    public LayoutQuery width(int width, boolean dip) {
        size(true, width, dip);
        return this;
    }

    public int dip2pixel(Context context, float n) {
        int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, n, context.getResources().getDisplayMetrics());
        return value;
    }

    public float pixel2dip(Context context, float n) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = n / (metrics.densityDpi / 160f);
        return dp;

    }

    public void release() {
        mContext = mActivity = null;
        mRootView = mView = null;
    }
}