package com.aren.thewitnesspuzzle.puzzle.animation;

import com.aren.thewitnesspuzzle.core.graph.Tile;
import com.aren.thewitnesspuzzle.core.math.MathUtils;
import com.aren.thewitnesspuzzle.core.rules.EliminationRule;
import com.aren.thewitnesspuzzle.core.rules.RuleBase;
import com.aren.thewitnesspuzzle.graphics.shape.Shape;
import com.aren.thewitnesspuzzle.render.PuzzleRenderer;

public class EliminatedAnimation extends Animation {

    private RuleBase ruleBase;
    private Shape shape;
    private PuzzleRenderer puzzleRenderer;

    public EliminatedAnimation(RuleBase ruleBase, PuzzleRenderer puzzleRenderer) {
        super(1000, 1, true);
        this.ruleBase = ruleBase;
        this.puzzleRenderer = puzzleRenderer;
        shape = puzzleRenderer.getRuleShape().get(ruleBase);
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
        if (ruleBase.getGraphElement() instanceof Tile)
            bgColor = puzzleRenderer.getPuzzleBase().getColorPalette().getBackgroundColor();
        else bgColor = puzzleRenderer.getPuzzleBase().getColorPalette().getPathColor();

        int rr = android.graphics.Color.red(bgColor);
        int gg = android.graphics.Color.green(bgColor);
        int bb = android.graphics.Color.blue(bgColor);

        int c = android.graphics.Color.rgb(
                (int) MathUtils.lerp(sr, (r + rr) / 2f, rate),
                (int) MathUtils.lerp(sg, (g + gg) / 2f, rate),
                (int) MathUtils.lerp(sb, (b + bb) / 2f, rate));
        shape.color.setAnimationValue(this, c);
    }
}
