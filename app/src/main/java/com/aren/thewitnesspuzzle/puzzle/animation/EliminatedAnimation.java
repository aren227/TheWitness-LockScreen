package com.aren.thewitnesspuzzle.puzzle.animation;

import com.aren.thewitnesspuzzle.graphics.shape.Shape;
import com.aren.thewitnesspuzzle.math.MathUtils;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.graph.Tile;
import com.aren.thewitnesspuzzle.puzzle.rules.EliminationRule;
import com.aren.thewitnesspuzzle.puzzle.rules.Rule;

public class EliminatedAnimation extends Animation{

    private Rule rule;
    private Shape shape;
    private Puzzle puzzle;

    public EliminatedAnimation(Rule rule){
        super(1000, 1, true);
        this.rule = rule;
        shape = rule.getShape();
        puzzle = rule.getPuzzle();
    }

    @Override
    protected void update(float rate) {
        shape.scale.setAnimationValue(this, MathUtils.lerp(1f, 0.9f, rate));

        int sr = android.graphics.Color.red(EliminationRule.COLOR);
        int sg = android.graphics.Color.green(EliminationRule.COLOR);
        int sb = android.graphics.Color.blue(EliminationRule.COLOR);

        // Fake alpha effect
        int r = android.graphics.Color.red(shape.color.getOriginalValue());
        int g = android.graphics.Color.green(shape.color.getOriginalValue());
        int b = android.graphics.Color.blue(shape.color.getOriginalValue());

        int bgColor;
        if(rule.getGraphElement() instanceof Tile) bgColor = puzzle.getColorPalette().getBackgroundColor();
        else bgColor = puzzle.getColorPalette().getPathColor();

        int rr = android.graphics.Color.red(bgColor);
        int gg = android.graphics.Color.green(bgColor);
        int bb = android.graphics.Color.blue(bgColor);

        int c = android.graphics.Color.rgb(
                (int)MathUtils.lerp(sr, (r + rr) / 2f, rate),
                (int)MathUtils.lerp(sg, (g + gg) / 2f, rate),
                (int)MathUtils.lerp(sb, (b + bb) / 2f, rate));
        shape.color.setAnimationValue(this, c);
    }
}
