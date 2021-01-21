package com.aren.thewitnesspuzzle.puzzle.animation;

import com.aren.thewitnesspuzzle.core.color.PuzzleColorPalette;
import com.aren.thewitnesspuzzle.core.math.MathUtils;
import com.aren.thewitnesspuzzle.render.PuzzleRenderer;

public class CursorEndingPointReachedAnimation extends Animation {

    private PuzzleRenderer puzzleRenderer;

    public CursorEndingPointReachedAnimation(PuzzleRenderer puzzleRenderer) {
        super(500, 10000, false);
        this.puzzleRenderer = puzzleRenderer;
    }

    @Override
    protected void update(float rate) {
        PuzzleColorPalette colorPalette = puzzleRenderer.getPuzzleBase().getColorPalette();

        float s = (float) (-Math.cos(rate * Math.PI * 2) + 1) / 2f;
        int rr = android.graphics.Color.red(colorPalette.getCursorColor());
        int gg = android.graphics.Color.green(colorPalette.getCursorColor());
        int bb = android.graphics.Color.blue(colorPalette.getCursorColor());
        int c = android.graphics.Color.rgb(
                (int) MathUtils.lerp(rr, 255, s),
                (int) MathUtils.lerp(gg, 255, s),
                (int) MathUtils.lerp(bb, 255, s));
        puzzleRenderer.getCursorColor().setAnimationValue(this, c);
    }
}
