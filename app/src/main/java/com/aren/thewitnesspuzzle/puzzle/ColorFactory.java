package com.aren.thewitnesspuzzle.puzzle;

import android.graphics.Color;

public class ColorFactory {

    public static void setRandomColor(Puzzle puzzle){
        puzzle.setBackgroundColor(android.graphics.Color.parseColor("#ffbb02"));
        puzzle.setPathColor(android.graphics.Color.parseColor("#4b3906"));
        puzzle.setCursorColor(android.graphics.Color.parseColor("#fefffe"));
    }

}
