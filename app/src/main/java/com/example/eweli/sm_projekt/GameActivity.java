package com.example.eweli.sm_projekt;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ButtonBarLayout;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.Toast;

import com.example.eweli.sm_projekt.database.DatabaseCrud;

import java.io.FileReader;
import java.util.List;
import java.util.Random;

import static com.example.eweli.sm_projekt.R.id.answerLabel;
import static com.example.eweli.sm_projekt.R.id.parent;

public class GameActivity extends AppCompatActivity implements View.OnClickListener { //implements SensorEventListener,{

    public static int categoriesCounter = Category.getCount();
    public final String[] CATEGORIES = Category.getCategories();
    private String currentCategory;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity
    private List<Word> currentCategoryWords;

    private LetterTextView wordText;
    private LetterTextView resultText;
    private LetterTextView nextWordRedirect;
    private LetterTextView newCategoryRedirect;
    private LetterTextView menuRedirect;
    private FrameLayout alphabetFrame;
    private FrameLayout summaryFrame;
    private FrameLayout hangmanImage;
    private GridLayout alphabetGrid;
    private AlertDialog.Builder catDialog;
    private AlertDialog alert;
    private Random rand = new Random();
    private int baseFontColor;

    private DatabaseCrud database;
    private String currentWord;
    private int errorsCounter;
    private int gameMode;

    private boolean showCategoryChooser = true;


    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    public GameActivity game;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        gameMode = intent.getIntExtra("mode", 1);
        Log.d("MODE", String.valueOf(gameMode));

        prepareContent();

        if (gameMode == 1) {
            prepareSensors();

            if (showCategoryChooser) {
                showCategoryChooser();
            }
        } else {
            showDuelStartDialog();
        }

    }

    private void showDuelStartDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(getString(R.string.gameModeSelect));
       // builder.setMessage("Player 1");
        View rootView = View.inflate(this, R.layout.duel_input_name, null);
        // Set up the input
        final EditText input = (EditText) rootView.findViewById(R.id.firstPlayerName);
        final Button saveBtn = (Button) rootView.findViewById(R.id.saveButton);
        builder.setView(rootView);
        builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alert = builder.create();

        alert.show();
        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //alert.show();

    }

    @Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        if (gameMode == 1)
            mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        if (gameMode == 1)

            // Add the following line to unregister the Sensor Manager onPause
            mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }


    private void resetGameScreen() {
        summaryFrame.setVisibility(View.INVISIBLE);
        alphabetFrame.setVisibility(View.VISIBLE);
        int counter = alphabetGrid.getChildCount();
        for (int i = 0; i < counter; i++) {
            LetterTextView tmpLetter = (LetterTextView) alphabetGrid.getChildAt(i);
            tmpLetter.setTextColor(baseFontColor);
        }
        errorsCounter = 0;
        hangmanImage.setBackground(null);

    }

    private void prepareContent() {
        game = this;
        database = new DatabaseCrud(getApplicationContext());
        baseFontColor = Color.rgb(10, 10, 10);

        wordText = (LetterTextView) findViewById(R.id.answerLabel);
        resultText = (LetterTextView) findViewById(R.id.resultText);
        nextWordRedirect = (LetterTextView) findViewById(R.id.nextWordRedirect);
        newCategoryRedirect = (LetterTextView) findViewById(R.id.newCategoryRedirect);
        menuRedirect = (LetterTextView) findViewById(R.id.menuRedirect);

        hangmanImage = (FrameLayout) findViewById(R.id.hangmanImage);
        alphabetFrame = (FrameLayout) findViewById(R.id.alphabetFrame);
        summaryFrame = (FrameLayout) findViewById(R.id.summaryFrame);
        alphabetGrid = (GridLayout) findViewById(R.id.alphabetGrid);

        nextWordRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGameScreen();
                prepareNewWord();

            }
        });

        newCategoryRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideFrames();
                resetGameScreen();
                showCategoryChooser = true;
                showCategoryChooser();
            }
        });

        menuRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menu = new Intent(game, MenuActivity.class);
                startActivity(menu);
            }
        });

        catDialog = new AlertDialog.Builder(this);

        errorsCounter = 0;

    }

    private void startNewLevel(){
        alphabetFrame.setVisibility(View.VISIBLE);
        wordText.setVisibility(View.VISIBLE);
        hangmanImage.setVisibility(View.VISIBLE);
    }

    private void hideFrames(){
        alphabetFrame.setVisibility(View.INVISIBLE);
        wordText.setVisibility(View.INVISIBLE);
        hangmanImage.setVisibility(View.INVISIBLE);
        summaryFrame.setVisibility(View.INVISIBLE);
    }

    private void prepareNewWord() {

        if (currentCategoryWords.isEmpty()) {
            prepareWordsList();
            Toast.makeText(getApplicationContext(), "No more words", Toast.LENGTH_SHORT).show();
        }


        int currentWordNr = rand.nextInt(currentCategoryWords.size());
        currentWord = currentCategoryWords.get(currentWordNr).getWord().toUpperCase();
        String startText = "";

        for (int i = 0; i < currentWord.length(); i++) {
            startText += "_";
        }
        wordText.setText(startText);
        Log.d("CURRENT WORD - ", currentWord);
        currentCategoryWords.remove(currentWord);

    }


    private void prepareSensors() {
// ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
                /*
                 * The following method, "handleShakeEvent(count):" is a stub //
				 * method you would use to setup whatever you want done once the
				 * device has been shook.
				 */
                if (showCategoryChooser) {
                    showCategoryChooser = false;
                    int catNr = rand.nextInt(categoriesCounter);
                    currentCategory = CATEGORIES[catNr];
                    alert.hide();
                    catDialog.setMessage(getString(R.string.newCategory) + ":\n" + getStringResourceByName(currentCategory));
                    catDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

//                        showCategoryChooser = false;
                            prepareWordsList();
                            prepareNewWord();
                            startNewLevel();
                        }
                    });
                    alert = catDialog.create();
                    alert.show();
                }
            }
        });

    }


    private void showCategoryChooser() {
        catDialog = new AlertDialog.Builder(this);

        catDialog.setCancelable(false); // This blocks the 'BACK' button
        catDialog.setMessage(R.string.categoryChooseMsg);

        alert = catDialog.create();
        alert.show();
    }


    private void prepareWordsList() {
//        gameLayout.setVisibility(View.VISIBLE);
        database.open();
        if (currentCategory.equals(Category.ALL.name())) {
            currentCategoryWords = database.getAllWords();
        } else {
            currentCategoryWords = database.getWordsByCategory(currentCategory);

        }
        database.close();
    }


    @Override
    public void onClick(View v) {

        String letter = String.valueOf(v.getTag());
        Log.d("ID", letter);
        LetterTextView letterTextView = (LetterTextView) v;
        boolean found = false;
        for (int i = 0; i < currentWord.length(); i++) {
            if ((String.valueOf(currentWord.charAt(i))).equals(letter)) {
                letterTextView.setTextColor(Color.GREEN);
                StringBuilder updatedText = new StringBuilder(wordText.getText());
                updatedText.setCharAt(i, letter.charAt(0));
                wordText.setText(updatedText);
                found = true;
            }

        }
        if (!found) {
            changeHangman();
            letterTextView.setTextColor(Color.RED);


        } else {
            if (!wordText.getText().toString().contains("_")) {
                alphabetFrame.setVisibility(View.INVISIBLE);
                summaryFrame.setVisibility(View.VISIBLE);
                resultText.setText(R.string.winner);
                resultText.setTextColor(Color.GREEN);
            }
        }

    }

    private void changeHangman() {
        errorsCounter++;
        hangmanImage.setBackground(getDrawableResourceByName("hangman" + errorsCounter));

        if (errorsCounter >= 7) {
            wordText.setText(currentWord);
            alphabetFrame.setVisibility(View.INVISIBLE);
            summaryFrame.setVisibility(View.VISIBLE);
            resultText.setText(R.string.looser);
            resultText.setTextColor(Color.RED);
        }
    }

//
//    @Override
//    public void onSensorChanged(SensorEvent event) {
//        if (showCategoryChooser) {
//            float x = event.values[0];
//            float y = event.values[1];
//            float z = event.values[2];
//            mAccelLast = mAccelCurrent;
//            mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
//            float delta = mAccelCurrent - mAccelLast;
//            mAccel = mAccel * 0.9f + delta; // perform low-cut filter
//
//            if (mAccel > 12) {
//                showCategoryChooser = false;
//                int catNr = rand.nextInt(categoriesCounter);
//                currentCategory = CATEGORIES[catNr];
//                alert.hide();
//                catDialog.setMessage(getString(R.string.newCategory) + ":\n" + getStringResourceByName(currentCategory));
//                catDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
////                        showCategoryChooser = false;
//                        prepareWordsList();
//                        prepareNewWord();
//                    }
//                });
//                alert = catDialog.create();
//                alert.show();
//
//
//            }
//
//        }
//
//    }
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//
//    }


    private String getStringResourceByName(String aString) {
        String packageName = getPackageName();
        int resId = getResources().getIdentifier(aString, "string", packageName);

        return getString(resId, null);
    }

    private Drawable getDrawableResourceByName(String aString) {
        String packageName = getPackageName();
        int resId = getResources().getIdentifier(aString, "drawable", packageName);

        return getDrawable(resId);
    }
}
