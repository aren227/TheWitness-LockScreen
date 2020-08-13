package com.aren.thewitnesspuzzle.puzzle.animation;

import com.aren.thewitnesspuzzle.puzzle.Puzzle;

public class WaitAnimation extends Animation {

    private Puzzle puzzle;

    public WaitAnimation(Puzzle puzzle, Runnable runnable){
        super(1000, 1, true);
        this.puzzle = puzzle;
        whenDone(runnable);
    }

    @Override
    protected void update(float rate) {
        puzzle.cursorColor().setAnimationValue(this, CursorFailedAnimation.COLOR);
    }
}
