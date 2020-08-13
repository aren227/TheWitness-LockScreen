package com.aren.thewitnesspuzzle.puzzle.animation;

import com.aren.thewitnesspuzzle.math.MathUtils;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;

public class CursorFailedAnimation extends Animation {

    public static final int COLOR = android.graphics.Color.parseColor("#050a0f");

    private Puzzle puzzle;
    private int originalColor;

    public CursorFailedAnimation(final Puzzle puzzle){
        super(5000, 1);
        this.puzzle = puzzle;
        originalColor = puzzle.getCursorColor();

        whenDone(new Runnable() {
            @Override
            public void run() {
                puzzle.clearCursor();
            }
        });
    }

    @Override
    protected void update(float rate) {
        int rr = android.graphics.Color.red(COLOR);
        int gg = android.graphics.Color.green(COLOR);
        int bb = android.graphics.Color.blue(COLOR);
        int r = android.graphics.Color.red(puzzle.getPathColor());
        int g = android.graphics.Color.green(puzzle.getPathColor());
        int b = android.graphics.Color.blue(puzzle.getPathColor());
        int c = android.graphics.Color.rgb(
                (int) MathUtils.lerp(rr, r, rate),
                (int)MathUtils.lerp(gg, g, rate),
                (int)MathUtils.lerp(bb, b, rate));
        puzzle.setCursorColor(c);
    }

    @Override
    protected void done() {
        super.done();
        puzzle.setCursorColor(originalColor);
    }
}
