package com.example.eweli.sm_projekt;

import android.app.Dialog;
import android.app.Fragment;
import android.app.PendingIntent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.eweli.sm_projekt.database.DatabaseCrud;

import java.io.FileReader;
import java.util.List;
import java.util.Random;

import static com.example.eweli.sm_projekt.R.id.answerLabel;
import static com.example.eweli.sm_projekt.R.id.firstPlayerName;
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
    private LetterTextView duetNextWordRedirect;
    private LetterTextView newCategoryRedirect;
    private LetterTextView menuRedirect;
    private LetterTextView infoLabel;
    private LetterTextView pointsLabel;

    private FrameLayout alphabetFrame;
    private FrameLayout summaryFrame;
    private FrameLayout hangmanImage;
    private FrameLayout infoFrame;
    private FrameLayout pointsFrame;
    private FrameLayout duetSummaryFrame;


    private GridLayout alphabetGrid;
    private AlertDialog.Builder catDialog;
    private AlertDialog alert;
    private Random rand = new Random();
    private DatabaseCrud database;

    private int errorsCounter;
    private int gameMode;
    private int baseFontColor;
    private int allWordsCounter;
    private int correctWordsCounter;
    private int firstPlayerPoints;
    private int secondPlayerPoints;
    private int currentPlayer;
    private int targetWordsNumber;

    private String currentWord;
    private String firstPlayer;
    private String secondPlayer;


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
        View rootView = View.inflate(this, R.layout.duel_input_name, null);

        final EditText firstPlayerName = (EditText) rootView.findViewById(R.id.firstPlayerName);
        final EditText secondPlayerName = (EditText) rootView.findViewById(R.id.secondPlayerName);
        final EditText targetWords = (EditText) rootView.findViewById(R.id.targetRoundsNumber);

        builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                firstPlayer = firstPlayerName.getText().toString();
                secondPlayer = secondPlayerName.getText().toString();
                targetWordsNumber = Integer.parseInt(targetWords.getText().toString()) * 2;

                showWordInput();
            }
        });
        builder.setView(rootView);

        AlertDialog alert = builder.create();

        alert.show();
        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


    }

    private void showWordInput() {
        targetWordsNumber--;
        if (targetWordsNumber > 0) {


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View rootView = View.inflate(this, R.layout.new_word_input, null);

            final EditText newWord = (EditText) rootView.findViewById(R.id.newWord);
            final LetterTextView message = (LetterTextView) rootView.findViewById(R.id.newWordDialogMessage);
            message.setText(getDuelMessage());
            builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    currentWord = newWord.getText().toString();
                    Log.d("CURRENT", currentWord);
                    startNewDuelLevel();

                }
            });
            builder.setView(rootView);

            AlertDialog alert = builder.create();

            alert.show();
            alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);



        } else {
            summaryFrame.setVisibility(View.VISIBLE);
        }
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
        duetNextWordRedirect = (LetterTextView) findViewById(R.id.duelNextWordRedirect);
        newCategoryRedirect = (LetterTextView) findViewById(R.id.newCategoryRedirect);
        menuRedirect = (LetterTextView) findViewById(R.id.menuRedirect);
        infoLabel = (LetterTextView) findViewById(R.id.infoLabel);
        pointsLabel = (LetterTextView) findViewById(R.id.pointsLabel);

        hangmanImage = (FrameLayout) findViewById(R.id.hangmanImage);
        alphabetFrame = (FrameLayout) findViewById(R.id.alphabetFrame);
        summaryFrame = (FrameLayout) findViewById(R.id.summaryFrame);
        infoFrame = (FrameLayout) findViewById(R.id.infoFrame);
        pointsFrame = (FrameLayout) findViewById(R.id.pointsFrame);
        duetSummaryFrame = (FrameLayout) findViewById(R.id.duetSummaryFrame);

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

        duetNextWordRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWordInput();
            }
        });

        catDialog = new AlertDialog.Builder(this);

        errorsCounter = 0;
        allWordsCounter = 0;
        correctWordsCounter = 0;
        firstPlayerPoints = 0;
        secondPlayerPoints = 0;
        currentPlayer = 1;

    }

    private void changePlayer() {
        if (currentPlayer == 1) {
            currentPlayer = 2;
        } else currentPlayer = 1;
    }

    private void startNewLevel() {
        alphabetFrame.setVisibility(View.VISIBLE);
        wordText.setVisibility(View.VISIBLE);
        hangmanImage.setVisibility(View.VISIBLE);
        infoFrame.setVisibility(View.VISIBLE);
        pointsFrame.setVisibility(View.VISIBLE);
        infoLabel.setText(getStringResourceByName(currentCategory));
        updatePoints();


    }

    private void startNewDuelLevel(){
        alphabetFrame.setVisibility(View.VISIBLE);
        wordText.setVisibility(View.VISIBLE);
        duetSummaryFrame.setVisibility(View.INVISIBLE);
        hangmanImage.setVisibility(View.VISIBLE);
        infoFrame.setVisibility(View.VISIBLE);
        pointsFrame.setVisibility(View.VISIBLE);
        infoLabel.setText(getCurrentPlayerName());

        hangmanImage.setBackground(null);

        String startText = "";

        for (int i = 0; i < currentWord.length(); i++) {
            startText += "_";
        }
        wordText.setText(startText);

//        infoLabel.setText(getStringResourceByName(currentCategory));
    }

    private void hideFrames() {
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
                if (gameMode == 1) {
                    correctWordsCounter++;
                    allWordsCounter++;
                    updatePoints();
                } else {
                    updateDuetPoints();
                    hideFrames();
                    changePlayer();
                    duetSummaryFrame.setVisibility(View.VISIBLE);

//                    showWordInput();

                }
            }
        }

    }

    private void updateDuetPoints() {
        if (currentPlayer == 1) {
            firstPlayerPoints++;
        } else secondPlayerPoints++;
    }

    private void updatePoints() {
        pointsLabel.setText(correctWordsCounter + " / " + allWordsCounter);
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

            if (gameMode == 1) {

                allWordsCounter++;
                updatePoints();
            } else {
                hideFrames();
                changePlayer();
                duetSummaryFrame.setVisibility(View.VISIBLE);
            }

        }
    }



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

    public String getDuelMessage() {
        if (currentPlayer == 1) {
            return firstPlayer + " -> " + secondPlayer;
        } else return secondPlayer + " -> " + firstPlayer;
    }

    public String getCurrentPlayerName() {
        if (currentPlayer == 1)
            return firstPlayer;
        else return secondPlayer;
    }
}

