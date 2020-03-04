package com.testapp.videocallingwithsnich;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionHandler {
public final static int REQUEST_CAM_MIC_PERMISSION =1001;

    private PermissionHandler() {
    }


    public static boolean checkAllPermissions(Context context) {
        int writeExternalResults = ContextCompat.checkSelfPermission(context, "android.permission.WRITE_EXTERNAL_STORAGE");
        int readExternalResults = ContextCompat.checkSelfPermission(context, "android.permission.READ_EXTERNAL_STORAGE");
        int micPermission = ContextCompat.checkSelfPermission(context, "android.permission.RECORD_AUDIO");
        int micmodifPermission = ContextCompat.checkSelfPermission(context, "android.permission.MODIFY_AUDIO_SETTINGS");
        int checkCameraPermission = ContextCompat.checkSelfPermission(context, "android.permission.CAMERA");

        return writeExternalResults == 0 && readExternalResults == 0 && micPermission == 0 && micmodifPermission==0 && checkCameraPermission == 0 ;
    }

    public static void requestcamPermissions(Activity activity) {
         ActivityCompat.requestPermissions(activity, new String[]{
                "android.permission.CAMERA",
                "android.permission.WRITE_EXTERNAL_STORAGE",
                "android.permission.READ_EXTERNAL_STORAGE",
                 "android.permission.RECORD_AUDIO",
                 "android.permission.MODIFY_AUDIO_SETTINGS"
                }, REQUEST_CAM_MIC_PERMISSION);

    }

}
