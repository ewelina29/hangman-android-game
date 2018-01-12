package com.example.eweli.sm_projekt;

import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import static android.view.View.*;

public class MenuActivity extends AppCompatActivity {

    public static final String PREFERENCES_NAME = "SharedPreferences";
    public static final String PREFERENCES_CURRENT_LEVEL = "currentLevel";
    public static final String KINDERGARTEN_LEVEL = "kindergarten";
    public static final String UNIVERSITY_LEVEL = "university";

    private static SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_menu);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.activity_main_fragment_container, MainMenuFragment.newInstance(), "MainMenuFragment")
                .commit();


    }

    public static String getCurrentLevel() {
        String currentLevel = preferences.getString(MenuActivity.PREFERENCES_CURRENT_LEVEL, "kindergarten");
        return currentLevel;
    }
    public static void setCurrentLevel(String newLevel){
        SharedPreferences.Editor preferencesEditor = preferences.edit();;
        preferencesEditor.putString(PREFERENCES_CURRENT_LEVEL, newLevel);
        preferencesEditor.commit();
    }


}
