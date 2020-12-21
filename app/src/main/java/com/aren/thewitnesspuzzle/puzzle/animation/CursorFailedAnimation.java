package com.aren.thewitnesspuzzle.puzzle.animation;

import com.aren.thewitnesspuzzle.math.MathUtils;
import com.aren.thewitnesspuzzle.puzzle.base.color.PuzzleColorPalette;
import com.aren.thewitnesspuzzle.render.PuzzleRenderer;

public class CursorFailedAnimation extends Animation {

    private PuzzleRenderer puzzleRenderer;

    public CursorFailedAnimation(final PuzzleRenderer puzzleRenderer) {
        super(5000, 1, false);
        this.puzzleRenderer = puzzleRenderer;

        whenDone(new Runnable() {
            @Override
            public void run() {
                puzzleRenderer.clearCursor();
            }
        });
    }

    @Override
    protected void update(float rate) {
        PuzzleColorPalette colorPalette = puzzleRenderer.getPuzzleBase().getColorPalette();
        int rr = android.graphics.Color.red(colorPalette.getCursorFailedColor());
        int gg = android.graphics.Color.green(colorPalette.getCursorFailedColor());
        int bb = android.graphics.Color.blue(colorPalette.getCursorFailedColor());
        int r = android.graphics.Color.red(colorPalette.getPathColor());
        int g = android.graphics.Color.green(colorPalette.getPathColor());
        int b = android.graphics.Color.blue(colorPalette.getPathColor());
        int c = android.graphics.Color.rgb(
                (int) MathUtils.lerp(rr, r, rate),
                (int) MathUtils.lerp(gg, g, rate),
                (int) MathUtils.lerp(bb, b, rate));
        puzzleRenderer.getCursorColor().setAnimationValue(this, c);
    }
}
