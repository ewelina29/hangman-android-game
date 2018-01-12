package com.example.eweli.sm_projekt;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import static com.example.eweli.sm_projekt.MenuActivity.KINDERGARTEN_LEVEL;
import static com.example.eweli.sm_projekt.MenuActivity.PREFERENCES_CURRENT_LEVEL;
import static com.example.eweli.sm_projekt.MenuActivity.PREFERENCES_NAME;
import static com.example.eweli.sm_projekt.MenuActivity.UNIVERSITY_LEVEL;

/**
 * Created by eweli on 30.12.2017.
 */

public class SettingsFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {

    RadioGroup leverRadioGroup;
    SharedPreferences preferences;

    public static Fragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, parent, false);
        String currentLevel = MenuActivity.getCurrentLevel();

        leverRadioGroup = (RadioGroup) rootView.findViewById(R.id.levelRadioGroup);

        if(currentLevel.equals(UNIVERSITY_LEVEL)){

            leverRadioGroup.check(R.id.universityLevel);
        }else {
            leverRadioGroup.check(R.id.kindergartenLevel);

        }

        leverRadioGroup.setOnCheckedChangeListener(this);


        return rootView;

    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

        if (checkedId == R.id.universityLevel){
            MenuActivity.setCurrentLevel(MenuActivity.UNIVERSITY_LEVEL);
        }
        else if (checkedId == R.id.kindergartenLevel){
            MenuActivity.setCurrentLevel(MenuActivity.KINDERGARTEN_LEVEL);

        }
    }
}

