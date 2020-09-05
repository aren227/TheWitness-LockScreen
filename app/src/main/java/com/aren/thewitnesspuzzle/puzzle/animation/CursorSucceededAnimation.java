package com.aren.thewitnesspuzzle.puzzle.animation;

import com.aren.thewitnesspuzzle.math.MathUtils;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;

public class CursorSucceededAnimation extends Animation {

    private Puzzle puzzle;

    public CursorSucceededAnimation(Puzzle puzzle) {
        super(500, 1, true);
        this.puzzle = puzzle;
    }

    @Override
    protected void update(float rate) {
        int rr = android.graphics.Color.red(puzzle.getColorPalette().actualCursorColor.getOriginalValue());
        int gg = android.graphics.Color.green(puzzle.getColorPalette().actualCursorColor.getOriginalValue());
        int bb = android.graphics.Color.blue(puzzle.getColorPalette().actualCursorColor.getOriginalValue());
        int r = android.graphics.Color.red(puzzle.getColorPalette().getCursorSucceededColor());
        int g = android.graphics.Color.green(puzzle.getColorPalette().getCursorSucceededColor());
        int b = android.graphics.Color.blue(puzzle.getColorPalette().getCursorSucceededColor());
        int c = android.graphics.Color.rgb(
                (int) MathUtils.lerp(rr, r, rate),
                (int) MathUtils.lerp(gg, g, rate),
                (int) MathUtils.lerp(bb, b, rate));
        puzzle.getColorPalette().actualCursorColor.setAnimationValue(this, c);
    }
}
