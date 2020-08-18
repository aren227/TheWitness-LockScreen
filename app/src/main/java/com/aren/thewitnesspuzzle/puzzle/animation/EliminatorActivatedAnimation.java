package com.aren.thewitnesspuzzle.puzzle.animation;

import com.aren.thewitnesspuzzle.graphics.shape.Shape;
import com.aren.thewitnesspuzzle.math.MathUtils;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.rules.Rule;

public class EliminatorActivatedAnimation extends Animation {

    private Shape shape;
    private Puzzle puzzle;

    public EliminatorActivatedAnimation(Rule rule){
        super(1000, 1, true);
        shape = rule.getShape();
        puzzle = rule.getPuzzle();
    }

    @Override
    protected void update(float rate) {
        // Fake alpha effect
        float s = rate * 0.5f; // Alpha 1 -> 0.5
        int r = android.graphics.Color.red(shape.color.getOriginalValue());
        int g = android.graphics.Color.green(shape.color.getOriginalValue());
        int b = android.graphics.Color.blue(shape.color.getOriginalValue());
        int rr = android.graphics.Color.red(puzzle.getColorPalette().getBackgroundColor());
        int gg = android.graphics.Color.green(puzzle.getColorPalette().getBackgroundColor());
        int bb = android.graphics.Color.blue(puzzle.getColorPalette().getBackgroundColor());
        int c = android.graphics.Color.rgb(
                (int)MathUtils.lerp(r, rr, s),
                (int)MathUtils.lerp(g, gg, s),
                (int)MathUtils.lerp(b, bb, s));
        shape.color.setAnimationValue(this, c);
    }
}
