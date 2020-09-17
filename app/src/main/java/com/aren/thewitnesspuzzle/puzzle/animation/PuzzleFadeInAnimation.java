package com.aren.thewitnesspuzzle.puzzle.animation;

import com.aren.thewitnesspuzzle.puzzle.Puzzle;

public class PuzzleFadeInAnimation extends Animation {

    private Puzzle puzzle;

    public PuzzleFadeInAnimation(Puzzle puzzle, long duration){
        super(duration, 1, true);
        this.puzzle = puzzle;
    }

    @Override
    protected void update(float rate) {
        float s = rate * (2 - rate);
        puzzle.getFadeIntensity().setAnimationValue(this, s);
    }
}
