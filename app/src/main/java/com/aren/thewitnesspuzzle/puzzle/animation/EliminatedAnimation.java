package com.aren.thewitnesspuzzle.puzzle.animation;

import com.aren.thewitnesspuzzle.math.MathUtils;
import com.aren.thewitnesspuzzle.puzzle.rules.Rule;

public class EliminatedAnimation extends Animation{

    private Rule rule;

    public EliminatedAnimation(Rule rule){
        super(1000, 1, true);
        this.rule = rule;
    }

    @Override
    protected void update(float rate) {
        this.rule.getShape().scale.setAnimationValue(this, MathUtils.lerp(1f, 0.85f, rate));
    }
}
