package com.aren.thewitnesspuzzle.puzzle.rules;

import com.aren.thewitnesspuzzle.graphics.shape.RectangleShape;
import com.aren.thewitnesspuzzle.graphics.shape.Shape;

import org.json.JSONObject;

// Actually, it's not a rule but for convenience.
public class SquareVertexRule extends Rule {

    public static final String NAME = "squarevertex";

    public SquareVertexRule() {
        super();
    }

    public SquareVertexRule(JSONObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public Shape generateShape() {
        return new RectangleShape(getGraphElement().getPosition().toVector3(), getPuzzle().getPathWidth(), getPuzzle().getPathWidth(), 0, getGraphElement().getPuzzle().getColorPalette().getPathColor());
    }

    @Override
    public String getName() {
        return NAME;
    }

}
