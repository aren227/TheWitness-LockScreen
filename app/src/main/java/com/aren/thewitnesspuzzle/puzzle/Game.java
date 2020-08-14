package com.aren.thewitnesspuzzle.puzzle;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.view.WindowManager;

import com.aren.thewitnesspuzzle.PuzzleGLSurfaceView;
import com.aren.thewitnesspuzzle.puzzle.sound.Sounds;

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
        puzzle.touchEvent(x, y, action);
        update();
    }

    public PuzzleGLSurfaceView getSurfaceView(){
        return surfaceView;
    }

    public void update(){
        surfaceView.requestRender();
    }

    public void close(){
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.removeView(surfaceView);
    }

    public void setPuzzle(Puzzle puzzle){
        this.puzzle = puzzle;

        //update();
    }

    public Puzzle getPuzzle(){
        return puzzle;
    }

    public int getBackgroundColor(){
        if(puzzle != null) return puzzle.getBackgroundColor();
        return Color.BLACK;
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
