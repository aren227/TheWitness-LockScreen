package com.aren.thewitnesspuzzle.puzzle.animation;

import com.aren.thewitnesspuzzle.render.PuzzleRenderer;

public class PuzzleFadeInAnimation extends Animation {

    private PuzzleRenderer puzzleRenderer;

    public PuzzleFadeInAnimation(PuzzleRenderer puzzleRenderer, long duration){
        super(duration, 1, true);
        this.puzzleRenderer = puzzleRenderer;
    }

    @Override
    protected void update(float rate) {
        float s = rate * (2 - rate);
        puzzleRenderer.getFadeIntensity().setAnimationValue(this, s);
    }
}
