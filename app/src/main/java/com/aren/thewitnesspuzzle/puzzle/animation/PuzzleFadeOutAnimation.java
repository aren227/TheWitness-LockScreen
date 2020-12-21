package com.aren.thewitnesspuzzle.puzzle.animation;

import com.aren.thewitnesspuzzle.render.PuzzleRenderer;

public class PuzzleFadeOutAnimation extends Animation {

    private PuzzleRenderer puzzleRenderer;

    public PuzzleFadeOutAnimation(PuzzleRenderer puzzleRenderer, long duration){
        super(duration, 1, true);
        this.puzzleRenderer = puzzleRenderer;
    }

    @Override
    protected void update(float rate) {
        float s = -rate * rate + 1;
        puzzleRenderer.getFadeIntensity().setAnimationValue(this, s);
    }

}
