package com.aren.thewitnesspuzzle.puzzle.rules;

import android.graphics.Color;

import com.aren.thewitnesspuzzle.graphics.shape.HexagonShape;
import com.aren.thewitnesspuzzle.graphics.shape.Shape;
import com.aren.thewitnesspuzzle.math.Vector3;
import com.aren.thewitnesspuzzle.puzzle.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.cursor.SymmetryCursor;
import com.aren.thewitnesspuzzle.puzzle.factory.spawn.SpawnByRate;
import com.aren.thewitnesspuzzle.puzzle.factory.spawn.SpawnSelector;
import com.aren.thewitnesspuzzle.puzzle.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.graph.Tile;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class HexagonRule extends SymmetricColorable {

    public static final float Z_INDEX_NORMAL = 0f;
    public static final float Z_INDEX_FLOAT = 0.1f;

    private int overrideColor = 0;

    public HexagonRule() {
        super();
    }

    public HexagonRule(SymmetricColor symmetricColor) {
        super(symmetricColor);
    }

    @Override
    public Shape generateShape() {
        if (getGraphElement() instanceof Tile) return null;
        if(overrideColor != 0)
            return new HexagonShape(new Vector3(getGraphElement().x, getGraphElement().y, getPuzzle().getGame().isEditorMode() ? Z_INDEX_FLOAT : Z_INDEX_NORMAL), getPuzzle().getPathWidth() * 0.4f, overrideColor);
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

    public void setOverrideColor(int color){
        this.overrideColor = color;
    }

    public static void generate(Cursor solution, Random random, float spawnRate) {
        generate(solution, random, spawnRate, false);
    }

    public static void generate(Cursor solution, Random random, float spawnRate, boolean onEdge) {
        generate(solution, random, new SpawnByRate(spawnRate), onEdge);
    }

    public static void generate(Cursor solution, Random random, SpawnSelector spawnSelector, boolean onEdge){
        if (solution instanceof SymmetryCursor && ((SymmetryCursor) solution).hasSymmetricColor()) {
            SymmetryCursor symmetrySolution = (SymmetryCursor) solution;

            if(onEdge){
                ArrayList<Edge> edges = new ArrayList<>();
                for (Edge edge : symmetrySolution.getFullyVisitedEdges()) {
                    if (edge.getRule() == null) edges.add(edge);

                    Edge opposite = symmetrySolution.getPuzzle().getOppositeEdge(edge);
                    if (opposite.getRule() == null) edges.add(opposite);
                }

                for(Edge edge : spawnSelector.select(edges, random)){
                    if (random.nextFloat() > 0.8f) edge.setRule(new HexagonRule());
                    else
                        edge.setRule(new HexagonRule(symmetrySolution.getSymmetricColor(edge)));
                }
            }
            else{
                ArrayList<Vertex> vertices = new ArrayList<>();
                for (Vertex vertex : symmetrySolution.getVisitedVertices()) {
                    if (vertex.getRule() == null) vertices.add(vertex);

                    Vertex opposite = symmetrySolution.getPuzzle().getOppositeVertex(vertex);
                    if (opposite.getRule() == null) vertices.add(opposite);
                }

                for(Vertex vertex : spawnSelector.select(vertices, random)){
                    if (random.nextFloat() > 0.8f) vertex.setRule(new HexagonRule());
                    else
                        vertex.setRule(new HexagonRule(symmetrySolution.getSymmetricColor(vertex)));
                }
            }
        } else {
            if(onEdge){
                ArrayList<Edge> edges = new ArrayList<>();
                for (Edge edge : solution.getFullyVisitedEdges()) {
                    if (edge.getRule() == null) edges.add(edge);
                }

                for(Edge edge : spawnSelector.select(edges, random)){
                    edge.setRule(new HexagonRule());
                }
            }
            else{
                ArrayList<Vertex> vertices = new ArrayList<>();
                for (Vertex vertex : solution.getVisitedVertices()) {
                    if (vertex.getRule() == null) vertices.add(vertex);
                }

                for(Vertex vertex : spawnSelector.select(vertices, random)){
                    vertex.setRule(new HexagonRule());
                }
            }
        }
    }

    // For the challenge
    public static void generate(SymmetryCursor solution, Random random, SpawnSelector spawnSelectorCyan, SpawnSelector spawnSelectorYellow, SpawnSelector spawnSelectorHex){
        List<Vertex> yellow = new ArrayList<>();
        List<Vertex> all = new ArrayList<>();
        for(Vertex vertex : solution.getVisitedVertices()){
            yellow.add(solution.getPuzzle().getOppositeVertex(vertex));
        }

        all.addAll(solution.getVisitedVertices());
        all.addAll(yellow);

        for(Vertex vertex : spawnSelectorCyan.select(solution.getVisitedVertices(), random)){
            vertex.setRule(new HexagonRule(solution.getSymmetricColor(vertex)));
        }
        for(Vertex vertex : spawnSelectorYellow.select(yellow, random)){
            vertex.setRule(new HexagonRule(solution.getSymmetricColor(vertex)));
        }
        for(Vertex vertex : spawnSelectorHex.select(all, random)){
            vertex.setRule(new HexagonRule());
        }
    }
}
