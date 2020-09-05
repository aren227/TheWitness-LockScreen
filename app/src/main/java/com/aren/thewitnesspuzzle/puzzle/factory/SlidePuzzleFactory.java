package com.aren.thewitnesspuzzle.puzzle.factory;

import android.content.Context;

import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.color.PalettePreset;
import com.aren.thewitnesspuzzle.puzzle.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.EndingPointRule;
import com.aren.thewitnesspuzzle.puzzle.rules.StartingPointRule;

import java.util.Random;

public class SlidePuzzleFactory extends PuzzleFactory {

    public SlidePuzzleFactory(Context context) {
        super(context);
    }

    @Override
    public Puzzle generate(Game game, Random random) {
        Puzzle puzzle = new Puzzle(game, PalettePreset.get("General_Slide"));

        Vertex a = puzzle.addVertex(new Vertex(puzzle, 0, 0));
        Vertex b = puzzle.addVertex(new Vertex(puzzle, 0, 3));
        puzzle.addEdge(new Edge(a, b));

        a.setRule(new StartingPointRule());
        b.setRule(new EndingPointRule());

        return puzzle;
    }

    @Override
    public Difficulty getDifficulty() {
        return Difficulty.ALWAYS_SOLVABLE;
    }

    @Override
    public String getName() {
        return "Lock #3";
    }
}
