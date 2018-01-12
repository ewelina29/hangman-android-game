package com.example.eweli.sm_projekt;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.android.volley.Request;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.android.volley.toolbox.StringRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestHandle;

//import com.example.eweli.sm_projekt.database.DatabaseCrud;

import org.json.JSONObject;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import static com.example.eweli.sm_projekt.MenuActivity.PREFERENCES_NAME;

/**
 * Created by eweli on 01.01.2018.
 */

public class GameActivity extends AppCompatActivity implements /*SensorEventListener,*/ View.OnClickListener {

    public static int categoriesCounter = Category.getCount();
    public final String[] CATEGORIES = Category.getCategories();
    private String currentCategory;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity
    private List<Question> currentCategoryQuestions;
    private TextView currentCorrectAnswer;

    private TextView question;
    private TextView answer1;
    private TextView answer2;
    private TextView answer3;
    private TextView answer4;
    private TextView timer;
    private String currentLevel;
    private AsyncHttpClient client;

    private GridLayout gameLayout;
    AlertDialog.Builder catDialog;
    AlertDialog alert;
    private Random rand = new Random();
    private String SESSION_TOKEN;

//    DatabaseCrud database;

    private boolean showCategoryChooser = true;

    private static final long START_TIME_IN_MILLIS = 10000;

    private CountDownTimer mCountDownTimer;

    private boolean mTimerRunning;

    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_game);


        prepareSensors();
        prepareContent();
//        requestForSessionToken();

        //if (showCategoryChooser) {
          //  showCategoryChooser();
        //}

    }

//    private void requestForSessionToken() {
//        String url = "http://opentdb.com/api_category.php";
////        String url = "http://httpbin.org/get?param1=hello";
////        JsonObjectRequest jsObjRequest = new JsonObjectRequest
//
////
//        RequestHandle requestHandle = client.get(url,
//                new JsonHttpResponseHandler() {
//
//                    @Override
//                    public void onSuccess(JSONObject jsonObject) {
//                       // SESSION_TOKEN = jsonObject.optString("token");
//                        Toast.makeText(getApplicationContext(), jsonObject.toString(), Toast.LENGTH_SHORT).show();
//                        Log.d("JSON", jsonObject.toString());
//
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Throwable throwable, JSONObject error) {
//                        Toast.makeText(getApplicationContext(), "wE CANNOT CONNECT TO THE API",Toast.LENGTH_SHORT).show();
//                        Log.e("Error: ", statusCode + " " + throwable.getMessage());
//                    }
//                });
//
//
////        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
////                new Response.Listener<JSONObject>()
////                {
////                    @Override
////                    public void onResponse(JSONObject response) {
////                        // display response
//////                        String token = response.optString("token");
////                        Log.d("resssssss", response.toString());
//////                        Log.d("Response", token);
////                        Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
////
////                    }
////                },
////                new Response.ErrorListener()
////                {
////                    @Override
////                    public void onErrorResponse(VolleyError error) {
////                        Log.d("Error.Response", error.toString());
////                    }
////                }
////    );

// Request a string response from the provided URL.
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.d("RESPONSE", response);
//                        SESSION_TOKEN = response;
//                        Toast.makeText(getApplicationContext(), SESSION_TOKEN, Toast.LENGTH_SHORT).show();
//                        // Display the first 500 characters of the response string.
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                                        Toast.makeText(getApplicationContext(), "wE CANNOT CONNECT TO THE API",Toast.LENGTH_SHORT).show();
//
//            }
//        });

//    }

    private void prepareQuestions() {

        if (currentCategoryQuestions.isEmpty()){
            Toast.makeText(getApplicationContext(), "No more questions", Toast.LENGTH_SHORT).show();
        }
        else {

            int currentQuestionNr = rand.nextInt(currentCategoryQuestions.size());
            Question currentQuestion = currentCategoryQuestions.get(currentQuestionNr);

            question.setText(getStringResourceByName(currentQuestion.getQuestion()));
            int correctAnswerPlace = rand.nextInt(4) + 1;
            if (correctAnswerPlace == 1) {
                answer1.setText(getStringResourceByName(currentQuestion.getCorrectAnswer()));
                currentCorrectAnswer = answer1;
                answer2.setText(getStringResourceByName(currentQuestion.getIncorrectAnswer1()));
                answer3.setText(getStringResourceByName(currentQuestion.getIncorrectAnswer2()));
                answer4.setText(getStringResourceByName(currentQuestion.getIncorrectAnswer3()));
            } else if (correctAnswerPlace == 2) {
                answer2.setText(getStringResourceByName(currentQuestion.getCorrectAnswer()));
                currentCorrectAnswer = answer2;
                answer1.setText(getStringResourceByName(currentQuestion.getIncorrectAnswer1()));
                answer3.setText(getStringResourceByName(currentQuestion.getIncorrectAnswer2()));
                answer4.setText(getStringResourceByName(currentQuestion.getIncorrectAnswer3()));
            } else if (correctAnswerPlace == 3) {
                answer3.setText(getStringResourceByName(currentQuestion.getCorrectAnswer()));
                currentCorrectAnswer = answer3;
                answer1.setText(getStringResourceByName(currentQuestion.getIncorrectAnswer1()));
                answer2.setText(getStringResourceByName(currentQuestion.getIncorrectAnswer2()));
                answer4.setText(getStringResourceByName(currentQuestion.getIncorrectAnswer3()));
            } else {
                answer4.setText(getStringResourceByName(currentQuestion.getCorrectAnswer()));
                currentCorrectAnswer = answer4;
                answer1.setText(getStringResourceByName(currentQuestion.getIncorrectAnswer1()));
                answer2.setText(getStringResourceByName(currentQuestion.getIncorrectAnswer2()));
                answer3.setText(getStringResourceByName(currentQuestion.getIncorrectAnswer3()));
            }

            currentCategoryQuestions.remove(currentQuestion);
            startTimer();

        }




    }


    private void prepareContent() {
//        question = (TextView) findViewById(R.id.question);
//        answer1 = (TextView) findViewById(R.id.answer1);
//        answer2 = (TextView) findViewById(R.id.answer2);
//        answer3 = (TextView) findViewById(R.id.answer3);
//        answer4 = (TextView) findViewById(R.id.answer4);
//        gameLayout = (GridLayout) findViewById(R.id.game_layout);
//        timer = (TextView) findViewById(R.id.timer);
//
//        answer1.setOnClickListener(this);
//        answer2.setOnClickListener(this);
//        answer3.setOnClickListener(this);
//        answer4.setOnClickListener(this);

        currentLevel = MenuActivity.getCurrentLevel();
        client = new AsyncHttpClient();

        catDialog = new AlertDialog.Builder(this);

    }

    private void prepareSensors() {
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        sensorManager.registerListener(this,
//                accelerometer,
//                SensorManager.SENSOR_DELAY_GAME);
    }


    private void showCategoryChooser() {
        catDialog.setCancelable(false); // This blocks the 'BACK' button
        catDialog.setMessage("Shake the device to choose category");

        alert = catDialog.create();
        alert.show();
    }

    /*
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (showCategoryChooser) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta; // perform low-cut filter

            if (mAccel > 2) {
                int catNr = rand.nextInt(categoriesCounter);
                currentCategory = CATEGORIES[catNr];
                alert.hide();
                catDialog.setMessage("New category: \n" + currentCategory);
                catDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        showCategoryChooser = false;
                        prepareCategoryQuestionsList();

                        //prepareQuestions();
                    }
                });
                alert = catDialog.create();
                alert.show();


            }

        }

    }*/


    private void prepareCategoryQuestionsList() {
        gameLayout.setVisibility(View.VISIBLE);
//        database.open();
//        currentCategoryQuestions = database.getQuestionsByCategory(currentCategory);
//        database.close();
    }

    /*@Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(this);
        super.onPause();
    }
*/

    private String getStringResourceByName(String aString) {
        String packageName = getPackageName();
        int resId = getResources().getIdentifier(aString, "string", packageName);
        return getString(resId, null);
    }

    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
            }
        }.start();

        mTimerRunning = true;
    }

    private void updateCountDownText() {
//        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        int hundredthsOfSecond = (int) (mTimeLeftInMillis / 10) % 100;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", seconds, hundredthsOfSecond);

        timer.setText(timeLeftFormatted);
    }

    @Override
    public void onClick(View v) {

        String letterId = String.valueOf(v.getTag());
        Log.d("ID", letterId);
        switch (v.getId()){
//            case R.id.answer1:
//                Log.d("CLICK", "ANSWER 1");
//                if (answer1 == currentCorrectAnswer){
//                    Toast.makeText(getApplicationContext(), "Correct Answer", Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    Toast.makeText(getApplicationContext(), "Incorrect Answer", Toast.LENGTH_SHORT).show();
//
//                }
//                prepareQuestions();
//
//                break;
//            case R.id.answer2:
//                Log.d("CLICK", "ANSWER 2");
//                if (answer2 == currentCorrectAnswer){
//                    Toast.makeText(getApplicationContext(), "Correct Answer", Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    Toast.makeText(getApplicationContext(), "Incorrect Answer", Toast.LENGTH_SHORT).show();
//
//                }
//                prepareQuestions();
//                break;
//            case R.id.answer3:
//                Log.d("CLICK", "ANSWER 3");
//                if (answer3 == currentCorrectAnswer){
//                    Toast.makeText(getApplicationContext(), "Correct Answer", Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    Toast.makeText(getApplicationContext(), "Incorrect Answer", Toast.LENGTH_SHORT).show();
//
//                }
//
//                prepareQuestions();
//                break;
//            case R.id.answer4:
//                Log.d("CLICK", "ANSWER 4");
//                if (answer4 == currentCorrectAnswer){
//                    Toast.makeText(getApplicationContext(), "Correct Answer", Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    Toast.makeText(getApplicationContext(), "Incorrect Answer", Toast.LENGTH_SHORT).show();
//
//                }
//
//                prepareQuestions();
//                break;
        }
    }
}
