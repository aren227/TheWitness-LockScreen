package com.aren.thewitnesspuzzle.game;

import android.content.Context;
import android.content.SharedPreferences;

public class GameSettings {

    private SharedPreferences sharedPreferences;

    public GameSettings(Context context){
        sharedPreferences = context.getSharedPreferences("com.aren.thewitnesspuzzle.game", Context.MODE_PRIVATE);
    }

    public boolean getSoundsEnabled(){
        return sharedPreferences.getBoolean("sounds", true);
    }

    public void setSoundsEnabled(boolean enabled){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("sounds", enabled);
        editor.commit();
    }

    public boolean getHoldingPuzzles(){
        return sharedPreferences.getBoolean("holding", false);
    }

    public void setHoldingPuzzles(boolean holding){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("holding", holding);
        editor.commit();
    }

}
