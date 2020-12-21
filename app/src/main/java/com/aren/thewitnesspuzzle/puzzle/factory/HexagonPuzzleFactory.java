package com.aren.thewitnesspuzzle.puzzle.factory;

import android.content.Context;

import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.base.GridPuzzle;
import com.aren.thewitnesspuzzle.puzzle.base.color.PalettePreset;
import com.aren.thewitnesspuzzle.puzzle.base.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.base.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.base.rules.BrokenLineRule;
import com.aren.thewitnesspuzzle.puzzle.base.rules.HexagonRule;
import com.aren.thewitnesspuzzle.puzzle.walker.FastGridTreeWalker;
import com.aren.thewitnesspuzzle.render.PuzzleRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class HexagonPuzzleFactory extends PuzzleFactory {
    public HexagonPuzzleFactory(Context context) {
        super(context);
    }

    @Override
    public PuzzleRenderer generate(Game game, Random random) {
        GridPuzzle puzzle = new GridPuzzle(PalettePreset.get("Green_1"), 4, 4);

        List<Vertex> borderVertices = puzzle.getBorderVertices();
        List<Vertex> innerVertices = puzzle.getInnerVertices();
        Collections.shuffle(borderVertices, random);
        Collections.shuffle(innerVertices, random);

        Vertex start = innerVertices.get(0);
        Vertex end = borderVertices.get(0);

        puzzle.addStartingPoint(start.getGridX(), start.getGridY());
        puzzle.addEndingPoint(end.getGridX(), end.getGridY());

        FastGridTreeWalker walker = FastGridTreeWalker.getLongest(puzzle, random, 10, start.getGridX(), start.getGridY(), end.getGridX(), end.getGridY());
        ArrayList<Vertex> vertexPositions = walker.getResult();

        Cursor cursor = new Cursor(puzzle, vertexPositions, null);

        HexagonRule.generate(cursor, random, 0.5f);
        BrokenLineRule.generate(cursor, random, 0.4f);

        return new PuzzleRenderer(game, puzzle);
    }

    @Override
    public Difficulty getDifficulty() {
        return Difficulty.EASY;
    }

    @Override
    public String getName() {
        return "Green Panel";
    }
}
