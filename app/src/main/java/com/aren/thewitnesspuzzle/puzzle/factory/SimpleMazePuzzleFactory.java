package com.aren.thewitnesspuzzle.puzzle.factory;

import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.puzzle.color.PalettePreset;
import com.aren.thewitnesspuzzle.puzzle.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.BrokenLineRule;
import com.aren.thewitnesspuzzle.puzzle.walker.RandomGridWalker;

import java.util.ArrayList;
import java.util.Random;

public class SimpleMazePuzzleFactory extends PuzzleFactory {

    @Override
    public GridPuzzle generate(Game game, Random random) {
        GridPuzzle puzzle = new GridPuzzle(game, PalettePreset.get("Entry_1"), 6, 6);

        puzzle.addStartingPoint(0, 0);
        puzzle.addEndingPoint(puzzle.getWidth(), puzzle.getHeight());

        RandomGridWalker walker = new RandomGridWalker(puzzle, random, 10, 0, 0, 6, 6);
        ArrayList<Vertex> vertexPositions = walker.getResult();

        Cursor cursor = new Cursor(puzzle, vertexPositions, null);

        BrokenLineRule.generate(cursor, random, 0.8f);

        return puzzle;
    }

    @Override
    public Difficulty getDifficulty() {
        return Difficulty.VERY_EASY;
    }

    @Override
    public String getName(){
        return "Entry Area #1";
    }
}
