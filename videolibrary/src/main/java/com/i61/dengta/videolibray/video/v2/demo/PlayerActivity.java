package com.i61.dengta.videolibray.video.v2.demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.i61.dengta.videolibray.R;
import com.i61.dengta.videolibray.video.v2.VideoPlayer;
import com.i61.dengta.videolibray.video.v2.listener.OnShowThumbnailListener;
import com.i61.dengta.videolibray.video.v2.util.GlideUtil;

public class PlayerActivity extends Activity {

    private VideoPlayer player;
    private Context mContext;
    private View rootView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        rootView = LayoutInflater.from(this).inflate(R.layout.v2_simple_player_view, null);
        setContentView(rootView);
        String url = "http://media6.61info.cn/groupbuy/c_13_8_video.mp4?v=20180823";

        player = VideoPlayer.createCommonVideoPlayer(this, rootView, null, new OnShowThumbnailListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onShowThumbnail(ImageView ivThumbnail) {
                GlideUtil.loadPicture(ivThumbnail, "http://pic2.nipic.com/20090413/406638_125424003_2.jpg", getResources().getColor(R.color.common_text_color));
            }
        })
                .setPlaySource(url)
                .setTitle("深圳市绿色低碳科技促进会")
                .hideCast(true)
                .hideMenu(true)
                .startPlay();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            player.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.onDestroy();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (player != null) {
            player.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onBackPressed() {
        if (player != null && player.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }
}
