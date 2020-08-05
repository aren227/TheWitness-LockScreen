package com.aren.thewitnesspuzzle.puzzle.rules;

import com.aren.thewitnesspuzzle.graphics.Circle;
import com.aren.thewitnesspuzzle.graphics.RoundSquare;
import com.aren.thewitnesspuzzle.graphics.Shape;
import com.aren.thewitnesspuzzle.math.Vector3;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;

public class StartingPoint extends Rule {

    public StartingPoint(Puzzle puzzle, int x, int y) {
        super(puzzle, x, y, Site.CORNER);
    }

    @Override
    public Shape getShape() {
        return new Circle(new Vector3(x , y , 0), getCircleRadius(puzzle), puzzle.getPathColor());
    }

    public static float getCircleRadius(Puzzle puzzle){
        return puzzle.getPathWidth() * 1.2f;
    }

}
