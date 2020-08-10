package com.aren.thewitnesspuzzle.puzzle.rules;

import com.aren.thewitnesspuzzle.graphics.Shape;
import com.aren.thewitnesspuzzle.graphics.Triangles;
import com.aren.thewitnesspuzzle.puzzle.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.graph.Tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Triangle extends Rule {

    public final int COLOR = android.graphics.Color.parseColor("#ffaa00");

    public int count;

    public Triangle(int count){
        super();
        this.count = count;
    }

    @Override
    public Shape getShape(){
        if(!(getGraphElement() instanceof Tile)) return null;
        return new Triangles(getGraphElement().getPosition().toVector3(), getPuzzle().getPathWidth() * 0.5f, count, COLOR);
    }

    @Override
    public boolean validate(Cursor cursor){
        if(getGraphElement() instanceof Tile){
            Tile tile = (Tile)getGraphElement();
            int c = 0;
            for(Edge edge : tile.edges){
                if(cursor.containsEdge(edge)) c++;
            }
            return c == count;
        }
        return true;
    }

    public static void generate(Cursor solution, Random random, float spawnRate){
        ArrayList<Tile> tiles = new ArrayList<>();
        for(Tile tile : solution.getPuzzle().getTiles()){
            if(tile.getRule() == null) tiles.add(tile);
        }

        int triangleCount = (int)(tiles.size() * spawnRate);

        Collections.shuffle(tiles, random);

        for(int i = 0; i < triangleCount; i++){
            int edgeCount = 0;
            for(Edge edge : tiles.get(i).edges){
                if(solution.containsEdge(edge)) edgeCount++;
            }
            tiles.get(i).setRule(new Triangle(edgeCount));
        }
    }

}
