package com.aren.thewitnesspuzzle.puzzle.base;

import com.aren.thewitnesspuzzle.puzzle.base.color.PuzzleColorPalette;
import com.aren.thewitnesspuzzle.puzzle.base.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.base.cursor.SymmetryCursor;
import com.aren.thewitnesspuzzle.puzzle.base.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.base.graph.EdgeProportion;
import com.aren.thewitnesspuzzle.puzzle.base.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.base.rules.EndingPointRule;

import java.util.HashMap;
import java.util.Map;

public class GridSymmetryPuzzle extends GridPuzzle {

    public enum SymmetryType { VLINE, POINT }

    protected SymmetryType symmetryType;
    protected boolean hasSymmetricColor;

    protected Map<Integer, Vertex> oppositeVertex;
    protected Map<Integer, Edge> oppositeEdge;

    protected Cursor oppositeCursor;

    public GridSymmetryPuzzle(PuzzleColorPalette color, int width, int height, SymmetryType symmetryType, boolean hasSymmetricColor) {
        super(color, width, height);

        this.symmetryType = symmetryType;
        this.hasSymmetricColor = hasSymmetricColor;

        oppositeVertex = new HashMap<>();
        oppositeEdge = new HashMap<>();

        for (int i = 0; i <= width; i++) {
            for (int j = 0; j <= height; j++) {
                if (symmetryType == SymmetryType.VLINE) {
                    oppositeVertex.put(getVertexAt(i, j).index, getVertexAt(width - i, j));
                } else if (symmetryType == SymmetryType.POINT) {
                    oppositeVertex.put(getVertexAt(i, j).index, getVertexAt(width - i, height - j));
                }
            }
        }

        // Horizontal lines
        for (int i = 0; i < width; i++) {
            for (int j = 0; j <= height; j++) {
                if (symmetryType == SymmetryType.VLINE) {
                    oppositeEdge.put(getEdgeAt(i, j, true).index, getEdgeAt(width - i - 1, j, true));
                } else if (symmetryType == SymmetryType.POINT) {
                    oppositeEdge.put(getEdgeAt(i, j, true).index, getEdgeAt(width - i - 1, height - j, true));
                }
            }
        }

        // Vertical lines
        for (int i = 0; i <= width; i++) {
            for (int j = 0; j < height; j++) {
                if (symmetryType == SymmetryType.VLINE) {
                    oppositeEdge.put(getEdgeAt(i, j, false).index, getEdgeAt(width - i, j, false));
                } else if (symmetryType == SymmetryType.POINT) {
                    oppositeEdge.put(getEdgeAt(i, j, false).index, getEdgeAt(width - i, height - j - 1, false));
                }
            }
        }
    }

    public SymmetryType getSymmetryType() {
        return symmetryType;
    }

    public boolean hasSymmetricColor() {
        return hasSymmetricColor;
    }

    @Override
    public void addStartingPoint(int x, int y) {
        super.addStartingPoint(x, y);
        if (symmetryType == SymmetryType.VLINE) super.addStartingPoint(width - x, y);
        else if (symmetryType == SymmetryType.POINT) super.addStartingPoint(width - x, height - y);
    }

    @Override
    public Edge addEndingPoint(int x, int y) {
        Edge edge1 = super.addEndingPoint(x, y);
        Edge edge2 = null;
        if (symmetryType == SymmetryType.VLINE) edge2 = super.addEndingPoint(width - x, y);
        else if (symmetryType == SymmetryType.POINT)
            edge2 = super.addEndingPoint(width - x, height - y);

        // edge1 and edge2 are already opposite each other (check super.addEndingPoint)
        if (edge2 != null) {
            oppositeVertex.put(edge1.to.index, edge2.to);
            oppositeVertex.put(edge2.to.index, edge1.to);

            oppositeEdge.put(edge1.index, edge2);
            oppositeEdge.put(edge2.index, edge1);
        }
        return edge1;
    }

    public Vertex getOppositeVertex(Vertex vertex) {
        if (oppositeVertex.containsKey(vertex.index)) return oppositeVertex.get(vertex.index);
        return null;
    }

    public Edge getOppositeEdge(Edge edge) {
        if (oppositeEdge.containsKey(edge.index)) {
            return oppositeEdge.get(edge.index);
        }
        return null;
    }

    public EdgeProportion getOppositeEdgeProportion(EdgeProportion edgeProportion) {
        EdgeProportion opposite = new EdgeProportion(getOppositeEdge(edgeProportion.edge));
        opposite.proportion = edgeProportion.proportion;
        opposite.reverse = edgeProportion.reverse;
        if (symmetryType == SymmetryType.VLINE) {
            if (opposite.edge.isHorizontal()) opposite.reverse = !opposite.reverse;
            return opposite;
        } else if (symmetryType == SymmetryType.POINT) {
            // If it contains ending point, the direction of two opposite edges are already symmetric
            if (!(edgeProportion.to().getRule() instanceof EndingPointRule)) {
                opposite.reverse = !opposite.reverse;
            }
            return opposite;
        }
        return null;
    }

    @Override
    public Cursor createCursor(Vertex start) {
        return new SymmetryCursor(this, start);
    }
}
