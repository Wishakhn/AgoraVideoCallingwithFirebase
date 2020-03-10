package com.testapp.videocallingwithsnich.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.testapp.videocallingwithsnich.R;

import java.util.Random;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

public class CallingActivity extends AppCompatActivity {
    private static final String TAG = "VIDEOCALL_TAG";
    TextView callname;
    TextView callstatus;
    ImageView switchcamera;
    ImageView mutebtn;
    ImageView callingbtn;
    Intent gtent;
    FrameLayout smallvideocontainer;
    FrameLayout callcameracontainer;
    String setcallername="";
    RelativeLayout callernameContainer;
    private SurfaceView mLocalView;
    private SurfaceView mRemoteView;
    private RtcEngine mRtcEngine;
    private boolean mCallEnd;
    private boolean mMuted;


    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Join channel success, uid: " + (uid & 0xFFFFFFFFL));
                }
            });
        }

        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
            super.onFirstRemoteVideoDecoded(uid, width, height, elapsed);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("First remote video decoded, uid: " + (uid & 0xFFFFFFFFL));
                    setupRemoteVideo(uid,callcameracontainer);
                    oncallAttended();
                }
            });
        }

        @Override
        public void onUserOffline(final int uid, int reason) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("User offline, uid: " + (uid & 0xFFFFFFFFL));
                    onRemoteUserLeft();
                }
            });
        }
    };

    private void setupRemoteVideo(int uid, ViewGroup smallScreenContainer) {
        int count = smallScreenContainer.getChildCount();
        View view = null;
        for (int i = 0; i < count; i++) {
            View v = smallScreenContainer.getChildAt(i);
            if (v.getTag() instanceof Integer && ((int) v.getTag()) == uid) {
                view = v;
            }
        }

        if (view != null) {
            return;
        }
        callstatus.setText("Ringing . . . . .");
        mRemoteView = RtcEngine.CreateRendererView(getBaseContext());
        smallScreenContainer.addView(mRemoteView);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(mRemoteView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
        mRemoteView.setTag(uid);
    }

    private void onRemoteUserLeft() {
        removeRemoteVideo();
    }

    private void removeRemoteVideo() {
        if (mRemoteView != null) {
            callcameracontainer.removeView(mRemoteView);
        }
        mRemoteView = null;

    }

private void oncallAttended(){
    callernameContainer.setVisibility(View.GONE);
    callcameracontainer.setVisibility(View.VISIBLE);
}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);
        initView();
        initEngineAndJoinChannel();


    }
    private void initView() {
        gtent = getIntent();
        callcameracontainer = findViewById(R.id.remote_video_view_container);
        smallvideocontainer = findViewById(R.id.local_video_view_container);
        callname = findViewById(R.id.callname);
        callstatus = findViewById(R.id.callstatus);
        switchcamera = findViewById(R.id.switchcamera);
        mutebtn = findViewById(R.id.mutebtn);
        callernameContainer = findViewById(R.id.callernameContainer);
        callingbtn = findViewById(R.id.callingbtn);
        callingbtn.setOnClickListener(callListner);
        if (gtent != null) {
             setcallername = gtent.getStringExtra("recivername");
            callname.setText(setcallername);
        }
    }

    private View.OnClickListener callListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mCallEnd) {
                startCall();
                mCallEnd = false;
                callingbtn.setImageResource(R.drawable.btn_endcall_normal);
                finishOnnoResponse();
            } else {
                endCall();
                mCallEnd = true;
                callingbtn.setImageResource(R.drawable.btn_startcall_normal);
                finish();
            }

            showButtons(!mCallEnd);

        }
    };

    private void initEngineAndJoinChannel() {
        initializeEngine();
        setupVideoConfig();
        setupLocalVideo(smallvideocontainer);
        joinChannel();
        finishOnnoResponse();
    }

    private void initializeEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), mRtcEventHandler);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    private void setupVideoConfig() {
        // In simple use cases, we only need to enable video capturing
        // and rendering once at the initialization step.
        // Note: audio recording and playing is enabled by default.
        mRtcEngine.enableVideo();

        // Please go to this page for detailed explanation
        // https://docs.agora.io/en/Video/API%20Reference/java/classio_1_1agora_1_1rtc_1_1_rtc_engine.html#af5f4de754e2c1f493096641c5c5c1d8f
        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
    }

    private void setupLocalVideo(ViewGroup largeScreen) {
        mLocalView = RtcEngine.CreateRendererView(getBaseContext());
        mLocalView.setZOrderMediaOverlay(true);
        largeScreen.addView(mLocalView);
        mRtcEngine.setupLocalVideo(new VideoCanvas(mLocalView, VideoCanvas.RENDER_MODE_HIDDEN, 0));

    }

    private void joinChannel() {
        String token = getString(R.string.agora_access_token);
        if (TextUtils.isEmpty(token) || TextUtils.equals(token, "#YOUR ACCESS TOKEN#")) {
            token = null; // default, no token
        }
        mRtcEngine.joinChannel(token, setcallername, "Extra Optional Data", 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mCallEnd) {
            leaveChannel();
        }
        RtcEngine.destroy();
    }

    private void leaveChannel() {
        if(mRtcEngine!=null)
            mRtcEngine.leaveChannel();
    }

    public void onLocalAudioMuteClicked(View view) {
        mMuted = !mMuted;
        mRtcEngine.muteLocalAudioStream(mMuted);
        int res = mMuted ? R.drawable.btn_mute_normal : R.drawable.btn_unmute_normal;
        mutebtn.setImageResource(res);
    }

    public void onSwitchCameraClicked(View view) {
        mRtcEngine.switchCamera();
    }



    void finishOnnoResponse(){
        int FinishTime = 10;
        int countDownInterval = 2000;
        CountDownTimer counterTimer = new CountDownTimer(FinishTime * 1000, countDownInterval) {
            public void onFinish() {
                //finish your activity here
                finish();
                endCall();
            }

            public void onTick(long millisUntilFinished) {
                //called every 1 sec coz countDownInterval = 1000 (1 sec)
            }
        };
        counterTimer.start();
    }
    private void startCall() {
        setupLocalVideo(smallvideocontainer);
        joinChannel();
    }

    private void endCall() {
        removeLocalVideo();
        removeRemoteVideo();
        leaveChannel();
    }

    private void removeLocalVideo() {
        if (mLocalView != null) {
            smallvideocontainer.removeView(mLocalView);
        }
        mLocalView = null;
    }

    private void showButtons(boolean show) {
        int visibility = show ? View.VISIBLE : View.GONE;
        mutebtn.setVisibility(visibility);
        switchcamera.setVisibility(visibility);
    }
}
