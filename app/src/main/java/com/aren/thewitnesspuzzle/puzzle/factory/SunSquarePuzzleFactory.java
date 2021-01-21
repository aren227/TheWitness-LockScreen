package com.aren.thewitnesspuzzle.puzzle.factory;

import android.content.Context;

import com.aren.thewitnesspuzzle.core.color.PalettePreset;
import com.aren.thewitnesspuzzle.core.cursor.Cursor;
import com.aren.thewitnesspuzzle.core.cursor.area.GridAreaSplitter;
import com.aren.thewitnesspuzzle.core.graph.Vertex;
import com.aren.thewitnesspuzzle.core.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.core.rules.Color;
import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.generator.SquareRuleGenerator;
import com.aren.thewitnesspuzzle.puzzle.generator.SunRuleGenerator;
import com.aren.thewitnesspuzzle.puzzle.walker.FastGridTreeWalker;
import com.aren.thewitnesspuzzle.render.PuzzleRenderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class SunSquarePuzzleFactory extends PuzzleFactory {
    public SunSquarePuzzleFactory(Context context) {
        super(context);
    }

    @Override
    public PuzzleRenderer generate(Game game, Random random) {
        GridPuzzle puzzle = new GridPuzzle(PalettePreset.get("Treehouse_2"), 4, 4);

        puzzle.addStartingPoint(0, 0);
        puzzle.addEndingPoint(4, 4);

        FastGridTreeWalker walker = FastGridTreeWalker.getLongest(puzzle, random, 10, 0, 0, 4, 4);
        ArrayList<Vertex> vertexPositions = walker.getResult();

        Cursor cursor = new Cursor(puzzle, vertexPositions, null);

        GridAreaSplitter splitter = new GridAreaSplitter(cursor);
        splitter.assignAreaColorRandomly(random, Arrays.asList(Color.WHITE, Color.BLACK));

        SunRuleGenerator.generate(splitter, random, Arrays.asList(Color.ORANGE), 1f, 1f, 0);
        SquareRuleGenerator.generate(splitter, random, 0.9f);

        return new PuzzleRenderer(game, puzzle);
    }

    @Override
    public Difficulty getDifficulty() {
        return Difficulty.HARD;
    }

    @Override
    public String getName() {
        return "Treehouse #2";
    }
}
