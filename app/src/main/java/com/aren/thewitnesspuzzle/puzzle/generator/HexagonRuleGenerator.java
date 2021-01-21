package com.aren.thewitnesspuzzle.puzzle.generator;

import com.aren.thewitnesspuzzle.core.cursor.Cursor;
import com.aren.thewitnesspuzzle.core.cursor.SymmetryCursor;
import com.aren.thewitnesspuzzle.core.graph.Edge;
import com.aren.thewitnesspuzzle.core.graph.Vertex;
import com.aren.thewitnesspuzzle.core.rules.HexagonRule;
import com.aren.thewitnesspuzzle.puzzle.factory.spawn.SpawnByRate;
import com.aren.thewitnesspuzzle.puzzle.factory.spawn.SpawnSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HexagonRuleGenerator {

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
        for(Vertex vertex : spawnSelectorCyan.select(solution.getVisitedVerticesWithNoRule(), random)){
            vertex.setRule(new HexagonRule(solution.getSymmetricColor(vertex)));
        }

        List<Vertex> oppositeVertices = new ArrayList<>();
        for(Vertex vertex : solution.getVisitedVertices()){
            Vertex opposite = solution.getPuzzle().getOppositeVertex(vertex);
            if(opposite.getRule() == null)
                oppositeVertices.add(opposite);
        }
        for(Vertex vertex : spawnSelectorYellow.select(oppositeVertices, random)){
            vertex.setRule(new HexagonRule(solution.getSymmetricColor(vertex)));
        }

        for(Vertex vertex : spawnSelectorHex.select(solution.getVisitedVerticesWithNoRule(), random)){
            vertex.setRule(new HexagonRule());
        }
    }

}
