package com.aren.thewitnesspuzzle.puzzle.animation;

import com.aren.thewitnesspuzzle.puzzle.Puzzle;

public class WaitAnimation extends Animation {

    private Puzzle puzzle;
    private int originalColor;

    public WaitAnimation(Puzzle puzzle, Runnable runnable){
        super(1000, 1);
        this.puzzle = puzzle;
        originalColor = puzzle.getCursorColor();
        whenDone(runnable);
    }

    @Override
    protected void update(float rate) {
        puzzle.setCursorColor(CursorFailedAnimation.COLOR);
    }

    @Override
    protected void done(){
        super.done();
        puzzle.setCursorColor(originalColor);
    }
}
