package com.testapp.videocallingwithsnich;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class CallingActivity extends AppCompatActivity {
    TextView callname;
    TextView callstatus;
    ImageView switchcamera;
    ImageView mutebtn;
    ImageView callingbtn;
    Intent gtent;
    FrameLayout smallvideocontainer;
    FrameLayout callcameracontainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);
        initView();

    }

    private void initView() {
        gtent = getIntent();
        callcameracontainer = findViewById(R.id.callcameracontainer);
        smallvideocontainer = findViewById(R.id.smallvideocontainer);
        callname = findViewById(R.id.callname);
        callstatus = findViewById(R.id.callstatus);
        switchcamera = findViewById(R.id.switchcamera);
        mutebtn = findViewById(R.id.mutebtn);
        callingbtn = findViewById(R.id.callingbtn);
        callingbtn.setOnClickListener(callListner);
        if (gtent != null) {
            String setcallername = gtent.getStringExtra("uname");
            callname.setText(setcallername);
        }
    }

    private View.OnClickListener callListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };
}
