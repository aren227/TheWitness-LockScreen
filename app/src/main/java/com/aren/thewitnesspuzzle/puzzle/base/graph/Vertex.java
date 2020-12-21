package com.aren.thewitnesspuzzle.puzzle.base.graph;

import com.aren.thewitnesspuzzle.puzzle.base.PuzzleBase;

import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class Vertex extends GraphElement {

    public Set<Vertex> adj = new HashSet<>();

    public Vertex(PuzzleBase puzzleBase, float x, float y) {
        super(puzzleBase, puzzleBase.getNextVertexIndex(), x, y);
    }

    public Vertex(PuzzleBase puzzleBase, JSONObject jsonObject) {
        super(puzzleBase, jsonObject);
    }

}
