package com.aren.thewitnesspuzzle.puzzle.graph;

import com.aren.thewitnesspuzzle.math.Vector2;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Tile extends GraphElement {

    public List<Edge> edges;

    public Tile() {
        this(0, 0);
    }

    public Tile(float x, float y) {
        super();
        this.x = x;
        this.y = y;

        edges = new ArrayList<>();
    }

    @Override
    public Vector2 getPosition() {
        return new Vector2(x, y);
    }

    public static Tile deserialize(JSONObject jsonObject) throws JSONException {
        Tile tile = new Tile();
        tile.baseDeserialize(jsonObject);
        return tile;
    }

}
