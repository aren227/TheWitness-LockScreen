package com.aren.thewitnesspuzzle.puzzle.graph;

import com.aren.thewitnesspuzzle.math.Vector2;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;

public class Tile extends GraphElement{

    public float x, y;

    public Tile(Puzzle puzzle, float x, float y){
        super(puzzle);
        this.x = x;
        this.y = y;
    }

    @Override
    public Vector2 getPosition(){
        return new Vector2(x, y);
    }

}
