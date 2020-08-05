package com.aren.thewitnesspuzzle.puzzle.rules;

import com.aren.thewitnesspuzzle.graphics.Circle;
import com.aren.thewitnesspuzzle.graphics.Rectangle;
import com.aren.thewitnesspuzzle.math.Vector3;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;

public class EndingPoint extends Rule {

    public EndingPoint(Puzzle puzzle, int x, int y) {
        super(puzzle, x, y, Site.CORNER);
    }

    public static float getLength(Puzzle puzzle){
        return puzzle.getPathWidth();
    }

    public boolean isHorizontal(){
        return 0 < y && y < puzzle.getHeight();
    }

    public Vector3 getActualEndPosition(){
        if(isHorizontal()){
            return new Vector3(x + getLength(puzzle) * (x == 0 ? -1 : 1), y, 0);
        }
        else{
            return new Vector3(x, y + getLength(puzzle) * (y == 0 ? -1 : 1), 0);
        }
    }
}
