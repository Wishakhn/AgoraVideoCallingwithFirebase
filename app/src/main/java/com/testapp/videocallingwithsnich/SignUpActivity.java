package com.testapp.videocallingwithsnich;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    EditText username;
    EditText email;
    EditText password;
    EditText gender;
    private FirebaseAuth auth;
    DatabaseReference firebaseRef;
    ProgressBar loading;
    RadioGroup getgender;
    String gen="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_sign_up);
        getgender = findViewById(R.id.getgender);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        gender = findViewById(R.id.gender);
        loading = findViewById(R.id.loading);
        getgender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                // find the radiobutton by returned id
               RadioButton radioButton = (RadioButton) findViewById(selectedId);
               gen = radioButton.getText().toString();
               gender.setText(gen);
            }
        });
    }

    public void RegisterHandler(View view) {
        loading.setVisibility(View.VISIBLE);
        String name = username.getText().toString().trim();
        String mail = email.getText().toString().trim();
        String pass = password.getText().toString().trim();
        requestRegistration(name, mail, pass, gen);

    }

    private void requestRegistration(final String name, String mail, String pass, final String gen) {
        auth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser fUser = auth.getCurrentUser();
                    assert fUser != null;
                    String userId = fUser.getUid();
                    System.out.println("User ID is " + userId);
                    firebaseRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
                    HashMap<String, String> hashmap = new HashMap<>();
                    hashmap.put("userId", userId);
                    hashmap.put("username", name);
                    hashmap.put("gender", gen);
                    hashmap.put("userStatus", "active");
                    firebaseRef.setValue(hashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                loading.setVisibility(View.GONE);
                                Intent gotent = new Intent(SignUpActivity.this, MainActivity.class);
                                gotent.putExtra("uname",name);
                                startActivity(gotent);
                                finish();
                            }
                            else {
                                Toast.makeText(SignUpActivity.this, "Unable to create database record \n"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    loading.setVisibility(View.GONE);
                    Toast.makeText(SignUpActivity.this, "Error Occured Due to \n" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
