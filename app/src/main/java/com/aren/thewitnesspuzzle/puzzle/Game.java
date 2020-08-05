package com.aren.thewitnesspuzzle.puzzle;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.aren.thewitnesspuzzle.PuzzleGLSurfaceView;
import com.aren.thewitnesspuzzle.graphics.Circle;
import com.aren.thewitnesspuzzle.graphics.Shape;
import com.aren.thewitnesspuzzle.math.Vector3;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class Game {

    private Context context;

    private PuzzleGLSurfaceView surfaceView;

    private Puzzle puzzle;

    private HashMap<Integer, MediaPlayer> mediaPlayers = new HashMap<>();

    public Game(Context context){
        this.context = context;
        surfaceView = new PuzzleGLSurfaceView(this, context);
    }

    public void touchEvent(float x, float y, int action){
        if(puzzle.touchEvent(x, y, action)){
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            windowManager.removeView(surfaceView);
        }
        update();
    }

    public PuzzleGLSurfaceView getSurfaceView(){
        return surfaceView;
    }

    public void update(){
        puzzle.calcDynamicShapes();

        surfaceView.requestRender();
    }

    public void setPuzzle(Puzzle puzzle){
        this.puzzle = puzzle;

        update();
    }

    public Puzzle getPuzzle(){
        return puzzle;
    }

    public int getBackgroundColor(){
        if(puzzle != null) return puzzle.getBackgroundColor();
        return Color.BLACK;
    }

    public float getSceneWidth(){
        if(puzzle != null) return puzzle.getSceneWidth();
        return 1;
    }

    public float getPaddingWidth(){
        return getSceneWidth() * 0.2f;
    }

    public void playSound(Sounds sound){
        if(!mediaPlayers.containsKey(sound.getId())){
            MediaPlayer mp = MediaPlayer.create(context, sound.getId());
            mediaPlayers.put(sound.getId(), mp);
        }

        MediaPlayer mp = mediaPlayers.get(sound.getId());
        mp.seekTo(0);
        mp.start();
    }

}
