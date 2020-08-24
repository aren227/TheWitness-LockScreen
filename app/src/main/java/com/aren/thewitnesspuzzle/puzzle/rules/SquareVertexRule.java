package com.aren.thewitnesspuzzle.puzzle.rules;

import com.aren.thewitnesspuzzle.graphics.shape.RectangleShape;
import com.aren.thewitnesspuzzle.graphics.shape.Shape;

// Actually, it's not rule but for convenience.
public class SquareVertexRule extends Rule {

    @Override
    public Shape generateShape() {
        return new RectangleShape(getGraphElement().getPosition().toVector3(), getPuzzle().getPathWidth(), getPuzzle().getPathWidth(), 0, getGraphElement().getPuzzle().getColorPalette().getPathColor());
    }

}
