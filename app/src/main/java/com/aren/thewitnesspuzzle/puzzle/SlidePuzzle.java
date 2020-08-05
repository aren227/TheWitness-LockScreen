package com.aren.thewitnesspuzzle.puzzle;

import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;

import com.aren.thewitnesspuzzle.graphics.Circle;
import com.aren.thewitnesspuzzle.graphics.Rectangle;
import com.aren.thewitnesspuzzle.graphics.Shape;
import com.aren.thewitnesspuzzle.math.Vector3;
import com.aren.thewitnesspuzzle.puzzle.rules.Rule;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

public class SlidePuzzle implements Puzzle {

    Game game;

    ArrayList<Shape> staticShapes = new ArrayList<>();
    ArrayList<Shape> dynamicShapes = new ArrayList<>();

    int backgroundColor;
    int pathColor;
    int cursorColor;

    boolean touching = false;
    Vector3 cursorPosition;

    Vector3 startPosition = new Vector3(0.5f, 0, 0);

    public SlidePuzzle(Game game){
        this.game = game;

        ColorFactory.setRandomColor(this);

        calcStaticShapes();
    }

    //고정된 퍼즐을 미리 그려놓자
    public void calcStaticShapes(){
        staticShapes.add(new Rectangle(new Vector3(0.5f, 0.5f, 0), 0.07f, 1f, getPathColor()));
        staticShapes.add(new Circle(new Vector3(0.5f, 0, 0), 0.07f * 1.5f, getPathColor()));
        staticShapes.add(new Circle(new Vector3(0.5f, 1, 0), 0.07f * 0.5f, getPathColor()));
    }

    //커서 등은 따로 그리자
    public void calcDynamicShapes(){
        dynamicShapes.clear();

        if(touching){
            dynamicShapes.add(new Circle(new Vector3(0.5f, 0, 0), 0.07f * 1.5f, getCursorColor()));
            dynamicShapes.add(new Rectangle(new Vector3(0.5f, cursorPosition.y / 2, 0), 0.07f, cursorPosition.y, getCursorColor()));
            dynamicShapes.add(new Circle(cursorPosition, 0.07f * 0.5f, getCursorColor()));
        }
    }

    public boolean touchEvent(float x, float y, int action){
        if(action == MotionEvent.ACTION_DOWN){
            if(Math.sqrt((x - startPosition.x) * (x - startPosition.x) + (y - startPosition.y) * (y - startPosition.y)) <= 0.07f * 1.5f){
                touching = true;
                cursorPosition = calculateCursorPosition(x, y);
            }
        }
        else if(action == MotionEvent.ACTION_MOVE){
            if(touching){
                cursorPosition = calculateCursorPosition(x, y);
            }
        }
        else if(action == MotionEvent.ACTION_UP){
            if(touching){
                touching = false;
                if(cursorPosition.y >= 1 - 0.01){
                    return true;
                }
            }
        }
        return false;
    }

    //calc cursor position from touch position
    private Vector3 calculateCursorPosition(float x, float y){
        return new Vector3(0.5f, Math.max(Math.min(y, 1), 0), 0);
    }

    @Override
    public int getVertexCount(){
        int vertexCount = 0;
        for(Shape shape : staticShapes){
            vertexCount += shape.getVertexCount();
        }
        for(Shape shape : dynamicShapes){
            vertexCount += shape.getVertexCount();
        }
        return vertexCount;
    }

    @Override
    public FloatBuffer getVertexBuffer(){
        ByteBuffer bb = ByteBuffer.allocateDirect(getVertexCount() * 3 * 4);
        bb.order(ByteOrder.nativeOrder());

        FloatBuffer vertexBuffer = bb.asFloatBuffer();

        for(Shape shape : staticShapes){
            shape.draw(vertexBuffer);
        }
        for(Shape shape : dynamicShapes){
            shape.draw(vertexBuffer);
        }

        vertexBuffer.position(0);

        return vertexBuffer;
    }

    public FloatBuffer getVertexColorBuffer(){
        ByteBuffer bb = ByteBuffer.allocateDirect(getVertexCount() * 3 * 4);
        bb.order(ByteOrder.nativeOrder());

        FloatBuffer vertexBuffer = bb.asFloatBuffer();

        //TODO: ConcurrentModificationException
        for(Shape shape : staticShapes){
            shape.drawColor(vertexBuffer);
        }
        for(Shape shape : dynamicShapes){
            shape.drawColor(vertexBuffer);
        }

        vertexBuffer.position(0);

        return vertexBuffer;
    }

    public void setBackgroundColor(int color){
        backgroundColor = color;
    }

    @Override
    public int getBackgroundColor(){
        return backgroundColor;
    }

    public void setPathColor(int color){
        pathColor = color;
    }

    public int getPathColor(){
        return pathColor;
    }

    public void setCursorColor(int color){
        cursorColor = color;
    }

    public int getCursorColor(){
        return cursorColor;
    }

    public float getSceneWidth(){
        return 1;
    }

    @Override
    public float getPathWidth() {
        return 0.07f;
    }

    @Override
    public boolean validate(ArrayList<Vector3> path) {
        return true;
    }

    @Override
    public Rule[][] getTileRules() {
        return new Rule[0][];
    }

    @Override
    public Rule[][] getHLineRules() {
        return new Rule[0][];
    }

    @Override
    public Rule[][] getVLineRules() {
        return new Rule[0][];
    }

    @Override
    public Rule[][] getCornerRules() {
        return new Rule[0][];
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }
}
