package com.testapp.videocallingwithsnich;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class noteDialog extends Dialog {
    Button btnclose;

    public noteDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialognote);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        initViews();

    }

    private void initViews() {
        btnclose = findViewById(R.id.btnclose);
        btnclose.setOnClickListener(notCloserListener);
    }
    private View.OnClickListener notCloserListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dismiss();
        }
    };

}
