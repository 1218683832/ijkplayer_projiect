package com.i61.dengta.videolibray.video.v2;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.i61.dengta.videolibray.R;
import com.i61.dengta.videolibray.video.v2.listener.OnChangeAudioThumbSizeListener;
import com.i61.dengta.videolibray.video.v2.listener.OnClickedViewListener;
import com.i61.dengta.videolibray.video.v2.listener.OnControlPanelVisibilityChangeListener;
import com.i61.dengta.videolibray.video.v2.listener.OnPlayCompleteListener;
import com.i61.dengta.videolibray.video.v2.listener.OnPlayerBackListener;
import com.i61.dengta.videolibray.video.v2.listener.OnShowThumbnailListener;
import com.i61.dengta.videolibray.video.v2.util.MediaUtils;
import com.i61.dengta.videolibray.video.v2.util.NetworkUtils;
import com.i61.dengta.videolibray.video.v2.util.VideoUtils;

import java.util.ArrayList;
import java.util.List;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * 播放器类
 *
 * @author lipin
 * @version 1.0
 * @date 2019.06.05
 */
public class VideoPlayer implements View.OnClickListener {
    /**
     * 打印日志的TAG
     */
    private static final String TAG = VideoPlayer.class.getSimpleName();

    /**
     * 依附的容器Activity
     */
    private final Activity mActivity;
    /**
     * 全局上下文
     */
    private final Context mContext;
    /**
     * 音频管理器
     */
    private AudioManager mAudioManager;
    /**
     * 获取当前设备的宽度
     */
    private int mScreenWidthPixels;
    /**
     * 记录播放器竖屏时的高度
     */
    private int mInitHeight;
    /**
     * 设备最大音量
     */
    private int mMaxVolume;
    /**
     * 当前声音大小
     */
    private int mVolume;
    /**
     * 当前亮度大小
     */
    private float mBrightness;
    /**
     * Activity界面中的布局的查询器
     */
    private LayoutQuery mQuery;
    private Drawable mSeekBarThumb;
    /**
     * 第三方so是否支持，默认不支持，true为支持
     */
    private boolean mPlayerSupport;
    /**
     * 是否作为音频播放器，true表示音频播放器，false表示视频播放器，默认false视频播放器
     */
    private boolean mAsAudioPlayer = false;
    /**
     * Ijkplayer控件
     */
    private IjkVideoView mVideoView;
    /**
     * 播放器界面View
     */
    private View mVideoWholeView;
    private View mRootView;
    /**
     * 播放器手势监听
     */
    private GestureDetector mGestureDetector;
    /**
     * 播放器顶部控件显示栏
     */
    private View mTopControlbarContainer;
    /**
     * 播放器底部控件显示栏
     */
    private View mBottomControlbarContainer;
    /**
     * 播放器中间控件显示栏
     */
    private View mCenterControlbarContainer;
    /**
     * 播放器右边分辨率选择
     */
    private View mSelectStreamContainer;
    /**
     * 不同分辨率的列表布局
     */
    private ListView mSelectStreamListView;
    /**
     * 播放器封面，播放前的封面或者缩列图
     */
    private ImageView mIvTrumb;
    private ImageView mIvTrumbAudio;
    /**
     * 视频分辨率
     */
    private TextView mTvSteam;
    /**
     * 播放器中间控件显示栏，播放按钮
     */
    private ImageView mIvPlay;
    /**
     * 播放器中间控件显示栏，声音显示lay
     */
    private View mVolumeBox;
    /**
     * 播放器中间控件显示栏，亮度显示lay
     */
    private View mBrightnessBox;
    /**
     * 播放器中间控件显示栏，快进快退显示lay
     */
    private View mFastForwardBox;
    /**
     * 播放器中间控件显示栏，加载中lay
     */
    private View mLoadingBox;
    /**
     * 播放器中间控件显示栏，最大试看时长提示lay
     */
    private View mFreeTieBox;
    /**
     * 播放器中间控件显示栏，重新播放lay
     */
    private View mReplayBox;
    /**
     * 播放器中间控件显示栏，移动网络流量费用提示lay
     */
    private View mNetTieBox;
    /**
     * 视频播放进度条
     */
    private SeekBar mSeekBar;
    /**
     * 视频播放进度条最大值
     */
    private static final int sMaxVideoProgress = 1000;
    /**
     * 视频旋转的角度，默认只有0,90.270分别对应向上、向左、向右三个方向
     */
    private int mRotation = 0;
    /**
     * 视频显示比例
     */
    private int mCurrentShowType = PlayStateParams.wrapcontent;
    /**
     * 是否只有全屏，默认非全屏，true为全屏，false为非全屏
     */
    private boolean mIsOnlyFullScreen = false;
    /**
     * 是否禁止双击，默认不禁止，true为禁止，false为不禁止
     */
    private boolean mIsForbidDoulbeUp = false;
    /**
     * 是否是竖屏，默认为竖屏，true为竖屏，false为横屏
     */
    private boolean mIsPortrait = true;
    /**
     * 是否隐藏中间播放按钮，默认不隐藏，true为隐藏，false为不隐藏
     */
    private boolean mIsHideCenterPlayer = false;
    /**
     * 是否显示加载网速，默认不显示，true为显示，false为不显示
     */
    private boolean mIsShowSpeed = false;
    /**
     * 是否付费观看，true为收费 false为免费即不做限制
     */
    private boolean mIsCharge = false;
    /**
     * 禁止触摸，默认可以触摸，true为禁止false为可触摸
     */
    private boolean mIsForbidTouch = false;
    /**
     * 禁止收起控制面板，默认可以收起，true为禁止false为可触摸
     */
    private boolean mIsForbidHideControlPanl = false;
    /**
     * 是否显示控制面板，默认为隐藏，true为显示false为隐藏
     */
    private boolean mIsShowControlPanl = false;
    /**
     * 播放的时候是否需要移动网络流量费用提示，默认显示移动网络流量费用提示，true为显示移动网络流量费用提示，false不显示移动网络流量费用提示
     */
    private boolean mIsShowNetWorkTip = true;
    /**
     * 是否出错停止播放，默认是用户点击停止播放，true出错停止播放,false为用户点击停止播放
     */
    private boolean mIsErrorStop = false;
    /**
     * 是否自动重连，默认5秒重连，true为重连，false为不重连
     */
    private boolean mIsAutoReConnect = true;
    /**
     * 当前是否切换视频流，默认为false，true是切换视频流，false没有切换
     */
    private boolean mIsHasSwitchStream = false;
    /**
     * 自动重连延迟时间，单位秒
     */
    private static int mAutoConnectTime = 5;
    /**
     * 回退时间 5 * 1000
     */
    private static final int mBackTime = 5 * 1000;
    /**
     * 付费观看最大能播放时长，单位毫秒
     */
    private int mMaxPlaytime;
    /**
     * 是否在拖动进度条中，默认为停止拖动，true为在拖动中，false为停止拖动
     */
    private boolean mIsDragging = false;
    /**
     * 设置进度条和时长显示的样式，视频当前播放/总时长，位于进度条左右端样式
     */
    private int mProcessDurationOrientation = PlayStateParams.PROCESS_LANDSCAPE_LEFT_RIGHT;
    /**
     * 当前播放地址
     */
    private String mCurrentUrl;
    /**
     * 当前选择的视频流索引
     */
    private int mCurrentSelect;
    /**
     * 播放总时长
     */
    private long mDuration;
    /**
     * 视频当前播放位置
     */
    private int mCurrentPosition;
    /**
     * 滑动进度条得到的新位置，和当前播放位置是有区别的,mNewPosition =0也会调用设置的，故初始化值为-1
     */
    private long mNewPosition = -1;
    /**
     * 当前播放器状态
     */
    private int mStatus = PlayStateParams.STATE_IDLE;
    /**
     * 记录进行后台时的播放状态0为播放，1为暂停
     */
    private int mBgState;
    /**
     * 音频旋转动画
     */
    private ObjectAnimator mRotateAnimator;

    /**
     * 码流列表
     */
    private List<VideoijkBean> mListVideos = new ArrayList<>();
    /**
     * 码流列表适配器
     */
    private StreamSelectAdapter mStreamSelectAdapter;

    /**========================================视频的监听方法==============================================*/
    /**
     * 视频播放完成监听
     */
    private OnPlayCompleteListener mOnPlayCompleteListener;
    /**
     * 视频的返回键监听
     */
    private OnPlayerBackListener mPlayerBack;
    /**
     * 控制面板显示或隐藏监听
     */
    private OnControlPanelVisibilityChangeListener mOnControlPanelVisibilityChangeListener;
    /**
     * 视频播放时信息回调
     */
    private IMediaPlayer.OnInfoListener mOnInfoListener;
    /**
     * 视频封面显示监听
     */
    private OnShowThumbnailListener mOnShowThumbnailListener;
    private OnChangeAudioThumbSizeListener mOnChangeAudioThumbSizeListener;
    /**
     * 投屏按钮、重播按钮点击监听
     */
    private OnClickedViewListener mOnClickedViewListener;
    /**
     * Activity界面方向监听
     */
    private OrientationEventListener mOrientationEventListener;
    /**
     * 控制面板收起或者显示的轮询监听
     */
    private AutoPlayRunnable mAutoPlayRunnable = new AutoPlayRunnable();
    /**
     * 消息处理
     */
    @SuppressWarnings("HandlerLeak")
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                /**滑动完成，隐藏滑动提示的box*/
                case PlayStateParams.MESSAGE_HIDE_CENTER_BOX:
                    /**隐藏声音显示lay*/
                    mVolumeBox.setVisibility(View.GONE);
                    /**隐藏亮度显示lay*/
                    mBrightnessBox.setVisibility(View.GONE);
                    /**隐藏快进快退lay*/
                    mFastForwardBox.setVisibility(View.GONE);
                    break;
                /**滑动完成，设置播放进度*/
                case PlayStateParams.MESSAGE_SEEK_NEW_POSITION:
                    if (mNewPosition >= 0) {
                        mVideoView.seekTo((int) mNewPosition);
                        mNewPosition = -1;
                    }
                    break;
                /**播放器不断的同步播放进度*/
                case PlayStateParams.MESSAGE_SHOW_PROGRESS:
                    long pos = syncProgress();
                    if (!mIsDragging && mIsShowControlPanl) {
                        msg = obtainMessage(PlayStateParams.MESSAGE_SHOW_PROGRESS);
                        sendMessageDelayed(msg, sMaxVideoProgress - (pos % sMaxVideoProgress));
                        updatePausePlayUI();
                    }
                    break;
                /**重新去播放*/
                case PlayStateParams.MESSAGE_RESTART_PLAY:
//                    mStatus = PlayStateParams.STATE_ERROR;
//                    startPlay();
//                    updatePausePlayUI();
                    rePlay();
                    break;
            }
        }
    };

    /**
     * 保留旧的调用方法
     */
    public VideoPlayer(Activity activity) {
        this(activity, null);
    }

    /**
     * 新的调用方法，适用非Activity中使用PlayerView，例如fragment、holder中使用
     */
    public VideoPlayer(Activity activity, View rootView) {
        this.mActivity = activity;
        this.mContext = activity;
        this.mRootView = rootView;
        init();
    }

    /**
     * 创建具备通用功能的播放器及播放界面
     *
     * @param activity
     * @param rootView
     * @return
     */
    public static VideoPlayer createCommonVideoPlayer(Activity activity, View rootView, List<VideoijkBean> list, OnShowThumbnailListener onShowThumbnailListener) {
        VideoPlayer player = new VideoPlayer(activity, rootView) {
            @Override
            public VideoPlayer toggleProcessDurationOrientation() {
                /**竖屏时隐藏视频分辨率按钮，横屏时显示视频分辨率按钮*/
                hideSteam(getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                /**ActivityInfo.SCREEN_ORIENTATION_PORTRAIT---竖屏*/
                return setProcessDurationOrientation(getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        ? PlayStateParams.PROCESS_LANDSCAPE_LEFT_RIGHT : PlayStateParams.PROCESS_LANDSCAPE_LEFT_RIGHT);
            }

            @Override
            public VideoPlayer setPlaySource(List<VideoijkBean> list) {
                return super.setPlaySource(list);
            }
        }.setScaleType(PlayStateParams.wrapcontent)
                .setProcessDurationOrientation(PlayStateParams.PROCESS_LANDSCAPE_LEFT_RIGHT)
                .forbidTouch(false)
                .forbidHideControlPanl(false)
                .forbidDoulbeUp(false)
                .hideBack(false)
                .hideFullscreen(false)
                .hideCenterPlayer(false)
                .hideRotation(false)
                .hideMenu(false)
                .hideSteam(true)
                .hideTitle(false)
                .hideCast(true)
                .showSpeed(false)
                .setAutoReConnect(false, 0)
                .setChargeTie(false, 0)
                .setNetWorkTypeTie(true)
                .setPlaySource(list)
                .asAudioPlayer(false)
                .showThumbnail(onShowThumbnailListener);
        return player;
    }

    /**
     * 创建小灯塔app需求功能的播放器及播放界面
     *
     * @param activity
     * @param rootView
     * @param list
     * @param onShowThumbnailListener
     * @return
     */
    public static VideoPlayer createDengTaVideoPlayer(Activity activity, View rootView, List<VideoijkBean> list, OnShowThumbnailListener onShowThumbnailListener) {
        VideoPlayer player = new VideoPlayer(activity, rootView) {
            @Override
            public VideoPlayer toggleProcessDurationOrientation() {
                /**ActivityInfo.SCREEN_ORIENTATION_PORTRAIT---竖屏*/
                return setProcessDurationOrientation(getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        ? PlayStateParams.PROCESS_LANDSCAPE_LEFT_RIGHT : PlayStateParams.PROCESS_LANDSCAPE_LEFT_RIGHT);
            }

            @Override
            public VideoPlayer setPlaySource(List<VideoijkBean> list) {
                return super.setPlaySource(list);
            }
        }.setScaleType(PlayStateParams.wrapcontent)
                .setProcessDurationOrientation(PlayStateParams.PROCESS_LANDSCAPE_LEFT_RIGHT)
                .forbidTouch(false)
                .forbidHideControlPanl(false)
                .forbidDoulbeUp(false)
                .hideBack(false)
                .hideFullscreen(false)
                .hideCenterPlayer(false)
                .hideRotation(true)
                .hideMenu(true)
                .hideSteam(true)
                .hideThumb(false)
                .hideTitle(true)
                .hideCast(false)
                .showSpeed(false)
                .setAutoReConnect(false, 0)
                .setChargeTie(false, 0)
                .setNetWorkTypeTie(true)
                .setPlaySource(list)
                .asAudioPlayer(false)
                .showThumbnail(onShowThumbnailListener);
        return player;
    }

    /**
     * 初始化
     */
    private void init() {
        loadIjkMediaSupport();
        keepScreenBrightness();
        createLayoutQuery();
        initView();
        initAnimation();
        initData();
    }

    private void initAnimation() {
        if (mAsAudioPlayer && mRotateAnimator == null) {
            initRotateAnimotor();
        }
    }

    private void initRotateAnimotor() {
        mRotateAnimator = ObjectAnimator.ofFloat(mIvTrumbAudio, "rotation", 0f, 360f);
        mRotateAnimator.setDuration(8000);
        mRotateAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mRotateAnimator.setInterpolator(new LinearInterpolator());
    }

    /**
     * 界面中的布局的查询器
     */
    private void createLayoutQuery() {
        mQuery = new LayoutQuery(mActivity, mRootView);
    }

    /**
     * 保持屏幕亮度
     */
    private void keepScreenBrightness() {
        try {
            int e = Settings.System.getInt(this.mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            float progress = 1.0F * (float) e / 255.0F;
            android.view.WindowManager.LayoutParams layout = this.mActivity.getWindow().getAttributes();
            layout.screenBrightness = progress;
            mActivity.getWindow().setAttributes(layout);
        } catch (Settings.SettingNotFoundException var7) {
            var7.printStackTrace();
        }
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mScreenWidthPixels = mContext.getResources().getDisplayMetrics().widthPixels;
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    private void initView() {
        initVideoView();
        initTopControlbar();
        initBottomControlbar();
        initCenterControlbar();
        initOrientation();
    }

    /**
     * 初始化界面方向
     */
    private void initOrientation() {
        mOrientationEventListener = new OrientationEventListener(mActivity) {
            @Override
            public void onOrientationChanged(int orientation) {
                if (orientation >= 0 && orientation <= 30 || orientation >= 330 || (orientation >= 150 && orientation <= 210)) {
                    // 竖屏
                    if (mIsPortrait) {
                        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                        mOrientationEventListener.disable();
                    }
                } else if ((orientation >= 90 && orientation <= 120) || (orientation >= 240 && orientation <= 300)) {
                    if (!mIsPortrait) {
                        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                        mOrientationEventListener.disable();
                    }
                }
            }
        };
        if (mIsOnlyFullScreen) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        mIsPortrait = (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mInitHeight = mVideoWholeView.getLayoutParams().height;
        hideAllUI();
        if (!mPlayerSupport) {
            showStatus(mActivity.getResources().getString(R.string.not_support));
        } else {
            mQuery.id(R.id.iv_trumb).visible();
        }
    }

    /**
     * 初始化播放器中间控件显示栏
     */
    private void initCenterControlbar() {
        mCenterControlbarContainer = mQuery.getView(R.id.v2_simple_player_center_controlbar_container);
        mSelectStreamContainer = mQuery.getView(R.id.v2_simple_player_select_stream_container);
        mSelectStreamListView = (ListView) mQuery.getView(R.id.v2_simple_player_select_streams_list);
        mIvPlay = (ImageView) mQuery.getView(R.id.play_icon);
        mIvTrumb = (ImageView) mQuery.getView(R.id.iv_trumb);
        mIvTrumbAudio = (ImageView) mQuery.getView(R.id.iv_trumb_audio);
        mTvSteam = (TextView) mQuery.getView(R.id.app_video_stream);
        mVolumeBox = mQuery.getView(R.id.app_video_volume_box);
        mBrightnessBox = mQuery.getView(R.id.app_video_brightness_box);
        mFastForwardBox = mQuery.getView(R.id.app_video_fastForward_box);
        mLoadingBox = mQuery.getView(R.id.app_video_loading_box);
        mFreeTieBox = mQuery.getView(R.id.app_video_freeTie_box);
        mReplayBox = mQuery.getView(R.id.app_video_replay_box);
        mNetTieBox = mQuery.getView(R.id.app_video_netTie_box);

        mIvPlay.setOnClickListener(this);
        mQuery.id(R.id.app_video_netTie_icon).clicked(this);
        mQuery.id(R.id.app_video_replay_icon).clicked(this);
        mQuery.id(R.id.app_video_menu).clicked(this);
        mQuery.id(R.id.app_video_cast).clicked(this);
        mStreamSelectAdapter = new StreamSelectAdapter(mContext, mListVideos);
        mSelectStreamListView.setAdapter(this.mStreamSelectAdapter);
        mSelectStreamListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switchStream(position);
                startPlay();
            }
        });
    }

    /**
     * 初始化播放器底部控件显示栏
     */
    private void initBottomControlbar() {
        mBottomControlbarContainer = mQuery.getView(R.id.v2_simple_player_bottom_controlbar_container);
        /**视频进度条*/
        mSeekBar = (SeekBar) mQuery.getView(R.id.app_video_seekBar);

        mSeekBarThumb = mSeekBar.getThumb();
        mSeekBar.setEnabled(false);
        mSeekBar.setMax(sMaxVideoProgress);
        mSeekBar.setOnSeekBarChangeListener(mSeekListener);
        mQuery.id(R.id.app_video_play).clicked(this);
        mQuery.id(R.id.app_video_fullscreen).clicked(this);
        mQuery.id(R.id.ijk_iv_rotation).clicked(this);
        mQuery.id(R.id.app_video_stream).clicked(this);
    }

    /**
     * 初始化播放器顶部控件显示栏
     */
    private void initTopControlbar() {
        mTopControlbarContainer = mQuery.getView(R.id.v2_simple_player_top_controlbar_container);
        /**播放器返回键*/
        mQuery.id(R.id.app_video_finish).clicked(this);
    }

    /**
     * 初始化播放器界面View、Ijkplayer控件
     */
    private void initVideoView() {
        mVideoWholeView = mQuery.getView(R.id.app_video_player_box);
        mVideoView = (IjkVideoView) mQuery.getView(R.id.app_video_view);

        mVideoWholeView.setClickable(true);

        mGestureDetector = new GestureDetector(mContext, new PlayerGestureListener());
        mVideoWholeView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        if (mAutoPlayRunnable != null) {
                            mAutoPlayRunnable.stop();
                        }
                        break;
                }
                if (mGestureDetector.onTouchEvent(motionEvent))
                    return true;
                // 处理手势结束
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        endGesture();
                        break;
                }
                return false;
            }
        });
        mInitHeight = mVideoWholeView.getLayoutParams().height;

        mVideoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int extra) {
                if (what == PlayStateParams.MEDIA_INFO_NETWORK_BANDWIDTH || what == PlayStateParams.MEDIA_INFO_BUFFERING_BYTES_UPDATE) {
                    Log.e("", "====extra=======" + extra);
                    /**加载网速*/
                    if (mIsShowSpeed) {
                        mQuery.id(R.id.app_video_speed).text(VideoUtils.formatSize(extra));
                    }
                }
                statusChange(what);
                if (mOnInfoListener != null) {
                    mOnInfoListener.onInfo(mp, what, extra);
                }
                if (hasEndedChargePlayTime()) {
                    mQuery.id(R.id.app_video_freeTie_box).visible();
                    pausePlay();
                }
                return true;
            }
        });
    }

    /**
     * 第三方so是否支持
     */
    private void loadIjkMediaSupport() {
        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
            mPlayerSupport = true;
        } catch (Throwable e) {
            mPlayerSupport = false;
            Log.e(TAG, "loadLibraries error", e);
        }
    }

    /**
     * 手势结束
     */
    private void endGesture() {
        mVolume = -1;
        mBrightness = -1f;
        if (mNewPosition >= 0) {
            mHandler.removeMessages(PlayStateParams.MESSAGE_SEEK_NEW_POSITION);
            mHandler.sendEmptyMessage(PlayStateParams.MESSAGE_SEEK_NEW_POSITION);
        } else {
            /**什么都不做(do nothing)*/
        }
        mHandler.removeMessages(PlayStateParams.MESSAGE_HIDE_CENTER_BOX);
        mHandler.sendEmptyMessageDelayed(PlayStateParams.MESSAGE_HIDE_CENTER_BOX, 500);
        if (mAutoPlayRunnable != null) {
            mAutoPlayRunnable.start();
        }
    }

    /**
     * 状态改变同步UI
     *
     * @param newStatus
     */
    private void statusChange(int newStatus) {
//        LogUtil.i(TAG, "statusChange newStatus = " + newStatus);
        if (newStatus == PlayStateParams.STATE_COMPLETED) {
            mStatus = PlayStateParams.STATE_COMPLETED;
            if (mOnPlayCompleteListener != null) {
                mOnPlayCompleteListener.onComplete("播放结束");
            }
            // mCurrentPosition = 0;
            hideAllUI();
            // 播放结束
            showStatus("重播");
        } else if (newStatus == PlayStateParams.STATE_PREPARING
                || newStatus == PlayStateParams.MEDIA_INFO_BUFFERING_START) {
            mStatus = PlayStateParams.STATE_PREPARING;
            /**视频缓冲*/
            hideAllUI();
            /**加载loadingUI*/
            mQuery.id(R.id.app_video_loading_box).visible();
        } else if (newStatus == PlayStateParams.MEDIA_INFO_VIDEO_RENDERING_START
                || newStatus == PlayStateParams.STATE_PLAYING
//                || newStatus == PlayStateParams.STATE_PREPARED
                || newStatus == PlayStateParams.MEDIA_INFO_BUFFERING_END
                || newStatus == PlayStateParams.STATE_PAUSED) {
            if (mStatus == PlayStateParams.STATE_PAUSED) {
                mStatus = PlayStateParams.STATE_PAUSED;
            } else {
                mStatus = PlayStateParams.STATE_PLAYING;
            }
            /**视频缓冲结束后隐藏状态界面*/
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    hideStatusUI();
//                    /**显示控制bar*/
//                    mIsShowControlPanl = false;
//                    if (!mIsForbidTouch) {
//                        operatorPanl();
//                    }
//                    /**隐藏视频封面*/
////                    mIvTrumb.setVisibility(mAsAudioPlayer ? View.VISIBLE : View.GONE);
//                    hideThumbUI();
//
//                }
//            }, 500);
            hideThumbUI();
        } else if (newStatus == PlayStateParams.MEDIA_INFO_VIDEO_INTERRUPT
                || newStatus == PlayStateParams.STATE_ERROR
                || newStatus == PlayStateParams.MEDIA_INFO_UNKNOWN
                || newStatus == PlayStateParams.MEDIA_ERROR_IO
                || newStatus == PlayStateParams.MEDIA_ERROR_MALFORMED
                || newStatus == PlayStateParams.MEDIA_ERROR_UNSUPPORTED
                || newStatus == PlayStateParams.MEDIA_ERROR_TIMED_OUT
                || newStatus == PlayStateParams.MEDIA_ERROR_SERVER_DIED) {
            mStatus = PlayStateParams.STATE_ERROR;
            mIsErrorStop = true;
            if (!(mIsShowNetWorkTip && (NetworkUtils.getNetworkType(mContext) == 4 || NetworkUtils.getNetworkType(mContext) == 5 || NetworkUtils.getNetworkType(mContext) == 6))) {
                /**付费观看时长是否已结束*/
                if (hasEndedChargePlayTime()) {
                    mFreeTieBox.setVisibility(View.VISIBLE);
                } else {
                    hideStatusUI();
                    showStatus(mActivity.getResources().getString(R.string.small_problem));
                    /**多少秒（5s）尝试重连*/
                    if (mIsErrorStop && mIsAutoReConnect) {
                        mHandler.sendEmptyMessageDelayed(PlayStateParams.MESSAGE_RESTART_PLAY, mAutoConnectTime * 1000);
                    }
                }
            } else {
                /**移动网络流量费用提示*/
                mNetTieBox.setVisibility(View.VISIBLE);
            }
        }
        updatePausePlayUI();
//        LogUtil.i(TAG, "statusChange mStatus = " + mStatus);
    }

    /**
     * 显示或隐藏操作面板
     */
    private void operatorPanl() {
//        LogUtil.d(TAG, "operatorPanl");
        mIsShowControlPanl = !mIsShowControlPanl;
        if (mIsForbidHideControlPanl) {
            mIsShowControlPanl = true;
        }
//        LogUtil.d(TAG, "operatorPanl mIsShowControlPanl = " + mIsShowControlPanl);
        hideStreamSelectView();
        // mQuery.id(R.id.simple_player_settings_container).gone();
        if (mIsShowControlPanl) {
            showBarUI();
            updateFullscreenUI();
            updatePausePlayUI();
            mHandler.sendEmptyMessage(PlayStateParams.MESSAGE_SHOW_PROGRESS);
            mAutoPlayRunnable.start();
        } else {
            hideBarUI();
            // hideShowCenterPlayerUI();
            updatePausePlayUI();
            mHandler.removeMessages(PlayStateParams.MESSAGE_SHOW_PROGRESS);
            mAutoPlayRunnable.stop();
        }
        if (mOnControlPanelVisibilityChangeListener != null) {
            mOnControlPanelVisibilityChangeListener.change(mIsShowControlPanl);
        }
        return;
    }

    /**
     * 显示或隐藏全屏icon
     */
    private void updateFullscreenUI() {
        if (mIsOnlyFullScreen || mIsForbidDoulbeUp) {
            mQuery.id(R.id.app_video_fullscreen).gone();
        } else {
            mQuery.id(R.id.app_video_fullscreen).visible();
        }
    }

    /**
     * 更新全屏和半屏按钮
     */
    private void updateFullScreenButton() {
        if (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            /**半屏icon*/
            mQuery.id(R.id.app_video_fullscreen).image(R.drawable.v2_simple_player_fullscreen_shrink_icon);
        } else {
            /**全屏icon*/
            mQuery.id(R.id.app_video_fullscreen).image(R.drawable.v2_simple_player_fullscreen_stretch_icon);
        }
    }

    /**
     * 获取界面方向
     */
    public int getScreenOrientation() {
        int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0
                || rotation == Surface.ROTATION_180) && height > width ||
                (rotation == Surface.ROTATION_90
                        || rotation == Surface.ROTATION_270) && width > height) {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        }
        // if the device's natural orientation is landscape or if the device
        // is square:
        else {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }
        return orientation;
    }

    /**
     * 显示视频播放状态提示
     *
     * @param statusText
     */
    private void showStatus(String statusText) {
        mSeekBar.setEnabled(false);
        cancleAudioAnimation();
        if (mAsAudioPlayer) {
            return;
        }
        mReplayBox.setVisibility(View.VISIBLE);
        mQuery.id(R.id.app_video_status_text).text(statusText);
    }

    /**
     * 设置界面方向带隐藏actionbar
     *
     * @param fullScreen
     */
    private void tryFullScreen(boolean fullScreen) {
        if (mActivity instanceof AppCompatActivity) {
            ActionBar supportActionBar = ((AppCompatActivity) mActivity).getSupportActionBar();
            if (supportActionBar != null) {
                if (fullScreen) {
                    supportActionBar.hide();
                } else {
                    supportActionBar.show();
                }
            }
        }
        setFullScreen(fullScreen);
    }

    /**
     * 设置界面方向
     *
     * @param fullScreen
     */
    private void setFullScreen(boolean fullScreen) {
        if (mActivity != null) {
            WindowManager.LayoutParams attrs = mActivity.getWindow().getAttributes();
            if (fullScreen) {
                attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                mActivity.getWindow().setAttributes(attrs);
                mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            } else {
                attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
                mActivity.getWindow().setAttributes(attrs);
                mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }
            toggleProcessDurationOrientation();
        }
    }

    /**
     * 同步进度
     */
    private long syncProgress() {
        if (mIsDragging) {
            return 0;
        }
        long position = mVideoView.getCurrentPosition();
        long duration = mVideoView.getDuration();
        if (mSeekBar != null) {
            if (duration > 0) {
                long pos = 1000L * position / duration;
                mSeekBar.setProgress((int) pos);
            }
            int percent = mVideoView.getBufferPercentage();
            mSeekBar.setSecondaryProgress(percent * 10);
        }

        /**付费观看时长是否已结束*/
        hasEndedChargePlayTime();
        if (hasEndedChargePlayTime()) {
            mFreeTieBox.setVisibility(View.VISIBLE);
            pausePlay();
        } else {
            updateDurationTime(VideoUtils.generateTime(position), VideoUtils.generateTime(duration));
        }
        return position;
    }

    /**
     * 更新播放、暂停和停止按钮
     */
    private void updatePausePlayUI() {
        hideShowCenterPlayerUI();
        if (mStatus == PlayStateParams.STATE_PAUSED || !mVideoView.isPlaying()) {
            /**暂停和停止*/
            mQuery.id(R.id.play_icon).image(R.drawable.v2_simple_player_center_play_icon);
            mQuery.id(R.id.app_video_play).image(R.drawable.v2_simple_player_bottom_play_icon);
        } else {
            /**播放*/
            mQuery.id(R.id.play_icon).image(R.drawable.v2_simple_player_center_pause_icon);
            mQuery.id(R.id.app_video_play).image(R.drawable.v2_simple_player_bottom_pause_icon);
        }
    }

    /**
     * 显示或隐藏中间播放按钮
     */
    private void hideShowCenterPlayerUI() {
        if (mIsHideCenterPlayer) {
            // 如果是音频播放，播放按钮一直显示
            mIvPlay.setVisibility(mAsAudioPlayer ? View.VISIBLE : View.GONE);
            return;
        }
        if (mStatus == PlayStateParams.STATE_IDLE || mStatus == PlayStateParams.STATE_PAUSED && !mVideoView.isPlaying()) {
            /**暂停时一直显示按钮*/
            mIvPlay.setVisibility(View.VISIBLE);
        } else {
            // 如果是音频播放，播放按钮一直显示
            mIvPlay.setVisibility(mAsAudioPlayer ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 付费观看时长是否已结束
     */
    private boolean hasEndedChargePlayTime() {
        return mIsCharge && mMaxPlaytime + 1000 < getCurrentPosition();
    }

    /**
     * ==========================================Activity生命周期方法回调=============================
     */
    /**
     * @Override protected void onDestroy() {
     * super.onDestroy();
     * if (player != null) {
     * player.onDestroy();
     * }
     * }
     */
    public VideoPlayer onDestroy() {
        mOrientationEventListener.disable();
        mHandler.removeMessages(PlayStateParams.MESSAGE_RESTART_PLAY);
        mHandler.removeMessages(PlayStateParams.MESSAGE_SEEK_NEW_POSITION);
        stopPlay();
        if (mQuery != null) {
            mQuery.release();
        }
        return this;
    }

    /**
     * @Override protected void onResume() {
     * super.onResume();
     * if (player != null) {
     * player.onResume();
     * }
     * }
     */
    public VideoPlayer onResume() {
        /**demo的内容，暂停系统其它媒体的状态*/
        MediaUtils.muteAudioFocus(mContext, false);
        mVideoView.onResume();
        mVideoView.seekTo(mCurrentPosition);
        if (mBgState == 0) {
            startAudioAnimation();
            updatePausePlayUI();
        } else {
            if (mStatus == PlayStateParams.STATE_COMPLETED || mStatus == PlayStateParams.STATE_ERROR || mStatus == PlayStateParams.STATE_IDLE) {
                return this;
            }
            pausePlay();
        }
        return this;
    }

    /**
     * @Override protected void onPause() {
     * super.onPause();
     * if (player != null) {
     * player.onPause();
     * }
     * }
     */
    public VideoPlayer onPause() {
        /**demo的内容，恢复系统其它媒体的状态*/
        MediaUtils.muteAudioFocus(mContext, true);
        mBgState = (mVideoView.isPlaying() ? 0 : 1);
        if (mStatus == PlayStateParams.STATE_COMPLETED || mStatus == PlayStateParams.STATE_ERROR || mStatus == PlayStateParams.STATE_IDLE) {
            return this;
        }
        getCurrentPosition();
        mVideoView.onPause();
        pauseAudioAnimation();
        updatePausePlayUI();
        return this;
    }

    /**
     * @Override public void onConfigurationChanged(Configuration newConfig) {
     * super.onConfigurationChanged(newConfig);
     * if (player != null) {
     * player.onConfigurationChanged(newConfig);
     * }
     * }
     */
    public VideoPlayer onConfigurationChanged(final Configuration newConfig) {
        mIsPortrait = newConfig.orientation == Configuration.ORIENTATION_PORTRAIT;
        doOnConfigurationChanged(mIsPortrait);
        return this;
    }

    /**
     * @Override public void onBackPressed() {
     * if (player != null && player.onBackPressed()) {
     * return;
     * }
     * super.onBackPressed();
     * }
     */
    public boolean onBackPressed() {
        if (!mIsOnlyFullScreen && getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return true;
        }
        return false;
    }

    /**
     * ==========================================对外的方法=============================
     */
    /**
     * 是否显示加载网速，默认不显示，true为显示，默认隐藏
     * 非必要设置
     *
     * @param isShow
     * @return
     */
    public VideoPlayer showSpeed(boolean isShow) {
        mIsShowSpeed = isShow;
        mQuery.id(R.id.app_video_speed).visibility(isShow ? View.VISIBLE : View.GONE);
        return this;
    }

    /**
     * 设置自动重连的模式或者重连时间，isAuto true 出错重连，false出错不重连，connectTime重连的时间
     * isAuto默认true重连，connectTime默认重连的时间5秒
     * 非必要设置
     *
     * @param isAuto
     * @param connectTime 单位秒如5
     * @return
     */
    public VideoPlayer setAutoReConnect(boolean isAuto, int connectTime) {
        this.mIsAutoReConnect = isAuto;
        this.mAutoConnectTime = connectTime;
        return this;
    }

    /**
     * 播放之前显示的缩略图
     * 非必要设置
     *
     * @param listener
     * @return
     */
    public VideoPlayer showThumbnail(OnShowThumbnailListener listener) {
        this.mOnShowThumbnailListener = listener;
        if (mOnShowThumbnailListener != null && mIvTrumb != null) {
            if (!mIvTrumb.isShown()) {
                mIvTrumb.setVisibility(View.VISIBLE);
            }
            mOnShowThumbnailListener.onShowThumbnail(mIvTrumb);
        }
        return this;
    }

    /**
     * 播放之前显示的音频画面
     * 非必要设置
     *
     * @param listener
     * @return
     */
    public VideoPlayer onChangeAudioImageSizeListener(OnChangeAudioThumbSizeListener listener) {
        this.mOnChangeAudioThumbSizeListener = listener;
        return this;
    }

    /**
     * 投屏按钮、重播按钮点击监听
     * 非必要设置
     *
     * @param listener
     * @return
     */
    public VideoPlayer setClickedViewListener(OnClickedViewListener listener) {
        this.mOnClickedViewListener = listener;
        return this;
    }

    /**
     * 设置控制面板显示隐藏监听
     * 非必要设置
     *
     * @param listener
     * @return
     */
    public VideoPlayer setOnControlPanelVisibilityChangListenter(OnControlPanelVisibilityChangeListener listener) {
        this.mOnControlPanelVisibilityChangeListener = listener;
        return this;
    }

    /**
     * 是否付费观看及最大观看时长，isCharge true为收费 false为免费即不做限制，默认false不做限制
     * 非必要设置
     *
     * @param isCharge    true为付费 false为免费即不做限制
     * @param maxPlaytime 最大能播放时长，单位秒
     */
    public VideoPlayer setChargeTie(boolean isCharge, int maxPlaytime) {
        this.mIsCharge = isCharge;
        this.mMaxPlaytime = maxPlaytime * 1000;
        return this;
    }

    /**
     * 设置2/3/4/5G和WiFi网络类型提示，true为显示移动网络流量费用提示，false不显示移动网络流量费用提示,，默认显示移动网络流量费用提示
     * 非必要设置
     *
     * @param isGNetWork true为进行2/3/4/5G网络类型提示
     *                   false 不进行网络类型提示
     */
    public VideoPlayer setNetWorkTypeTie(boolean isGNetWork) {
        this.mIsShowNetWorkTip = isGNetWork;
        return this;
    }

    /**
     * 开始播放
     */
    public VideoPlayer startPlay() {
//        hideStatusUI();
        if (mCurrentUrl == null) {
            /**隐藏视频封面*/
            hideThumbUI();
            showStatus(mActivity.getResources().getString(R.string.giraffe_player_url_empty));
            return this;
        }
        mCurrentPosition = mVideoView.getCurrentPosition();
        if (mIsHasSwitchStream || mStatus == PlayStateParams.STATE_ERROR) {
            // 换源之后声音可播，画面卡住，主要是渲染问题，目前只是提供了软解方式，后期提供设置方式
            mVideoView.setRender(mVideoView.RENDER_TEXTURE_VIEW);
            mVideoView.setVideoPath(mCurrentUrl);
            mVideoView.seekTo(mCurrentPosition);
            mIsHasSwitchStream = false;
        }

        if (mIsShowNetWorkTip && (NetworkUtils.getNetworkType(mContext) == 4 || NetworkUtils.getNetworkType(mContext) == 5 || NetworkUtils.getNetworkType(mContext) == 6)) {
            hideThumbUI();

//            /**隐藏视频封面*/
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    /**隐藏视频封面*/
//                    mIvTrumb.setVisibility(View.GONE);
//                }
//            }, 500);

            mNetTieBox.setVisibility(View.VISIBLE);
        } else {
            if (hasEndedChargePlayTime()) {
                mFreeTieBox.setVisibility(View.VISIBLE);
            } else {
                if (mPlayerSupport) {
                    mQuery.id(R.id.app_video_loading_box).visible();
                    mStatus = PlayStateParams.STATE_PREPARING;
                    mSeekBar.setEnabled(true);
                    mVideoView.start();
                    /**播放音频，执行旋转动画*/
                    startAudioAnimation();
                } else {
                    showStatus(mActivity.getResources().getString(R.string.not_support));
                }
            }
        }
        updatePausePlayUI();
        return this;
    }

    private void startAudioAnimation() {
//        LogUtil.d(TAG, "startAudioAnimation");
        if (mAsAudioPlayer) {
            if (mRotateAnimator == null) {
                initRotateAnimotor();
            }
            if (mRotateAnimator.isStarted()) {
                mRotateAnimator.resume();
            } else {
                mRotateAnimator.start();
            }
        }
    }

    private void pauseAudioAnimation() {
//        LogUtil.d(TAG, "pauseAudioAnimation");
        if (mAsAudioPlayer) {
            if (mRotateAnimator != null) {
                mRotateAnimator.pause();
            }
        }
    }

    private void cancleAudioAnimation() {
//        LogUtil.d(TAG, "cancleAudioAnimation");
        if (mAsAudioPlayer) {
            if (mRotateAnimator != null) {
                mRotateAnimator.cancel();
            }
        }
    }

    /**
     * 隐藏视频封面
     */
    private void hideThumbUI() {
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                /**隐藏视频封面*/
//                mIvTrumb.setVisibility(mAsAudioPlayer ? View.VISIBLE : View.GONE);
//            }
//        }, 500);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideStatusUI();
                /**显示控制bar*/
                mIsShowControlPanl = false;
                if (!mIsForbidTouch) {
                    operatorPanl();
                }
                /**隐藏视频封面*/
                mIvTrumb.setVisibility(mAsAudioPlayer ? View.VISIBLE : View.GONE);
                mIvTrumbAudio.setVisibility(mAsAudioPlayer ? View.VISIBLE : View.GONE);
//                hideThumbUI();

            }
        }, 500);
    }

    /**
     * 暂停播放
     */
    public VideoPlayer pausePlay() {
        mStatus = PlayStateParams.STATE_PAUSED;
        mVideoView.pause();
        pauseAudioAnimation();
        getCurrentPosition();
        updatePausePlayUI();
        return this;
    }

    /**
     * 停止播放
     */
    public VideoPlayer stopPlay() {
        mVideoView.stopPlayback();
        cancleAudioAnimation();
        if (mHandler != null) {
            mHandler.removeMessages(PlayStateParams.MESSAGE_RESTART_PLAY);
        }
        updatePausePlayUI();
        return this;
    }

    /**
     * 进度条和时长显示的方向切换
     */
    public VideoPlayer toggleProcessDurationOrientation() {
        setProcessDurationOrientation(mProcessDurationOrientation);
        return this;
    }

    /**
     * 进度条和时长显示的方向切换
     */
//    public PlayerView toggleProcessDurationOrientation() {
//        setProcessDurationOrientation(PlayStateParams.PROCESS_PORTRAIT);
//        return this;
//    }

    /**
     * 设置进度条和时长显示的方向，默认为上下显示，true为上下显示false为左右显示
     */
    public VideoPlayer setProcessDurationOrientation(int portrait) {
        switch (portrait) {
            case PlayStateParams.PROCESS_PORTRAIT_LEFT: {/**视频当前播放/总时长，位于进度条左端样式*/
                mQuery.id(R.id.app_video_currentTime_left_end).gone();
                mQuery.id(R.id.app_video_endTime_right_end).gone();

                mQuery.id(R.id.app_video_time_left_center).visible();

                mQuery.id(R.id.app_video_time_right_center).gone();

                mQuery.id(R.id.app_video_time_left_bottom).invisible();

                mProcessDurationOrientation = portrait;
                break;
            }
            case PlayStateParams.PROCESS_LANDSCAPE_LEFT_RIGHT: {/**视频当前播放/总时长，位于进度条左右端样式*/
                mQuery.id(R.id.app_video_currentTime_left_end).visible();
                mQuery.id(R.id.app_video_endTime_right_end).visible();

                mQuery.id(R.id.app_video_time_left_center).gone();

                mQuery.id(R.id.app_video_time_right_center).gone();

                mQuery.id(R.id.app_video_time_left_bottom).invisible();

                mProcessDurationOrientation = portrait;
                break;
            }
            case PlayStateParams.PROCESS_PORTRAIT_RIGHT: {/**视频当前播放/总时长，位于进度条右端样式*/
                mQuery.id(R.id.app_video_currentTime_left_end).gone();
                mQuery.id(R.id.app_video_endTime_right_end).gone();

                mQuery.id(R.id.app_video_time_left_center).gone();

                mQuery.id(R.id.app_video_time_right_center).visible();

                mQuery.id(R.id.app_video_time_left_bottom).invisible();

                mProcessDurationOrientation = portrait;
                break;
            }
            case PlayStateParams.PROCESS_CENTER: {/**视频当前播放/总时长，位于进度条左下角样式*/
                mQuery.id(R.id.app_video_currentTime_left_end).gone();
                mQuery.id(R.id.app_video_endTime_right_end).gone();

                mQuery.id(R.id.app_video_time_left_center).gone();

                mQuery.id(R.id.app_video_time_right_center).gone();

                mQuery.id(R.id.app_video_time_left_bottom).visible();

                mProcessDurationOrientation = portrait;
                break;
            }
            default:
                break;
        }
        return this;
    }

    /**
     * 旋转指定角度
     */
    public VideoPlayer setPlayerRotation(int rotation) {
        if (mVideoView != null) {
            mVideoView.setPlayerRotation(rotation);
            mVideoView.setAspectRatio(mCurrentShowType);
        }
        return this;
    }

    /**
     * 选择要播放的流
     *
     * @param position
     * @return
     */
    public VideoPlayer switchStream(int position) {
        if (mListVideos.size() > position) {
            if (mCurrentSelect == position) {
                return this;
            }
            mCurrentSelect = position;
            for (int i = 0; i < mListVideos.size(); i++) {
                if (i == position) {
                    mListVideos.get(i).setSelect(true);
                } else {
                    mListVideos.get(i).setSelect(false);
                }
            }
            mTvSteam.setText(mListVideos.get(position).getStream());
            mCurrentUrl = mListVideos.get(position).getUrl();
            mListVideos.get(position).setSelect(true);
            // isLive();
            if (mVideoView.isPlaying()) {
                getCurrentPosition();
                mVideoView.release(false);
            }
            mIsHasSwitchStream = true;
            mStreamSelectAdapter.update(mListVideos);
        }
        hideStreamSelectView();
        return this;
    }

    /**
     * 设置播放地址
     * 包括视频清晰度列表
     * 对应地址列表
     *
     * @param list
     * @return
     */
    public VideoPlayer setPlaySource(List<VideoijkBean> list) {
        mListVideos.clear();
        mCurrentSelect = -1;
        mIsHasSwitchStream = false;
        if (list != null && list.size() > 0) {
            mListVideos.addAll(list);
            switchStream(0);
        }
        return this;
    }

    /**
     * 设置播放地址
     * 单个视频VideoijkBean
     *
     * @param videoijkBean
     * @return
     */
    public VideoPlayer setPlaySource(VideoijkBean videoijkBean) {
        List<VideoijkBean> list = new ArrayList<>(1);
        list.add(videoijkBean);
        setPlaySource(list);
        return this;
    }

    /**
     * 设置播放地址
     * 单个视频地址
     * 带流名称
     *
     * @param stream
     * @param url
     * @return
     */
    public VideoPlayer setPlaySource(String stream, String url) {
        VideoijkBean mVideoijkBean = new VideoijkBean();
        mVideoijkBean.setStream(stream);
        mVideoijkBean.setUrl(url);
        setPlaySource(mVideoijkBean);
        return this;
    }

    /**
     * 设置播放地址
     * 单个视频地址
     */
    public VideoPlayer setPlaySource(String url) {
        setPlaySource("标清", url);
        return this;
    }

    /**
     * 是否作为音频播放器，true表示音频播放器，false表示视频播放器，默认false视频播放器
     * 非必要设置
     *
     * @param asAudioPlayer
     * @return
     */
    public VideoPlayer asAudioPlayer(boolean asAudioPlayer) {
        mAsAudioPlayer = asAudioPlayer;
        changeAudioImageSize(mInitHeight);
        return this;
    }

    /**
     * 自动播放
     * 非必要设置
     *
     * @param path
     * @return
     */
    public VideoPlayer autoPlay(String path) {
        setPlaySource(path);
        startPlay();
        return this;
    }

    /**
     * 设置视频名称
     * 非必要设置
     *
     * @param title
     * @return
     */
    public VideoPlayer setTitle(String title) {
        mQuery.id(R.id.app_video_title).text(title);
        return this;
    }

    /**
     * 设置播放区域拉伸类型
     * 必要设置
     *
     * @param showType
     * @return
     */
    public VideoPlayer setScaleType(int showType) {
        mCurrentShowType = showType;
        mVideoView.setAspectRatio(mCurrentShowType);
        return this;
    }

    /**
     * 是否仅仅为全屏
     * 非必要设置
     *
     * @param isOnlyFullScreen
     * @return
     */
    public VideoPlayer onlyFullScreen(boolean isOnlyFullScreen) {
        this.mIsOnlyFullScreen = isOnlyFullScreen;
        tryFullScreen(isOnlyFullScreen);
        if (isOnlyFullScreen) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
        return this;
    }

    /**
     * 设置是否禁止双击，true为禁止双击，false为允许双击，默认false允许双击
     * 非必要设置
     *
     * @param isForbidDoulbeUp
     * @return
     */
    public VideoPlayer forbidDoulbeUp(boolean isForbidDoulbeUp) {
        this.mIsForbidDoulbeUp = isForbidDoulbeUp;
        return this;
    }

    /**
     * 隐藏中间播放按钮，ture为一直隐藏中间播放按钮，false为不做隐藏处理，默认隐藏
     * 非必要设置
     *
     * @param isHide
     * @return
     */
    public VideoPlayer hideCenterPlayer(boolean isHide) {
        mIsHideCenterPlayer = isHide;
        if (mAsAudioPlayer) {
            mIvPlay.setVisibility(View.VISIBLE);
            return this;
        }
        mIvPlay.setVisibility(mIsHideCenterPlayer ? View.GONE : View.VISIBLE);
        return this;
    }

    /**
     * 隐藏返回键，true隐藏，false为显示，默认显示
     * 非必要设置
     *
     * @param isHide
     * @return
     */
    public VideoPlayer hideBack(boolean isHide) {
        mQuery.id(R.id.app_video_finish).visibility(isHide ? View.GONE : View.VISIBLE);
        return this;
    }

    /**
     * 隐藏投屏按钮，true隐藏，false为显示，默认显示
     * 非必要设置
     *
     * @param isHide
     * @return
     */
    public VideoPlayer hideCast(boolean isHide) {
        mQuery.id(R.id.app_video_cast).visibility(isHide ? View.GONE : View.VISIBLE);
        return this;
    }

    /**
     * 隐藏菜单键，true隐藏，false为显示，默认显示
     * 非必要设置
     *
     * @param isHide
     * @return
     */
    public VideoPlayer hideMenu(boolean isHide) {
        mQuery.id(R.id.app_video_menu).visibility(isHide ? View.GONE : View.VISIBLE);
        return this;
    }

    /**
     * 隐藏分辨率按钮，true隐藏，false为显示，默认隐藏
     * 非必要设置
     *
     * @param isHide
     * @return
     */
    public VideoPlayer hideSteam(boolean isHide) {
        mQuery.id(R.id.app_video_stream).visibility(isHide ? View.GONE : View.VISIBLE);
        return this;
    }

    /**
     * 隐藏视频标题
     *
     * @param isHide
     * @return
     */
    public VideoPlayer hideTitle(boolean isHide) {
        mQuery.id(R.id.app_video_title).visibility(isHide ? View.GONE : View.VISIBLE);
        return this;
    }

    /**
     * 隐藏视频标题
     *
     * @param isHide
     * @return
     */
    public VideoPlayer hideThumb(boolean isHide) {
        ColorDrawable colorDrawable = new ColorDrawable(mContext.getResources().getColor(android.R.color.transparent));
        mSeekBar.setThumb(isHide ? colorDrawable : mSeekBarThumb);
        return this;
    }

    /**
     * 隐藏旋转按钮，true隐藏，false为显示，默认隐藏
     * 非必要设置
     *
     * @param isHide
     * @return
     */
    public VideoPlayer hideRotation(boolean isHide) {
        mQuery.id(R.id.ijk_iv_rotation).visibility(isHide ? View.GONE : View.VISIBLE);
        return this;
    }

    /**
     * 隐藏全屏按钮，true隐藏，false为显示，默认显示
     * 非必要设置
     *
     * @param isHide
     * @return
     */
    public VideoPlayer hideFullscreen(boolean isHide) {
        mQuery.id(R.id.app_video_fullscreen).visibility(isHide ? View.GONE : View.VISIBLE);
        return this;
    }

    /**
     * 设置是否禁止隐藏bar，topbar & bottombar，false允许，true禁止，默认允许
     * 非必要设置
     *
     * @param flag
     * @return
     */
    public VideoPlayer forbidHideControlPanl(boolean flag) {
        this.mIsForbidHideControlPanl = flag;
        return this;
    }

    /**
     * 是否禁止触摸，false允许，true禁止，默认允许
     * 非必要设置
     *
     * @param forbidTouch
     * @return
     */
    public VideoPlayer forbidTouch(boolean forbidTouch) {
        this.mIsForbidTouch = forbidTouch;
        return this;
    }

    /**
     * 设置播放完成监听
     * 非必要设置
     *
     * @param listener
     * @return
     */
    public VideoPlayer setOnPlayCompleteListener(OnPlayCompleteListener listener) {
        this.mOnPlayCompleteListener = listener;
        return this;
    }

    /**
     * 设置播放器中的返回键监听
     * 非必要设置
     *
     * @param listener
     * @return
     */
    public VideoPlayer setPlayerBackListener(OnPlayerBackListener listener) {
        this.mPlayerBack = listener;
        return this;
    }

    /**
     * 设置播放信息监听回调
     * 非必要设置
     *
     * @param onInfoListener
     * @return
     */
    public VideoPlayer setOnInfoListener(IMediaPlayer.OnInfoListener onInfoListener) {
        this.mOnInfoListener = onInfoListener;
        return this;
    }

    /**
     * 获取视频当前播放位置
     */
    public long getCurrentPosition() {
        mCurrentPosition = mVideoView.getCurrentPosition();
        return mCurrentPosition;
    }

    /**
     * 获取视频播放总时长
     */
    public long getDuration() {
        mDuration = mVideoView.getDuration();
        return mDuration;
    }

    /**
     * 界面方向改变是刷新界面
     *
     * @param portrait
     */
    private void doOnConfigurationChanged(final boolean portrait) {
        if (mVideoView != null && !mIsOnlyFullScreen) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    tryFullScreen(!portrait);
                    if (portrait) {
                        mQuery.id(R.id.app_video_player_box).height(mInitHeight, false);
                        changeAudioImageSize(mInitHeight);
                    } else {
                        int heightPixels = mActivity.getResources().getDisplayMetrics().heightPixels;
                        int widthPixels = mActivity.getResources().getDisplayMetrics().widthPixels;
                        changeAudioImageSize(Math.min(heightPixels, widthPixels));
                        mQuery.id(R.id.app_video_player_box).height(Math.min(heightPixels, widthPixels), false);
                    }
                    mIvTrumbAudio.invalidate();
                    updateFullScreenButton();
                }
            });
            mOrientationEventListener.enable();
        }
    }

    private static final float sHeightFactor = 0.936f;

    private void changeAudioImageSize(int heightPixels) {
        if (mAsAudioPlayer) {
//            LogUtil.d(TAG, "changeAudioImageSize");
            mQuery.id(R.id.iv_trumb_audio).width((int) (heightPixels * sHeightFactor), false).height((int) (heightPixels * sHeightFactor), false);
            if (mOnChangeAudioThumbSizeListener != null && mIvTrumbAudio != null) {
                if (!mIvTrumbAudio.isShown()) {
                    mIvTrumbAudio.setVisibility(mAsAudioPlayer ? View.VISIBLE : View.GONE);
                }
                mOnChangeAudioThumbSizeListener.onChangeSize(mIvTrumbAudio, heightPixels, heightPixels);
            }
        }
    }

    /**
     * 更新当前播放时长
     *
     * @param currentTime
     * @param endTime
     */
    private void updateDurationTime(String currentTime, String endTime) {
        int portrait = mProcessDurationOrientation;
        switch (portrait) {
            case PlayStateParams.PROCESS_PORTRAIT_LEFT: {/**视频当前播放/总时长，位于进度条左端样式*/
                mQuery.id(R.id.app_video_currentTime_left_center).text(currentTime);
                mQuery.id(R.id.app_video_endTime_left_center).text(endTime);
                break;
            }
            case PlayStateParams.PROCESS_LANDSCAPE_LEFT_RIGHT: {/**视频当前播放/总时长，位于进度条左右端样式*/
                mQuery.id(R.id.app_video_currentTime_left_end).text(currentTime);
                mQuery.id(R.id.app_video_endTime_right_end).text(endTime);
                break;
            }
            case PlayStateParams.PROCESS_PORTRAIT_RIGHT: {/**视频当前播放/总时长，位于进度条右端样式*/
                mQuery.id(R.id.app_video_currentTime_right_center).text(currentTime);
                mQuery.id(R.id.app_video_endTime_right_center).text(endTime);
                break;
            }
            case PlayStateParams.PROCESS_CENTER: {/**视频当前播放/总时长，位于进度条左下角样式*/
                mQuery.id(R.id.app_video_currentTime_left_bottom).text(currentTime);
                mQuery.id(R.id.app_video_endTime_left_bottom).text(endTime);
                break;
            }
            default:
                break;
        }
    }

    /**
     * 隐藏中间控件显示栏的状态界面
     */
    private void hideStatusUI() {
        // 如果是音频播放，播放按钮一直显示
        mIvPlay.setVisibility(mAsAudioPlayer ? View.VISIBLE : View.GONE);
        mVolumeBox.setVisibility(View.GONE);
        mBrightnessBox.setVisibility(View.GONE);
        mFastForwardBox.setVisibility(View.GONE);
        mLoadingBox.setVisibility(View.GONE);
        mFreeTieBox.setVisibility(View.GONE);
        mReplayBox.setVisibility(View.GONE);
        mSelectStreamContainer.setVisibility(View.GONE);
        mNetTieBox.setVisibility(View.GONE);
        // query.id(R.id.simple_player_settings_container).gone();
    }

    /**
     * 隐藏所有界面
     */
    private void hideAllUI() {
        hideBarUI();
        hideStatusUI();
        if (mOnControlPanelVisibilityChangeListener != null) {
            mOnControlPanelVisibilityChangeListener.change(mIsShowControlPanl);
        }
    }

    /**
     * 隐藏bar，topbar & bottombar
     */
    private void hideBarUI() {
        if (!mIsForbidHideControlPanl) {
            mTopControlbarContainer.setVisibility(View.GONE);
            mBottomControlbarContainer.setVisibility(View.GONE);
            mIsShowControlPanl = false;
        }
    }

    /**
     * 显示bar，topbar & bottombar
     */
    private void showBarUI() {
        mTopControlbarContainer.setVisibility(View.VISIBLE);
        mBottomControlbarContainer.setVisibility(View.VISIBLE);
        mIsShowControlPanl = true;
    }

    /**
     * 快进或者快退滑动改变进度
     *
     * @param percent
     */
    private void onProgressSlide(float percent) {
        int position = mVideoView.getCurrentPosition();
        long duration = mVideoView.getDuration();
        long deltaMax = Math.min(100 * sMaxVideoProgress, duration - position);
        long delta = (long) (deltaMax * percent);
        mNewPosition = delta + position;
        if (mNewPosition > duration) {
            mNewPosition = duration;
        } else if (mNewPosition <= 0) {
            mNewPosition = 0;
            delta = -position;
        }
        int showDelta = (int) delta / sMaxVideoProgress;
        if (showDelta != 0) {
            mFastForwardBox.setVisibility(View.VISIBLE);
            String text = showDelta > 0 ? ("+" + showDelta) : "" + showDelta;
            mQuery.id(R.id.app_video_fastForward).text(text + "s");
            mQuery.id(R.id.app_video_fastForward_target).text(VideoUtils.generateTime(mNewPosition));
            mQuery.id(R.id.app_video_fastForward_all).text(VideoUtils.generateTime(duration));
        }
        playOrPause(false);
    }

    /**
     * 滑动改变声音大小
     *
     * @param percent
     */
    private void onVolumeSlide(float percent) {
        if (mVolume == -1) {
            mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (mVolume < 0)
                mVolume = 0;
        }
        int index = (int) (percent * mMaxVolume) + mVolume;
        if (index > mMaxVolume)
            index = mMaxVolume;
        else if (index < 0) {
            index = 0;
        }

        // 变更声音
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

        // 变更进度条
        int i = (int) (index * 1.0 / mMaxVolume * 100);
        String s = i + "%";
        if (i == 0) {
            s = "off";
        }
        // 显示
        mQuery.id(R.id.app_video_volume_icon).image(i == 0 ? R.drawable.v2_simple_player_volume_off_icon : R.drawable.v2_simple_player_volume_up_icon);
        mBrightnessBox.setVisibility(View.GONE);
        mFastForwardBox.setVisibility(View.GONE);
        mVolumeBox.setVisibility(View.VISIBLE);
        mQuery.id(R.id.app_video_volume_text).text(s);
    }

    /**
     * 亮度滑动改变亮度
     *
     * @param percent
     */
    private void onBrightnessSlide(float percent) {
        if (mBrightness < 0) {
            mBrightness = mActivity.getWindow().getAttributes().screenBrightness;
            if (mBrightness <= 0.00f) {
                mBrightness = 0.50f;
            } else if (mBrightness < 0.01f) {
                mBrightness = 0.01f;
            }
        }
        Log.d(this.getClass().getSimpleName(), "brightness:" + mBrightness + ",percent:" + percent);
        mBrightnessBox.setVisibility(View.VISIBLE);
        WindowManager.LayoutParams lpa = mActivity.getWindow().getAttributes();
        lpa.screenBrightness = mBrightness + percent;
        if (lpa.screenBrightness > 1.0f) {
            lpa.screenBrightness = 1.0f;
        } else if (lpa.screenBrightness < 0.01f) {
            lpa.screenBrightness = 0.01f;
        }
        mQuery.id(R.id.app_video_brightness_text).text(((int) (lpa.screenBrightness * 100)) + "%");
        mActivity.getWindow().setAttributes(lpa);
    }

    /**
     * 进度条滑动监听
     */
    private final SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {

        /**数值的改变*/
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser) {
                /**不是用户拖动的，自动播放滑动的情况*/
                return;
            } else {
                /**视频当前播放总时长*/
                mDuration = getDuration();
                int position = (int) ((mDuration * progress * 1.0f) / sMaxVideoProgress);
                /**更新当前/总播放时长*/
                updateDurationTime(VideoUtils.generateTime(position), VideoUtils.generateTime(mDuration));
            }
        }

        /**开始拖动*/
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mIsDragging = true;
            mHandler.removeMessages(PlayStateParams.MESSAGE_SHOW_PROGRESS);
        }

        /**停止拖动*/
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            long duration = getDuration();
            if (mDuration - mBackTime <= duration) {// 如果用户直接拖到末尾,就回退5s时间
                duration = duration - mBackTime;
            }
            mVideoView.seekTo((int) ((duration * seekBar.getProgress() * 1.0) / sMaxVideoProgress));
            mHandler.removeMessages(PlayStateParams.MESSAGE_SHOW_PROGRESS);
            mIsDragging = false;
            mHandler.sendEmptyMessageDelayed(PlayStateParams.MESSAGE_SHOW_PROGRESS, 1000);
            playOrPause(false);
        }
    };

    /**
     * 亮度进度条滑动监听
     */
    private final SeekBar.OnSeekBarChangeListener mOnBrightnessControllerChangeListener = new SeekBar.OnSeekBarChangeListener() {
        /**数值的改变*/
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            setBrightness(progress);
        }

        /**开始拖动*/
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        /**停止拖动*/
        public void onStopTrackingTouch(SeekBar seekBar) {
            mBrightness = -1;
        }
    };

    public void setBrightness(int value) {
        android.view.WindowManager.LayoutParams layout = this.mActivity.getWindow().getAttributes();
        if (mBrightness < 0) {
            mBrightness = mActivity.getWindow().getAttributes().screenBrightness;
            if (mBrightness <= 0.00f) {
                mBrightness = 0.50f;
            } else if (mBrightness < 0.01f) {
                mBrightness = 0.01f;
            }
        }
        if (value < 1) {
            value = 1;
        }
        if (value > 100) {
            value = 100;
        }
        layout.screenBrightness = 1.0F * (float) value / 100.0F;
        if (layout.screenBrightness > 1.0f) {
            layout.screenBrightness = 1.0f;
        } else if (layout.screenBrightness < 0.01f) {
            layout.screenBrightness = 0.01f;
        }
        this.mActivity.getWindow().setAttributes(layout);
    }

    /**
     * 声音进度条滑动监听
     */
    private final SeekBar.OnSeekBarChangeListener mOnVolumeControllerChangeListener = new SeekBar.OnSeekBarChangeListener() {
        /**数值的改变*/
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            int index = (int) (mMaxVolume * progress * 0.01);
            if (index > mMaxVolume) {
                index = mMaxVolume;
            } else if (index < 0) {
                index = 0;
            }
            // 变更声音
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
        }

        /**开始拖动*/
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        /**停止拖动*/
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mVolume = -1;
        }
    };

    /**
     * 播放器的手势监听
     */
    public class PlayerGestureListener extends GestureDetector.SimpleOnGestureListener {

        /**
         * 是否是按下的标识，默认为其他动作，true为按下标识，false为其他动作
         */
        private boolean isDownTouch;
        /**
         * 是否声音控制,默认为亮度控制，true为声音控制，false为亮度控制
         */
        private boolean isVolume;
        /**
         * 是否横向滑动，默认为纵向滑动，true为横向滑动，false为纵向滑动
         */
        private boolean isLandscape;

        /**
         * 视频视窗双击事件
         */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
//            /**双击全屏切换*/
//            if (!mIsForbidTouch && !mIsOnlyFullScreen && !mIsForbidDoulbeUp) {
//                toggleFullScreen();
//            }
            /**
             * 视频播放完成后，目前双击可以实现重播，需求是：未播放完成双击暂停与播放，播放完成后，需要点击按钮才能重新播放
             */
            if (mStatus == PlayStateParams.STATE_COMPLETED || mStatus == PlayStateParams.STATE_ERROR) {
                return true;
            }

            /**双击播放或暂停视频*/
            if (!mIsForbidTouch && !mIsOnlyFullScreen && !mIsForbidDoulbeUp) {
                playOrPause(true);
            }
            return true;
        }

        /**
         * 按下
         */
        @Override
        public boolean onDown(MotionEvent e) {
            isDownTouch = true;
            return super.onDown(e);
        }

        /**
         * 滑动
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (!mIsForbidTouch) {
                float mOldX = e1.getX(), mOldY = e1.getY();
                float deltaY = mOldY - e2.getY();
                float deltaX = mOldX - e2.getX();
                if (isDownTouch) {
                    isLandscape = Math.abs(distanceX) >= Math.abs(distanceY);
                    isVolume = mOldX > mScreenWidthPixels * 0.5f;
                    isDownTouch = false;
                }
                /**视频处于这些状态时不执行下列操作*/
                if (mStatus == PlayStateParams.STATE_IDLE || mStatus == PlayStateParams.STATE_ERROR || mStatus == PlayStateParams.STATE_COMPLETED) {
                    return super.onScroll(e1, e2, distanceX, distanceY);
                }
                /**横向滑动*/
                if (isLandscape) {
                    /**进度设置*/
                    onProgressSlide(-deltaX / mVideoView.getWidth());
                } else {
                    float percent = deltaY / mVideoView.getHeight();
                    if (isVolume) {
                        /**声音设置*/
                        onVolumeSlide(percent);
                    } else {
                        /**亮度设置*/
                        onBrightnessSlide(percent);
                    }
                }
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        /**
         * 单击
         */
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            /**视频视窗单击事件*/
            if (!mIsForbidTouch) {
                operatorPanl();
            }
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.app_video_cast) {
            if (mOnClickedViewListener != null) {
                mOnClickedViewListener.onCastViewClicked(v);
            }
            /**投屏*/
            showCast();
        } else if (id == R.id.app_video_menu) {
            /**菜单*/
            showMenu();
        } else if (id == R.id.app_video_stream) {
            /**选择分辨率*/
            showStreamSelectView();
        } else if (id == R.id.ijk_iv_rotation) {
            /**旋转视频方向*/
            setPlayerRotation();
        } else if (id == R.id.app_video_fullscreen) {
            /**视频全屏切换*/
            toggleFullScreen();
        } else if (id == R.id.play_icon || id == R.id.app_video_play) {
            Log.d(TAG, "视频播放和暂停");
            /**视频播放和暂停*/
            playOrPause(true);
        } else if (id == R.id.app_video_finish) {
            /**返回*/
            if (!mIsOnlyFullScreen && !mIsPortrait) {
                mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                if (mPlayerBack != null) {
                    mPlayerBack.onPlayerBack();
                } else {
                    mActivity.finish();
                }
            }
        } else if (id == R.id.app_video_replay_icon) {
            if (mOnClickedViewListener != null) {
                mOnClickedViewListener.onReplayViewClicked(v);
            }
            /**重新播放*/
            rePlay();
        } else if (id == R.id.app_video_netTie_icon || id == R.id.app_video_netTie_box) {
            // 新加入功能后，这里暂时没用到，没测
            /**使用移动网络提示继续播放*/
            setNetWorkTypeTie(false);
            hideStatusUI();
            /**显示视频封面*/
            mIvTrumb.setVisibility(View.VISIBLE);
            startPlay();
        }
    }

    /**
     * 重新播放
     */
    private void rePlay() {
        mStatus = PlayStateParams.STATE_ERROR;
        mCurrentPosition = 0;
        stopPlay();
        hideStatusUI();
        startPlay();
        updatePausePlayUI();
    }

    /**
     * 视频播放和暂停
     */
    private void playOrPause(boolean canPause) {
        if (mStatus == PlayStateParams.STATE_COMPLETED) {
            rePlay();
            return;
        }
        if (mVideoView.isPlaying()) {
            if (canPause) {
                pausePlay();
//                updatePausePlayUI();
            } else {
                return;
            }
        } else {
            startPlay();
            if (mVideoView.isPlaying()) {
                /**ijkplayer内部的监听没有回调，只能手动修改状态*/
                mStatus = PlayStateParams.STATE_PREPARING;
                hideStatusUI();
//                updatePausePlayUI();
            }
        }
        updatePausePlayUI();
    }

    /**
     * 旋转视频方向
     */
    private void setPlayerRotation() {
        if (mRotation == 0) {
            mRotation = 90;
        } else if (mRotation == 90) {
            mRotation = 270;
        } else if (mRotation == 270) {
            mRotation = 0;
        }
        setPlayerRotation(mRotation);
    }

    /**
     * 全屏切换
     */
    private void toggleFullScreen() {
        if (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        updateFullScreenButton();
    }

    /**
     * 显示菜单设置
     */
    private void showMenu() {
//        volumeController.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) * 100 / mMaxVolume);
//        brightnessController.setProgress((int) (mActivity.getWindow().getAttributes().screenBrightness * 100));
//        settingsContainer.setVisibility(View.VISIBLE);
//        if (!isForbidHideControlPanl) {
//            ll_topbar.setVisibility(View.GONE);
//            ll_bottombar.setVisibility(View.GONE);
//        }
//        return this;
    }

    /**
     * 显示投屏设置
     */
    private void showCast() {
    }

    /**
     * 显示分辨率列表
     */
    private void showStreamSelectView() {
        mSelectStreamContainer.setVisibility(View.VISIBLE);
        if (!mIsForbidHideControlPanl) {
            mTopControlbarContainer.setVisibility(View.GONE);
            mBottomControlbarContainer.setVisibility(View.GONE);
        }
        mSelectStreamListView.setItemsCanFocus(true);
    }

    /**
     * 隐藏分辨率列表
     */
    private void hideStreamSelectView() {
        mSelectStreamContainer.setVisibility(View.GONE);
    }

    /**
     * 收起控制面板轮询，默认5秒无操作，收起控制面板，
     */
    private class AutoPlayRunnable implements Runnable {
        private int AUTO_PLAY_INTERVAL = 5000;
        private boolean mShouldAutoPlay;

        /**
         * 五秒无操作，收起控制面板
         */
        public AutoPlayRunnable() {
            mShouldAutoPlay = false;
        }

        public void start() {
//            LogUtil.d(TAG, "AutoPlayRunnable start");
            if (!mShouldAutoPlay) {
                mShouldAutoPlay = true;
                mHandler.removeCallbacks(this);
                mHandler.postDelayed(this, AUTO_PLAY_INTERVAL);
            }
        }

        public void stop() {
//            LogUtil.d(TAG, "AutoPlayRunnable stop");
            if (mShouldAutoPlay) {
                mHandler.removeCallbacks(this);
                mShouldAutoPlay = false;
            }
        }

        @Override
        public void run() {
//            LogUtil.d(TAG, "AutoPlayRunnable run");
            if (mShouldAutoPlay) {
                mHandler.removeCallbacks(this);
                if (!mIsForbidTouch) {
                    operatorPanl();
                }
            }
        }
    }
}
