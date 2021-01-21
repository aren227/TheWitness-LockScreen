package com.aren.thewitnesspuzzle.puzzle.factory;

import android.content.Context;

import com.aren.thewitnesspuzzle.core.color.PalettePreset;
import com.aren.thewitnesspuzzle.core.cursor.Cursor;
import com.aren.thewitnesspuzzle.core.cursor.area.GridAreaSplitter;
import com.aren.thewitnesspuzzle.core.graph.Vertex;
import com.aren.thewitnesspuzzle.core.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.generator.BrokenLineRuleGenerator;
import com.aren.thewitnesspuzzle.puzzle.generator.EliminationRuleGenerator;
import com.aren.thewitnesspuzzle.puzzle.generator.HexagonRuleGenerator;
import com.aren.thewitnesspuzzle.puzzle.walker.FastGridTreeWalker;
import com.aren.thewitnesspuzzle.render.PuzzleRenderer;

import java.util.ArrayList;
import java.util.Random;

public class HexagonEliminationPuzzleFactory extends PuzzleFactory {
    public HexagonEliminationPuzzleFactory(Context context) {
        super(context);
    }

    @Override
    public PuzzleRenderer generate(Game game, Random random) {
        GridPuzzle puzzle = new GridPuzzle(PalettePreset.get("Quarry_1"), 3, 3);

        puzzle.addStartingPoint(0, 0);
        puzzle.addEndingPoint(3, 3);

        FastGridTreeWalker walker = FastGridTreeWalker.getLongest(puzzle, random, 3, 0, 0, 3, 3);
        ArrayList<Vertex> vertexPositions = walker.getResult();

        Cursor cursor = new Cursor(puzzle, vertexPositions, null);

        GridAreaSplitter splitter = new GridAreaSplitter(cursor);

        BrokenLineRuleGenerator.generate(cursor, random, 0.25f);
        HexagonRuleGenerator.generate(cursor, random, 0.75f);
        EliminationRuleGenerator.generateFakeHexagon(splitter, random);

        return new PuzzleRenderer(game, puzzle);
    }

    @Override
    public Difficulty getDifficulty() {
        return Difficulty.EASY;
    }

    @Override
    public String getName() {
        return "Quarry #1";
    }
}
