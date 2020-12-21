package com.aren.thewitnesspuzzle.puzzle.factory;

import android.content.Context;

import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.base.GridPuzzle;
import com.aren.thewitnesspuzzle.puzzle.base.color.PalettePreset;
import com.aren.thewitnesspuzzle.puzzle.base.rules.TrianglesRule;
import com.aren.thewitnesspuzzle.render.PuzzleRenderer;

import java.util.Random;

public class TriangleLockPuzzleFactory extends PuzzleFactory {
    public TriangleLockPuzzleFactory(Context context) {
        super(context);
    }

    @Override
    public PuzzleRenderer generate(Game game, Random random) {
        GridPuzzle puzzle = new GridPuzzle(PalettePreset.get("General_Panel"), 1, 1);

        puzzle.addStartingPoint(0, 0);
        puzzle.addEndingPoint(1, 1);

        puzzle.getTileAt(0, 0).setRule(new TrianglesRule(2));

        return new PuzzleRenderer(game, puzzle);
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
