package com.aren.thewitnesspuzzle.puzzle.factory;

import com.aren.thewitnesspuzzle.puzzle.ColorFactory;
import com.aren.thewitnesspuzzle.puzzle.Game;
import com.aren.thewitnesspuzzle.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.BrokenLineRule;
import com.aren.thewitnesspuzzle.puzzle.walker.RandomGridWalker;

import java.util.ArrayList;
import java.util.Random;

public class SimpleMazePuzzleFactory implements PuzzleFactory {

    @Override
    public GridPuzzle generate(Game game, Random random) {
        GridPuzzle puzzle = new GridPuzzle(game, 6, 6);

        ColorFactory.setRandomColor(puzzle);

        puzzle.addStartingPoint(0, 0);
        puzzle.addEndingPoint(puzzle.getWidth(), puzzle.getHeight());

        RandomGridWalker walker = new RandomGridWalker(puzzle, random, 0, 0, 6, 6);
        ArrayList<Vertex> vertexPositions = walker.getResult();

        Cursor cursor = new Cursor(puzzle, vertexPositions, null);

        BrokenLineRule.generate(cursor, random, 0.8f);

        return puzzle;
    }

    @Override
    public Difficulty getDifficulty() {
        return Difficulty.VERY_EASY;
    }
}
