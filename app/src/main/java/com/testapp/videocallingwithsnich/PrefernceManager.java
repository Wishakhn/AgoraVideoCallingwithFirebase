package com.testapp.videocallingwithsnich;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefernceManager {
    private SharedPreferences sherdPrefs;
    private Context context;
    private final String SHARED_PREFERNCE_NAME = "VIDEO_PREFERNCES";
    private final int PREF_MODE = Context.MODE_PRIVATE;
    SharedPreferences.Editor editor;
    public static final String USER_ONLINE_KEY = "isUserOnline";
    public static final String LAUNCH_KEY = "serviceisRunning";

    public PrefernceManager(Context context) {
        this.context = context;
    }

    public void initPrefernce(){
        sherdPrefs = context.getSharedPreferences(SHARED_PREFERNCE_NAME,PREF_MODE);
    }

    public  void saveUserState(boolean state){
        editor  = sherdPrefs.edit();
        editor.putBoolean(USER_ONLINE_KEY,state);
        editor.apply();
    }
    public void saveBooleanPrefernce(String key, boolean value){
        editor  = sherdPrefs.edit();
        editor.putBoolean(key,value);
        editor.apply();
    }
    public boolean loadBooleanPrefernce(String key){
        return sherdPrefs.getBoolean(key,false);
    }
    public String loadStringPreference(String key){
        return  sherdPrefs.getString(key," ");
    }
    public int loadIntegerPrefernce(String key){
        return  sherdPrefs.getInt(key,0);
    }
}
