package com.aren.thewitnesspuzzle.puzzle.cursor;

import com.aren.thewitnesspuzzle.puzzle.GridSymmetryPuzzle;
import com.aren.thewitnesspuzzle.puzzle.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.graph.EdgeProportion;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.BrokenLineRule;

public class SymmetryCursor extends Cursor {

    public SymmetryCursor(GridSymmetryPuzzle puzzle, Vertex start) {
        super(puzzle, start);
    }

    @Override
    public void updateProportionWithCollision(EdgeProportion edgeProportion, float from, float to){
        Edge edge = edgeProportion.edge;
        float length = edge.getLength();

        GridSymmetryPuzzle gridSymmetryPuzzle = (GridSymmetryPuzzle)puzzle;

        Edge oppositeEdge = gridSymmetryPuzzle.getOppositeEdge(edge);

        // Broken edge collision check
        if(edge.getRule() instanceof BrokenLineRule || oppositeEdge.getRule() instanceof BrokenLineRule){
            float collisionProportion = BrokenLineRule.getCollisionCircleRadius() + puzzle.getPathWidth() * 0.5f / length;
            if(from <= 0.5f) to = Math.min(0.5f - collisionProportion, to);
            else to = Math.max(0.5f + collisionProportion, to);
        }

        // Cursor self collision check
        for(int i = 0; i < visited.size() - 1; i++){
            Vertex v = visited.get(i);
            if(edgeProportion.to() == v){
                float collisionProportion = puzzle.getPathWidth() / length;
                to = Math.min(1 - collisionProportion, to);
            }
        }

        // Opposite cursor collision check
        for(int i = 0; i < visited.size() - 1; i++){
            Vertex v = gridSymmetryPuzzle.getOppositeVertex(visited.get(i));
            if(edgeProportion.to() == v){
                float collisionProportion = puzzle.getPathWidth() / length;
                to = Math.min(1 - collisionProportion, to);
            }
        }

        // Two cursors collided each other
        if(edgeProportion.to() == gridSymmetryPuzzle.getOppositeVertex(edgeProportion.to())){
            float collisionProportion = puzzle.getPathWidth() * 0.5f / length;
            to = Math.min(1 - collisionProportion, to);
        }

        edgeProportion.proportion = to;
    }
}
