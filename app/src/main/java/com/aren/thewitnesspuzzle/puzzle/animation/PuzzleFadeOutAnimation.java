package com.aren.thewitnesspuzzle.puzzle.animation;

import com.aren.thewitnesspuzzle.puzzle.Puzzle;

public class PuzzleFadeOutAnimation extends Animation {

    private Puzzle puzzle;

    public PuzzleFadeOutAnimation(Puzzle puzzle, long duration){
        super(duration, 1, true);
        this.puzzle = puzzle;
    }

    @Override
    protected void update(float rate) {
        float s = -rate * rate + 1;
        puzzle.getFadeIntensity().setAnimationValue(this, s);
    }

}
