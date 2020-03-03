package com.testapp.videocallingwithsnich;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class operationDialog extends Dialog {
    Button btncall;
    Button btnmsg;
    String username;

    public operationDialog(@NonNull Context context, String username) {
        super(context);
        this.username=username;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        initViews();

    }

    private void initViews() {
        btnmsg = findViewById(R.id.btnmsg);
        btncall = findViewById(R.id.btncall);
        btncall.setOnClickListener(callListener);
        btnmsg.setOnClickListener(msgListner);
    }
    private View.OnClickListener callListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dismiss();
            Intent makecall = new Intent(getContext(),CallingActivity.class);
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
