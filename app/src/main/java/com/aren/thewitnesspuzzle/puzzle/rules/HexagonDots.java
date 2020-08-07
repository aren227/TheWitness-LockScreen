package com.aren.thewitnesspuzzle.puzzle.rules;

import android.graphics.Color;

import com.aren.thewitnesspuzzle.graphics.Hexagon;
import com.aren.thewitnesspuzzle.graphics.Rectangle;
import com.aren.thewitnesspuzzle.graphics.Shape;
import com.aren.thewitnesspuzzle.math.Vector2Int;
import com.aren.thewitnesspuzzle.math.Vector3;
import com.aren.thewitnesspuzzle.puzzle.Cursor;
import com.aren.thewitnesspuzzle.puzzle.Line;
import com.aren.thewitnesspuzzle.puzzle.Path;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.graph.GraphElement;
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
    public boolean validate(Cursor cursor){
        if(getGraphElement() instanceof Edge){
            return cursor.containsEdge((Edge)getGraphElement());
        }
        if(getGraphElement() instanceof Vertex){
            return cursor.containsVertex((Vertex)getGraphElement());
        }
        return true;
    }

    public static void generate(Cursor solution, Random random, float spawnRate){
        ArrayList<Vertex> vertices = solution.getVisitedVertices();

        int hexagonVertexCount = (int)(vertices.size() * spawnRate);

        Collections.shuffle(vertices, random);

        for(int i = 0; i < hexagonVertexCount; i++){
            vertices.get(i).setRule(new HexagonDots());
        }
    }
}
