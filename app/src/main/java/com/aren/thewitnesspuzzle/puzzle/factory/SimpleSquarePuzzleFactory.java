package com.aren.thewitnesspuzzle.puzzle.factory;

import com.aren.thewitnesspuzzle.puzzle.Game;
import com.aren.thewitnesspuzzle.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.color.PalettePreset;
import com.aren.thewitnesspuzzle.puzzle.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.cursor.area.GridAreaSplitter;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.Color;
import com.aren.thewitnesspuzzle.puzzle.rules.SquareRule;
import com.aren.thewitnesspuzzle.puzzle.walker.RandomGridWalker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SimpleSquarePuzzleFactory extends PuzzleFactory{

    @Override
    public Puzzle generate(Game game, Random random) {
        GridPuzzle puzzle = new GridPuzzle(game, PalettePreset.get("Blue_1"), 3, 3);

        List<Vertex> vertices = puzzle.getBorderVertices();
        Collections.shuffle(vertices, random);

        Vertex start = vertices.get(0);
        Vertex end = vertices.get(1);

        puzzle.addStartingPoint(start.gridPosition.x, start.gridPosition.y);
        puzzle.addEndingPoint(end.gridPosition.x, end.gridPosition.y);

        RandomGridWalker walker = new RandomGridWalker(puzzle, random, 10, start.gridPosition.x, start.gridPosition.y, end.gridPosition.x, end.gridPosition.y);
        ArrayList<Vertex> vertexPositions = walker.getResult();

        Cursor cursor = new Cursor(puzzle, vertexPositions, null);

        GridAreaSplitter splitter = new GridAreaSplitter(cursor);
        splitter.assignAreaColorRandomly(random, Arrays.asList(Color.WHITE, Color.BLACK));

        SquareRule.generate(splitter, random, 1f);

        return puzzle;
    }

    @Override
    public Difficulty getDifficulty(){
        return Difficulty.VERY_EASY;
    }

    @Override
    public String getName(){
        return "Blue Panel";
    }
}
