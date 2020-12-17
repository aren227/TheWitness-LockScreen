package com.aren.thewitnesspuzzle.puzzle.color;

import com.aren.thewitnesspuzzle.puzzle.animation.value.Value;

import org.json.JSONException;
import org.json.JSONObject;

public class PuzzleColorPalette {

    private int background;
    private int path;
    private int cursor;
    private int cursorSucceeded;
    private int cursorFailed;

    private float bloomIntensity;

    public PuzzleColorPalette(int background, int path, int cursor) {
        this(background, path, cursor, cursor);
    }

    public PuzzleColorPalette(int background, int path, int cursor, int cursorSucceeded) {
        this(background, path, cursor, cursorSucceeded, ColorUtils.RGB("#050a0f"));
    }

    public PuzzleColorPalette(int background, int path, int cursor, int cursorSucceeded, int cursorFailed) {
        this(background, path, cursor, cursorSucceeded, cursorFailed, 1f);
    }

    public PuzzleColorPalette(int background, int path, int cursor, int cursorSucceeded, int cursorFailed, float bloomIntensity) {
        this.background = background;
        this.path = path;
        this.cursor = cursor;
        this.cursorSucceeded = cursorSucceeded;
        this.cursorFailed = cursorFailed;
        this.bloomIntensity = bloomIntensity;
    }

    @Override
    public PuzzleColorPalette clone() {
        return new PuzzleColorPalette(background, path, cursor, cursorSucceeded, cursorFailed, bloomIntensity);
    }

    public int getBackgroundColor() {
        return background;
    }

    public void setBackgroundColor(int color) {
        background = color;
    }

    public int getPathColor() {
        return path;
    }

    public void setPathColor(int color) {
        path = color;
    }

    public int getCursorColor() {
        return cursor;
    }

    public void setCursorColor(int color) {
        cursor = color;
    }

    public int getCursorSucceededColor() {
        return cursorSucceeded;
    }

    public void setCursorSucceededColor(int color) {
        cursorSucceeded = color;
    }

    public int getCursorFailedColor() {
        return cursorFailed;
    }

    public void setCursorFailedColor(int color) {
        cursorFailed = color;
    }

    public float getBloomIntensity() {
        return bloomIntensity;
    }

    public void setBloomIntensity(float intensity) {
        bloomIntensity = intensity;
    }

    public void set(PuzzleColorPalette palette) {
        background = palette.getBackgroundColor();
        path = palette.getPathColor();
        cursor = palette.getCursorColor();
        cursorSucceeded = palette.getCursorSucceededColor();
        cursorFailed = palette.getCursorFailedColor();
    }

    public JSONObject serialize() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("background", getBackgroundColor());
        jsonObject.put("path", getPathColor());
        jsonObject.put("line", getCursorColor());
        jsonObject.put("success", getCursorSucceededColor());
        jsonObject.put("failure", getCursorFailedColor());
        jsonObject.put("bloom", getBloomIntensity());
        return jsonObject;
    }

    public static PuzzleColorPalette deserialize(JSONObject jsonObject) throws JSONException {
        int background = jsonObject.getInt("background");
        int path = jsonObject.getInt("path");
        int line = jsonObject.getInt("line");
        int success = jsonObject.getInt("success");
        int failure = jsonObject.getInt("failure");
        float bloom = (float) jsonObject.getDouble("bloom");
        return new PuzzleColorPalette(background, path, line, success, failure, bloom);
    }

}
