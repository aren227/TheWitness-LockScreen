package com.aren.thewitnesspuzzle.puzzle.cursor.area;

import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.graph.Tile;
import com.aren.thewitnesspuzzle.puzzle.rules.Color;

import java.util.ArrayList;

public class Area {

    public int id;
    public Color color;
    public int colorIndex;

    public Puzzle puzzle;
    public ArrayList<Tile> tiles;

    public Area(Puzzle puzzle) {
        this.puzzle = puzzle;
        tiles = new ArrayList<>();
    }
}
