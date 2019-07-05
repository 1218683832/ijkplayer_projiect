package com.i61.dengta.videolibray.video.v2.util;

public class VideoUtils {
    /**
     * 播放时长格式化显示
     *
     * @param totalMilliSecond，单位毫秒
     * @return
     */
    public static String generateTime(long totalMilliSecond) {
        int totalSeconds = (int) (totalMilliSecond / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * 播放时长格式化显示
     *
     * @param totalSeconds，单位秒
     * @return
     */
    public static String generateTime(String totalSeconds) {
        long duration = Long.parseLong(totalSeconds);
        int seconds = (int) (duration % 60);
        int minutes = (int) ((duration / 60) % 60);
        int hours = (int) (duration / 3600);
        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * 下载速度格式化显示
     */
    public static String formatSize(int size) {
        long fileSize = (long) size;
        String showSize = "";
        if (fileSize >= 0 && fileSize < 1024) {
            showSize = fileSize + "Kb/s";
        } else if (fileSize >= 1024 && fileSize < (1024 * 1024)) {
            showSize = Long.toString(fileSize / 1024) + "KB/s";
        } else if (fileSize >= (1024 * 1024) && fileSize < (1024 * 1024 * 1024)) {
            showSize = Long.toString(fileSize / (1024 * 1024)) + "MB/s";
        }
        return showSize;
    }
}
