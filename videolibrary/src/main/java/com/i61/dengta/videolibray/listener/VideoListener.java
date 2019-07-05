package com.i61.dengta.videolibray.listener;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * @author caoruijia
 * 合并MediaPlayer所有的监听器
 * IMediaPlayer.OnBufferingUpdateListener
 * IMediaPlayer.OnCompletionListener
 * IMediaPlayer.OnPreparedListener
 * IMediaPlayer.OnInfoListener
 * IMediaPlayer.OnErrorListener
 * IMediaPlayer.OnSeekCompleteListener
 */
public interface VideoListener extends IMediaPlayer.OnBufferingUpdateListener, IMediaPlayer.OnCompletionListener, IMediaPlayer.OnPreparedListener, IMediaPlayer.OnInfoListener, IMediaPlayer.OnErrorListener, IMediaPlayer.OnSeekCompleteListener {
}
