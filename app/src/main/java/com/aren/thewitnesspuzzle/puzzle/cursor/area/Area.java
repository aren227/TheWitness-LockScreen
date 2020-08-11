package com.aren.thewitnesspuzzle.puzzle.cursor.area;

import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.graph.Tile;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.Color;
import com.aren.thewitnesspuzzle.puzzle.rules.Rule;
import com.aren.thewitnesspuzzle.puzzle.rules.Square;
import com.aren.thewitnesspuzzle.puzzle.rules.Sun;

import java.util.ArrayList;
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

    public boolean validate(Cursor cursor){
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

        return localErrors.size() == 0 && areaErrors.size() == 0;
    }
}
