package com.i61.dengta.videolibray.video.v2.listener;

import android.widget.ImageView;

public interface OnChangeAudioThumbSizeListener {

    /**
     * 回传封面的view，让用户自主设置
     */
    void onChangeSize(ImageView ivThumbnail, int width, int height);
}