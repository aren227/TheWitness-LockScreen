package com.aren.thewitnesspuzzle.puzzle.factory;

import android.content.Context;

import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.color.PalettePreset;
import com.aren.thewitnesspuzzle.puzzle.rules.BlocksRule;
import com.aren.thewitnesspuzzle.puzzle.rules.TrianglesRule;

import java.util.Random;

public class TriangleLockPuzzleFactory extends PuzzleFactory {
    public TriangleLockPuzzleFactory(Context context) {
        super(context);
    }

    @Override
    public Puzzle generate(Game game, Random random) {
        GridPuzzle puzzle = new GridPuzzle(game, PalettePreset.get("General_Panel"), 1, 1);

        puzzle.addStartingPoint(0, 0);
        puzzle.addEndingPoint(1, 1);

        puzzle.getTileAt(0, 0).setRule(new TrianglesRule(2));

        return puzzle;
    }

    @Override
    public Difficulty getDifficulty(){
        return Difficulty.ALWAYS_SOLVABLE;
    }

    @Override
    public String getName() {
        return "Lock #5";
    }
}
