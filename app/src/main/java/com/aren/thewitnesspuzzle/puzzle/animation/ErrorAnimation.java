package com.aren.thewitnesspuzzle.puzzle.animation;

import com.aren.thewitnesspuzzle.core.math.MathUtils;
import com.aren.thewitnesspuzzle.graphics.shape.HexagonShape;
import com.aren.thewitnesspuzzle.graphics.shape.Shape;
import com.aren.thewitnesspuzzle.render.RuleShape;

public class ErrorAnimation extends Animation {

    private Shape shape;
    private boolean shouldFloat;

    public ErrorAnimation(Shape shape) {
        this(shape, 10);
    }

    public ErrorAnimation(Shape shape, int repeat) {
        super(500, repeat, false);
        this.shape = shape;
        if (shape instanceof HexagonShape) shouldFloat = true;
    }

    @Override
    protected void update(float rate) {
        float s = (float) (-Math.cos(rate * Math.PI * 2) + 1) / 2f;
        int r = android.graphics.Color.red(shape.color.getOriginalValue());
        int g = android.graphics.Color.green(shape.color.getOriginalValue());
        int b = android.graphics.Color.blue(shape.color.getOriginalValue());
        int c = android.graphics.Color.rgb(
                (int) MathUtils.lerp(r, 255, s),
                (int) MathUtils.lerp(g, 0, s),
                (int) MathUtils.lerp(b, 0, s));
        shape.color.setAnimationValue(this, c);

        if (shouldFloat) {
            shape.zIndex.setAnimationValue(this, RuleShape.Z_INDEX_FLOAT);
        }
    }
}
