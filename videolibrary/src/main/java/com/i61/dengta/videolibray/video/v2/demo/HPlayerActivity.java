package com.i61.dengta.videolibray.video.v2.demo;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.i61.dengta.videolibray.R;
import com.i61.dengta.videolibray.video.v2.VideoPlayer;
import com.i61.dengta.videolibray.video.v2.VideoijkBean;
import com.i61.dengta.videolibray.video.v2.listener.OnShowThumbnailListener;
import com.i61.dengta.videolibray.video.v2.ui.BaseVideoActivity;
import com.i61.dengta.videolibray.video.v2.util.GlideUtil;

import java.util.ArrayList;
import java.util.List;

public class HPlayerActivity extends BaseVideoActivity {

    private List<VideoijkBean> list = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String url1 = url1 = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4";
        String url2 = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f30.mp4";
        VideoijkBean m1 = new VideoijkBean();
        m1.setStream("标清");
        m1.setUrl(url1);
        VideoijkBean m2 = new VideoijkBean();
        m2.setStream("高清");
        m2.setUrl(url2);
        list.add(m1);
        list.add(m2);

        mPlayer = VideoPlayer.createCommonVideoPlayer(this, mRootView, list, new OnShowThumbnailListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onShowThumbnail(ImageView ivThumbnail) {
                GlideUtil.loadPicture(ivThumbnail, "http://pic2.nipic.com/20090413/406638_125424003_2.jpg", getResources().getColor(R.color.common_text_color));
            }
        })
                .setTitle("深圳市绿色低碳科技促进会").startPlay();
    }
}
