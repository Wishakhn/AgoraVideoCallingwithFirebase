package com.testapp.videocallingwithsnich;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    public void locateClass(View view) {
        switch (view.getId()){
            case R.id.signinbtn:
                handleIntent(LoginActivity.class);
                break;

            case R.id.signupbtn:
                handleIntent(SignUpActivity.class);
                break;
        }
    }

    private void handleIntent(Class Target) {
        Intent move = new Intent(StartActivity.this,Target);
        startActivity(move);
    }
}
