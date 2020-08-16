package com.aren.thewitnesspuzzle.puzzle.factory;

import com.aren.thewitnesspuzzle.puzzle.ColorFactory;
import com.aren.thewitnesspuzzle.puzzle.Game;
import com.aren.thewitnesspuzzle.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.cursor.area.GridAreaSplitter;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.Color;
import com.aren.thewitnesspuzzle.puzzle.rules.EliminationRule;
import com.aren.thewitnesspuzzle.puzzle.rules.SquareRule;
import com.aren.thewitnesspuzzle.puzzle.walker.RandomGridWalker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SimpleSquareEliminationPuzzleFactory implements PuzzleFactory {
    @Override
    public Puzzle generate(Game game, Random random) {
        GridPuzzle puzzle = new GridPuzzle(game, 4, 4);

        ColorFactory.setRandomColor(puzzle);

        puzzle.addStartingPoint(0, 0);
        puzzle.addEndingPoint(4, 4);

        RandomGridWalker walker = new RandomGridWalker(puzzle, random, 10, 0, 0, 4, 4);
        ArrayList<Vertex> vertexPositions = walker.getResult();

        Cursor cursor = new Cursor(puzzle, vertexPositions, null);

        GridAreaSplitter splitter = new GridAreaSplitter(cursor);
        splitter.assignAreaColorRandomly(random, Arrays.asList(Color.RED, Color.LIME, Color.CYAN));

        SquareRule.generate(splitter, random, 0.8f);
        EliminationRule.generateFakeSquare(splitter, random, Arrays.asList(Color.RED, Color.LIME, Color.CYAN));

        puzzle.setCursor(cursor);

        return puzzle;
    }

    @Override
    public Difficulty getDifficulty() {
        return Difficulty.NORMAL;
    }
}
