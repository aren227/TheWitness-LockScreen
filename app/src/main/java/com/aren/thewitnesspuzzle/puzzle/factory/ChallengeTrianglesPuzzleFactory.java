package com.aren.thewitnesspuzzle.puzzle.factory;

import com.aren.thewitnesspuzzle.puzzle.Game;
import com.aren.thewitnesspuzzle.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.color.PalettePreset;
import com.aren.thewitnesspuzzle.puzzle.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.TrianglesRule;
import com.aren.thewitnesspuzzle.puzzle.walker.RandomGridWalker;

import java.util.ArrayList;
import java.util.Random;

public class ChallengeTrianglesPuzzleFactory extends PuzzleFactory {
    @Override
    public Puzzle generate(Game game, Random random) {
        GridPuzzle puzzle = new GridPuzzle(game, PalettePreset.get("Challenge_3"), 4, 4);

        puzzle.addStartingPoint(0, 0);
        puzzle.addEndingPoint(4, 4);

        RandomGridWalker walker = new RandomGridWalker(puzzle, random, 5, 0, 0, 4, 4);
        ArrayList<Vertex> vertexPositions = walker.getResult();

        Cursor cursor = new Cursor(puzzle, vertexPositions, null);

        TrianglesRule.generate(cursor, random, 0.5f);

        return puzzle;
    }

    @Override
    public Difficulty getDifficulty(){
        return Difficulty.HARD;
    }

    @Override
    public String getName() {
        return "Challenge #1";
    }
}
