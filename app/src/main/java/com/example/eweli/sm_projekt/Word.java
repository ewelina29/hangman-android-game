package com.example.eweli.sm_projekt;

import com.google.gson.annotations.SerializedName;

/**
 * Created by eweli on 02.01.2018.
 */

public class Word {

    private long id;

    @SerializedName("WORD")
    private String word;

    @SerializedName("CATEGORY")
    private String category;

    public Word(){

    }

    public Word(String word,  String category) {
        this.word = word;
        this.category = category;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
