package com.aren.thewitnesspuzzle.puzzle.cursor.area;

import android.util.Log;

import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.graph.Tile;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.BlocksRule;
import com.aren.thewitnesspuzzle.puzzle.rules.Color;
import com.aren.thewitnesspuzzle.puzzle.rules.EliminationRule;
import com.aren.thewitnesspuzzle.puzzle.rules.Rule;
import com.aren.thewitnesspuzzle.puzzle.rules.SquareRule;
import com.aren.thewitnesspuzzle.puzzle.rules.SunRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Area {

    public int id;
    public Color color;
    public int colorIndex;

    public Puzzle puzzle;

    public List<Tile> tiles;
    public List<Edge> edges;
    public List<Vertex> vertices;
    public boolean edgesAndVerticesCalculated = false;

    public Area(Puzzle puzzle) {
        this.puzzle = puzzle;
        tiles = new ArrayList<>();
        edges = new ArrayList<>();
        vertices = new ArrayList<>();
    }

    private void calculateEdgesAndVertices(Cursor cursor){
        if(edgesAndVerticesCalculated) return;
        edgesAndVerticesCalculated = true;
        for(Tile tile : tiles){
            for(Edge edge : tile.edges){
                if(!cursor.containsEdge(edge) && !edges.contains(edge)) edges.add(edge);
            }
        }
        for(Edge edge : edges){
            if(!cursor.containsVertex(edge.from) && !vertices.contains(edge.from)) vertices.add(edge.from);
            if(!cursor.containsVertex(edge.to) && !vertices.contains(edge.to)) vertices.add(edge.to);
        }
    }

    public AreaValidationResult validate(Cursor cursor){
        calculateEdgesAndVertices(cursor);

        for(Rule rule : getAllRules()){
            rule.eliminated = false;
        }

        List<Rule> localErrors = new ArrayList<>();

        // Local validation
        for(Rule rule : getAllRules()){
            if(!rule.validateLocally(cursor)) localErrors.add(rule);
        }

        List<Rule> areaErrors = new ArrayList<>();
        areaErrors.addAll(SquareRule.areaValidate(this));
        areaErrors.addAll(SunRule.areaValidate(this));
        areaErrors.addAll(BlocksRule.areaValidate(this));

        List<EliminationRule> eliminationRules = new ArrayList<>();
        for(Tile tile : tiles){
            if(tile.getRule() instanceof EliminationRule){
                eliminationRules.add((EliminationRule)tile.getRule());
            }
        }

        AreaValidationResult result = new AreaValidationResult(this);
        result.originalErrors.addAll(localErrors);
        result.originalErrors.addAll(areaErrors);

        //FIXME: Dirty code
        if(eliminationRules.size() == 0){
            return result;
        }
        else if(eliminationRules.size() == 1){
            if(localErrors.size() + areaErrors.size() == 0){
                result.originalErrors.add(eliminationRules.get(0));
                return result;
            }
            else if(localErrors.size() + areaErrors.size() == 1){
                result.eliminated = true;
                result.originalErrors.get(0).eliminated = true;
                return result;
            }
            // localErrors.size() + areaErrors.size() >= 2
            else if(localErrors.size() == 0){
                // Shuffle area errors to randomly eliminate symbol when validation is failed
                Collections.shuffle(areaErrors);

                result.eliminated = true;
                for(Rule rule : areaErrors){
                    rule.eliminated = true;

                    result.newErrors.clear();
                    result.newErrors.addAll(SquareRule.areaValidate(this));
                    result.newErrors.addAll(SunRule.areaValidate(this));
                    result.newErrors.addAll(BlocksRule.areaValidate(this));

                    if(result.newErrors.size() == 0){
                        return result;
                    }

                    rule.eliminated = false;
                }
                // Failed. Mark last rule as eliminated
                areaErrors.get(areaErrors.size() - 1).eliminated = true;
                return result;
            }
            else{
                result.eliminated = true;
                localErrors.get(0).eliminated = true;
                for(int i = 1; i < localErrors.size(); i++) result.newErrors.add(localErrors.get(i));
                result.newErrors.addAll(areaErrors);
                return result;
            }
        }

        //TODO: I think it's undefined behaviour. Can elimination symbols cancel each other?
        throw new RuntimeException("Multiple elimination symbols in the same area are not supported.");
    }

    public List<Rule> getAllRules(){
        List<Rule> rules = new ArrayList<>();
        for(Tile tile : tiles){
            if(tile.getRule() != null) rules.add(tile.getRule());
        }
        for(Edge edge : edges){
            if(edge.getRule() != null) rules.add(edge.getRule());
        }
        for(Vertex vertex : vertices){
            if(vertex.getRule() != null) rules.add(vertex.getRule());
        }
        return rules;
    }

    public List<Vertex> getVertices(Cursor cursor){
        calculateEdgesAndVertices(cursor);
        return new ArrayList<>(vertices);
    }

    public List<Edge> getEdges(Cursor cursor){
        calculateEdgesAndVertices(cursor);
        return new ArrayList<>(edges);
    }

    public class AreaValidationResult{

        public Area area;
        public List<Rule> originalErrors = new ArrayList<>();
        public boolean eliminated = false;
        public List<Rule> newErrors = new ArrayList<>();

        public AreaValidationResult(Area area){
            this.area = area;
        }

    }
}
