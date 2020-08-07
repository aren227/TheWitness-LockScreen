package com.aren.thewitnesspuzzle.puzzle.cursor;

import android.util.Log;

import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.BrokenLine;
import com.aren.thewitnesspuzzle.puzzle.rules.EndingPoint;

import java.util.ArrayList;

public class Cursor {

    private Puzzle puzzle;
    private ArrayList<Vertex> visited;
    private Edge currentCursorEdge;

    public Cursor(Puzzle puzzle, Vertex start){
        this.puzzle = puzzle;

        visited = new ArrayList<>();
        visited.add(start);

        currentCursorEdge = null;
    }

    public Cursor(Puzzle puzzle, ArrayList<Vertex> vertices, Edge cursorEdge){
        this.puzzle = puzzle;

        visited = new ArrayList<>(vertices);

        currentCursorEdge = cursorEdge;
    }

    public Vertex getLastVisitedVertex(){
        if(visited == null) return null;
        return visited.get(visited.size() - 1);
    }

    public Vertex getSecondLastVisitedVertex(){
        if(visited == null || visited.size() < 2) return null;
        return visited.get(visited.size() - 2);
    }

    public ArrayList<Vertex> getVisitedVertices(boolean excludeEndingPoint){
        ArrayList<Vertex> vertices = new ArrayList<>(visited);
        if(!excludeEndingPoint && vertices.size() > 0 && vertices.get(vertices.size() - 1).getRule() instanceof EndingPoint){
            vertices.remove(vertices.size() - 1);
        }
        return vertices;
    }

    public ArrayList<Edge> getVisitedEdges(boolean excludeCurrentCursorEdge){
        Log.i("CURSOR", "Visited Vertex Count: " + visited.size());

        ArrayList<Edge> edges = new ArrayList<>();
        for(int i = 0; i < visited.size() - 1; i++){
            Edge edge = puzzle.getEdgeByVertex(visited.get(i), visited.get(i + 1)).clone();
            edge.proportion = 1;
            edges.add(edge);
        }

        if(!excludeCurrentCursorEdge && currentCursorEdge != null){
            if(currentCursorEdge.from == getLastVisitedVertex()) edges.add(currentCursorEdge);
            else edges.add(currentCursorEdge.reverse());
        }

        return edges;
    }

    public Edge getCurrentCursorEdge(){
        if(currentCursorEdge.to == getLastVisitedVertex()) return currentCursorEdge.reverse();
        return currentCursorEdge.clone();
    }

    public void connectTo(Edge targetEdge){
        // First call
        if(currentCursorEdge == null){
            if(targetEdge.containsVertex(getLastVisitedVertex())){
                currentCursorEdge = targetEdge;
                updateProportionWithCollision(currentCursorEdge, currentCursorEdge.getProportionFromVertex(getLastVisitedVertex()), currentCursorEdge.proportion);
            }
            return;
        }

        // Just update proportion
        if(targetEdge.equals(currentCursorEdge)){
            currentCursorEdge.updateProportion(this, targetEdge);
            return;
        }

        // Remove visited vertex from top
        if(targetEdge.containsVertex(getLastVisitedVertex()) && targetEdge.containsVertex(getSecondLastVisitedVertex())){
            visited.remove(visited.size() - 1);
            currentCursorEdge = targetEdge;
            // No need to check collision since it was previously proved
            return;
        }

        // Can be connected with last visited vertex
        if(targetEdge.containsVertex(getLastVisitedVertex())){
            currentCursorEdge = targetEdge;
            updateProportionWithCollision(currentCursorEdge, currentCursorEdge.getProportionFromVertex(getLastVisitedVertex()), currentCursorEdge.proportion);
            return;
        }

        // Can be connected with current edge
        if(targetEdge.containsVertex(currentCursorEdge.getAnotherVertex(getLastVisitedVertex()))){
            // First, check collision before adding a new vertex
            Vertex newlyVisited = currentCursorEdge.getAnotherVertex(getLastVisitedVertex());
            updateProportionWithCollision(currentCursorEdge, currentCursorEdge.proportion, currentCursorEdge.getProportionFromVertex(newlyVisited));

            // Collided
            if(currentCursorEdge.proportion != currentCursorEdge.getProportionFromVertex(newlyVisited)){
                return;
            }

            visited.add(newlyVisited);

            currentCursorEdge = targetEdge;
            updateProportionWithCollision(currentCursorEdge, currentCursorEdge.getProportionFromVertex(newlyVisited), currentCursorEdge.proportion);
        }

        // Failed to update
    }

    public void updateProportionWithCollision(Edge edge, float from, float to){
        Log.i("CURSOR", "from: " + from + ", to: " + to);

        float length = edge.getLength();

        // Broken edge collision check
        if(edge.getRule() instanceof BrokenLine){
            float collisionProportion = BrokenLine.getCollisionCircleRadius() + puzzle.getPathWidth() * 0.5f / length;
            if(from <= 0.5f) to = Math.min(0.5f - collisionProportion, to);
            else to = Math.max(0.5f + collisionProportion, to);
        }

        // Cursor self collision check
        for(int i = 0; i < visited.size() - 1; i++){
            Vertex v = visited.get(i);
            if(edge.containsVertex(v)){
                float collisionProportion = puzzle.getPathWidth() / length;
                if(edge.from == v) to = Math.max(collisionProportion, to);
                else to = Math.min(1 - collisionProportion, to);
            }
        }

        edge.proportion = to;
    }

    public Puzzle getPuzzle(){
        return puzzle;
    }

    public boolean containsEdge(Edge edge){
        if(visited.size() > 0){
            for(int i = 0; i < visited.size() - 1; i++){
                if(edge.equals(new Edge(visited.get(i), visited.get(i + 1)))){
                    return true;
                }
            }
        }
        if(currentCursorEdge != null) return currentCursorEdge.equals(edge);
        return false;
    }

    public boolean containsVertex(Vertex vertex){
        for(int i = 0; i < visited.size(); i++){
            if(visited.get(i) == vertex) return true;
        }
        return false;
    }

}
