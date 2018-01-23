package com.example.eweli.sm_projekt;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import static com.example.eweli.sm_projekt.MenuActivity.MUSIC_ON;
import static com.example.eweli.sm_projekt.MenuActivity.PREFERENCES_NAME;

/**
 * Created by eweli on 30.12.2017.
 */

public class SettingsFragment extends Fragment implements View.OnClickListener {

    private SharedPreferences preferences;
    private LetterTextView musicMode;

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
        musicMode = (LetterTextView) rootView.findViewById(R.id.soundSettings);
        musicMode.setOnClickListener(this);
        if (MenuActivity.getMusicMode().equals(MenuActivity.MUSIC_ON)){
            musicMode.setBackground(getDrawableResourceByName("music_on"));
        }else {
            musicMode.setBackground(getDrawableResourceByName("music_off"));
        }


        return rootView;

    }

    @Override
    public void onClick(View v) {
        if (MenuActivity.getMusicMode().equals(MenuActivity.MUSIC_ON)){
            musicMode.setBackground(getDrawableResourceByName("music_off"));
            MenuActivity.setCurrentMusicMode(MenuActivity.MUSIC_OFF);
        }else {
            musicMode.setBackground(getDrawableResourceByName("music_on"));
            MenuActivity.setCurrentMusicMode(MenuActivity.MUSIC_ON);

        }

    }


    private Drawable getDrawableResourceByName(String aString) {
        String packageName = getActivity().getPackageName();
        int resId = getResources().getIdentifier(aString, "drawable", packageName);

        return getActivity().getDrawable(resId);
    }
}

