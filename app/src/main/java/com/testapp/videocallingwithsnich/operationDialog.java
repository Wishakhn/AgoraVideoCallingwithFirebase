package com.testapp.videocallingwithsnich;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class operationDialog extends Dialog {
    Button btncall;
    Button btnmsg;
    String username;
    Activity activity;

    public operationDialog(@NonNull Activity context, String username) {
        super(context);
        this.username=username;
        this.activity=context;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        initViews();
    }
    private static final int PERMISSION_REQ_ID = 22;
    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private void initViews() {
        btnmsg = findViewById(R.id.btnmsg);
        btncall = findViewById(R.id.btncall);
        btncall.setOnClickListener(callListener);
        btnmsg.setOnClickListener(msgListner);
    }
    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(getContext(), permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }

        return true;
    }
    private View.OnClickListener callListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dismiss();
            Intent makecall = new Intent(getContext(),VideoChatViewActivity.class);
            makecall.putExtra("uname",username);
            view.getContext().startActivity(makecall);
        }
    };
    private View.OnClickListener msgListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

}
