package com.aren.thewitnesspuzzle.puzzle.rules;

import com.aren.thewitnesspuzzle.graphics.shape.RectangleShape;
import com.aren.thewitnesspuzzle.graphics.shape.Shape;
import com.aren.thewitnesspuzzle.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.puzzle.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.graph.Edge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class BrokenLineRule extends Rule {

    public BrokenLineRule() {
        super();
    }

    @Override
    public Shape generateShape(){
        if(getGraphElement() instanceof Edge){
            Edge edge = (Edge)getGraphElement();
            return new RectangleShape(edge.getMiddlePoint().toVector3(), getSize() / edge.getLength(), edge.getPuzzle().getPathWidth(), edge.getAngle(), edge.getPuzzle().getBackgroundColor());
        }
        return null;
    }

    public static float getSize(){
        return 0.3f;
    }

    public static void generate(Cursor solution, Random random, float blockRate){
        Puzzle puzzle = solution.getPuzzle();

        ArrayList<Edge> notSolutionEdges = new ArrayList<>();

        for(Edge edge : puzzle.getEdges()){
            if(edge.getRule() == null && !edge.isEndingEdge() && !solution.containsEdge(edge)){
                notSolutionEdges.add(edge);
            }
        }

        int brokenEdges = (int)(notSolutionEdges.size() * blockRate);
        Collections.shuffle(notSolutionEdges, random);
        for(int i = 0; i < brokenEdges; i++){
            notSolutionEdges.get(i).setRule(new BrokenLineRule());
        }
    }

    public static float getCollisionCircleRadius(){
        return getSize() / 2f;
    }

}