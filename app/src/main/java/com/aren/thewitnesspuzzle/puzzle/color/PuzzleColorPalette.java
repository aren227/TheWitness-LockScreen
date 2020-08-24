package com.aren.thewitnesspuzzle.puzzle.color;

import com.aren.thewitnesspuzzle.puzzle.animation.value.Value;

public class PuzzleColorPalette {

    private int background;
    private int path;
    private int cursor;
    private int cursorSucceeded;
    private int cursorFailed;

    private float bloomIntensity;

    public final Value<Integer> actualCursorColor;

    public PuzzleColorPalette(int background, int path, int cursor){
        this(background, path, cursor, cursor);
    }

    public PuzzleColorPalette(int background, int path, int cursor, int cursorSucceeded){
        this(background, path, cursor, cursorSucceeded, ColorUtils.RGB("#050a0f"));
    }

    public PuzzleColorPalette(int background, int path, int cursor, int cursorSucceeded, int cursorFailed){
        this(background, path, cursor, cursorSucceeded, cursorFailed, 1f);
    }

    public PuzzleColorPalette(int background, int path, int cursor, int cursorSucceeded, int cursorFailed, float bloomIntensity){
        this.background = background;
        this.path = path;
        this.cursor = cursor;
        this.cursorSucceeded = cursorSucceeded;
        this.cursorFailed = cursorFailed;
        this.bloomIntensity = bloomIntensity;

        actualCursorColor = new Value<>(this.cursor);
    }

    @Override
    public PuzzleColorPalette clone(){
        return new PuzzleColorPalette(background, path, cursor, cursorSucceeded, cursorFailed, bloomIntensity);
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

    public float getBloomIntensity(){
        return bloomIntensity;
    }

}
