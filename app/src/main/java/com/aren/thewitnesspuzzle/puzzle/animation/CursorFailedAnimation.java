package com.aren.thewitnesspuzzle.puzzle.animation;

import com.aren.thewitnesspuzzle.math.MathUtils;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;

public class CursorFailedAnimation extends Animation {

    private Puzzle puzzle;

    public CursorFailedAnimation(final Puzzle puzzle) {
        super(5000, 1, false);
        this.puzzle = puzzle;

        whenDone(new Runnable() {
            @Override
            public void run() {
                puzzle.clearCursor();
            }
        });
    }

    @Override
    protected void update(float rate) {
        int rr = android.graphics.Color.red(puzzle.getColorPalette().getCursorFailedColor());
        int gg = android.graphics.Color.green(puzzle.getColorPalette().getCursorFailedColor());
        int bb = android.graphics.Color.blue(puzzle.getColorPalette().getCursorFailedColor());
        int r = android.graphics.Color.red(puzzle.getColorPalette().getPathColor());
        int g = android.graphics.Color.green(puzzle.getColorPalette().getPathColor());
        int b = android.graphics.Color.blue(puzzle.getColorPalette().getPathColor());
        int c = android.graphics.Color.rgb(
                (int) MathUtils.lerp(rr, r, rate),
                (int) MathUtils.lerp(gg, g, rate),
                (int) MathUtils.lerp(bb, b, rate));
        puzzle.getColorPalette().actualCursorColor.setAnimationValue(this, c);
    }
}
