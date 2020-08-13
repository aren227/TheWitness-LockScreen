package com.aren.thewitnesspuzzle.puzzle.animation;

import com.aren.thewitnesspuzzle.graphics.shape.Shape;
import com.aren.thewitnesspuzzle.math.MathUtils;
import com.aren.thewitnesspuzzle.puzzle.rules.Rule;

public class ErrorAnimation extends Animation {

    private Shape shape;

    public ErrorAnimation(Rule rule){
        super(500, 10, false);
        shape = rule.getShape();
    }

    public ErrorAnimation(Rule rule, int repeat){
        super(500, repeat, false);
        shape = rule.getShape();
    }

    @Override
    protected void update(float rate) {
        float s = (float)(-Math.cos(rate * Math.PI * 2) + 1) / 2f;
        int r = android.graphics.Color.red(shape.color.getOriginalValue());
        int g = android.graphics.Color.green(shape.color.getOriginalValue());
        int b = android.graphics.Color.blue(shape.color.getOriginalValue());
        int c = android.graphics.Color.rgb(
                (int)MathUtils.lerp(r, 255, s),
                (int)MathUtils.lerp(g, 0, s),
                (int)MathUtils.lerp(b, 0, s));
        shape.color.setAnimationValue(this, c);
    }
}
