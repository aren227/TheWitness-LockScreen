package com.aren.thewitnesspuzzle.puzzle.animation;

import com.aren.thewitnesspuzzle.core.color.PuzzleColorPalette;
import com.aren.thewitnesspuzzle.core.math.MathUtils;
import com.aren.thewitnesspuzzle.render.PuzzleRenderer;

public class CursorSucceededAnimation extends Animation {

    private PuzzleRenderer puzzleRenderer;

    public CursorSucceededAnimation(PuzzleRenderer puzzleRenderer) {
        super(500, 1, true);
        this.puzzleRenderer = puzzleRenderer;
    }

    @Override
    protected void update(float rate) {
        PuzzleColorPalette colorPalette = puzzleRenderer.getPuzzleBase().getColorPalette();
        int rr = android.graphics.Color.red(colorPalette.getCursorColor());
        int gg = android.graphics.Color.green(colorPalette.getCursorColor());
        int bb = android.graphics.Color.blue(colorPalette.getCursorColor());
        int r = android.graphics.Color.red(colorPalette.getCursorSucceededColor());
        int g = android.graphics.Color.green(colorPalette.getCursorSucceededColor());
        int b = android.graphics.Color.blue(colorPalette.getCursorSucceededColor());
        int c = android.graphics.Color.rgb(
                (int) MathUtils.lerp(rr, r, rate),
                (int) MathUtils.lerp(gg, g, rate),
                (int) MathUtils.lerp(bb, b, rate));
        puzzleRenderer.getCursorColor().setAnimationValue(this, c);
    }
}
