package com.testapp.videocallingwithsnich;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefernceManager {
    SharedPreferences sherdPrefs;
    Context context;
    final String SHARED_PREFERNCE_NAME = "VIDEO_PREFERNCES";
    final int PREF_MODE = Context.MODE_PRIVATE;
    SharedPreferences.Editor editor;

    public PrefernceManager(Context context) {
        this.context = context;
    }

    public void init_prefernce(){
        sherdPrefs = context.getSharedPreferences(SHARED_PREFERNCE_NAME,PREF_MODE);
        editor  = sherdPrefs.edit();
    }

    public  void saveUserState(boolean state){
        editor.putBoolean("isUserOnline",state);
        editor.apply();
    }

}
