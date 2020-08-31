package com.aren.thewitnesspuzzle.puzzle.rules;

import com.aren.thewitnesspuzzle.graphics.shape.RectangleShape;
import com.aren.thewitnesspuzzle.graphics.shape.Shape;
import com.aren.thewitnesspuzzle.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.puzzle.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.walker.RandomGridTreeWalker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class BrokenLineRule extends Rule {

    public BrokenLineRule() {
        super();
    }

    @Override
    public Shape generateShape(){
        if(getGraphElement() instanceof Edge){
            Edge edge = (Edge)getGraphElement();
            return new RectangleShape(edge.getMiddlePoint().toVector3(), getCollisionCircleRadius() * 2f / edge.getLength(), edge.getPuzzle().getPathWidth(), edge.getAngle(), edge.getPuzzle().getColorPalette().getBackgroundColor());
        }
        return null;
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

    // Generate more interesting maze
    public static void generate(GridPuzzle puzzle, final RandomGridTreeWalker walker, Random random, float blockRate){
        ArrayList<Edge> blockEdges = new ArrayList<>();

        for(int i = 0; i <= puzzle.getWidth(); i++){
            for(int j = 0; j <= puzzle.getHeight(); j++){
                for(int k = 0; k < 4; k++){
                    if(((walker.bidirection[i][j] >> k) & 1) == 0){
                        if(i + walker.delta[k][0] < 0 || i + walker.delta[k][0] > puzzle.getWidth() || j + walker.delta[k][1] < 0 || j + walker.delta[k][1] > puzzle.getHeight()) continue;
                        Vertex from = puzzle.getVertexAt(i, j);
                        Vertex to = puzzle.getVertexAt(i + walker.delta[k][0], j + walker.delta[k][1]);
                        blockEdges.add(puzzle.getEdgeByVertex(from, to));
                    }
                }
            }
        }

        int brokenEdges = (int)(blockEdges.size() * blockRate);

        /*for(Edge edge : puzzle.getEdges()){
            if(edge.getRule() == null && !edge.isEndingEdge() && !solution.containsEdge(edge)){
                notSolutionEdges.add(edge);
            }
        }

        int brokenEdges = (int)(notSolutionEdges.size() * blockRate);
        Collections.sort(notSolutionEdges, new Comparator<Edge>() {
            @Override
            public int compare(Edge o1, Edge o2) {
                return -Integer.compare(Math.abs(walker.dist[o1.from.gridPosition.x][o1.from.gridPosition.y] - walker.dist[o1.to.gridPosition.x][o1.to.gridPosition.y]),
                        Math.abs(walker.dist[o2.from.gridPosition.x][o2.from.gridPosition.y] - walker.dist[o2.to.gridPosition.x][o2.to.gridPosition.y]));
            }
        });*/
        for(int i = 0; i < brokenEdges; i++){
            blockEdges.get(i).setRule(new BrokenLineRule());
        }
    }

    public float getCollisionCircleRadius(){
        return 0.07f / getGraphElement().getPuzzle().getPathWidth() * 0.5f;
    }

}
