package com.aren.thewitnesspuzzle.puzzle.rules;

import android.graphics.Color;

import com.aren.thewitnesspuzzle.graphics.shape.HexagonShape;
import com.aren.thewitnesspuzzle.graphics.shape.Shape;
import com.aren.thewitnesspuzzle.math.Vector3;
import com.aren.thewitnesspuzzle.puzzle.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.cursor.SymmetryCursor;
import com.aren.thewitnesspuzzle.puzzle.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.graph.Tile;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class HexagonRule extends SymmetricColorable {

    public static final float Z_INDEX_NORMAL = 0f;
    public static final float Z_INDEX_FLOAT = 0.1f;

    public HexagonRule() {
        super();
    }

    public HexagonRule(SymmetricColor symmetricColor) {
        super(symmetricColor);
    }

    @Override
    public Shape generateShape() {
        if (getGraphElement() instanceof Tile) return null;
        if (hasSymmetricColor())
            return new HexagonShape(new Vector3(getGraphElement().x, getGraphElement().y, getPuzzle().getGame().isEditorMode() ? Z_INDEX_FLOAT : Z_INDEX_NORMAL), getPuzzle().getPathWidth() * 0.4f, getSymmetricColor().getRGB());
        return new HexagonShape(new Vector3(getGraphElement().x, getGraphElement().y, getPuzzle().getGame().isEditorMode() ? Z_INDEX_FLOAT : Z_INDEX_NORMAL), getPuzzle().getPathWidth() * 0.4f, Color.BLACK);
    }

    @Override
    public boolean validateLocally(Cursor cursor) {
        if (getGraphElement() instanceof Edge) {
            if (!cursor.containsEdge((Edge) getGraphElement())) return false;
            if (hasSymmetricColor() && cursor instanceof SymmetryCursor && ((SymmetryCursor) cursor).hasSymmetricColor()) {
                return ((SymmetryCursor) cursor).getSymmetricColor((Edge) getGraphElement()) == getSymmetricColor();
            }
            return true;
        }
        if (getGraphElement() instanceof Vertex) {
            if (!cursor.containsVertex((Vertex) getGraphElement())) return false;
            if (hasSymmetricColor() && cursor instanceof SymmetryCursor && ((SymmetryCursor) cursor).hasSymmetricColor()) {
                return ((SymmetryCursor) cursor).getSymmetricColor((Vertex) getGraphElement()) == getSymmetricColor();
            }
            return true;
        }
        return true;
    }

    public static void generate(Cursor solution, Random random, float spawnRate) {
        if (solution instanceof SymmetryCursor && ((SymmetryCursor) solution).hasSymmetricColor()) {
            SymmetryCursor symmetrySolution = (SymmetryCursor) solution;

            ArrayList<Vertex> vertices = new ArrayList<>();
            for (Vertex vertex : symmetrySolution.getVisitedVertices()) {
                if (vertex.getRule() == null) vertices.add(vertex);

                Vertex opposite = symmetrySolution.getPuzzle().getOppositeVertex(vertex);
                if (opposite.getRule() == null) vertices.add(opposite);
            }

            int hexagonVertexCount = (int) (vertices.size() * spawnRate);

            Collections.shuffle(vertices, random);

            for (int i = 0; i < hexagonVertexCount; i++) {
                if (random.nextFloat() > 0.8f) vertices.get(i).setRule(new HexagonRule());
                else
                    vertices.get(i).setRule(new HexagonRule(symmetrySolution.getSymmetricColor(vertices.get(i))));
            }
        } else {
            ArrayList<Vertex> vertices = new ArrayList<>();
            for (Vertex vertex : solution.getVisitedVertices()) {
                if (vertex.getRule() == null) vertices.add(vertex);
            }

            int hexagonVertexCount = (int) (vertices.size() * spawnRate);

            Collections.shuffle(vertices, random);

            for (int i = 0; i < hexagonVertexCount; i++) {
                vertices.get(i).setRule(new HexagonRule());
            }
        }
    }
}
