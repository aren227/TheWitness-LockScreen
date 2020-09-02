package com.aren.thewitnesspuzzle.game;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.WindowManager;

import com.aren.thewitnesspuzzle.PuzzleGLSurfaceView;
import com.aren.thewitnesspuzzle.math.BoundingBox;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.sound.Sounds;

import java.util.HashMap;

public class Game {

    private Context context;

    private GameSettings settings;

    private PuzzleGLSurfaceView surfaceView;

    private Puzzle puzzle;

    private HashMap<Integer, MediaPlayer> mediaPlayers = new HashMap<>();

    private Runnable onSolved;
    private Runnable onPreTouched;

    public enum Mode {PLAY, GALLERY, EDITOR}
    private Mode mode;

    public Game(Context context, Mode mode){
        this.context = context;
        this.mode = mode;
        settings = new GameSettings(context);
        surfaceView = new PuzzleGLSurfaceView(this, context);

        /*if(settings.getSoundsEnabled()){
            prepareSounds();
        }*/
    }

    public GameSettings getSettings(){
        return settings;
    }

    public void touchEvent(float x, float y, int action){
        if(onPreTouched != null) onPreTouched.run();
        puzzle.touchEvent(x, y, action);
        update();
    }

    public PuzzleGLSurfaceView getSurfaceView(){
        return surfaceView;
    }

    public void update(){
        surfaceView.requestRender();
    }

    public void solved(){
        if(onSolved != null){
            onSolved.run();
        }
    }

    public void setOnSolved(Runnable runnable){
        onSolved = runnable;
    }

    public void setOnPreTouched(Runnable runnable){
        onPreTouched = runnable;
    }

    public void setPuzzle(Puzzle puzzle){
        this.puzzle = puzzle;

        //update();
    }

    public Puzzle getPuzzle(){
        return puzzle;
    }

    public int getBackgroundColor(){
        if(puzzle != null) return puzzle.getColorPalette().getBackgroundColor();
        return Color.BLACK;
    }

    /*public void prepareSounds(){
        for(Sounds sound : Sounds.values()){
            MediaPlayer mp = MediaPlayer.create(context, sound.getId());
            mediaPlayers.put(sound.getId(), mp);
        }
    }*/

    public void playSound(Sounds sound){
        if(!settings.getSoundsEnabled()) return;

        if(!mediaPlayers.containsKey(sound.getId())){
            MediaPlayer mp = MediaPlayer.create(context, sound.getId());
            mediaPlayers.put(sound.getId(), mp);
        }

        MediaPlayer mp = mediaPlayers.get(sound.getId());
        mp.seekTo(0);
        mp.start();
    }

    public boolean isPlayMode(){
        return mode == Mode.PLAY;
    }

    public boolean isEditorMode(){
        return mode == Mode.EDITOR;
    }

    public boolean isGalleryMode(){
        return mode == Mode.GALLERY;
    }

    public float getDPScale(float dp){
        BoundingBox bb = surfaceView.glRenderer.getFrustumBoundingBox(puzzle);
        float px = surfaceView.getResources().getDisplayMetrics().density * dp;
        return px / surfaceView.getWidth() * bb.getWidth();
    }

}
