package com.aren.thewitnesspuzzle.puzzle;

import android.graphics.Color;

import com.aren.thewitnesspuzzle.math.Vector3;
import com.aren.thewitnesspuzzle.puzzle.rules.Rule;

import java.nio.FloatBuffer;
import java.util.ArrayList;

public interface Puzzle {

    int getVertexCount();
    FloatBuffer getVertexBuffer();
    FloatBuffer getVertexColorBuffer();

    void calcStaticShapes();
    void calcDynamicShapes();

    boolean touchEvent(float x, float y, int action);

    void setBackgroundColor(int color);
    int getBackgroundColor();
    void setPathColor(int color);
    int getPathColor();
    void setCursorColor(int color);
    int getCursorColor();

    float getSceneWidth();
    float getPathWidth();

    boolean validate(ArrayList<Vector3> path);

    Rule[][] getTileRules();
    Rule[][] getHLineRules();
    Rule[][] getVLineRules();
    Rule[][] getCornerRules();

    int getWidth();
    int getHeight();

}
