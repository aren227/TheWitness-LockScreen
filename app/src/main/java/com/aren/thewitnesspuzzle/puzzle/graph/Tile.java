package com.aren.thewitnesspuzzle.puzzle.graph;

import com.aren.thewitnesspuzzle.puzzle.Puzzle;

public class Tile extends GraphElement{

    public float x, y;

    public Tile(Puzzle puzzle, float x, float y){
        super(puzzle);
        this.x = x;
        this.y = y;
    }

}
