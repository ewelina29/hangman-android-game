package com.example.eweli.sm_projekt;

import com.google.gson.annotations.SerializedName;

/**
 * Created by eweli on 02.01.2018.
 */

public class Question {

    private long id;
    @SerializedName("QUESTION")
    private String question;
    @SerializedName("CORRECT_ANSWER")
    private String correctAnswer;
    @SerializedName("INCORRECT_ANSWER_1")
    private String incorrectAnswer1;
    @SerializedName("INCORRECT_ANSWER_2")

    private String incorrectAnswer2;
    @SerializedName("INCORRECT_ANSWER_3")

    private String incorrectAnswer3;
    @SerializedName("CATEGORY")

    private String category;

    public Question(){

    }

    public Question(String question, String correctAnswer, String incorrectAnswer1, String incorrectAnswer2, String incorrectAnswer3, String category) {
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.incorrectAnswer1 = incorrectAnswer1;
        this.incorrectAnswer2 = incorrectAnswer2;
        this.incorrectAnswer3 = incorrectAnswer3;
        this.category = category;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getIncorrectAnswer1() {
        return incorrectAnswer1;
    }

    public void setIncorrectAnswer1(String incorrectAnswer1) {
        this.incorrectAnswer1 = incorrectAnswer1;
    }

    public String getIncorrectAnswer2() {
        return incorrectAnswer2;
    }

    public void setIncorrectAnswer2(String incorrectAnswer2) {
        this.incorrectAnswer2 = incorrectAnswer2;
    }

    public String getIncorrectAnswer3() {
        return incorrectAnswer3;
    }

    public void setIncorrectAnswer3(String incorrectAnswer3) {
        this.incorrectAnswer3 = incorrectAnswer3;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
