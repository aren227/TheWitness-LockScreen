package com.aren.thewitnesspuzzle.puzzle.factory;

import android.content.Context;

import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.color.PalettePreset;
import com.aren.thewitnesspuzzle.puzzle.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.cursor.area.GridAreaSplitter;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.BlocksRule;
import com.aren.thewitnesspuzzle.puzzle.rules.BrokenLineRule;
import com.aren.thewitnesspuzzle.puzzle.rules.Color;
import com.aren.thewitnesspuzzle.puzzle.walker.RandomGridWalker;

import java.util.ArrayList;
import java.util.Random;

public class BlocksRotatableBlocksPuzzleFactory extends PuzzleFactory {
    public BlocksRotatableBlocksPuzzleFactory(Context context) {
        super(context);
    }

    @Override
    public Puzzle generate(Game game, Random random) {
        GridPuzzle puzzle = new GridPuzzle(game, PalettePreset.get("Swamp_3"), 5, 5);

        puzzle.addStartingPoint(0, 0);
        puzzle.addEndingPoint(5, 5);

        RandomGridWalker walker = new RandomGridWalker(puzzle, random, 5, 0, 0, 5, 5);
        ArrayList<Vertex> vertexPositions = walker.getResult();

        Cursor cursor = new Cursor(puzzle, vertexPositions, null);

        GridAreaSplitter splitter = new GridAreaSplitter(cursor);

        BrokenLineRule.generate(cursor, random, 0.2f);
        BlocksRule.generate(splitter, random, Color.YELLOW, 0.4f, 0.5f);

        return puzzle;
    }

    @Override
    public Difficulty getDifficulty() {
        return Difficulty.VERY_HARD;
    }

    @Override
    public String getName() {
        return "Swamp #3";
    }
}
