package com.aren.thewitnesspuzzle.puzzle.rules;

import com.aren.thewitnesspuzzle.graphics.shape.CircleShape;
import com.aren.thewitnesspuzzle.graphics.shape.Shape;

public class StartingPointRule extends Rule {

    public StartingPointRule() {
        super();
    }

    @Override
    public Shape generateShape() {
        return new CircleShape(getGraphElement().getPosition().toVector3(), getRadius(), getGraphElement().getPuzzle().getColorPalette().getPathColor());
    }

    public float getRadius(){
        return getGraphElement().getPuzzle().getPathWidth() * 1.1f;
    }

}
