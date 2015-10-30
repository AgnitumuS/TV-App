package com.google.android.exoplayer.demo;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.google.android.exoplayer.util.PlayerControl;

import java.lang.ref.WeakReference;
import java.util.Formatter;
/**
 * Modified by MortadhaS on 10/3/2015.
 **/
public class CustomMediaController extends FrameLayout
{
    private static final String TAG = "VideoControllerView";


    private Activity host;
    private AudioManager amanager;
    private PlayerControl mPlayer;
    private Context mContext;
    private ViewGroup mAnchor;
    private View mRoot;
    private boolean mShowing;
    private boolean mDragging;
    private static final int sDefaultTimeout = 3000;
    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;
    private boolean mFromXml;
    private boolean mListenersSet;
    private boolean muteButtoneSelected;
    private View.OnClickListener mNextListener, mPrevListener;
    StringBuilder mFormatBuilder;
    Formatter mFormatter;
    private ImageButton mPauseButton;

    private ImageButton mFullscreenButton;
    private ImageButton mHDButton;
    private ImageButton mVolumeButton;

    private Handler mHandler = new MessageHandler(this);

    public CustomMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRoot = null;
        mContext = context;
        mFromXml = true;

        Log.i(TAG, TAG);
    }

    public CustomMediaController(Context context, boolean useFastForward) {
        super(context);
        mContext = context;
        Log.i(TAG, TAG);
    }

    public CustomMediaController(Context context) {
        this(context, true);

        Log.i(TAG, TAG);
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        if (mRoot != null)
            initControllerView(mRoot);
    }

    public void setMediaPlayer(PlayerControl player) {
        mPlayer = player;
        updatePausePlay();
        updateFullScreen();
    }

    /**
     * Set the view that acts as the anchor for the control view. This can for
     * example be a VideoView, or your Activity's main view.
     *
     * @param view
     *            The view to which to anchor the controller when it is visible.
     */
    public void setAnchorView(ViewGroup view) {
        mAnchor = view;

        FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        removeAllViews();
        View v = makeControllerView();
        addView(v, frameParams);
    }

    /**
     * Create the view that holds the widgets that control playback. Derived
     * classes can override this to create their own.
     *
     * @return The controller view.
     * @hide This doesn't work as advertised
     */
    protected View makeControllerView() {
        LayoutInflater inflate = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRoot = inflate.inflate(R.layout.custom_media_controller, null);

        initControllerView(mRoot);

        return mRoot;
    }
//Initialize button
    private void initControllerView(View v)
    {

        host=(Activity) this.getContext();
        amanager=(AudioManager)host.getSystemService(Context.AUDIO_SERVICE);
        mPauseButton = (ImageButton) v.findViewById(R.id.pause);
        if (mPauseButton != null)
        {
            mPauseButton.requestFocus();
            mPauseButton.setOnClickListener(mPauseListener);
        }

        mFullscreenButton = (ImageButton) v.findViewById(R.id.fullscreen);
        if (mFullscreenButton != null)
        {
            mFullscreenButton.requestFocus();
            mFullscreenButton.setOnClickListener(mFullscreenListener);
            PlayerActivity host = (PlayerActivity) this.getContext();
            // mFullscreenButton.setImageResource(R.drawable.ic_fullscreen_white_48dp);
            if (host.getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE)
            {
                mFullscreenButton.setImageResource(R.drawable.ic_fullscreen_exit_white_48dp);
            }
            else if(host.getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT)
            {
                mFullscreenButton.setImageResource(R.drawable.ic_fullscreen_white_48dp);
            }

        }

        mVolumeButton = (ImageButton) v.findViewById(R.id.volume);
        if (amanager.getStreamVolume(AudioManager.STREAM_MUSIC)!=0)
        {

            muteButtoneSelected=false;
            mVolumeButton.setImageResource(R.drawable.ic_volume_up_white_48dp);
            mVolumeButton.requestFocus();
            mVolumeButton.setOnClickListener(mVolumeListener);
        }
        else
        {
            muteButtoneSelected=true;
            mVolumeButton.setImageResource(R.drawable.ic_volume_off_white_48dp);
            mVolumeButton.requestFocus();
            mVolumeButton.setOnClickListener(mVolumeListener);
        }


        mHDButton = (ImageButton) v.findViewById(R.id.hd);
        if (mHDButton != null)
        {
            mHDButton.requestFocus();
            mHDButton.setImageResource(R.drawable.ic_hd_white_48dp);
            //mHDButton.setOnClickListener(mPauseListener);
        }
    }

    /**
     * Show the controller on screen. It will go away automatically after 3
     * seconds of inactivity.
     */
    public void show() {
        show(sDefaultTimeout);
    }

    /**
     * Disable pause or seek buttons if the stream cannot be paused or seeked.
     * This requires the control interface to be a MediaPlayerControlExt
     */
    private void disableUnsupportedButtons() {
        if (mPlayer == null) {
            return;
        }

        try {
            if (mPauseButton != null && !mPlayer.canPause()) {
                mPauseButton.setEnabled(false);
            }
        } catch (IncompatibleClassChangeError ex) {
            // We were given an old version of the interface, that doesn't have
            // the canPause/canSeekXYZ methods. This is OK, it just means we
            // assume the media can be paused and seeked, and so we don't
            // disable
            // the buttons.
        }
    }

    /**
     * Show the controller on screen. It will go away automatically after
     * 'timeout' milliseconds of inactivity.
     *
     * @param timeout
     *            The timeout in milliseconds. Use 0 to show the controller
     *            until hide() is called.
     */
    public void show(int timeout) {
        if (!mShowing && mAnchor != null) {
              if (mPauseButton != null) {
                mPauseButton.requestFocus();
            }
            disableUnsupportedButtons();

            FrameLayout.LayoutParams tlp = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);

            mAnchor.addView(this, tlp);
            mShowing = true;
        }
        updatePausePlay();
        updateFullScreen();

        // cause the progress bar to be updated even if mShowing
        // was already true. This happens, for example, if we're
        // paused with the progress bar showing the user hits play.
        mHandler.sendEmptyMessage(SHOW_PROGRESS);

        Message msg = mHandler.obtainMessage(FADE_OUT);
        if (timeout != 0) {
            mHandler.removeMessages(FADE_OUT);
            mHandler.sendMessageDelayed(msg, timeout);
        }
    }

    public boolean isShowing() {
        return mShowing;
    }

    /**
     * Remove the controller from the screen.
     */
    public void hide() {
        if (mAnchor == null) {
            return;
        }

        try {
            mAnchor.removeView(this);
            mHandler.removeMessages(SHOW_PROGRESS);
        } catch (IllegalArgumentException ex) {
            Log.w("MediaController", "already removed");
        }
        mShowing = false;
    }

    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds)
                    .toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        show(sDefaultTimeout);
        return true;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        show(sDefaultTimeout);
        return false;
    }



    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mPlayer == null) {
            return true;
        }

        int keyCode = event.getKeyCode();
        final boolean uniqueDown = event.getRepeatCount() == 0
                && event.getAction() == KeyEvent.ACTION_DOWN;
        if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK
                || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
                || keyCode == KeyEvent.KEYCODE_SPACE) {
            if (uniqueDown) {
                doPauseResume();
                show(sDefaultTimeout);
                if (mPauseButton != null) {
                    mPauseButton.requestFocus();
                }
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
            if (uniqueDown && !mPlayer.isPlaying()) {
                mPlayer.start();
                updatePausePlay();
                show(sDefaultTimeout);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP
                || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
            if (uniqueDown && mPlayer.isPlaying()) {
                mPlayer.pause();
                updatePausePlay();
                show(sDefaultTimeout);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
                || keyCode == KeyEvent.KEYCODE_VOLUME_UP
                || keyCode == KeyEvent.KEYCODE_MUTE) {
            // don't show the controls for volume adjustment
            return super.dispatchKeyEvent(event);
        } else if (keyCode == KeyEvent.KEYCODE_BACK
                || keyCode == KeyEvent.KEYCODE_MENU) {
            if (uniqueDown) {
                hide();
            }
            return true;
        }

        show(sDefaultTimeout);
        return super.dispatchKeyEvent(event);
    }

    private View.OnClickListener mPauseListener = new View.OnClickListener() {
        public void onClick(View v) {
            doPauseResume();
            show(sDefaultTimeout);
        }
    };

    private View.OnClickListener mFullscreenListener = new View.OnClickListener() {
        public void onClick(View v) {
            doToggleFullscreen();
            show(sDefaultTimeout);
        }
    };

    private View.OnClickListener mVolumeListener = new View.OnClickListener() {
        public void onClick(View v) {
            doChangeVolume();
            show(sDefaultTimeout);
        }
    };

    public void updatePausePlay() {
        if (mRoot == null || mPauseButton == null || mPlayer == null) {
            return;
        }

        if (mPlayer.isPlaying()) {
            mPauseButton.setImageResource(R.drawable.ic_pause_white_48dp);
        } else {
            mPauseButton.setImageResource(R.drawable.ic_play_arrow_white_48dp);
        }
    }

    public void updateFullScreen()
    {
        if (mRoot == null || mFullscreenButton == null || mPlayer == null)
        {
            return;
        }

   /*     Activity host = (Activity) this.getContext();
       // mFullscreenButton.setImageResource(R.drawable.ic_fullscreen_white_48dp);
        if (host.getRequestedOrientation()==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            mFullscreenButton.setImageResource(R.drawable.ic_fullscreen_exit_white_48dp);
        }
        else
        {
            mFullscreenButton.setImageResource(R.drawable.ic_fullscreen_white_48dp);
        }*/
    }

    private void doPauseResume() {
        if (mPlayer == null) {
            return;
        }

        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        } else {
            mPlayer.start();
        }
        updatePausePlay();
    }

    private void doToggleFullscreen() {
        if (mPlayer == null)
        {
            return;
        }
        final Activity host = (Activity) this.getContext();
        if(host.getRequestedOrientation()==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        {
            host.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {

                    host.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

                }
            }, 2000);
        }
        else
        {
            host.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {

                    host.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

                }
            }, 2000);

        }

    }

    private void doChangeVolume()
    {
        Log.v("music", String.valueOf(amanager.getStreamVolume(AudioManager.STREAM_MUSIC)));


        if(amanager.getStreamVolume(AudioManager.STREAM_MUSIC)!=0)
        {

            muteButtoneSelected=true;
            amanager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            mVolumeButton.setImageResource(R.drawable.ic_volume_off_white_48dp);
        }
        else
        {
            muteButtoneSelected=false;
            amanager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            mVolumeButton.setImageResource(R.drawable.ic_volume_up_white_48dp);
        }
    }


        public void onStopTrackingTouch(SeekBar bar) {
            mDragging = false;
            updatePausePlay();
            show(sDefaultTimeout);

            // Ensure that progress is properly updated in the future,
            // the call to show() does not guarantee this because it is a
            // no-op if we are already showing.
            mHandler.sendEmptyMessage(SHOW_PROGRESS);
        }


    @Override
    public void setEnabled(boolean enabled) {
        if (mPauseButton != null) {
            mPauseButton.setEnabled(enabled);
        }
        disableUnsupportedButtons();
        super.setEnabled(enabled);
    }



    private static class MessageHandler extends Handler {
        private final WeakReference<CustomMediaController> mView;

        MessageHandler(CustomMediaController view) {
            mView = new WeakReference<CustomMediaController>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            CustomMediaController view = mView.get();
            if (view == null || view.mPlayer == null) {
                return;
            }

            int pos;
            switch (msg.what) {
                case FADE_OUT:
                    view.hide();
                    break;
                case SHOW_PROGRESS:
                    if (!view.mDragging && view.mShowing
                            && view.mPlayer.isPlaying()) {
                        msg = obtainMessage(SHOW_PROGRESS);
                    }
                    break;
            }
        }
    }
}
