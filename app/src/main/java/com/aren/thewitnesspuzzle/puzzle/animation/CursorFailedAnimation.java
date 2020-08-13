package com.aren.thewitnesspuzzle.puzzle.animation;

import com.aren.thewitnesspuzzle.math.MathUtils;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;

public class CursorFailedAnimation extends Animation {

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
        int r = android.graphics.Color.red(puzzle.getPathColor());
        int g = android.graphics.Color.green(puzzle.getPathColor());
        int b = android.graphics.Color.blue(puzzle.getPathColor());
        int c = android.graphics.Color.rgb(
                (int) MathUtils.lerp(5, r, rate),
                (int)MathUtils.lerp(10, g, rate),
                (int)MathUtils.lerp(15, b, rate));
        puzzle.setCursorColor(c);
    }

    @Override
    protected void done() {
        super.done();
        puzzle.setCursorColor(originalColor);
    }
}
