package com.testapp.videocallingwithsnich.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.testapp.videocallingwithsnich.Models.CallModel;
import com.testapp.videocallingwithsnich.Service.CallHandlingService;
import com.testapp.videocallingwithsnich.Helpers.PermissionHandler;
import com.testapp.videocallingwithsnich.Helpers.PrefernceManager;
import com.testapp.videocallingwithsnich.R;
import com.testapp.videocallingwithsnich.Models.UserModel;
import com.testapp.videocallingwithsnich.Models.callLogModel;
import com.testapp.videocallingwithsnich.callLogadapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static com.testapp.videocallingwithsnich.Helpers.PrefernceManager.LAUNCH_KEY;
import static com.testapp.videocallingwithsnich.LetsVideoCall.VIDEO_CALL_LOG;

public class MainActivity extends AppCompatActivity {
    FirebaseUser user;
    DatabaseReference reference;
    FirebaseAuth auth;
    RecyclerView itemcontainer;
    TextView callbtn;
    TextView userTitle;
    TextView userbtn;
    TextView norectext;
    ProgressBar loading;
    List<UserModel> users;
    List<callLogModel> calls;
    ImageView menubtn;
    LinearLayout logoutbtn;
    int show = 0;
    ArrayList<UserModel> totallist;
    ArrayList<UserModel> malelist;
    ArrayList<UserModel> femalelist;
    LinearLayout containerOptions;
    PrefernceManager prefsManager;
    Boolean startservice = false;
    CallHandlingService callhandlingService;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = user.getUid();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        containerOptions = findViewById(R.id.containerOptions);
        prefsManager = new PrefernceManager(MainActivity.this);
        prefsManager.initPrefernce();
        callbtn = findViewById(R.id.callbtn);
        userTitle = findViewById(R.id.userTitle);
        userbtn = findViewById(R.id.userbtn);
        logoutbtn = findViewById(R.id.logoutbtn);
        menubtn = findViewById(R.id.menubtn);
        norectext = findViewById(R.id.norectext);
        loading = findViewById(R.id.loading);
        totallist = new ArrayList<>();
        malelist = new ArrayList<>();
        femalelist = new ArrayList<>();
        users = new ArrayList<>();
        calls = new ArrayList<>();
        itemcontainer = findViewById(R.id.itemcontainer);
        itemcontainer.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
        aFunction();
        userLoader();

    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            CallHandlingService.CallAppBinder binder = (CallHandlingService.CallAppBinder) service;
            callhandlingService = binder.getService();
            startservice = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            callhandlingService = null;
            startservice = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        checkifAnyoneCalling();
      /*  if (prefsManager.loadBooleanPrefernce(LAUNCH_KEY)) {
            bindService(new Intent(this, CallHandlingService.class), mServiceConnection,
                    Context.BIND_AUTO_CREATE);
        }*/

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void changeScreen(View view) {
        switch (view.getId()) {
            case R.id.callbtn:
                callLoader();
                break;

            case R.id.userbtn:
                userLoader();
                break;

            case R.id.menubtn:
                if (show == 0) {
                    logoutbtn.setVisibility(View.VISIBLE);
                } else {
                    logoutbtn.setVisibility(View.GONE);
                }
                break;

            case R.id.logoutbtn:
                logOut();
                break;
        }
    }

    private void userLoader() {
        containerOptions.setVisibility(View.VISIBLE);
        itemcontainer.setVisibility(View.GONE);
        userbtn.setBackgroundColor(getResources().getColor(R.color.fadeblue));
        callbtn.setBackgroundColor(getResources().getColor(R.color.transparent));
        loading.setVisibility(View.GONE);
        loadUserlist();

    }

    private void callLoader() {
        containerOptions.setVisibility(View.GONE);
        itemcontainer.setVisibility(View.VISIBLE);
        callbtn.setBackgroundColor(getResources().getColor(R.color.fadeblue));
        userbtn.setBackgroundColor(getResources().getColor(R.color.transparent));
        loadCalllist();
        callLogadapter cAdapter = new callLogadapter(calls);
        itemcontainer.setAdapter(cAdapter);
        cAdapter.notifyDataSetChanged();
    }

    private void loadUserlist() {
        malelist.clear();
        femalelist.clear();
        totallist.clear();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println("Database is :" + dataSnapshot.getChildren());
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    System.out.println("Login User is " + userModel);
                    assert userModel != null;
                    System.out.println("Loginn Id with getter" + userModel.getUserId());
                    if (!userModel.getUserId().equals(user.getUid())) {
                        if (userModel.getGender().equalsIgnoreCase("Male")) {
                            malelist.add(userModel);
                        } else {
                            femalelist.add(userModel);
                        }
                        totallist.add(userModel);
                    }

                }
                System.out.println("All user list" + totallist.size());
                System.out.println("All Male list" + malelist.size());
                System.out.println("All Female list" + femalelist.size());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loading.setVisibility(View.GONE);
                norectext.setVisibility(View.VISIBLE);
            }
        });

    }

    private void loadCalllist() {
        calls.clear();
        calls.add(new callLogModel("35 mins", "Gippy Girewal"));
        calls.add(new callLogModel("1 hr 2 mins", "Honney Singh"));
        calls.add(new callLogModel("10 mins", "Gippy Girewal"));
        calls.add(new callLogModel("0 min", "Sophiee"));
        calls.add(new callLogModel("35 mins", "Bohemiea"));
        loading.setVisibility(View.GONE);

    }

    void aFunction() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel umodel = dataSnapshot.getValue(UserModel.class);
                System.out.println("User model data is ::" + umodel);
                System.out.println("User model id is ::" + umodel.getUserId());
                System.out.println("User model name is ::" + umodel.getUsername());
                assert umodel != null;
                userTitle.setText(umodel.getUsername());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void logOut() {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("userStatus");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reference.setValue("offline");
                prefsManager.saveUserState(false);
                FirebaseAuth.getInstance().signOut();
                Intent goback = new Intent(MainActivity.this, StartActivity.class);
                goback.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(goback);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Error !! \n" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void createaCall(View view) {
        switch (view.getId()) {
            case R.id.bothgenderLay:
                callRandomUser();
                break;
            case R.id.malegenderLay:
                callaMaleUser();
                break;
            case R.id.femalegenderLay:
                callaFemaleUser();
                break;

        }
    }

    private void createCallingDatabase(final String rname, final String rId) {
        reference = FirebaseDatabase.getInstance().getReference("Calls").child(userId);
        HashMap<String, String> hashmap = new HashMap<>();
        hashmap.put("callerName", user.getDisplayName());
        hashmap.put("callerId", user.getUid());
        hashmap.put("ReciverName", rname);
        hashmap.put("reciverId", rId);
        hashmap.put("callState", "calling");
        hashmap.put("callDur", "00:00:00");
        reference.setValue(hashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Intent callaRandom = new Intent(MainActivity.this, CallingActivity.class);
                    callaRandom.putExtra("recivername", rname);
                    callaRandom.putExtra("reciverid", rId);
                    startActivity(callaRandom);
                } else {
                    Toast.makeText(MainActivity.this, "Error !! \n" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void callRandomUser() {
        if (PermissionHandler.checkAllPermissions(MainActivity.this)) {
            generateAnygenderId();
        } else {
            PermissionHandler.requestcamPermissions(MainActivity.this);
        }
    }

    private void generateAnygenderId() {
        Random randb = new Random();
        int randomIndex = randb.nextInt(totallist.size());
        String anyrandomUser = totallist.get(randomIndex).getUsername();
        String randomuserId = totallist.get(randomIndex).getUserId();
        createCallingDatabase(anyrandomUser, randomuserId);
    }

    private void callaFemaleUser() {
        if (PermissionHandler.checkAllPermissions(MainActivity.this)) {
            generateFemalegenderId();
        } else {
            PermissionHandler.requestcamPermissions(MainActivity.this);
        }
    }

    private void generateFemalegenderId() {
        Random rand1 = new Random();
        int randomIndex = rand1.nextInt(femalelist.size());
        String anyrandomFemale = femalelist.get(randomIndex).getUsername();
        String femaleId = femalelist.get(randomIndex).getUserId();
        createCallingDatabase(anyrandomFemale, femaleId);

    }

    private void callaMaleUser() {
        if (PermissionHandler.checkAllPermissions(MainActivity.this)) {
            generateMalegenderId();
        } else {
            PermissionHandler.requestcamPermissions(MainActivity.this);
        }

    }

    private void generateMalegenderId() {
        Random rand = new Random();
        int randomIndex = rand.nextInt(malelist.size());
        String anyrandomMale = malelist.get(randomIndex).getUsername();
        String maleId = malelist.get(randomIndex).getUserId();
        createCallingDatabase(anyrandomMale, maleId);

    }
    private void launchCallActivity(String reciver, String reciverId, String caller, String callerId) {
        Intent showcall = new Intent(getApplicationContext(), IncommingCallActivity.class);
        showcall.putExtra("recivername", reciver);
        showcall.putExtra("callername", caller);
        showcall.putExtra("callerid", callerId);
        showcall.putExtra("reciverid", reciverId);
        startActivity(showcall);
    }
    private void startCallingService(String rec, String caller) {
        prefsManager.saveBooleanPrefernce(LAUNCH_KEY, true);
        if (callhandlingService != null) {
            callhandlingService.startCallService(rec, caller);
            if (startservice) {
                unbindService(mServiceConnection);
                startservice = false;
            }
        } else {
            Intent serviceIntent = new Intent(getApplicationContext(), CallHandlingService.class);
            serviceIntent.putExtra("recivername",rec);
            serviceIntent.putExtra("callername",caller);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getApplicationContext().startForegroundService(serviceIntent);
            } else {
                getApplicationContext().startService(serviceIntent);
            }
        }

    }

    private void stopCallingService() {
        prefsManager.saveBooleanPrefernce(LAUNCH_KEY, false);
        startservice = false;
        if (callhandlingService != null) {
            callhandlingService.stopCallService();
        } else {
            Intent serviceIntent = new Intent(getApplicationContext(), CallHandlingService.class);
            stopService(serviceIntent);
        }
    }
    void checkifAnyoneCalling() {
      DatabaseReference  database = FirebaseDatabase.getInstance().getReference("Calls");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot shots : dataSnapshot.getChildren()) {
                    CallModel model = shots.getValue(CallModel.class);
                    Log.i(VIDEO_CALL_LOG,"Call model we get "+model);
                    String reciverId = model.getReciverId();
                    String recivername = model.getReciverName();
                    String callerid = model.getCallerId();
                    String caller = model.getCallerName();
                    String callState = model.getCallState();
                    if (userId.equalsIgnoreCase(reciverId) && callState.equalsIgnoreCase("calling")) {
                        Log.i(VIDEO_CALL_LOG,"We got the call");
                        launchCallActivity(recivername,reciverId,caller,callerid);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i(VIDEO_CALL_LOG,"Error we got "+databaseError.getMessage());
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionHandler.REQUEST_CAM_MIC_PERMISSION) {
            if (grantResults.length <= 0 || grantResults[0] != 0) {
                Toast.makeText(this, "you cannot make a call until you allow these permissions", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "Required permissions granted !", Toast.LENGTH_SHORT).show();
                generateMalegenderId();
            }
        }
    }
}
