package com.i61.dengta.videolibray.video.v2.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;

import com.i61.dengta.videolibray.R;
import com.i61.dengta.videolibray.video.v2.VideoPlayer;

public abstract class BaseVideoActivity extends AppCompatActivity {

    /**
     * mRootView
     */
    protected View mRootView;

    protected VideoPlayer mPlayer;
    private PowerManager.WakeLock wakeLock;
    protected long mCurrentTime;

    protected View initRootView() {
        mRootView = LayoutInflater.from(this).inflate(R.layout.v2_activity_base_video, null);
        return mRootView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(initRootView());
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                //比较Activity根布局与当前布局的大小
                int heightDiff = mRootView.getRootView().getHeight() - mRootView.getHeight();
                if (heightDiff > 100) {
                    //大小超过100时，一般为显示虚拟键盘事件
                    mRootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                } else {
                    //大小小于100时，为不显示虚拟键盘或虚拟键盘隐藏
                    mRootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                }
            }
        });
        /**常亮*/
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "liveTAG");
        wakeLock.acquire();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPlayer != null) {
            mCurrentTime = mPlayer.getCurrentPosition();
            mPlayer.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPlayer != null) {
            mPlayer.onResume();
        }
        /**激活设备常亮状态*/
        if (wakeLock != null) {
            wakeLock.acquire();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            mPlayer.onDestroy();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mPlayer != null) {
            mPlayer.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onBackPressed() {
        if (mPlayer != null && mPlayer.onBackPressed()) {
            return;
        }
        super.onBackPressed();

        try {
            /**恢复设备亮度状态*/
            if (wakeLock != null) {
                wakeLock.release();
            }
        } catch (Throwable th) {
            // ignoring this exception, probably wakeLock was already released
        }
    }
}
