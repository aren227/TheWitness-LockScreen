package com.aren.thewitnesspuzzle.puzzle.graph;

import com.aren.thewitnesspuzzle.math.Vector2;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.base.PuzzleBase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Tile extends GraphElement {

    public List<Edge> edges = new ArrayList<>();

    public Tile(PuzzleBase puzzleBase, float x, float y) {
        super(puzzleBase, puzzleBase.getNextTileIndex(), x, y);
    }

    public Tile(PuzzleBase puzzleBase, JSONObject jsonObject) {
        super(puzzleBase, jsonObject);
    }

}
