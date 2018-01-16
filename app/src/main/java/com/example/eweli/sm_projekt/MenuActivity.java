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

import com.example.eweli.sm_projekt.database.DatabaseCrud;

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

        fillDatabase();

        setContentView(R.layout.activity_menu);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.activity_main_fragment_container, MainMenuFragment.newInstance(), "MainMenuFragment")
                .commit();


    }

    private void fillDatabase() {


        DatabaseCrud database = new DatabaseCrud(getApplicationContext());
        database.open();
        database.clearTable();

            Word word;
//amnimals
            word = new Word(getString(R.string.squirrel), Category.ANIMALS.name());
            database.addWord(word);
            word = new Word(getString(R.string.tiger), Category.ANIMALS.name());
            database.addWord(word);
            word = new Word(getString(R.string.eagle), Category.ANIMALS.name());
            database.addWord(word);
            word = new Word(getString(R.string.flamingo), Category.ANIMALS.name());
            database.addWord(word);
            word = new Word(getString(R.string.elephant), Category.ANIMALS.name());
            database.addWord(word);
            word = new Word(getString(R.string.penguin), Category.ANIMALS.name());
            database.addWord(word);
            word = new Word(getString(R.string.fox), Category.ANIMALS.name());
            database.addWord(word);
            word = new Word(getString(R.string.whale), Category.ANIMALS.name());
            database.addWord(word);
            word = new Word(getString(R.string.hamster), Category.ANIMALS.name());
            database.addWord(word);
            word = new Word(getString(R.string.gorilla), Category.ANIMALS.name());
            database.addWord(word);
            word = new Word(getString(R.string.hyena), Category.ANIMALS.name());
            database.addWord(word);
            word = new Word(getString(R.string.shark), Category.ANIMALS.name());
            database.addWord(word);
            word = new Word(getString(R.string.hedgehog), Category.ANIMALS.name());
            database.addWord(word);
            word = new Word(getString(R.string.jellyfish), Category.ANIMALS.name());
            database.addWord(word);
            word = new Word(getString(R.string.kangaroo), Category.ANIMALS.name());
            database.addWord(word);

            //geography
            word = new Word(getString(R.string.poland), Category.GEOGRAPHY.name());
            database.addWord(word);
            word = new Word(getString(R.string.france), Category.GEOGRAPHY.name());
            database.addWord(word);
            word = new Word(getString(R.string.germany), Category.GEOGRAPHY.name());
            database.addWord(word);
            word = new Word(getString(R.string.russia), Category.GEOGRAPHY.name());
            database.addWord(word);
            word = new Word(getString(R.string.spain), Category.GEOGRAPHY.name());
            database.addWord(word);
            word = new Word(getString(R.string.italy), Category.GEOGRAPHY.name());
            database.addWord(word);
            word = new Word(getString(R.string.ukraine), Category.GEOGRAPHY.name());
            database.addWord(word);
            word = new Word(getString(R.string.australia), Category.GEOGRAPHY.name());
            database.addWord(word);
            word = new Word(getString(R.string.asia), Category.GEOGRAPHY.name());
            database.addWord(word);
            word = new Word(getString(R.string.greenland), Category.GEOGRAPHY.name());
            database.addWord(word);
            word = new Word(getString(R.string.canada), Category.GEOGRAPHY.name());
            database.addWord(word);
            word = new Word(getString(R.string.slovakia), Category.GEOGRAPHY.name());
            database.addWord(word);
            word = new Word(getString(R.string.portugal), Category.GEOGRAPHY.name());
            database.addWord(word);
            word = new Word(getString(R.string.iceland), Category.GEOGRAPHY.name());
            database.addWord(word);

            //food
            word = new Word(getString(R.string.apple), Category.FOOD.name());
            database.addWord(word);
            word = new Word(getString(R.string.sausage), Category.FOOD.name());
            database.addWord(word);
            word = new Word(getString(R.string.pear), Category.FOOD.name());
            database.addWord(word);
            word = new Word(getString(R.string.lemon), Category.FOOD.name());
            database.addWord(word);
            word = new Word(getString(R.string.pizza), Category.FOOD.name());
            database.addWord(word);
            word = new Word(getString(R.string.carrot), Category.FOOD.name());
            database.addWord(word);
            word = new Word(getString(R.string.banana), Category.FOOD.name());
            database.addWord(word);
            word = new Word(getString(R.string.orange), Category.FOOD.name());
            database.addWord(word);
            word = new Word(getString(R.string.grapefruit), Category.FOOD.name());
            database.addWord(word);
            word = new Word(getString(R.string.tomato), Category.FOOD.name());
            database.addWord(word);
            word = new Word(getString(R.string.cucumber), Category.FOOD.name());
            database.addWord(word);
            word = new Word(getString(R.string.mandarine), Category.FOOD.name());
            database.addWord(word);


            database.close();
    }

    public static String getCurrentLevel() {
        String currentLevel = preferences.getString(MenuActivity.PREFERENCES_CURRENT_LEVEL, "kindergarten");
        return currentLevel;
    }

    public static void setCurrentLevel(String newLevel) {
        SharedPreferences.Editor preferencesEditor = preferences.edit();
        ;
        preferencesEditor.putString(PREFERENCES_CURRENT_LEVEL, newLevel);
        preferencesEditor.commit();
    }


}
