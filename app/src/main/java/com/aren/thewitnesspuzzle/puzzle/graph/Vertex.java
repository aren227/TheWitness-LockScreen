package com.aren.thewitnesspuzzle.puzzle.graph;

import com.aren.thewitnesspuzzle.puzzle.Puzzle;

public class Vertex extends GraphElement{

    public Puzzle puzzle;
    public float x, y;

    public Vertex(Puzzle puzzle, float x, float y){
        super(puzzle);
        this.x = x;
        this.y = y;
    }

}
