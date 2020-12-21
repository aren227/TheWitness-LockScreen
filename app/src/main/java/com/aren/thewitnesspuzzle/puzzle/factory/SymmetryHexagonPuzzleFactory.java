package com.aren.thewitnesspuzzle.puzzle.factory;

import android.content.Context;

import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.base.GridSymmetryPuzzle;
import com.aren.thewitnesspuzzle.puzzle.base.color.PalettePreset;
import com.aren.thewitnesspuzzle.puzzle.base.cursor.SymmetryCursor;
import com.aren.thewitnesspuzzle.puzzle.base.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.base.rules.BrokenLineRule;
import com.aren.thewitnesspuzzle.puzzle.base.rules.HexagonRule;
import com.aren.thewitnesspuzzle.puzzle.walker.FastGridTreeWalker;
import com.aren.thewitnesspuzzle.render.PuzzleRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SymmetryHexagonPuzzleFactory extends PuzzleFactory {
    public SymmetryHexagonPuzzleFactory(Context context) {
        super(context);
    }

    @Override
    public PuzzleRenderer generate(Game game, Random random) {
        GridSymmetryPuzzle symmetryPuzzle = new GridSymmetryPuzzle(PalettePreset.get("SymmetryIsland_1"), 5, 5, GridSymmetryPuzzle.SymmetryType.POINT, true);

        List<Vertex> borderVertices = symmetryPuzzle.getBorderVertices();
        borderVertices.remove(symmetryPuzzle.getVertexAt(0, 0));
        borderVertices.remove(symmetryPuzzle.getVertexAt(5, 5));
        Collections.shuffle(borderVertices, random);

        Vertex end = borderVertices.get(0);

        symmetryPuzzle.addStartingPoint(0, 0);
        symmetryPuzzle.addEndingPoint(end.getGridX(), end.getGridY());

        FastGridTreeWalker walker = FastGridTreeWalker.getLongest(symmetryPuzzle, random, 10, 0, 0, end.getGridX(), end.getGridY());
        ArrayList<Vertex> vertexPositions = walker.getResult();

        SymmetryCursor cursor = new SymmetryCursor(symmetryPuzzle, vertexPositions, null);

        HexagonRule.generate(cursor, random, 0.3f);
        BrokenLineRule.generate(cursor, random, 0.1f);

        return new PuzzleRenderer(game, symmetryPuzzle);
    }

    @Override
    public Difficulty getDifficulty() {
        return Difficulty.NORMAL;
    }

    @Override
    public String getName() {
        return "Symmetry Island #1";
    }
}
