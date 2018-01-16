package com.example.eweli.sm_projekt.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class AppDatabase extends SQLiteOpenHelper {

    private static final String DB_NAME = "words.db";
    private static final int DB_VERSION = 1;

    public static final String TABLE_WORDS = "WORDS";
    public static final String COLUMN_ID = "_ID";
    public static final String COLUMN_WORD = "WORD";
    public static final String COLUMN_CATEGORY = "CATEGORY";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_WORDS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_WORD + " TEXT, " +
                    COLUMN_CATEGORY + " TEXT " +
                    ")";


    public AppDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.delete(TABLE_WORDS, null, null);
        db.execSQL(TABLE_CREATE);


    }
}
