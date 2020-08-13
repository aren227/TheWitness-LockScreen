package com.aren.thewitnesspuzzle.puzzle.cursor.area;

import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.graph.Tile;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.Block;
import com.aren.thewitnesspuzzle.puzzle.rules.Color;
import com.aren.thewitnesspuzzle.puzzle.rules.Elimination;
import com.aren.thewitnesspuzzle.puzzle.rules.Rule;
import com.aren.thewitnesspuzzle.puzzle.rules.Square;
import com.aren.thewitnesspuzzle.puzzle.rules.Sun;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Area {

    public int id;
    public Color color;
    public int colorIndex;

    public Puzzle puzzle;

    public Set<Tile> tiles;
    public Set<Edge> edges;
    public Set<Vertex> vertices;

    public Area(Puzzle puzzle) {
        this.puzzle = puzzle;
        tiles = new HashSet<>();
        edges = new HashSet<>();
        vertices = new HashSet<>();
    }

    public AreaValidationResult validate(Cursor cursor){
        for(Tile tile : tiles){
            for(Edge edge : tile.edges){
                if(!cursor.containsEdge(edge)) edges.add(edge);
            }
        }
        for(Edge edge : edges){
            if(!cursor.containsVertex(edge.from)) vertices.add(edge.from);
            if(!cursor.containsVertex(edge.to)) vertices.add(edge.to);
        }

        List<Rule> localErrors = new ArrayList<>();

        // Local validation
        for(Tile tile : tiles){
            if(tile.getRule() != null && !tile.getRule().validateLocally(cursor)) localErrors.add(tile.getRule());
        }
        for(Edge edge : edges){
            if(edge.getRule() != null && !edge.getRule().validateLocally(cursor)) localErrors.add(edge.getRule());
        }
        for(Vertex vertex : vertices){
            if(vertex.getRule() != null && !vertex.getRule().validateLocally(cursor)) localErrors.add(vertex.getRule());
        }

        List<Rule> areaErrors = new ArrayList<>();
        areaErrors.addAll(Square.areaValidate(this));
        areaErrors.addAll(Sun.areaValidate(this));
        areaErrors.addAll(Block.areaValidate(this));

        List<Elimination> eliminationRules = new ArrayList<>();
        for(Tile tile : tiles){
            if(tile.getRule() instanceof Elimination){
                eliminationRules.add((Elimination)tile.getRule());
            }
        }

        AreaValidationResult result = new AreaValidationResult();
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
                    result.newErrors.addAll(Square.areaValidate(this));
                    result.newErrors.addAll(Sun.areaValidate(this));
                    result.newErrors.addAll(Block.areaValidate(this));

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

    public class AreaValidationResult{

        public List<Rule> originalErrors = new ArrayList<>();
        public boolean eliminated = false;
        public List<Rule> newErrors = new ArrayList<>();

    }
}
