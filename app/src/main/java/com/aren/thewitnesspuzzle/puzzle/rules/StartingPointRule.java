package com.aren.thewitnesspuzzle.puzzle.rules;

import com.aren.thewitnesspuzzle.graphics.shape.CircleShape;
import com.aren.thewitnesspuzzle.graphics.shape.Shape;

public class StartingPointRule extends Rule {

    public static final String NAME = "start";

    public StartingPointRule() {
        super();
    }

    @Override
    public Shape generateShape() {
        return new CircleShape(getGraphElement().getPosition().toVector3(), getRadius(), getGraphElement().getPuzzle().getColorPalette().getPathColor());
    }

    @Override
    public String getName() {
        return NAME;
    }

    public float getRadius() {
        return getGraphElement().getPuzzle().getPathWidth() * 1.1f;
    }

}
