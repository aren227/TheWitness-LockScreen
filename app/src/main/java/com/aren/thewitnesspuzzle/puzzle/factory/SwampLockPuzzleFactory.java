package com.aren.thewitnesspuzzle.puzzle.factory;

import android.content.Context;

import com.aren.thewitnesspuzzle.core.color.PalettePreset;
import com.aren.thewitnesspuzzle.core.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.core.rules.BlocksRule;
import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.render.PuzzleRenderer;

import java.util.Random;

public class SwampLockPuzzleFactory extends PuzzleFactory {
    public SwampLockPuzzleFactory(Context context){
        super(context);
    }

    @Override
    public PuzzleRenderer generate(Game game, Random random) {
        GridPuzzle puzzle = new GridPuzzle(PalettePreset.get("Swamp_3"), 1, 1);

        puzzle.addStartingPoint(0, 0);
        puzzle.addEndingPoint(1, 1);

        puzzle.getTileAt(0, 0).setRule(new BlocksRule(new boolean[][]{{true}}, puzzle.getHeight(), false, false));

        return new PuzzleRenderer(game, puzzle);
    }

    @Override
    public Difficulty getDifficulty(){
        return Difficulty.ALWAYS_SOLVABLE;
    }

    @Override
    public String getName() {
        return "Lock #4";
    }
}
