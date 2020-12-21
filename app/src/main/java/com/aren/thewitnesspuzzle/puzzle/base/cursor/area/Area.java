package com.aren.thewitnesspuzzle.puzzle.base.cursor.area;

import com.aren.thewitnesspuzzle.puzzle.base.PuzzleBase;
import com.aren.thewitnesspuzzle.puzzle.base.rules.RuleBase;
import com.aren.thewitnesspuzzle.puzzle.base.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.base.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.base.graph.Tile;
import com.aren.thewitnesspuzzle.puzzle.base.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.base.rules.BlocksRule;
import com.aren.thewitnesspuzzle.puzzle.base.rules.Color;
import com.aren.thewitnesspuzzle.puzzle.base.rules.EliminationRule;
import com.aren.thewitnesspuzzle.puzzle.base.rules.SquareRule;
import com.aren.thewitnesspuzzle.puzzle.base.rules.SunRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Area {

    public int id;
    public Color color;
    public int colorIndex;

    public PuzzleBase puzzle;

    public List<Tile> tiles;
    public List<Edge> edges;
    public List<Vertex> vertices;
    public boolean edgesAndVerticesCalculated = false;

    public Area(PuzzleBase puzzle) {
        this.puzzle = puzzle;
        tiles = new ArrayList<>();
        edges = new ArrayList<>();
        vertices = new ArrayList<>();
    }

    private void calculateEdgesAndVertices(Cursor cursor) {
        if (edgesAndVerticesCalculated) return;
        edgesAndVerticesCalculated = true;
        for (Tile tile : tiles) {
            for (Edge edge : tile.edges) {
                if (!cursor.containsEdge(edge) && !edges.contains(edge)) edges.add(edge);
            }
        }
        for (Edge edge : edges) {
            if (!cursor.containsVertex(edge.from) && !vertices.contains(edge.from))
                vertices.add(edge.from);
            if (!cursor.containsVertex(edge.to) && !vertices.contains(edge.to))
                vertices.add(edge.to);
        }
    }

    private boolean eliminate(Cursor cursor, List<RuleBase> errors, int idx, int eliminatorCnt, int eliminatorUsed){
        if(eliminatorCnt <= eliminatorUsed || idx >= errors.size()){
            // The half of the unused elimination rules can eliminate the rest of them
            if((eliminatorCnt - eliminatorUsed) % 2 != 0) return false;

            for (RuleBase rule : getAllRules()) {
                if (!rule.validateLocally(cursor)) return false;
            }
            return SunRule.areaValidate(this).size() == 0
                    && SquareRule.areaValidate(this).size() == 0
                    && BlocksRule.areaValidate(this).size() == 0;
        }
        else{
            errors.get(idx).eliminated = true;
            if(eliminate(cursor, errors, idx + 1, eliminatorCnt, eliminatorUsed + 1)) return true;
            errors.get(idx).eliminated = false;

            if(eliminate(cursor, errors, idx + 1, eliminatorCnt, eliminatorUsed)) return true;

            return false;
        }
    }

    // Make random elimination state
    private boolean randomlyEliminate(Cursor cursor, Random random, List<RuleBase> errors, int idx, List<EliminationRule> eliminators, int eliminatorUsed, AreaValidationResult result){
        if(eliminators.size() <= eliminatorUsed || idx >= errors.size()){
            // Force to use all elimination rules
            if(eliminatorUsed != Math.min(eliminators.size(), errors.size())) return false;

            Collections.shuffle(eliminators, random);
            for(int i = eliminatorUsed; i < eliminators.size(); i++){
                // Eliminate themselves
                if(i + 1 < eliminators.size()){
                    i++;
                    eliminatorUsed++;
                    continue;
                }
                eliminators.get(i).eliminated = false;
                result.originalErrors.add(eliminators.get(i));
            }

            // Elimination rule not used
            if(eliminatorUsed == 0) result.eliminated = false;

            for (RuleBase rule : getAllRules()) {
                if (!rule.validateLocally(cursor)) result.newErrors.add(rule);
            }
            result.newErrors.addAll(SquareRule.areaValidate(this));
            result.newErrors.addAll(SunRule.areaValidate(this));
            result.newErrors.addAll(BlocksRule.areaValidate(this));

            return true;
        }
        else{
            if(random.nextFloat() > 0.5f){
                errors.get(idx).eliminated = true;
                if(randomlyEliminate(cursor, random, errors, idx + 1, eliminators, eliminatorUsed + 1, result)) return true;
                errors.get(idx).eliminated = false;

                if(randomlyEliminate(cursor, random, errors, idx + 1, eliminators, eliminatorUsed, result)) return true;

                return false;
            }
            else{
                if(randomlyEliminate(cursor, random, errors, idx + 1, eliminators, eliminatorUsed, result)) return true;

                errors.get(idx).eliminated = true;
                if(randomlyEliminate(cursor, random, errors, idx + 1, eliminators, eliminatorUsed + 1, result)) return true;
                errors.get(idx).eliminated = false;

                return false;
            }
        }
    }

    public AreaValidationResult validate(Cursor cursor) {
        calculateEdgesAndVertices(cursor);

        for (RuleBase rule : getAllRules()) {
            rule.eliminated = false;
        }

        List<RuleBase> errors = new ArrayList<>();

        for (RuleBase rule : getAllRules()) {
            if (!rule.validateLocally(cursor)) errors.add(rule);
        }
        errors.addAll(SquareRule.areaValidate(this));
        errors.addAll(SunRule.areaValidate(this));
        errors.addAll(BlocksRule.areaValidate(this));

        List<EliminationRule> eliminationRules = new ArrayList<>();
        for (Tile tile : tiles) {
            if (tile.getRule() instanceof EliminationRule) {
                eliminationRules.add((EliminationRule) tile.getRule());
            }
        }

        AreaValidationResult result = new AreaValidationResult(this);
        result.originalErrors.addAll(errors);

        if(eliminationRules.size() == 0) return result;

        result.eliminated = true;

        // All elimination symbols are eliminated themselves (e.i. sun rules can't pair with them).
        for(EliminationRule eliminationRule : eliminationRules){
            eliminationRule.eliminated = true;
        }

        // Success
        if(eliminate(cursor, errors, 0, eliminationRules.size(), 0)){
            return result;
        }

        // Failed
        result.newErrors.clear();
        randomlyEliminate(cursor, new Random(), errors, 0, eliminationRules, 0, result);
        return result;
    }

    public List<RuleBase> getAllRules() {
        List<RuleBase> rules = new ArrayList<>();
        for (Tile tile : tiles) {
            if (tile.getRule() != null) rules.add(tile.getRule());
        }
        for (Edge edge : edges) {
            if (edge.getRule() != null) rules.add(edge.getRule());
        }
        for (Vertex vertex : vertices) {
            if (vertex.getRule() != null) rules.add(vertex.getRule());
        }
        return rules;
    }

    public List<Vertex> getVertices(Cursor cursor) {
        calculateEdgesAndVertices(cursor);
        return new ArrayList<>(vertices);
    }

    public List<Edge> getEdges(Cursor cursor) {
        calculateEdgesAndVertices(cursor);
        return new ArrayList<>(edges);
    }

    public class AreaValidationResult {

        public Area area;
        public List<RuleBase> originalErrors = new ArrayList<>();
        public boolean eliminated = false;
        public List<RuleBase> newErrors = new ArrayList<>();

        public AreaValidationResult(Area area) {
            this.area = area;
        }

    }
}
