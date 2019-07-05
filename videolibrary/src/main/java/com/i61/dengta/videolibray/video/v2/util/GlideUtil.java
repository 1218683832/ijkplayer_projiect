package com.i61.dengta.videolibray.video.v2.util;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

public class GlideUtil {

    public static void loadPicture(ImageView mIv, String url, int placeHolder) {
        try {
            Glide.with(mIv)
                    .load(url)
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .apply(RequestOptions.noAnimation())
                    .apply(new RequestOptions().placeholder(placeHolder))
                    .into(mIv);
        } catch (Exception | Error error) {
            error.printStackTrace();
        }
    }
}
