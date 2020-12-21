package com.aren.thewitnesspuzzle.puzzle.factory;

import android.content.Context;

import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.base.GridSymmetryPuzzle;
import com.aren.thewitnesspuzzle.puzzle.base.color.PalettePreset;
import com.aren.thewitnesspuzzle.puzzle.base.cursor.SymmetryCursor;
import com.aren.thewitnesspuzzle.puzzle.base.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.base.rules.BrokenLineRule;
import com.aren.thewitnesspuzzle.puzzle.base.rules.Symmetry;
import com.aren.thewitnesspuzzle.puzzle.base.rules.SymmetryType;
import com.aren.thewitnesspuzzle.puzzle.walker.FastGridTreeWalker;
import com.aren.thewitnesspuzzle.render.PuzzleRenderer;

import java.util.ArrayList;
import java.util.Random;

public class PSymmetryPuzzleFactory extends PuzzleFactory {

    public PSymmetryPuzzleFactory(Context context) {
        super(context);
    }

    @Override
    public PuzzleRenderer generate(Game game, Random random) {
        GridSymmetryPuzzle symmetryPuzzle = new GridSymmetryPuzzle(PalettePreset.get("GlassFactory_1"), 6, 6, new Symmetry(SymmetryType.POINT));

        int startX = 0;
        int startY = 0;
        int endX = random.nextInt(6);
        int endY = 6;

        symmetryPuzzle.addStartingPoint(startX, startY);
        symmetryPuzzle.addEndingPoint(endX, endY);

        FastGridTreeWalker walker = FastGridTreeWalker.getLongest(symmetryPuzzle, random, 10, startX, startY, endX, endY);
        ArrayList<Vertex> vertexPositions = walker.getResult();

        SymmetryCursor cursor = new SymmetryCursor(symmetryPuzzle, vertexPositions, null);

        BrokenLineRule.generate(cursor, random, 0.7f);

        return new PuzzleRenderer(game, symmetryPuzzle);
    }

    @Override
    public Difficulty getDifficulty() {
        return Difficulty.EASY;
    }

    @Override
    public String getName() {
        return "Glass Factory #2";
    }
}
