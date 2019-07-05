package com.i61.dengta.videolibray.video.v2.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class MediaUtils {

    /**
     * key:媒体格式类型，value:正则通配符
     */
    public final static Map<String, String> FILE_TYPE_MAP = new HashMap<>();

    public final static String sRegexAudio = ".*(mp3).*";

    public final static String sRegexVideo = ".*(mp4|m3u8).*";

    private MediaUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    static {
        allFileType(); //初始化文件类型信息
    }

    private static void allFileType() {
        FILE_TYPE_MAP.put("mp3", ".*(.mp3).*");
        FILE_TYPE_MAP.put("mp4", ".*(.mp4).*");
        FILE_TYPE_MAP.put("m3u8", ".*(.m3u8).*");
    }

    /**
     * 获得媒体类型
     *
     * @param url
     * @return
     */
    public static String obtainTypeByUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return "";
        }
        String inputUrl = url.toLowerCase();
        for (Map.Entry<String, String> entry : FILE_TYPE_MAP.entrySet()) {
            if (Pattern.matches(entry.getValue(), inputUrl)) {
                return entry.getKey();
            }
        }
        return "";
    }

    public static boolean isAudioMedia(String url) {
        String mediaType = obtainTypeByUrl(url);
        return Pattern.matches(sRegexAudio, mediaType);
    }

    public static boolean isVideoMedia(String url) {
        String mediaType = obtainTypeByUrl(url);
        return Pattern.matches(sRegexVideo, mediaType);
    }

    /**
     * @param bMute 值为true时为关闭背景音乐。
     */
    @TargetApi(Build.VERSION_CODES.FROYO)
    public static boolean muteAudioFocus(Context context, boolean bMute) {
        boolean bool = false;
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (bMute) {
            int result = am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        } else {
            int result = am.abandonAudioFocus(null);
            bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        }
        return bool;
    }
}