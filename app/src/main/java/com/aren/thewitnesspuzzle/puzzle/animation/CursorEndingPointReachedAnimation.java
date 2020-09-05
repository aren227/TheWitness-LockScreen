package com.aren.thewitnesspuzzle.puzzle.animation;

import com.aren.thewitnesspuzzle.math.MathUtils;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;

public class CursorEndingPointReachedAnimation extends Animation {

    private Puzzle puzzle;

    public CursorEndingPointReachedAnimation(Puzzle puzzle) {
        super(500, 10000, false);
        this.puzzle = puzzle;
    }

    @Override
    protected void update(float rate) {
        float s = (float) (-Math.cos(rate * Math.PI * 2) + 1) / 2f;
        int rr = android.graphics.Color.red(puzzle.getColorPalette().actualCursorColor.getOriginalValue());
        int gg = android.graphics.Color.green(puzzle.getColorPalette().actualCursorColor.getOriginalValue());
        int bb = android.graphics.Color.blue(puzzle.getColorPalette().actualCursorColor.getOriginalValue());
        int c = android.graphics.Color.rgb(
                (int) MathUtils.lerp(rr, 255, s),
                (int) MathUtils.lerp(gg, 255, s),
                (int) MathUtils.lerp(bb, 255, s));
        puzzle.getColorPalette().actualCursorColor.setAnimationValue(this, c);
    }
}
