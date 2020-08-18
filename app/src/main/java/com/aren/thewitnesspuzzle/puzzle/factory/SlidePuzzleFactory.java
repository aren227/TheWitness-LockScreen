package com.aren.thewitnesspuzzle.puzzle.factory;

import com.aren.thewitnesspuzzle.puzzle.Game;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.color.PalettePreset;
import com.aren.thewitnesspuzzle.puzzle.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.EndingPointRule;
import com.aren.thewitnesspuzzle.puzzle.rules.StartingPointRule;

import java.util.Random;

public class SlidePuzzleFactory extends PuzzleFactory {

    @Override
    public Puzzle generate(Game game, Random random) {
        Puzzle puzzle = new Puzzle(game, PalettePreset.get("TEST"));

        Vertex a = puzzle.addVertex(new Vertex(puzzle, 0, 0));
        Vertex b = puzzle.addVertex(new Vertex(puzzle, 0, 2f));
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
    public String getName(){
        return "Simple #1";
    }
}
