package com.aren.thewitnesspuzzle.puzzle.graph;

import com.aren.thewitnesspuzzle.math.Vector2;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class Vertex extends GraphElement {

    public Set<Vertex> adj = new HashSet<>();

    public Vertex() {
        this(0, 0);
    }

    public Vertex(float x, float y) {
        super();
        this.x = x;
        this.y = y;
    }

    public Vector2 getPosition() {
        return new Vector2(x, y);
    }

    public static Vertex deserialize(JSONObject jsonObject) throws JSONException {
        Vertex vertex = new Vertex();
        vertex.baseDeserialize(jsonObject);
        return vertex;
    }

}
