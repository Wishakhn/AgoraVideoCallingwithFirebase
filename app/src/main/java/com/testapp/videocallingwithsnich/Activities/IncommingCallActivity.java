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
import android.widget.LinearLayout;
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

import static com.testapp.videocallingwithsnich.LetsVideoCall.VIDEO_CALL_LOG;

public class IncommingCallActivity extends AppCompatActivity {
    private TextView callname;
    private TextView callstatus;
    private ImageView switchcamera;
    private ImageView mutebtn;
    private ImageView callingbtn;
    private ImageView callendbtn1;
    private Intent gtent;
    private FrameLayout smallvideocontainer;
    private FrameLayout callcameracontainer;
    private String setrecivername = "";
    private RelativeLayout callernameContainer;
    private SurfaceView mLocalView;
    private SurfaceView mRemoteView;
    private RtcEngine mRtcEngine;
    private boolean mStartCall;
    private boolean mMuted;
    private String setcallername;
    private DatabaseReference databse;
    private LinearLayout pickcallLayout;

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
        pickcallLayout = findViewById(R.id.pickcallLayout);
        callname = findViewById(R.id.callname1);
        callstatus = findViewById(R.id.callstatus1);
        switchcamera = findViewById(R.id.switchcamera1);
        mutebtn = findViewById(R.id.mutebtn1);
        callernameContainer = findViewById(R.id.callernameContainer1);
        callendbtn1 = findViewById(R.id.callendbtn1);
        callendbtn1.setOnClickListener(callendListener);
        callingbtn = findViewById(R.id.callingbtn1);
        callingbtn.setOnClickListener(callListner);
        switchcamera.setVisibility(View.GONE);
        mutebtn.setVisibility(View.GONE);
        callstatus.setText("Calling . . . . .");
        if (gtent != null) {
            setrecivername = gtent.getStringExtra("recivername");
            setcallername = gtent.getStringExtra("callername");
            String callerId = gtent.getStringExtra("callerid");
            String reciverid = gtent.getStringExtra("reciverid");
            String callId = callerId+"_"+reciverid;
            callname.setText(setcallername);
            databse = FirebaseDatabase.getInstance().getReference("Calls").child(callId).child("callState");
        }
    }

    private View.OnClickListener callListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startCall();
            mStartCall = false;
            callingbtn.setImageResource(R.drawable.btn_endcall_normal);
            oncallAttended();
            finishOnnoResponse();
            pickcallLayout.setVisibility(View.GONE);
            switchcamera.setVisibility(View.VISIBLE);
            mutebtn.setVisibility(View.VISIBLE);
        }
    };
    private View.OnClickListener callendListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            endCall();
            mStartCall = true;
            callingbtn.setImageResource(R.drawable.btn_startcall_normal);
            finish();
        }
    };

    private void initEngineAndJoinChannel() {
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
            Log.e(VIDEO_CALL_LOG, Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    private void setupVideoConfig() {
        mRtcEngine.enableVideo();
        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
    }

    private void setupLocalVideo() {
        mLocalView = RtcEngine.CreateRendererView(getBaseContext());
        mLocalView.setZOrderMediaOverlay(true);
        smallvideocontainer.addView(mLocalView);
        mRtcEngine.setupLocalVideo(new VideoCanvas(mLocalView, VideoCanvas.RENDER_MODE_HIDDEN, 0));
    }

    private void joinChannel() {
        String token = getString(R.string.agora_access_token);
        if (TextUtils.isEmpty(token) || TextUtils.equals(token, "#YOUR ACCESS TOKEN#")) {
            token = null;
        }
        mRtcEngine.joinChannel(token, setrecivername, "Extra Optional Data", 0);
    }

    private void oncallAttended() {
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
        if (mRtcEngine != null)
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


    void finishOnnoResponse() {
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
                                               }
        );
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
                                               }
        );
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
