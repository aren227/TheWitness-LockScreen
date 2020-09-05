package com.aren.thewitnesspuzzle.puzzle.factory;

import android.content.Context;

import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.puzzle.color.PalettePreset;
import com.aren.thewitnesspuzzle.puzzle.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.rules.BrokenLineRule;
import com.aren.thewitnesspuzzle.puzzle.walker.RandomGridTreeWalker;

import java.util.Random;

public class MazePuzzleFactory extends PuzzleFactory {

    public MazePuzzleFactory(Context context) {
        super(context);
    }

    @Override
    public GridPuzzle generate(Game game, Random random) {
        GridPuzzle puzzle = new GridPuzzle(game, PalettePreset.get("Entry_1"), 6, 6);

        puzzle.addStartingPoint(0, 0);
        puzzle.addEndingPoint(puzzle.getWidth(), puzzle.getHeight());

        RandomGridTreeWalker walker = RandomGridTreeWalker.getMaziest(puzzle.getWidth(), puzzle.getHeight(), random, 5, 0, 0, 6, 6, 3);

        Cursor cursor = new Cursor(puzzle, walker.getResult(puzzle, 6, 6), null);

        BrokenLineRule.generate(puzzle, walker, random, 0.9f);

        return puzzle;
    }

    @Override
    public Difficulty getDifficulty() {
        return Difficulty.VERY_EASY;
    }

    @Override
    public String getName() {
        return "Entry Area #2";
    }
}
