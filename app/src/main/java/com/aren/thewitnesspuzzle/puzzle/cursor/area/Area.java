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

    public List<Rule> validate(Cursor cursor){
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

        if(eliminationRules.size() == 0){
            areaErrors.addAll(localErrors);
            return areaErrors;
        }
        else if(eliminationRules.size() == 1){
            if(localErrors.size() + areaErrors.size() == 0){
                return Arrays.asList((Rule)eliminationRules.get(0));
            }
            else if(localErrors.size() + areaErrors.size() == 1){
                return new ArrayList<>();
            }
            else if(localErrors.size() == 0){
                List<Rule> newAreaErrors = new ArrayList<>();
                for(Rule rule : areaErrors){
                    rule.eliminated = true;

                    newAreaErrors.clear();
                    newAreaErrors.addAll(Square.areaValidate(this));
                    newAreaErrors.addAll(Sun.areaValidate(this));
                    newAreaErrors.addAll(Block.areaValidate(this));

                    if(newAreaErrors.size() == 0) break;

                    rule.eliminated = false;
                }
                return newAreaErrors;
            }
            else{
                localErrors.get(0).eliminated = true;
                areaErrors.addAll(localErrors);
                return areaErrors;
            }
        }

        //TODO: I think it's undefined behaviour. Can elimination symbols cancel each other?
        throw new RuntimeException("Multiple elimination symbols in the same area are not supported.");
    }
}
