package com.aren.thewitnesspuzzle.puzzle.animation;

import com.aren.thewitnesspuzzle.core.color.PuzzleColorPalette;
import com.aren.thewitnesspuzzle.render.PuzzleRenderer;

public class WaitForEliminationAnimation extends Animation {

    private PuzzleRenderer puzzleRenderer;

    public WaitForEliminationAnimation(PuzzleRenderer puzzleRenderer, Runnable runnable) {
        super(1000, 1, true);
        this.puzzleRenderer = puzzleRenderer;
        whenDone(runnable);
    }

    @Override
    protected void update(float rate) {
        PuzzleColorPalette colorPalette = puzzleRenderer.getPuzzleBase().getColorPalette();
        puzzleRenderer.getCursorColor().setAnimationValue(this, colorPalette.getCursorFailedColor());
    }
}
