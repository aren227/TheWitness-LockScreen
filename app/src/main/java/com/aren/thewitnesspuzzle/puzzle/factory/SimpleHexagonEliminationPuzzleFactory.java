package com.aren.thewitnesspuzzle.puzzle.factory;

import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.color.PalettePreset;
import com.aren.thewitnesspuzzle.puzzle.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.cursor.area.GridAreaSplitter;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.BrokenLineRule;
import com.aren.thewitnesspuzzle.puzzle.rules.EliminationRule;
import com.aren.thewitnesspuzzle.puzzle.rules.HexagonRule;
import com.aren.thewitnesspuzzle.puzzle.walker.RandomGridWalker;

import java.util.ArrayList;
import java.util.Random;

public class SimpleHexagonEliminationPuzzleFactory extends PuzzleFactory {
    @Override
    public Puzzle generate(Game game, Random random) {
        GridPuzzle puzzle = new GridPuzzle(game, PalettePreset.get("Quarry_1"), 3, 3);

        puzzle.addStartingPoint(0, 0);
        puzzle.addEndingPoint(3, 3);

        RandomGridWalker walker = new RandomGridWalker(puzzle, random, 3, 0, 0, 3, 3);
        ArrayList<Vertex> vertexPositions = walker.getResult();

        Cursor cursor = new Cursor(puzzle, vertexPositions, null);

        GridAreaSplitter splitter = new GridAreaSplitter(cursor);

        BrokenLineRule.generate(cursor, random, 0.25f);
        HexagonRule.generate(cursor, random, 0.75f);
        EliminationRule.generateFakeHexagon(splitter, random);

        return puzzle;
    }

    @Override
    public Difficulty getDifficulty() {
        return Difficulty.EASY;
    }

    @Override
    public String getName(){
        return "Quarry #1";
    }
}
