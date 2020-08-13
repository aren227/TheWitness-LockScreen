package com.aren.thewitnesspuzzle.puzzle.animation;

import com.aren.thewitnesspuzzle.graphics.shape.Shape;
import com.aren.thewitnesspuzzle.math.MathUtils;
import com.aren.thewitnesspuzzle.puzzle.rules.Rule;

public class ErrorAnimation extends Animation {

    private Shape shape;
    private int originalColor;

    public ErrorAnimation(Rule rule){
        super(500, 10);
        shape = rule.getShape();
        originalColor = shape.color;
    }

    public ErrorAnimation(Rule rule, int repeat){
        super(500, repeat);
        shape = rule.getShape();
        originalColor = shape.color;
    }

    @Override
    protected void update(float rate) {
        float s = (float)(-Math.cos(rate * Math.PI * 2) + 1) / 2f;
        int r = android.graphics.Color.red(originalColor);
        int g = android.graphics.Color.green(originalColor);
        int b = android.graphics.Color.blue(originalColor);
        int c = android.graphics.Color.rgb(
                (int)MathUtils.lerp(r, 255, s),
                (int)MathUtils.lerp(g, 0, s),
                (int)MathUtils.lerp(b, 0, s));
        shape.color = c;
    }

    @Override
    protected void done(){
        super.done();
        shape.color = originalColor;
    }
}
