package com.aren.thewitnesspuzzle.puzzle.rules;

import com.aren.thewitnesspuzzle.graphics.Rectangle;
import com.aren.thewitnesspuzzle.graphics.Shape;
import com.aren.thewitnesspuzzle.puzzle.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.graph.Edge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class BrokenLine extends Rule {

    public BrokenLine() {
        super();
    }

    @Override
    public Shape getShape() {
        if(getGraphElement() instanceof Edge){
            Edge edge = (Edge)getGraphElement();
            return new Rectangle(edge.getMiddlePoint().toVector3(), getSize() / edge.getLength(), edge.getPuzzle().getPathWidth(), edge.getAngle(), edge.getPuzzle().getBackgroundColor());
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
            if(edge.getRule() == null && !solution.containsEdge(edge)){
                notSolutionEdges.add(edge);
            }
        }

        int brokenEdges = (int)(notSolutionEdges.size() * blockRate);
        Collections.shuffle(notSolutionEdges, random);
        for(int i = 0; i < brokenEdges; i++){
            notSolutionEdges.get(i).setRule(new BrokenLine());
        }
    }

    public static float getCollisionCircleRadius(){
        return getSize() / 2f;
    }

}
