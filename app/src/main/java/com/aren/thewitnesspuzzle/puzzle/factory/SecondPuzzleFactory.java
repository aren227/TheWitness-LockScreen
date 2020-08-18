package com.aren.thewitnesspuzzle.puzzle.factory;

import com.aren.thewitnesspuzzle.puzzle.ColorFactory;
import com.aren.thewitnesspuzzle.puzzle.Game;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.EndingPointRule;
import com.aren.thewitnesspuzzle.puzzle.rules.StartingPointRule;

import java.util.Random;

public class SecondPuzzleFactory extends PuzzleFactory {

    @Override
    public Puzzle generate(Game game, Random random) {
        Puzzle puzzle = new Puzzle(game);

        ColorFactory.setRandomColor(puzzle);

        Vertex a = puzzle.addVertex(new Vertex(puzzle, 3, 0));
        Vertex b = puzzle.addVertex(new Vertex(puzzle, 3, 3));
        Vertex c = puzzle.addVertex(new Vertex(puzzle, 0, 3));
        puzzle.addEdge(new Edge(a, b));
        puzzle.addEdge(new Edge(b, c));

        a.setRule(new StartingPointRule());
        c.setRule(new EndingPointRule());

        return puzzle;
    }

    @Override
    public Difficulty getDifficulty() {
        return Difficulty.ALWAYS_SOLVABLE;
    }

    @Override
    public String getName(){
        return "Simple #2";
    }
}
