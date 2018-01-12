package com.example.eweli.sm_projekt;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class LetterTextView extends android.support.v7.widget.AppCompatTextView{
    public LetterTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public LetterTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LetterTextView(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Noteworthy-Lt.ttf");
        setTypeface(tf ,1);

    }
}
