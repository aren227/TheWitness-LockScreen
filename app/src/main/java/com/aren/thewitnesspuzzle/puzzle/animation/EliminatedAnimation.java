package com.aren.thewitnesspuzzle.puzzle.animation;

import com.aren.thewitnesspuzzle.math.MathUtils;
import com.aren.thewitnesspuzzle.puzzle.rules.Rule;

public class EliminatedAnimation extends Animation{

    private Rule rule;

    public EliminatedAnimation(Rule rule){
        super(1000, 1);
        this.rule = rule;
    }

    @Override
    protected void update(float rate) {
        this.rule.getShape().scale = MathUtils.lerp(1f, 0.9f, rate);
    }

    @Override
    protected void done(){
        super.done();
        this.rule.getShape().scale = 0.9f;
    }
}
