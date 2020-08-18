package com.aren.thewitnesspuzzle.puzzle;

import com.aren.thewitnesspuzzle.graphics.shape.CircleShape;
import com.aren.thewitnesspuzzle.graphics.shape.RectangleShape;
import com.aren.thewitnesspuzzle.math.Vector3;
import com.aren.thewitnesspuzzle.puzzle.color.PuzzleColorPalette;
import com.aren.thewitnesspuzzle.puzzle.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.cursor.SymmetryCursor;
import com.aren.thewitnesspuzzle.puzzle.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.graph.EdgeProportion;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.StartingPointRule;
import com.aren.thewitnesspuzzle.puzzle.rules.SymmetricColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GridSymmetryPuzzle extends GridPuzzle {

    public enum SymmetryType {VLINE, POINT}

    protected SymmetryType symmetryType;
    protected boolean hasSymmetricColor;

    protected Map<Integer, Vertex> oppositeVertex;
    protected Map<Integer, Edge> oppositeEdge;

    protected Cursor oppositeCursor;

    public GridSymmetryPuzzle(Game game, PuzzleColorPalette color, int width, int height, SymmetryType symmetryType, boolean hasSymmetricColor) {
        super(game, color, width, height);

        this.symmetryType = symmetryType;
        this.hasSymmetricColor = hasSymmetricColor;

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
                    oppositeEdge.put(getEdgeAt(i, j, true).index, getEdgeAt(width - i - 1, j, true));
                }
                else if(symmetryType == SymmetryType.POINT){
                    oppositeEdge.put(getEdgeAt(i, j, true).index, getEdgeAt(width - i - 1, height - j, true));
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
                    oppositeEdge.put(getEdgeAt(i, j, false).index, getEdgeAt(width - i, height - j - 1, false));
                }
            }
        }
    }

    public SymmetryType getSymmetryType(){
        return symmetryType;
    }

    public boolean hasSymmetricColor(){
        return hasSymmetricColor;
    }

    @Override
    public void calcDynamicShapes(){
        dynamicShapes.clear();

        if(cursor != null){
            dynamicShapes.add(new CircleShape(cursor.getFirstVisitedVertex().getPosition().toVector3(), ((StartingPointRule)cursor.getFirstVisitedVertex().getRule()).getRadius(), hasSymmetricColor ? SymmetricColor.CYAN.getRGB() : getColorPalette().getCursorColor()));
            dynamicShapes.add(new CircleShape(getOppositeVertex(cursor.getFirstVisitedVertex()).getPosition().toVector3(), ((StartingPointRule)getOppositeVertex(cursor.getFirstVisitedVertex()).getRule()).getRadius(), hasSymmetricColor ? SymmetricColor.YELLOW.getRGB() : getColorPalette().getCursorColor()));

            ArrayList<EdgeProportion> visitedEdges = cursor.getVisitedEdgesWithProportion(true);
            if(visitedEdges.size() == 0) return;
            for(int i = 0; i < visitedEdges.size(); i++){
                EdgeProportion edgeProportion = visitedEdges.get(i);
                EdgeProportion oppositeEdgeProportion = getOppositeEdgeProportion(edgeProportion);
                dynamicShapes.add(new CircleShape(new Vector3(edgeProportion.getProportionPoint().x, edgeProportion.getProportionPoint().y, 0), getPathWidth() * 0.5f, hasSymmetricColor ? SymmetricColor.CYAN.getRGB() : getColorPalette().getCursorColor()));
                dynamicShapes.add(new CircleShape(new Vector3(oppositeEdgeProportion.getProportionPoint().x, oppositeEdgeProportion.getProportionPoint().y, 0), getPathWidth() * 0.5f, hasSymmetricColor ? SymmetricColor.YELLOW.getRGB() : getColorPalette().getCursorColor()));
                dynamicShapes.add(new RectangleShape(edgeProportion.getProportionMiddlePoint().toVector3(), edgeProportion.getProportionLength(), getPathWidth(), edgeProportion.edge.getAngle(), hasSymmetricColor ? SymmetricColor.CYAN.getRGB() : getColorPalette().getCursorColor()));
                dynamicShapes.add(new RectangleShape(oppositeEdgeProportion.getProportionMiddlePoint().toVector3(), oppositeEdgeProportion.getProportionLength(), getPathWidth(), oppositeEdgeProportion.edge.getAngle(), hasSymmetricColor ? SymmetricColor.YELLOW.getRGB() : getColorPalette().getCursorColor()));
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
            return oppositeEdge.get(edge.index);
        }
        return null;
    }

    public EdgeProportion getOppositeEdgeProportion(EdgeProportion edgeProportion){
        EdgeProportion opposite = new EdgeProportion(getOppositeEdge(edgeProportion.edge));
        opposite.proportion = edgeProportion.proportion;
        opposite.reverse = edgeProportion.reverse;
        if(symmetryType == SymmetryType.VLINE){
            if(opposite.edge.isHorizontal) opposite.reverse = !opposite.reverse;
            return opposite;
        }
        else if(symmetryType == SymmetryType.POINT){
            opposite.reverse = !opposite.reverse;
            return opposite;
        }
        return null;
    }

    @Override
    protected Cursor createCursor(Vertex start){
        return new SymmetryCursor(this, start);
    }
}
