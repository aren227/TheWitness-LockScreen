package com.aren.thewitnesspuzzle.puzzle.cursor;

import com.aren.thewitnesspuzzle.puzzle.GridSymmetryPuzzle;
import com.aren.thewitnesspuzzle.puzzle.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.graph.EdgeProportion;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.BrokenLineRule;
import com.aren.thewitnesspuzzle.puzzle.rules.StartingPointRule;
import com.aren.thewitnesspuzzle.puzzle.rules.SymmetricColor;

import java.util.ArrayList;
import java.util.List;

public class SymmetryCursor extends Cursor {

    public SymmetryCursor(GridSymmetryPuzzle puzzle, Vertex start) {
        super(puzzle, start);
    }

    public SymmetryCursor(GridSymmetryPuzzle puzzle, ArrayList<Vertex> vertices, EdgeProportion cursorEdge){
        super(puzzle, vertices, cursorEdge);
    }

    @Override
    public void updateProportionWithCollision(EdgeProportion edgeProportion, float from, float to){
        Edge edge = edgeProportion.edge;
        float length = edge.getLength();

        GridSymmetryPuzzle gridSymmetryPuzzle = (GridSymmetryPuzzle)puzzle;

        Edge oppositeEdge = gridSymmetryPuzzle.getOppositeEdge(edge);

        // Broken edge collision check
        if(edge.getRule() instanceof BrokenLineRule || oppositeEdge.getRule() instanceof BrokenLineRule){
            float collisionProportion = ((BrokenLineRule)edge.getRule()).getCollisionCircleRadius() + puzzle.getPathWidth() * 0.5f / length;
            if(from <= 0.5f) to = Math.min(0.5f - collisionProportion, to);
            else to = Math.max(0.5f + collisionProportion, to);
        }

        // Cursor self collision check
        for(int i = 0; i < visited.size() - 1; i++){
            Vertex v = visited.get(i);
            if(edge.containsVertex(v)){
                float collisionProportion = puzzle.getPathWidth() / length;
                if(v.getRule() instanceof StartingPointRule) collisionProportion = (((StartingPointRule)v.getRule()).getRadius() + puzzle.getPathWidth() * 0.5f) / length;
                to = Math.min(1 - collisionProportion, to);
            }
        }

        // Opposite cursor collision check
        for(int i = 0; i < visited.size() - 1; i++){
            Vertex v = gridSymmetryPuzzle.getOppositeVertex(visited.get(i));
            if(edge.containsVertex(v)){
                float collisionProportion = puzzle.getPathWidth() / length;
                if(v.getRule() instanceof StartingPointRule) collisionProportion = (((StartingPointRule)v.getRule()).getRadius() + puzzle.getPathWidth() * 0.5f) / length;
                to = Math.min(1 - collisionProportion, to);
            }
        }

        // Two cursors collided each other
        if(edgeProportion.to() == gridSymmetryPuzzle.getOppositeVertex(edgeProportion.to())){
            float collisionProportion = puzzle.getPathWidth() * 0.5f / length;
            to = Math.min(1 - collisionProportion, to);
        }
        if(edge == oppositeEdge){
            float collisionProportion = puzzle.getPathWidth() * 0.5f / length;
            to = Math.min(0.5f - collisionProportion, to);
        }

        edgeProportion.proportion = to;
    }

    public boolean hasSymmetricColor(){
        return getPuzzle().hasSymmetricColor();
    }

    @Override
    public boolean containsEdge(Edge edge){
        List<Edge> edges = getFullyVisitedEdges();
        return edges.contains(edge) || edges.contains(((GridSymmetryPuzzle)puzzle).getOppositeEdge(edge));
    }

    public SymmetricColor getSymmetricColor(Edge edge){
        if(getFullyVisitedEdges().contains(edge)) return SymmetricColor.CYAN;
        return SymmetricColor.YELLOW;
    }

    @Override
    public boolean containsVertex(Vertex vertex){
        List<Vertex> vertices = getVisitedVertices();
        return vertices.contains(vertex) || vertices.contains(((GridSymmetryPuzzle)puzzle).getOppositeVertex(vertex));
    }

    public SymmetricColor getSymmetricColor(Vertex vertex){
        if(getVisitedVertices().contains(vertex)) return SymmetricColor.CYAN;
        return SymmetricColor.YELLOW;
    }

    @Override
    public GridSymmetryPuzzle getPuzzle(){
        return (GridSymmetryPuzzle)puzzle;
    }
}
