package com.testapp.videocallingwithsnich;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    List<callModel> calls;
    ImageView menubtn;
    LinearLayout logoutbtn;
    int show = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        callbtn = findViewById(R.id.callbtn);
        userTitle = findViewById(R.id.userTitle);
        userbtn = findViewById(R.id.userbtn);
        logoutbtn = findViewById(R.id.logoutbtn);
        menubtn = findViewById(R.id.menubtn);
        norectext = findViewById(R.id.norectext);
        loading = findViewById(R.id.loading);
        users = new ArrayList<>();
        calls = new ArrayList<>();
        itemcontainer = findViewById(R.id.itemcontainer);
        itemcontainer.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
        aFunction();

    }

    @Override
    protected void onStart() {
        super.onStart();
        userLoader();
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
        userbtn.setBackgroundColor(getResources().getColor(R.color.fadeblue));
        callbtn.setBackgroundColor(getResources().getColor(R.color.transparent));
        loadUserlist();

    }

    private void callLoader() {
        callbtn.setBackgroundColor(getResources().getColor(R.color.fadeblue));
        userbtn.setBackgroundColor(getResources().getColor(R.color.transparent));
        loadCalllist();
        callLogadapter cAdapter = new callLogadapter(calls);
        itemcontainer.setAdapter(cAdapter);
        cAdapter.notifyDataSetChanged();
    }

    private void loadUserlist() {
        users.clear();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println("Database is :" + dataSnapshot.getChildren());
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    System.out.println("Login User is " + userModel);
                    assert userModel != null;
                    System.out.println("Loginn Id with getter" + userModel.getUserId());
                    if (!userModel.getUserId().equals(user.getUid())) {
                        users.add(userModel);
                        System.out.println("user list is " + users);
                    }

                }
                loading.setVisibility(View.GONE);
                UserListadapter uAdapter = new UserListadapter(users, MainActivity.this);
                itemcontainer.setAdapter(uAdapter);
                uAdapter.notifyDataSetChanged();

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
        calls.add(new callModel("35 mins", "Gippy Girewal"));
        calls.add(new callModel("1 hr 2 mins", "Honney Singh"));
        calls.add(new callModel("10 mins", "Gippy Girewal"));
        calls.add(new callModel("0 min", "Sophiee"));
        calls.add(new callModel("35 mins", "Bohemiea"));
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


}
