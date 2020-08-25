package com.aren.thewitnesspuzzle.puzzle.cursor;

import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.graph.EdgeProportion;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.BrokenLineRule;
import com.aren.thewitnesspuzzle.puzzle.rules.EndingPointRule;
import com.aren.thewitnesspuzzle.puzzle.rules.StartingPointRule;

import java.util.ArrayList;

public class Cursor {

    protected Puzzle puzzle;
    protected ArrayList<Vertex> visited;
    protected ArrayList<Edge> visitedEdges;
    protected ArrayList<EdgeProportion> visitedEdgesWithProportion; // directional
    protected EdgeProportion currentCursorEdge;

    public Cursor(Puzzle puzzle, Vertex start){
        this.puzzle = puzzle;

        visited = new ArrayList<>();
        visited.add(start);

        visitedEdges = new ArrayList<>();
        visitedEdgesWithProportion = new ArrayList<>();

        currentCursorEdge = null;
    }

    public Cursor(Puzzle puzzle, ArrayList<Vertex> vertices, EdgeProportion cursorEdge){
        this.puzzle = puzzle;

        visited = new ArrayList<>();
        visitedEdges = new ArrayList<>();
        visitedEdgesWithProportion = new ArrayList<>();

        for(int i = 0; i < vertices.size(); i++){
            if(i == vertices.size() - 1 && vertices.get(i).getRule() instanceof EndingPointRule) continue;
            visited.add(vertices.get(i));
            if(i > 0){
                EdgeProportion edgeProportion = new EdgeProportion(puzzle.getEdgeByVertex(vertices.get(i - 1), vertices.get(i)));
                if(edgeProportion.to() == vertices.get(i - 1)) edgeProportion.reverse();
                edgeProportion.proportion = 1f;
                visitedEdges.add(edgeProportion.edge);
                visitedEdgesWithProportion.add(edgeProportion);
            }
        }

        currentCursorEdge = cursorEdge;
    }

    public Vertex getFirstVisitedVertex(){
        return visited.get(0);
    }

    public Vertex getLastVisitedVertex(){
        if(visited == null) return null;
        return visited.get(visited.size() - 1);
    }

    public Vertex getSecondLastVisitedVertex(){
        if(visited == null || visited.size() < 2) return null;
        return visited.get(visited.size() - 2);
    }

    public ArrayList<Vertex> getVisitedVertices(){
        return visited;
    }

    public ArrayList<Edge> getFullyVisitedEdges(){
        return visitedEdges;
    }

    public ArrayList<EdgeProportion> getVisitedEdgesWithProportion(boolean includeCurrentCursorEdge){
        if(includeCurrentCursorEdge){
            ArrayList<EdgeProportion> arr = new ArrayList<>(visitedEdgesWithProportion);
            if(currentCursorEdge != null) arr.add(currentCursorEdge);
            return arr;
        }
        return visitedEdgesWithProportion;
    }

    public EdgeProportion getCurrentCursorEdge(){
        return currentCursorEdge;
    }

    public void connectTo(EdgeProportion target){
        // NOTE: We should guarantee that visitedEdges[i - 1].to == visitedEdges[i].from so all edges are connected right direction.
        // But the direction of 'target' (target.reverse) is not configured here.

        // First call
        if(currentCursorEdge == null){
            if(target.to() == getLastVisitedVertex()) target.reverse();
            if(target.from() == getLastVisitedVertex()){
                currentCursorEdge = target;
                updateProportionWithCollision(currentCursorEdge, 0, currentCursorEdge.proportion);
            }
            return;
        }

        // Just update proportion
        if(currentCursorEdge.edge == target.edge){
            if(currentCursorEdge.to() == target.from()) target.reverse();
            currentCursorEdge.updateProportion(this, target.proportion);
            return;
        }

        // Remove visited vertex from top (backward)
        if(target.edge.containsVertex(getSecondLastVisitedVertex()) && target.edge.containsVertex(getLastVisitedVertex())){
            if(target.to() == getSecondLastVisitedVertex()) target.reverse();
            visited.remove(visited.size() - 1);
            visitedEdges.remove(visitedEdges.size() - 1);
            visitedEdgesWithProportion.remove(visitedEdgesWithProportion.size() - 1);

            currentCursorEdge = target;
            // No need to check collision since it was previously proved
            return;
        }

        // Can be connected with last visited vertex
        if(target.edge.containsVertex(getLastVisitedVertex())){
            if(target.to() == getLastVisitedVertex()) target.reverse();
            currentCursorEdge = target;
            updateProportionWithCollision(currentCursorEdge, 0, currentCursorEdge.proportion);
            return;
        }

        // Can be connected with current edge
        if(target.edge.containsVertex(currentCursorEdge.to())){
            if(target.to() == currentCursorEdge.to()) target.reverse();

            // First, check collision before adding a new vertex
            updateProportionWithCollision(currentCursorEdge, currentCursorEdge.proportion, 1f);

            // Collided
            if(currentCursorEdge.proportion < 1f){
                return;
            }

            visited.add(currentCursorEdge.to());
            visitedEdges.add(currentCursorEdge.edge);
            visitedEdgesWithProportion.add(currentCursorEdge);

            currentCursorEdge = target;
            updateProportionWithCollision(currentCursorEdge, 0, currentCursorEdge.proportion);
        }

        // Failed to update. Ignoring...
    }

    public void updateProportionWithCollision(EdgeProportion edgeProportion, float from, float to){
        Edge edge = edgeProportion.edge;
        float length = edge.getLength();

        // Broken edge collision check
        if(edge.getRule() instanceof BrokenLineRule){
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

        edgeProportion.proportion = to;
    }

    public Puzzle getPuzzle(){
        return puzzle;
    }

    public boolean containsEdge(Edge edge){
        return getFullyVisitedEdges().contains(edge);
    }

    public boolean containsVertex(Vertex vertex){
        return getVisitedVertices().contains(vertex);
    }

}
