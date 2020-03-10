package com.testapp.videocallingwithsnich.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.testapp.videocallingwithsnich.R;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

public class IncommingCallActivity extends AppCompatActivity {
    private static final String TAG = "VIDEOCALL_TAG";
    TextView callname;
    TextView callstatus;
    ImageView switchcamera;
    ImageView mutebtn;
    ImageView callingbtn;
    Intent gtent;
    FrameLayout smallvideocontainer;
    FrameLayout callcameracontainer;
    String setrecivername ="";
    RelativeLayout callernameContainer;

    private SurfaceView mLocalView;
    private SurfaceView mRemoteView;
    private RtcEngine mRtcEngine;
    private boolean mStartCall;
    private boolean mMuted;
    String setcallername;
    private DatabaseReference databse;

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
                    setupRemoteVideo(uid);
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

    private void setupRemoteVideo(int uid) {
        // Only one remote video view is available for this
        // tutorial. Here we check if there exists a surface
        // view tagged as this uid.
        int count = callcameracontainer.getChildCount();
        View view = null;
        for (int i = 0; i < count; i++) {
            View v = callcameracontainer.getChildAt(i);
            if (v.getTag() instanceof Integer && ((int) v.getTag()) == uid) {
                view = v;
            }
        }

        if (view != null) {
            return;
        }

        mRemoteView = RtcEngine.CreateRendererView(getBaseContext());
        callcameracontainer.addView(mRemoteView);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(mRemoteView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
        mRemoteView.setTag(uid);
        callstatus.setText("Ringing . . . . .");
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiving);
        initView();
        initEngineAndJoinChannel();
    }
    private void initView() {
        gtent = getIntent();
        callcameracontainer = findViewById(R.id.remote_video_view_container1);
        smallvideocontainer = findViewById(R.id.local_video_view_container1);
        callname = findViewById(R.id.callname1);
        callstatus = findViewById(R.id.callstatus1);
        switchcamera = findViewById(R.id.switchcamera1);
        mutebtn = findViewById(R.id.mutebtn1);
        callernameContainer = findViewById(R.id.callernameContainer1);
        callingbtn = findViewById(R.id.callingbtn1);
        callingbtn.setOnClickListener(callListner);
        switchcamera.setVisibility(View.GONE);
        mutebtn.setVisibility(View.GONE);
        callstatus.setText("Calling . . . . .");
        if (gtent != null) {
             setrecivername = gtent.getStringExtra("recivername");
              setcallername = gtent.getStringExtra("callername");
              String callerId = gtent.getStringExtra("callerid");
            callname.setText(setcallername);
            databse  = FirebaseDatabase.getInstance().getReference("Calls").child(callerId).child("callState");
        }
    }

    private View.OnClickListener callListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mStartCall) {
                endCall();
                mStartCall = true;
                callingbtn.setImageResource(R.drawable.btn_startcall_normal);
                finish();

            } else {
                startCall();
                mStartCall = false;
                callingbtn.setImageResource(R.drawable.btn_endcall_normal);
                oncallAttended();
                finishOnnoResponse();
            }
            showButtons(!mStartCall);

        }
    };

    private void initEngineAndJoinChannel() {
        // This is our usual steps for joining
        // a channel and starting a call.
        initializeEngine();
        setupVideoConfig();
        setupLocalVideo();
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

    private void setupLocalVideo() {
        // This is used to set a local preview.
        // The steps setting local and remote view are very similar.
        // But note that if the local user do not have a uid or do
        // not care what the uid is, he can set his uid as ZERO.
        // Our server will assign one and return the uid via the event
        // handler callback function (onJoinChannelSuccess) after
        // joining the channel successfully.
        mLocalView = RtcEngine.CreateRendererView(getBaseContext());
        mLocalView.setZOrderMediaOverlay(true);
        smallvideocontainer.addView(mLocalView);
        mRtcEngine.setupLocalVideo(new VideoCanvas(mLocalView, VideoCanvas.RENDER_MODE_HIDDEN, 0));
    }

    private void joinChannel() {
        // 1. Users can only see each other after they join the
        // same channel successfully using the same app id.
        // 2. One token is only valid for the channel name that
        // you use to generate this token.
        String token = getString(R.string.agora_access_token);
        if (TextUtils.isEmpty(token) || TextUtils.equals(token, "#YOUR ACCESS TOKEN#")) {
            token = null; // default, no token
        }
        mRtcEngine.joinChannel(token, setrecivername, "Extra Optional Data", 0);
    }
    private void oncallAttended(){
        callernameContainer.setVisibility(View.GONE);
        callcameracontainer.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mStartCall) {
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
        databse.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                databse.setValue("connected");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        setupLocalVideo();
        joinChannel();
    }

    private void endCall() {
        databse.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                databse.setValue("disconnected");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
