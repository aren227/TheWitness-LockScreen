package com.aren.thewitnesspuzzle.puzzle.rules;

import com.aren.thewitnesspuzzle.graphics.shape.Circle;
import com.aren.thewitnesspuzzle.graphics.shape.Shape;

public class StartingPoint extends Rule {

    public StartingPoint() {
        super();
    }

    @Override
    public Shape generateShape() {
        return new Circle(getGraphElement().getPosition().toVector3(), getRadius(), getGraphElement().getPuzzle().getPathColor());
    }

    public float getRadius(){
        return getGraphElement().getPuzzle().getPathWidth() * 1.2f;
    }

}
