package com.testapp.videocallingwithsnich;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    EditText mail;
    EditText pass;
    FirebaseAuth auth;
    ProgressBar loading;
    DatabaseReference firebaseRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();
        mail = findViewById(R.id.mail);
        pass = findViewById(R.id.pass);
        loading = findViewById(R.id.loading);
    }

    public void Loginhandler(View view) {
        String email = mail.getText().toString().trim();
        String password = pass.getText().toString().trim();
        loading.setVisibility(View.VISIBLE);
        loginUser(email, password);

    }

    private void loginUser(String email, String pass) {
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser fUser = auth.getCurrentUser();
                    assert fUser != null;
                    String userId = fUser.getUid();
                    System.out.println("User ID is " + userId);
                    firebaseRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("userStatus");
                    firebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            firebaseRef.setValue("live");
                            loading.setVisibility(View.GONE);
                            Intent gotent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(gotent);
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(LoginActivity.this, "Error !! \n"+databaseError.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    });

                }
                else {
                    loading.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "unable to Login ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
