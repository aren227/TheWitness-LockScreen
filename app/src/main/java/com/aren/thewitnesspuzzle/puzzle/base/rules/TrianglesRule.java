package com.aren.thewitnesspuzzle.puzzle.base.rules;

import com.aren.thewitnesspuzzle.puzzle.base.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.base.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.base.graph.Tile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class TrianglesRule extends RuleBase {

    public static final String NAME = "triangles";

    public static final int COLOR = android.graphics.Color.parseColor("#ffaa00");

    public int count;

    public TrianglesRule(int count) {
        super();
        this.count = count;
    }

    public TrianglesRule(JSONObject jsonObject) throws JSONException {
        super(jsonObject);

        count = jsonObject.getInt("count");
    }

    @Override
    public boolean validateLocally(Cursor cursor) {
        if(eliminated) return true;

        if (getGraphElement() instanceof Tile) {
            Tile tile = (Tile) getGraphElement();
            int c = 0;
            for (Edge edge : tile.edges) {
                if (cursor.containsEdge(edge)) c++;
            }
            return c == count;
        }
        return true;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void serialize(JSONObject jsonObject) throws JSONException {
        jsonObject.put("count", count);
    }

    public static void generate(Cursor solution, Random random, float spawnRate) {
        ArrayList<Tile> tiles = new ArrayList<>();
        for (Tile tile : solution.getPuzzle().getTiles()) {
            if (tile.getRule() == null) {
                // Only add tiles that have one or more edges
                boolean haveEdge = false;
                for (Edge edge : tile.edges) {
                    if (solution.containsEdge(edge)) {
                        haveEdge = true;
                        break;
                    }
                }
                if (haveEdge) tiles.add(tile);
            }
        }

        int triangleCount = Math.min((int) (solution.getPuzzle().getTiles().size() * spawnRate), tiles.size());

        Collections.shuffle(tiles, random);

        for (int i = 0; i < triangleCount; i++) {
            int edgeCount = 0;
            for (Edge edge : tiles.get(i).edges) {
                if (solution.containsEdge(edge)) edgeCount++;
            }
            tiles.get(i).setRule(new TrianglesRule(edgeCount));
        }
    }

}
