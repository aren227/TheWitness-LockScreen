package com.aren.thewitnesspuzzle.puzzle.graph;

import com.aren.thewitnesspuzzle.math.Vector2;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;

import java.util.ArrayList;
import java.util.List;

public class Tile extends GraphElement {

    public List<Edge> edges;

    public Tile(Puzzle puzzle, float x, float y) {
        super(puzzle);
        this.x = x;
        this.y = y;

        edges = new ArrayList<>();
    }

    @Override
    public Vector2 getPosition() {
        return new Vector2(x, y);
    }

}
