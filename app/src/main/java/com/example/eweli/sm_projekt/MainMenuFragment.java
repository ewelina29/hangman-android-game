package com.example.eweli.sm_projekt;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleCursorTreeAdapter;

import static com.example.eweli.sm_projekt.MenuActivity.PREFERENCES_NAME;

/**
 * Created by eweli on 30.12.2017.
 */

public class MainMenuFragment extends Fragment implements View.OnClickListener {


    private Button startGameBtn;
    private Button settingsBtn;
    private Button aboutBtn;
    private Button exitBtn;

    private FragmentManager manager;


    public static Fragment newInstance() {
        return new MainMenuFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_menu, parent, false);

        startGameBtn = (Button) rootView.findViewById(R.id.startGame);
        settingsBtn = (Button) rootView.findViewById(R.id.settings);
        aboutBtn = (Button) rootView.findViewById(R.id.about);
        exitBtn = (Button) rootView.findViewById(R.id.exit);

        startGameBtn.setOnClickListener(this);
        settingsBtn.setOnClickListener(this);
        aboutBtn.setOnClickListener(this);
        exitBtn.setOnClickListener(this);

        manager = getActivity().getSupportFragmentManager();

        return rootView;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.startGame:


               /* SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                //String currentLevel = getResources().getString(R.string.currentLevel);
                String currentLevel = sharedPref.getString(getString(R.string.currentLevel), getString(R.string.kindergarten));
                */

                String[] gameModes = {getString(R.string.relaxMode), getString(R.string.timeMode)};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.gameModeSelect));
                builder.setItems(gameModes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        Intent game = new Intent(getActivity(), GameActivity.class);
                        game.putExtra("mode", item);
                        startActivity(game);
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();




                break;
            case R.id.settings:

                manager.beginTransaction()
                        .hide(manager.findFragmentByTag("MainMenuFragment"))
                        .add(R.id.activity_main_fragment_container, SettingsFragment.newInstance(), "SettingsFragment")
                        .addToBackStack("SettingsFragment")
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();

                break;

            case R.id.about:

                manager.beginTransaction()
                        .hide(manager.findFragmentByTag("MainMenuFragment"))
                        .add(R.id.activity_main_fragment_container, AboutFragment.newInstance(), "AboutFragment")
                        .addToBackStack("AboutFragment")
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
                break;

            case R.id.exit:
                getActivity().finishAffinity();

        }
    }
}
