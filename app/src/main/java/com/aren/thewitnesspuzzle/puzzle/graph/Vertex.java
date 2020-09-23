package com.aren.thewitnesspuzzle.puzzle.graph;

import com.aren.thewitnesspuzzle.math.Vector2;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;

import java.util.HashSet;
import java.util.Set;

public class Vertex extends GraphElement {

    public Set<Vertex> adj;

    public Vertex(Puzzle puzzle, float x, float y) {
        super(puzzle);
        this.x = x;
        this.y = y;
        adj = new HashSet<>();
    }

    public Vector2 getPosition() {
        return new Vector2(x, y);
    }

}
