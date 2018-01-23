package com.example.eweli.sm_projekt;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.text.TextUtilsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.Toast;

import com.example.eweli.sm_projekt.database.DatabaseCrud;

import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

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
    private LetterTextView duetResultText;
    private LetterTextView nextWordRedirect;
    private LetterTextView duetNextWordRedirect;
    private LetterTextView newCategoryRedirect;
    private LetterTextView menuRedirect;
    private LetterTextView duetMenuRedirect;

    private LetterTextView infoLabel;
    private LetterTextView pointsLabel;

    private FrameLayout alphabetFrame;
    private FrameLayout summaryFrame;
    private FrameLayout hangmanImage;
    private FrameLayout infoFrame;
    private FrameLayout pointsFrame;
    private FrameLayout duetSummaryFrame;


    private GridLayout alphabetGrid;
    private AlertDialog catDialog;
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

    private MediaPlayer mediaPlayer;

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

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.music);

        if (MenuActivity.getMusicMode().equals(MenuActivity.MUSIC_ON)) {
            mediaPlayer.start();
        }
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
        duetMenuRedirect = (LetterTextView) findViewById(R.id.duetMenuRedirect);
        infoLabel = (LetterTextView) findViewById(R.id.infoLabel);
        pointsLabel = (LetterTextView) findViewById(R.id.pointsLabel);
        duetResultText = (LetterTextView) findViewById(R.id.duetResultText);

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
                changeFrames();
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

        duetMenuRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menu = new Intent(game, MenuActivity.class);
                startActivity(menu);
            }
        });

        duetNextWordRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetGameScreen();
                showWordInput();
            }
        });

        errorsCounter = 0;
        allWordsCounter = 0;
        correctWordsCounter = 0;
        firstPlayerPoints = 0;
        secondPlayerPoints = 0;
        currentPlayer = 1;

    }


    private void showDuelStartDialog() {
        final AlertDialog builder = new AlertDialog.Builder(this).create();
        builder.setCancelable(false);
        View rootView = View.inflate(this, R.layout.duel_input_name, null);

        final EditText firstPlayerName = (EditText) rootView.findViewById(R.id.firstPlayerName);
        final EditText secondPlayerName = (EditText) rootView.findViewById(R.id.secondPlayerName);
        final EditText targetWords = (EditText) rootView.findViewById(R.id.targetRoundsNumber);
        final LetterTextView saveBtn = (LetterTextView) rootView.findViewById(R.id.saveBtn);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = true;


                if (TextUtils.isEmpty(firstPlayerName.getText().toString())) {
                    firstPlayerName.setError(getString(R.string.Field_cannot_be_empty));
//                    firstPlayerName.requestFocus();
                    valid = false;
                }
                if (TextUtils.isEmpty(secondPlayerName.getText().toString())) {
                    secondPlayerName.setError(getString(R.string.Field_cannot_be_empty));
                    valid = false;
                }

                if (TextUtils.isEmpty(targetWords.getText().toString())) {
                    targetWords.setError(getString(R.string.Field_cannot_be_empty));
                    valid = false;

                }
                if (valid) {
                    builder.dismiss();
                    firstPlayer = firstPlayerName.getText().toString().toUpperCase();
                    secondPlayer = secondPlayerName.getText().toString().toUpperCase();
                    targetWordsNumber = Integer.parseInt(targetWords.getText().toString()) * 2;
                    showWordInput();
                }

            }
        });

        builder.setView(rootView);


        builder.show();
        builder.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


    }

    private void showWordInput() {
        targetWordsNumber--;
        if (targetWordsNumber >= 0) {

            duetSummaryFrame.setVisibility(View.INVISIBLE);
            wordText.setVisibility(View.INVISIBLE);


            final AlertDialog builder = new AlertDialog.Builder(this).create();
            builder.setCancelable(false);
            View rootView = View.inflate(this, R.layout.new_word_input, null);

            final EditText newWord = (EditText) rootView.findViewById(R.id.newWord);
            final LetterTextView message = (LetterTextView) rootView.findViewById(R.id.newWordDialogMessage);
            final LetterTextView saveBtn = (LetterTextView) rootView.findViewById(R.id.saveBtn);

            message.setText(getDuelMessage());
            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (TextUtils.isEmpty(newWord.getText().toString())) {
                        newWord.setError(getString(R.string.Field_cannot_be_empty));
                        newWord.requestFocus();
                    } else {
                        currentWord = newWord.getText().toString().toUpperCase();
                        startNewDuelLevel();
                        builder.dismiss();

                    }
                }
            });

            builder.setView(rootView);

            builder.show();
            builder.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        } else {

            hangmanImage.setVisibility(View.INVISIBLE);
            wordText.setVisibility(View.INVISIBLE);
            alphabetFrame.setVisibility(View.INVISIBLE);
            duetSummaryFrame.setVisibility(View.INVISIBLE);
            AlertDialog builder = new AlertDialog.Builder(this).create();
            builder.setCancelable(false);
            View rootView = View.inflate(this, R.layout.duet_results, null);

            final LetterTextView firstPlayerName = (LetterTextView) rootView.findViewById(R.id.firstPlayerName);
            final LetterTextView secondPlayerName = (LetterTextView) rootView.findViewById(R.id.secondPlayerName);
            final LetterTextView firstPlayerPts = (LetterTextView) rootView.findViewById(R.id.firstPlayerPoints);
            final LetterTextView secondPlayerPts = (LetterTextView) rootView.findViewById(R.id.secondPlayerPoints);
            final LetterTextView saveBtn = (LetterTextView) rootView.findViewById(R.id.saveBtn);

            firstPlayerName.setText(firstPlayer);
            secondPlayerName.setText(secondPlayer);
            firstPlayerPts.setText(String.valueOf(firstPlayerPoints));
            secondPlayerPts.setText(String.valueOf(secondPlayerPoints));

            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent menu = new Intent(game, MenuActivity.class);
                    startActivity(menu);
                }
            });

            builder.setView(rootView);

            builder.show();
            builder.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//            duetSummaryFrame.setVisibility(View.VISIBLE);

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
        //errorsCounter = 0;
        hangmanImage.setBackground(null);

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

    private void startNewDuelLevel() {
        alphabetFrame.setVisibility(View.VISIBLE);
        wordText.setVisibility(View.VISIBLE);
        duetSummaryFrame.setVisibility(View.INVISIBLE);
        hangmanImage.setVisibility(View.VISIBLE);
        infoFrame.setVisibility(View.VISIBLE);
        pointsFrame.setVisibility(View.VISIBLE);

        infoLabel.setText(getCurrentPlayerName());
        pointsLabel.setText(getCurrentPlayerPoints());

        hangmanImage.setBackground(null);

        String startText = "";

        for (int i = 0; i < currentWord.length(); i++) {
            startText += "_";
        }
        wordText.setText(startText);

    }

    private void changeFrames() {
        if (gameMode == 1) {

            alphabetFrame.setVisibility(View.INVISIBLE);
            wordText.setVisibility(View.INVISIBLE);
            hangmanImage.setVisibility(View.INVISIBLE);
            summaryFrame.setVisibility(View.INVISIBLE);
        } else {
            alphabetFrame.setVisibility(View.INVISIBLE);
            summaryFrame.setVisibility(View.INVISIBLE);
            duetSummaryFrame.setVisibility(View.VISIBLE);
        }

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
        currentCategoryWords.remove(currentWordNr);

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
                    catDialog.dismiss();
                    showCategoryChooser = false;
                    int catNr = rand.nextInt(categoriesCounter);
                    while (CATEGORIES[catNr] == currentCategory) {
                        catNr = rand.nextInt(categoriesCounter);
                    }
                    currentCategory = CATEGORIES[catNr];
                    showCategoryChooser = false;
                    prepareWordsList();
                    prepareNewWord();
                    startNewLevel();

//                    final AlertDialog dialog = new AlertDialog.Builder(game).create();
//                    dialog.setCancelable(false);
//                    View rootView = View.inflate(getApplicationContext(), R.layout.new_category, null);
//                    LetterTextView newCat = (LetterTextView) rootView.findViewById(R.id.newCategoryLabel);
//                    LetterTextView okBtn = (LetterTextView) rootView.findViewById(R.id.okButton);
//                    newCat.setText(getString(R.string.newCategory) + ":\n" + getStringResourceByName(currentCategory));
//                    dialog.setView(rootView);
//
//                    okBtn.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            dialog.dismiss();
//                            showCategoryChooser = false;
//                            prepareWordsList();
//                            prepareNewWord();
//                            startNewLevel();
//                        }
//                    });
////                    catDialog.setButton("OK", new DialogInterface.OnClickListener() {
////                        public void onClick(DialogInterface dialog, int which) {
////                            dialog.dismiss();
////
//////                        showCategoryChooser = false;
////                            prepareWordsList();
////                            prepareNewWord();
////                            startNewLevel();
////                        }
////                    });
//                    dialog.show();
                }
            }
        });

    }


    private void showCategoryChooser() {

        catDialog = new AlertDialog.Builder(this).create();
        catDialog.setCancelable(false);
        View rootView = View.inflate(getApplicationContext(), R.layout.category_chooser, null);

        catDialog.setView(rootView);
//        catDialog.setMessage(R.string.categoryChooseMsg);

        catDialog.show();
        catDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

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
                errorsCounter = 0;
                if (gameMode == 1) {
                    summaryFrame.setVisibility(View.VISIBLE);
                    resultText.setText(R.string.winner);
                    resultText.setTextColor(Color.GREEN);

                    correctWordsCounter++;
                    allWordsCounter++;
                    updatePoints();
                } else {
                    updateDuetPoints();
                    duetResultText.setText(R.string.winner);
                    duetResultText.setTextColor(Color.GREEN);
                    changeFrames();
                    changePlayer();
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

            errorsCounter = 0;

            if (gameMode == 1) {
                summaryFrame.setVisibility(View.VISIBLE);
                resultText.setText(R.string.looser);
                resultText.setTextColor(Color.RED);
                allWordsCounter++;
                updatePoints();
            } else {
                duetSummaryFrame.setVisibility(View.VISIBLE);
                duetResultText.setText(R.string.looser);
                duetResultText.setTextColor(Color.RED);
                changeFrames();
                changePlayer();
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
        if (gameMode == 1)
            mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);

        if (MenuActivity.getMusicMode().equals(MenuActivity.MUSIC_ON)){
            mediaPlayer.start();
        }
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume


    }

    @Override
    public void onPause() {
        if (gameMode == 1)

            // Add the following line to unregister the Sensor Manager onPause
            mSensorManager.unregisterListener(mShakeDetector);

        if (MenuActivity.getMusicMode().equals(MenuActivity.MUSIC_ON)){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        super.onPause();
    }

    public String getDuelMessage() {
        if (currentPlayer == 1) {
            return secondPlayer + " -> " + firstPlayer;
        } else return firstPlayer + " -> " + secondPlayer;
    }

    public String getCurrentPlayerName() {
        if (currentPlayer == 1)
            return firstPlayer;
        else return secondPlayer;
    }

    public String getCurrentPlayerPoints() {
        if (currentPlayer == 1)
            return String.valueOf(firstPlayerPoints);
        else return String.valueOf(secondPlayerPoints);
    }
}

