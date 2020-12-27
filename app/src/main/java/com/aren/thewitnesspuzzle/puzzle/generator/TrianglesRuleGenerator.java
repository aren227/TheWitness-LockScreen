package com.aren.thewitnesspuzzle.puzzle.generator;

import com.aren.thewitnesspuzzle.core.cursor.Cursor;
import com.aren.thewitnesspuzzle.core.graph.Edge;
import com.aren.thewitnesspuzzle.core.graph.Tile;
import com.aren.thewitnesspuzzle.core.rules.TrianglesRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class TrianglesRuleGenerator {

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
