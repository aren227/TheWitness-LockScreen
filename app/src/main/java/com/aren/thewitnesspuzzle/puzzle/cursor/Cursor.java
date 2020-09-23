package com.aren.thewitnesspuzzle.puzzle.cursor;

import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.graph.EdgeProportion;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.BrokenLineRule;
import com.aren.thewitnesspuzzle.puzzle.rules.EndingPointRule;
import com.aren.thewitnesspuzzle.puzzle.rules.StartingPointRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Cursor {

    protected Puzzle puzzle;
    protected ArrayList<Vertex> visited;
    protected ArrayList<Edge> visitedEdges;
    protected ArrayList<EdgeProportion> visitedEdgesWithProportion; // directional
    protected EdgeProportion currentCursorEdge;

    protected boolean[] visitedArr;

    public Cursor(Puzzle puzzle, Vertex start) {
        this.puzzle = puzzle;

        visited = new ArrayList<>();
        visited.add(start);

        visitedEdges = new ArrayList<>();
        visitedEdgesWithProportion = new ArrayList<>();

        currentCursorEdge = null;

        visitedArr = new boolean[puzzle.getVertices().size()];
        visitedArr[start.index] = true;
    }

    public Cursor(Puzzle puzzle, ArrayList<Vertex> vertices, EdgeProportion cursorEdge) {
        this.puzzle = puzzle;

        visited = new ArrayList<>();
        visitedEdges = new ArrayList<>();
        visitedEdgesWithProportion = new ArrayList<>();

        visitedArr = new boolean[puzzle.getVertices().size()];

        for (int i = 0; i < vertices.size(); i++) {
            if (i == vertices.size() - 1 && vertices.get(i).getRule() instanceof EndingPointRule)
                continue;
            visited.add(vertices.get(i));
            visitedArr[vertices.get(i).index] = true;
            if (i > 0) {
                EdgeProportion edgeProportion = new EdgeProportion(puzzle.getEdgeByVertex(vertices.get(i - 1), vertices.get(i)));
                if (edgeProportion.to() == vertices.get(i - 1)) edgeProportion.reverse();
                edgeProportion.proportion = 1f;
                visitedEdges.add(edgeProportion.edge);
                visitedEdgesWithProportion.add(edgeProportion);
            }
        }

        currentCursorEdge = cursorEdge;
    }

    public Vertex getFirstVisitedVertex() {
        return visited.get(0);
    }

    public Vertex getLastVisitedVertex() {
        if (visited == null) return null;
        return visited.get(visited.size() - 1);
    }

    public Vertex getSecondLastVisitedVertex() {
        if (visited == null || visited.size() < 2) return null;
        return visited.get(visited.size() - 2);
    }

    public ArrayList<Vertex> getVisitedVertices() {
        return visited;
    }

    public ArrayList<Integer> getVisitedVertexIndices() {
        ArrayList<Integer> list = new ArrayList<>();
        for (Vertex vertex : getVisitedVertices()) {
            list.add(vertex.index);
        }
        return list;
    }

    public ArrayList<Edge> getFullyVisitedEdges() {
        return visitedEdges;
    }

    public ArrayList<EdgeProportion> getVisitedEdgesWithProportion(boolean includeCurrentCursorEdge) {
        if (includeCurrentCursorEdge) {
            ArrayList<EdgeProportion> arr = new ArrayList<>(visitedEdgesWithProportion);
            if (currentCursorEdge != null) arr.add(currentCursorEdge);
            return arr;
        }
        return visitedEdgesWithProportion;
    }

    public EdgeProportion getCurrentCursorEdge() {
        return currentCursorEdge;
    }

    public void connectTo(EdgeProportion target) {
        // NOTE: We should guarantee that visitedEdges[i - 1].to == visitedEdges[i].from so all edges are connected in right direction.
        // But the direction of 'target' (target.reverse) is not configured here.

        // If the target position is on the cursor, Pop front edges until directly connected.
        // Otherwise, BFS to find path

        // Just update proportion
        if (currentCursorEdge != null && currentCursorEdge.edge == target.edge) {
            if (currentCursorEdge.to() == target.from()) target.reverse();
            currentCursorEdge.updateProportion(this, target.proportion);
            return;
        }

        // Pop edges
        if(containsEdge(target.edge)){
            int MAX_POP_COUNT = 2;
            int popCnt = -1;
            for(int i = 0; i < MAX_POP_COUNT; i++){
                if(visitedEdges.size() - i - 1 < 0) break;
                if(visitedEdges.get(visitedEdges.size() - i - 1) == target.edge){
                    popCnt = i + 1;
                    break;
                }
            }

            if(popCnt != -1){
                // Pop
                for(int i = 0; i < popCnt; i++){
                    visitedArr[visited.get(visited.size() - 1).index] = false;

                    visited.remove(visited.size() - 1);
                    visitedEdges.remove(visitedEdges.size() - 1);
                    visitedEdgesWithProportion.remove(visitedEdgesWithProportion.size() - 1);
                }

                if (target.to() == getLastVisitedVertex()) target.reverse();
                currentCursorEdge = target;
            }
        }
        // Search path
        else{
            int MAX_POP_COUNT = 2;

            for(int k = 0; k <= MAX_POP_COUNT; k++){
                if(visited.size() - k <= 0) break;

                // Clone
                boolean[] tempVisit = new boolean[visitedArr.length];

                // Math.max(0, visited.size() - MAX_POP_COUNT) <-- This will do the trick
                // When touch position cross a previous cursor line itself,
                // Remove the gap between the head of the cursor and the cursor line.
                for(int i = Math.max(0, visited.size() - MAX_POP_COUNT); i < visited.size(); i++){
                    tempVisit[visited.get(i).index] = true;
                }

                // Start from the k-th last vertex
                Vertex start = visited.get(visited.size() - 1 - k);
                tempVisit[start.index] = false;

                // Array for back tracking
                int[] prev = new int[visitedArr.length];
                for(int i = 0; i < prev.length; i++){
                    prev[i] = -1;
                }

                Queue<VertexD> q = new LinkedList<>();
                q.add(new VertexD(start, -1, 0));

                Vertex last = null;

                int MAX_DIST = MAX_POP_COUNT - k;
                while(q.size() > 0){
                    VertexD front = q.poll();
                    if(tempVisit[front.vertex.index]) continue;
                    if(front.dist > MAX_DIST) continue;

                    tempVisit[front.vertex.index] = true;
                    prev[front.vertex.index] = front.prev;

                    if(target.edge.containsVertex(front.vertex)){
                        last = front.vertex;
                        break;
                    }

                    // Give priority to another vertex of currentCursorEdge
                    Vertex anotherStart = null;
                    if(front.dist == 0){
                        anotherStart = currentCursorEdge.edge.getAnotherVertex(start);
                        q.add(new VertexD(anotherStart, front.vertex.index, front.dist + 1));
                    }

                    for(Vertex adj : front.vertex.adj){
                        if(adj == anotherStart) continue;
                        q.add(new VertexD(adj, front.vertex.index, front.dist + 1));
                    }
                }

                // Failed
                if(last == null){
                    continue;
                }

                List<Vertex> path = new ArrayList<>();
                for(int i = last.index; prev[i] != -1; i = prev[i]){
                    path.add(puzzle.getVertex(i));
                }
                Collections.reverse(path);

                // Pop
                for(int i = 0; i < k; i++){
                    visitedArr[visited.get(visited.size() - 1).index] = false;

                    visited.remove(visited.size() - 1);
                    visitedEdges.remove(visitedEdges.size() - 1);
                    visitedEdgesWithProportion.remove(visitedEdgesWithProportion.size() - 1);
                }

                // Connect
                Vertex head = getLastVisitedVertex();
                for(Vertex v : path){
                    Edge e = puzzle.getEdgeByVertex(head, v);
                    EdgeProportion ep = new EdgeProportion(e);

                    if(e.to == head){
                        ep.reverse = true;
                    }

                    currentCursorEdge = ep;
                    updateProportionWithCollision(currentCursorEdge, 0f, 1f);

                    if(ep.proportion < 1f){
                        return;
                    }

                    visited.add(v);
                    visitedArr[v.index] = true;
                    visitedEdges.add(e);
                    visitedEdgesWithProportion.add(ep);

                    head = v;
                }

                if(target.to() == head) target.reverse();

                currentCursorEdge = target;
                updateProportionWithCollision(currentCursorEdge, 0, currentCursorEdge.proportion);

                break;
            }
        }
        /*
        // First call
        if (currentCursorEdge == null) {
            if (target.to() == getLastVisitedVertex()) target.reverse();
            if (target.from() == getLastVisitedVertex()) {
                currentCursorEdge = target;
                updateProportionWithCollision(currentCursorEdge, 0, currentCursorEdge.proportion);
            }
            return;
        }



        // Remove visited vertex from top (backward)
        if (target.edge.containsVertex(getSecondLastVisitedVertex()) && target.edge.containsVertex(getLastVisitedVertex())) {
            if (target.to() == getSecondLastVisitedVertex()) target.reverse();
            visited.remove(visited.size() - 1);
            visitedEdges.remove(visitedEdges.size() - 1);
            visitedEdgesWithProportion.remove(visitedEdgesWithProportion.size() - 1);

            currentCursorEdge = target;
            // No need to check collision since it was previously proved
            return;
        }

        // Can be connected with last visited vertex
        if (target.edge.containsVertex(getLastVisitedVertex())) {
            if (target.to() == getLastVisitedVertex()) target.reverse();
            currentCursorEdge = target;
            updateProportionWithCollision(currentCursorEdge, 0, currentCursorEdge.proportion);
            return;
        }

        // Can be connected with current edge
        if (target.edge.containsVertex(currentCursorEdge.to())) {
            if (target.to() == currentCursorEdge.to()) target.reverse();

            // First, check collision before adding a new vertex
            updateProportionWithCollision(currentCursorEdge, currentCursorEdge.proportion, 1f);

            // Collided
            if (currentCursorEdge.proportion < 1f) {
                return;
            }

            visited.add(currentCursorEdge.to());
            visitedEdges.add(currentCursorEdge.edge);
            visitedEdgesWithProportion.add(currentCursorEdge);

            currentCursorEdge = target;
            updateProportionWithCollision(currentCursorEdge, 0, currentCursorEdge.proportion);
        }

        // Failed to update. Ignoring...
        */
    }

    public void updateProportionWithCollision(EdgeProportion edgeProportion, float from, float to) {
        Edge edge = edgeProportion.edge;
        float length = edge.getLength();

        // Broken edge collision check
        if (edge.getRule() instanceof BrokenLineRule) {
            float collisionProportion = ((BrokenLineRule) edge.getRule()).getCollisionCircleRadius() + puzzle.getPathWidth() * 0.5f / length;
            if (from <= 0.5f) to = Math.min(0.5f - collisionProportion, to);
            else to = Math.max(0.5f + collisionProportion, to);
        }

        // Cursor self collision check
        for (int i = 0; i < visited.size() - 1; i++) {
            Vertex v = visited.get(i);
            if (edge.containsVertex(v)) {
                float collisionProportion = puzzle.getPathWidth() / length;
                if (v.getRule() instanceof StartingPointRule && v == visited.get(0))
                    collisionProportion = (((StartingPointRule) v.getRule()).getRadius() + puzzle.getPathWidth() * 0.5f) / length;
                to = Math.min(1 - collisionProportion, to);
            }
        }

        edgeProportion.proportion = to;
    }

    public Puzzle getPuzzle() {
        return puzzle;
    }

    public boolean containsEdge(Edge edge) {
        return getFullyVisitedEdges().contains(edge);
    }

    public boolean containsVertex(Vertex vertex) {
        return getVisitedVertices().contains(vertex);
    }

    // Vertex with distance information
    // Used in bfs queue
    protected class VertexD{
        public Vertex vertex;
        public int prev;
        public int dist;

        public VertexD(Vertex vertex, int prev, int dist){
            this.vertex = vertex;
            this.prev = prev;
            this.dist = dist;
        }
    }
}
