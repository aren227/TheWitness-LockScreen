package com.aren.thewitnesspuzzle.puzzle.factory;

import android.content.Context;

import com.aren.thewitnesspuzzle.core.color.PalettePreset;
import com.aren.thewitnesspuzzle.core.cursor.Cursor;
import com.aren.thewitnesspuzzle.core.graph.Vertex;
import com.aren.thewitnesspuzzle.core.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.generator.TrianglesRuleGenerator;
import com.aren.thewitnesspuzzle.puzzle.walker.FastGridTreeWalker;
import com.aren.thewitnesspuzzle.render.PuzzleRenderer;

import java.util.ArrayList;
import java.util.Random;

public class ChallengeTrianglesPuzzleFactory extends PuzzleFactory {
    public ChallengeTrianglesPuzzleFactory(Context context) {
        super(context);
    }

    @Override
    public PuzzleRenderer generate(Game game, Random random) {
        GridPuzzle puzzle = new GridPuzzle(PalettePreset.get("Challenge_2"), 4, 4);

        puzzle.addStartingPoint(0, 0);
        puzzle.addEndingPoint(4, 4);

        FastGridTreeWalker walker = FastGridTreeWalker.getLongest(puzzle, random, 5, 0, 0, 4, 4);
        ArrayList<Vertex> vertexPositions = walker.getResult();

        Cursor cursor = new Cursor(puzzle, vertexPositions, null);

        TrianglesRuleGenerator.generate(cursor, random, 0.5f);

        return new PuzzleRenderer(game, puzzle);
    }

    @Override
    public Difficulty getDifficulty() {
        return Difficulty.HARD;
    }

    @Override
    public String getName() {
        return "Challenge #11";
    }
}
