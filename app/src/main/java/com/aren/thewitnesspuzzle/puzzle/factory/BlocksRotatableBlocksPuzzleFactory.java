package com.aren.thewitnesspuzzle.puzzle.factory;

import android.content.Context;

import com.aren.thewitnesspuzzle.core.color.PalettePreset;
import com.aren.thewitnesspuzzle.core.cursor.Cursor;
import com.aren.thewitnesspuzzle.core.cursor.area.GridAreaSplitter;
import com.aren.thewitnesspuzzle.core.graph.Vertex;
import com.aren.thewitnesspuzzle.core.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.core.rules.Color;
import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.generator.BlocksRuleGenerator;
import com.aren.thewitnesspuzzle.puzzle.generator.BrokenLineRuleGenerator;
import com.aren.thewitnesspuzzle.puzzle.walker.FastGridTreeWalker;
import com.aren.thewitnesspuzzle.render.PuzzleRenderer;

import java.util.ArrayList;
import java.util.Random;

public class BlocksRotatableBlocksPuzzleFactory extends PuzzleFactory {
    public BlocksRotatableBlocksPuzzleFactory(Context context) {
        super(context);
    }

    @Override
    public PuzzleRenderer generate(Game game, Random random) {
        GridPuzzle puzzle = new GridPuzzle(PalettePreset.get("Swamp_3"), 5, 5);

        puzzle.addStartingPoint(0, 0);
        puzzle.addEndingPoint(5, 5);

        FastGridTreeWalker walker = FastGridTreeWalker.getLongest(puzzle, random, 5, 0, 0, 5, 5);
        ArrayList<Vertex> vertexPositions = walker.getResult();

        Cursor cursor = new Cursor(puzzle, vertexPositions, null);

        GridAreaSplitter splitter = new GridAreaSplitter(cursor);

        BrokenLineRuleGenerator.generate(cursor, random, 0.2f);
        BlocksRuleGenerator.generate(splitter, random, Color.YELLOW, Color.BLUE, 0.4f, 0.5f, 0);

        return new PuzzleRenderer(game, puzzle);
    }

    @Override
    public Difficulty getDifficulty() {
        return Difficulty.VERY_HARD;
    }

    @Override
    public String getName() {
        return "Swamp #3";
    }
}
