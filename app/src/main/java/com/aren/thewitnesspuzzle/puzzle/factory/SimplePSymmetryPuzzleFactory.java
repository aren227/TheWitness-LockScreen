package com.aren.thewitnesspuzzle.puzzle.factory;

import com.aren.thewitnesspuzzle.puzzle.Game;
import com.aren.thewitnesspuzzle.puzzle.GridSymmetryPuzzle;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.color.PalettePreset;
import com.aren.thewitnesspuzzle.puzzle.cursor.SymmetryCursor;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.BrokenLineRule;
import com.aren.thewitnesspuzzle.puzzle.walker.RandomGridWalker;

import java.util.ArrayList;
import java.util.Random;

public class SimplePSymmetryPuzzleFactory extends PuzzleFactory {

    @Override
    public Puzzle generate(Game game, Random random) {
        GridSymmetryPuzzle.SymmetryType symmetryType = GridSymmetryPuzzle.SymmetryType.POINT;
        GridSymmetryPuzzle symmetryPuzzle;

        int startX, startY, endX, endY;

        symmetryPuzzle = new GridSymmetryPuzzle(game, PalettePreset.get("GlassFactory_1"), 6, 6, symmetryType, false);

        startX = 0;
        startY = 0;
        endX = random.nextInt(6);
        endY = 6;

        symmetryPuzzle.addStartingPoint(startX, startY);
        symmetryPuzzle.addEndingPoint(endX, endY);

        RandomGridWalker walker = new RandomGridWalker(symmetryPuzzle, random, 10, startX, startY, endX, endY);
        ArrayList<Vertex> vertexPositions = walker.getResult();

        SymmetryCursor cursor = new SymmetryCursor(symmetryPuzzle, vertexPositions, null);

        BrokenLineRule.generate(cursor, random, 0.7f);

        return symmetryPuzzle;
    }

    @Override
    public Difficulty getDifficulty() {
        return Difficulty.EASY;
    }

    @Override
    public String getName(){
        return "Glass Factory #2";
    }
}
