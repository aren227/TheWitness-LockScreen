package com.aren.thewitnesspuzzle.puzzle.color;

import com.aren.thewitnesspuzzle.puzzle.animation.value.Value;

public class PuzzleColorPalette {

    private int background;
    private int path;
    private int cursor;
    private int cursorSucceeded;
    private int cursorFailed;

    public final Value<Integer> actualCursorColor;

    public PuzzleColorPalette(int background, int path, int cursor){
        this.background = background;
        this.path = path;
        this.cursor = cursor;
        this.cursorSucceeded = this.cursor;
        this.cursorFailed = android.graphics.Color.parseColor("#050a0f");

        actualCursorColor = new Value<>(this.cursor);
    }

    public PuzzleColorPalette(int background, int path, int cursor, int cursorSucceeded){
        this.background = background;
        this.path = path;
        this.cursor = cursor;
        this.cursorSucceeded = cursorSucceeded;
        this.cursorFailed = android.graphics.Color.parseColor("#050a0f");

        actualCursorColor = new Value<>(this.cursor);
    }

    public PuzzleColorPalette(int background, int path, int cursor, int cursorSucceeded, int cursorFailed){
        this.background = background;
        this.path = path;
        this.cursor = cursor;
        this.cursorSucceeded = cursorSucceeded;
        this.cursorFailed = cursorFailed;

        actualCursorColor = new Value<>(this.cursor);
    }

    public int getBackgroundColor(){
        return background;
    }

    public int getPathColor(){
        return path;
    }

    public int getCursorColor(){
        return actualCursorColor.get();
    }

    public int getCursorSucceededColor(){
        return cursorSucceeded;
    }

    public int getCursorFailedColor(){
        return cursorFailed;
    }

}
