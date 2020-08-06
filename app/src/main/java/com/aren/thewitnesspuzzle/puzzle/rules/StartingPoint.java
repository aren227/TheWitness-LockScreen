package com.aren.thewitnesspuzzle.puzzle.rules;

import com.aren.thewitnesspuzzle.graphics.Circle;
import com.aren.thewitnesspuzzle.graphics.RoundSquare;
import com.aren.thewitnesspuzzle.graphics.Shape;
import com.aren.thewitnesspuzzle.math.Vector3;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.graph.GraphElement;

public class StartingPoint extends Rule {

    public StartingPoint() {
        super();
    }

    @Override
    public Shape getShape() {
        return new Circle(getGraphElement().getPosition().toVector3(), getRadius(), getGraphElement().getPuzzle().getPathColor());
    }

    public float getRadius(){
        return getGraphElement().getPuzzle().getPathWidth() * 1.2f;
    }

}
