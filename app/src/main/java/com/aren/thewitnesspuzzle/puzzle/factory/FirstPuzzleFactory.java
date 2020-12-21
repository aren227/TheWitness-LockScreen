package com.aren.thewitnesspuzzle.puzzle.factory;

import android.content.Context;

import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.base.PuzzleBase;
import com.aren.thewitnesspuzzle.puzzle.base.color.PalettePreset;
import com.aren.thewitnesspuzzle.puzzle.base.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.base.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.base.rules.EndingPointRule;
import com.aren.thewitnesspuzzle.puzzle.base.rules.StartingPointRule;
import com.aren.thewitnesspuzzle.render.PuzzleRenderer;

import java.util.Random;

public class FirstPuzzleFactory extends PuzzleFactory {

    public FirstPuzzleFactory(Context context) {
        super(context);
    }

    @Override
    public PuzzleRenderer generate(Game game, Random random) {
        PuzzleBase puzzle = new PuzzleBase(PalettePreset.get("Entry_1"));

        Vertex a = new Vertex(puzzle, 0, 0);
        Vertex b = new Vertex(puzzle, 3, 0);
        new Edge(puzzle, a, b);

        a.setRule(new StartingPointRule());
        b.setRule(new EndingPointRule());

        return new PuzzleRenderer(game, puzzle);
    }

    @Override
    public Difficulty getDifficulty() {
        return Difficulty.ALWAYS_SOLVABLE;
    }

    @Override
    public String getName() {
        return "Lock #1";
    }

}
