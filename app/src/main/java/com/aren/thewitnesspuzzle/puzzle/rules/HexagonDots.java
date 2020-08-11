package com.aren.thewitnesspuzzle.puzzle.rules;

import android.graphics.Color;

import com.aren.thewitnesspuzzle.graphics.Hexagon;
import com.aren.thewitnesspuzzle.graphics.Shape;
import com.aren.thewitnesspuzzle.math.Vector3;
import com.aren.thewitnesspuzzle.puzzle.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.graph.Tile;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class HexagonDots extends Rule {

    public HexagonDots(){
        super();
    }

    @Override
    public Shape getShape() {
        if(getGraphElement() instanceof Tile) return null;
        return new Hexagon(new Vector3(getGraphElement().x, getGraphElement().y, 0), getPuzzle().getPathWidth() * 0.4f, Color.BLACK);
    }

    @Override
    public boolean validateLocally(Cursor cursor){
        if(getGraphElement() instanceof Edge){
            return cursor.containsEdge((Edge)getGraphElement());
        }
        if(getGraphElement() instanceof Vertex){
            return cursor.containsVertex((Vertex)getGraphElement());
        }
        return true;
    }

    public static void generate(Cursor solution, Random random, float spawnRate){
        ArrayList<Vertex> vertices = new ArrayList<>();
        for(Vertex vertex : solution.getVisitedVertices()){
            if(vertex.getRule() == null) vertices.add(vertex);
        }

        int hexagonVertexCount = (int)(vertices.size() * spawnRate);

        Collections.shuffle(vertices, random);

        for(int i = 0; i < hexagonVertexCount; i++){
            vertices.get(i).setRule(new HexagonDots());
        }
    }
}
