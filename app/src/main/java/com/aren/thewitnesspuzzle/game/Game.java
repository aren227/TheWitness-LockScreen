package com.aren.thewitnesspuzzle.game;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.view.MotionEvent;

import com.aren.thewitnesspuzzle.core.math.BoundingBox;
import com.aren.thewitnesspuzzle.puzzle.sound.Sounds;
import com.aren.thewitnesspuzzle.render.PuzzleRenderer;
import com.aren.thewitnesspuzzle.view.PuzzleGLSurfaceView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class Game {

    private Context context;

    private GameSettings settings;

    private PuzzleGLSurfaceView surfaceView;

    private PuzzleRenderer puzzle;

    private HashMap<Integer, MediaPlayer> mediaPlayers = new HashMap<>();

    private Runnable onSolved;
    private Runnable onPreTouched;
    private Runnable onClicked;

    public enum Mode {PLAY, GALLERY, EDITOR}

    private Mode mode;

    private boolean disableTimer;
    private long endTime;
    private Timer timer;
    private Runnable onTLE;
    public boolean isTLE;

    public Game(Context context, Mode mode) {
        this.context = context;
        this.mode = mode;
        settings = new GameSettings(context);
        surfaceView = new PuzzleGLSurfaceView(this, context);

        /*if(settings.getSoundsEnabled()){
            prepareSounds();
        }*/
    }

    public GameSettings getSettings() {
        return settings;
    }

    public void touchEvent(float x, float y, int action) {
        if (onPreTouched != null) onPreTouched.run();
        if (action == MotionEvent.ACTION_DOWN && onClicked != null) onClicked.run();
        puzzle.touchEvent(x, y, action);
        update();
    }

    public PuzzleGLSurfaceView getSurfaceView() {
        return surfaceView;
    }

    public void update() {
        surfaceView.requestRender();
    }

    public void solved() {
        if (onSolved != null) {
            onSolved.run();
        }
    }

    public void setOnSolved(Runnable runnable) {
        onSolved = runnable;
    }

    public void setOnPreTouched(Runnable runnable) {
        onPreTouched = runnable;
    }

    public void setOnClicked(Runnable runnable){
        onClicked = runnable;
    }

    public void setPuzzle(PuzzleRenderer puzzle) {
        this.puzzle = puzzle;

        //update();
    }

    public PuzzleRenderer getPuzzle() {
        return puzzle;
    }

    public int getBackgroundColor() {
        if (puzzle != null) return puzzle.getPuzzleBase().getColorPalette().getBackgroundColor();
        return Color.BLACK;
    }

    /*public void prepareSounds(){
        for(Sounds sound : Sounds.values()){
            MediaPlayer mp = MediaPlayer.create(context, sound.getId());
            mediaPlayers.put(sound.getId(), mp);
        }
    }*/

    public void playExternalSound(String path){
        if (!settings.getSoundsEnabled()) return;

        if(mediaPlayers.containsKey(-1)){
            mediaPlayers.get(-1).release();
        }
        MediaPlayer mp = new MediaPlayer();
        try {
            mp.setDataSource(path);
            mp.prepare();
            mp.start();

            mediaPlayers.put(-1, mp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopExternalSound(){
        if(mediaPlayers.containsKey(-1)){
            mediaPlayers.get(-1).release();
        }
    }

    public void playSound(Sounds sound) {
        if (!settings.getSoundsEnabled()) return;

        if (!mediaPlayers.containsKey(sound.getId())) {
            MediaPlayer mp = MediaPlayer.create(context, sound.getId());
            mediaPlayers.put(sound.getId(), mp);
        }

        MediaPlayer mp = mediaPlayers.get(sound.getId());
        try {
            mp.seekTo(0);
            mp.start();
        } catch (Exception e) {
            mp = MediaPlayer.create(context, sound.getId());
            mediaPlayers.put(sound.getId(), mp);
            mp.start();
        }
    }

    public boolean isPlayMode() {
        return mode == Mode.PLAY;
    }

    public boolean isEditorMode() {
        return mode == Mode.EDITOR;
    }

    public boolean isGalleryMode() {
        return mode == Mode.GALLERY;
    }

    public float getDPScale(float dp) {
        BoundingBox bb = surfaceView.glRenderer.getFrustumBoundingBox(puzzle);
        float px = surfaceView.getResources().getDisplayMetrics().density * dp;
        return px / surfaceView.getWidth() * bb.getWidth();
    }

    public void checkTime(){
        if(disableTimer){
            timer.cancel();
            return;
        }

        if(System.currentTimeMillis() / 1000 >= endTime){
            puzzle.setUntouchable(true);
            stopExternalSound();
            timer.cancel();
            onTLE.run();
            isTLE = true;
        }
    }

    public void setTimerMode(long time, Runnable onTLE){
        if(timer != null){
            timer.cancel();
        }

        endTime = System.currentTimeMillis() / 1000 + time;
        this.onTLE = onTLE;
        isTLE = false;
        disableTimer = false;

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                checkTime();
            }
        };
        timer = new Timer();
        timer.schedule(task, 0, 500);
    }

    public void stopTimerMode(){
        disableTimer = true;
    }

    public void close(){
        for(MediaPlayer player : mediaPlayers.values()){
            player.release();
        }
        if(timer != null){
            timer.cancel();
        }
    }
}
