package com.aren.thewitnesspuzzle.puzzle;

import android.view.MotionEvent;

import com.aren.thewitnesspuzzle.graphics.Circle;
import com.aren.thewitnesspuzzle.graphics.Rectangle;
import com.aren.thewitnesspuzzle.math.Vector2;
import com.aren.thewitnesspuzzle.math.Vector2Int;
import com.aren.thewitnesspuzzle.math.Vector3;
import com.aren.thewitnesspuzzle.puzzle.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.cursor.SymmetryCursor;
import com.aren.thewitnesspuzzle.puzzle.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.EndingPoint;
import com.aren.thewitnesspuzzle.puzzle.rules.StartingPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GridSymmetryPuzzle extends GridPuzzle {

    public enum SymmetryType {VLINE, POINT}

    protected SymmetryType symmetryType;

    protected Map<Integer, Vertex> oppositeVertex;
    protected Map<Integer, Edge> oppositeEdge;

    protected Cursor oppositeCursor;

    public GridSymmetryPuzzle(Game game, int width, int height, SymmetryType symmetryType) {
        super(game, width, height);

        this.symmetryType = symmetryType;

        oppositeVertex = new HashMap<>();
        oppositeEdge = new HashMap<>();

        for(int i = 0; i <= width; i++){
            for(int j = 0; j <= height; j++){
                if(symmetryType == SymmetryType.VLINE){
                    oppositeVertex.put(getVertexAt(i, j).index, getVertexAt(width - i, j));
                }
                else if(symmetryType == SymmetryType.POINT){
                    oppositeVertex.put(getVertexAt(i, j).index, getVertexAt(width - i, height - j));
                }
            }
        }

        // Horizontal lines
        for(int i = 0; i < width; i++){
            for(int j = 0; j <= height; j++){
                if(symmetryType == SymmetryType.VLINE){
                    oppositeEdge.put(getEdgeAt(i, j, true).index, getEdgeAt(width - i - 1, j, true).reverse());
                }
                else if(symmetryType == SymmetryType.POINT){
                    oppositeEdge.put(getEdgeAt(i, j, true).index, getEdgeAt(width - i - 1, height - j, true).reverse());
                }
            }
        }

        // Vertical lines
        for(int i = 0; i <= width; i++){
            for(int j = 0; j < height; j++){
                if(symmetryType == SymmetryType.VLINE){
                    oppositeEdge.put(getEdgeAt(i, j, false).index, getEdgeAt(width - i, j, false));
                }
                else if(symmetryType == SymmetryType.POINT){
                    oppositeEdge.put(getEdgeAt(i, j, false).index, getEdgeAt(width - i, height - j - 1, false).reverse());
                }
            }
        }
    }

    public SymmetryType getSymmetryType(){
        return symmetryType;
    }

    @Override
    public void calcDynamicShapes(){
        dynamicShapes.clear();

        if(touching){
            dynamicShapes.add(new Circle(cursor.getFirstVisitedVertex().getPosition().toVector3(), ((StartingPoint)cursor.getFirstVisitedVertex().getRule()).getRadius(), getCursorColor()));
            dynamicShapes.add(new Circle(getOppositeVertex(cursor.getFirstVisitedVertex()).getPosition().toVector3(), ((StartingPoint)getOppositeVertex(cursor.getFirstVisitedVertex()).getRule()).getRadius(), getCursorColor()));

            // It is guaranteed that edges[i - 1].to == edges[i].from
            ArrayList<Edge> visitedEdges = cursor.getVisitedEdges(false);
            if(visitedEdges.size() == 0) return;
            for(int i = 0; i < visitedEdges.size(); i++){
                Edge edge = visitedEdges.get(i);
                Edge oppositeEdge = getOppositeEdge(edge);

                oppositeEdge.proportion = edge.proportion;

                dynamicShapes.add(new Circle(new Vector3(edge.from.x, edge.from.y, 0), getPathWidth() * 0.5f, getCursorColor()));
                dynamicShapes.add(new Circle(new Vector3(oppositeEdge.from.x, oppositeEdge.from.y, 0), getPathWidth() * 0.5f, getCursorColor()));
                dynamicShapes.add(new Rectangle(edge.getProportionMiddlePoint().toVector3(), edge.getLength() * edge.proportion, getPathWidth(), edge.getAngle(), getCursorColor()));
                dynamicShapes.add(new Rectangle(oppositeEdge.getProportionMiddlePoint().toVector3(), oppositeEdge.getLength() * oppositeEdge.proportion, getPathWidth(), oppositeEdge.getAngle(), getCursorColor()));

                if(i == visitedEdges.size() - 1){
                    dynamicShapes.add(new Circle(new Vector3(edge.getProportionPoint().x, edge.getProportionPoint().y, 0), getPathWidth() * 0.5f, getCursorColor()));
                    dynamicShapes.add(new Circle(new Vector3(oppositeEdge.getProportionPoint().x, oppositeEdge.getProportionPoint().y, 0), getPathWidth() * 0.5f, getCursorColor()));
                }
            }
        }
    }

    @Override
    public void addStartingPoint(int x, int y){
        super.addStartingPoint(x, y);
        if(symmetryType == SymmetryType.VLINE) super.addStartingPoint(width - x, y);
        else if(symmetryType == SymmetryType.POINT) super.addStartingPoint(width - x, height - y);
    }

    @Override
    public Edge addEndingPoint(int x, int y){
        Edge edge1 = super.addEndingPoint(x, y);
        Edge edge2 = null;
        if(symmetryType == SymmetryType.VLINE) edge2 = super.addEndingPoint(width - x, y);
        else if(symmetryType == SymmetryType.POINT) edge2 = super.addEndingPoint(width - x, height - y);

        // edge1 and edge2 are already opposite each other (check super.addEndingPoint)
        if(edge2 != null){
            oppositeVertex.put(edge1.to.index, edge2.to);
            oppositeVertex.put(edge2.to.index, edge1.to);

            oppositeEdge.put(edge1.index, edge2);
            oppositeEdge.put(edge2.index, edge1);
        }
        return edge1;
    }

    public Vertex getOppositeVertex(Vertex vertex){
        if(oppositeVertex.containsKey(vertex.index)) return oppositeVertex.get(vertex.index);
        return null;
    }

    public Edge getOppositeEdge(Edge edge){
        if(oppositeEdge.containsKey(edge.index)){
            if(edges.get(edge.index).from == edge.from) return oppositeEdge.get(edge.index);
            else return oppositeEdge.get(edge.index).reverse();
        }
        return null;
    }

    @Override
    protected Cursor createCursor(Vertex start){
        return new SymmetryCursor(this, start);
    }
}
