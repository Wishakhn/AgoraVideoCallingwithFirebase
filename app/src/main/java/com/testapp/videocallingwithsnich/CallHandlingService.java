package com.testapp.videocallingwithsnich;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.RingtoneManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CallHandlingService extends Service {
    private static final String VIDEO_CALL_LOG ="VIDEO_CALL_LOG" ;
    private static final String PACKAGE_NAME="com.testapp.videocallingwithsnich";
    private static final String EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME +".started_notification";
    private boolean mChangingConfiguration = false;
    private Handler mServiceHandler;
    private final IBinder mBinder = new CallAppBinder();
    private static final int NOTIFICATION_ID = 123427834;
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
     String userId ;
    DatabaseReference database;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(VIDEO_CALL_LOG, "Service is in onBind state");
        stopForeground(true);
        mChangingConfiguration = false;

        return mBinder;    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        if (!mChangingConfiguration) {
            Log.i(VIDEO_CALL_LOG, "Starting foreground service");
            startForeground(NOTIFICATION_ID, showCallNotification());
        }
        return true;

    }

    @Override
    public void onRebind(Intent intent) {
        Log.i(VIDEO_CALL_LOG, "Service is in onRebind state");
        stopForeground(true);
        mChangingConfiguration = false;
        super.onRebind(intent);


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mChangingConfiguration = true;
    }

    public void startCallService() {
        startService(new Intent(getApplicationContext(), CallHandlingService.class));

    }

    public class CallAppBinder extends Binder {
        public CallHandlingService getService() {
            return CallHandlingService.this;
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread handlerThread = new HandlerThread(VIDEO_CALL_LOG);
        handlerThread.start();
        mServiceHandler = new Handler(handlerThread.getLooper());
         firebaseAuth = FirebaseAuth.getInstance();
         currentUser = firebaseAuth.getCurrentUser();
         userId = currentUser.getUid();
         database = FirebaseDatabase.getInstance().getReference("Calls");
         checkifAnyoneCalling();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, showCallNotification());
        if (intent != null){
            checkifAnyoneCalling();
        }
        boolean startedFromNotification = intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION,
                false);
        if (startedFromNotification) {
            stopSelf();
        }
        return START_NOT_STICKY;
    }

    public void stopCallService(){
        stopSelf();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mServiceHandler.removeCallbacksAndMessages(null);
    }


    void checkifAnyoneCalling() {
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot shots : dataSnapshot.getChildren()) {
                    CallModel model = shots.getValue(CallModel.class);
                    String reciverId = model.getReciverId();
                    String recivername = model.getReciverName();
                    String caller = model.getCallerName();
                    String callState = model.getCallState();
                    if (userId.equalsIgnoreCase(reciverId) && callState.equalsIgnoreCase("calling")) {
                        launchCallActivity(recivername,caller);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i(VIDEO_CALL_LOG,"Error we got "+databaseError.getMessage());
            }
        });
    }

    private void launchCallActivity(String reciver, String caller) {
        Intent showcall = new Intent(getApplicationContext(),IncommingCallActivity.class);
        showcall.putExtra("recivername", reciver);
        showcall.putExtra("callername", caller);
        startActivity(showcall);
    }

    private Notification showCallNotification() {
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = notificationManager.getNotificationChannel("NotifitcationFromCaller");
            if (mChannel == null) {
                mChannel = new NotificationChannel("NotifitcationFromCaller", "LetsVideoCall", importance);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notificationManager.createNotificationChannel(mChannel);
            }
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, "NotifitcationFromCaller")
                .setContentTitle("LetsVideoCall").setContentText("LetsVideoCall is running in foreground")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false);


        assert notificationManager != null;
        notificationManager.notify(0, notification.build());
        return notification.build();
    }



}
