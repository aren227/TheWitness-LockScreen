package com.aren.thewitnesspuzzle.puzzle.factory;

import android.graphics.drawable.Drawable;

import com.aren.thewitnesspuzzle.R;

public enum Difficulty {

    ALWAYS_SOLVABLE, VERY_EASY, EASY, NORMAL, HARD, VERY_HARD;

    public int getColor(){
        if(this == ALWAYS_SOLVABLE) return android.graphics.Color.parseColor("#fafafa");
        else if(this == VERY_EASY) return android.graphics.Color.parseColor("#bdfdff");
        else if(this == EASY) return android.graphics.Color.parseColor("#c0ffbd");
        else if(this == NORMAL) return android.graphics.Color.parseColor("#fffdbd");
        else if(this == HARD) return android.graphics.Color.parseColor("#ffe1bd");
        else return android.graphics.Color.parseColor("#ffc9bd");
    }

}
