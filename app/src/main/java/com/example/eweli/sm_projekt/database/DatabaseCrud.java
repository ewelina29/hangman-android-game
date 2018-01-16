package com.example.eweli.sm_projekt.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.example.eweli.sm_projekt.Word;

import java.util.ArrayList;
import java.util.List;

public class DatabaseCrud {
    public static final String LOGTAG = "WORDS_DATABASE";

    SQLiteOpenHelper dbHandler;
    SQLiteDatabase database;
    Context context;

    private static final String[] allColumns = {
            AppDatabase.COLUMN_ID,
            AppDatabase.COLUMN_WORD,
            AppDatabase.COLUMN_CATEGORY,

    };

    public DatabaseCrud(Context context) {
        dbHandler = new AppDatabase(context);
        this.context = context;
    }

    public void open() {
        Log.i(LOGTAG, "Database opened");
        database = dbHandler.getWritableDatabase();
    }

    public void close() {
        Log.i(LOGTAG, "Database Closed");
        dbHandler.close();

    }

    public void addWord(Word word) {
        ContentValues values = new ContentValues();


        values.put(AppDatabase.COLUMN_WORD, word.getWord());
        values.put(AppDatabase.COLUMN_CATEGORY, word.getCategory());
        long insertID = database.insert(AppDatabase.TABLE_WORDS, null, values);
        word.setId(insertID);

    }

    public Word getWord(long id) {

        Cursor cursor = database.query(AppDatabase.TABLE_WORDS, allColumns, AppDatabase.COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Word word = new Word();
        word.setId(cursor.getLong(cursor.getColumnIndex(AppDatabase.COLUMN_ID)));
        word.setWord(cursor.getString(cursor.getColumnIndex(AppDatabase.COLUMN_WORD)));
        word.setCategory(cursor.getString(cursor.getColumnIndex(AppDatabase.COLUMN_CATEGORY)));

        return word;
    }

    public List<Word> getWordsByCategory(String cat) {

        Cursor cursor = database.query(AppDatabase.TABLE_WORDS, allColumns, AppDatabase.COLUMN_CATEGORY + "=?", new String[]{cat}, null, null, null, null);

        List<Word> products = new ArrayList<>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Word word = new Word();

                word.setId(cursor.getLong(cursor.getColumnIndex(AppDatabase.COLUMN_ID)));
                word.setWord(cursor.getString(cursor.getColumnIndex(AppDatabase.COLUMN_WORD)));
                word.setCategory(cursor.getString(cursor.getColumnIndex(AppDatabase.COLUMN_CATEGORY)));

                products.add(word);
            }
        }
        return products;
    }


    public List<Word> getAllWords() {

        Cursor cursor = database.query(AppDatabase.TABLE_WORDS, allColumns, null, null, null, null, null);

        List<Word> products = new ArrayList<>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Word word = new Word();

                word.setId(cursor.getLong(cursor.getColumnIndex(AppDatabase.COLUMN_ID)));
                word.setWord(cursor.getString(cursor.getColumnIndex(AppDatabase.COLUMN_WORD)));
                word.setCategory(cursor.getString(cursor.getColumnIndex(AppDatabase.COLUMN_CATEGORY)));

                products.add(word);
            }
        }
        return products;
    }

    public int updateWord(Word word) {

        ContentValues values = new ContentValues();

        values.put(AppDatabase.COLUMN_WORD, word.getWord());
        values.put(AppDatabase.COLUMN_CATEGORY, word.getCategory());


        // updating row
        return database.update(AppDatabase.TABLE_WORDS, values,
                AppDatabase.COLUMN_ID + "=?",new String[] { String.valueOf(word.getId())});
    }

    public void deleteWord(Word word) {

        database.delete(AppDatabase.TABLE_WORDS, AppDatabase.COLUMN_ID + "=" + word.getId(), null);
    }

    public void clearTable(){
        database.delete(AppDatabase.TABLE_WORDS, null, null);
    }

    public boolean isEmpty(){
        Cursor cursor = database.query(AppDatabase.TABLE_WORDS, allColumns, null, null, null, null, null);

        return cursor.getCount() <= 0;
    }


}
