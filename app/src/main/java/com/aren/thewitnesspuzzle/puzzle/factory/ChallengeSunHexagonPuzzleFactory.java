package com.aren.thewitnesspuzzle.puzzle.factory;

import android.content.Context;

import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.base.GridPuzzle;
import com.aren.thewitnesspuzzle.puzzle.base.PuzzleBase;
import com.aren.thewitnesspuzzle.puzzle.base.color.PalettePreset;
import com.aren.thewitnesspuzzle.puzzle.base.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.base.cursor.area.Area;
import com.aren.thewitnesspuzzle.puzzle.base.cursor.area.GridAreaSplitter;
import com.aren.thewitnesspuzzle.puzzle.base.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.base.graph.Tile;
import com.aren.thewitnesspuzzle.puzzle.base.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.base.rules.BrokenLineRule;
import com.aren.thewitnesspuzzle.puzzle.base.rules.Color;
import com.aren.thewitnesspuzzle.puzzle.base.rules.HexagonRule;
import com.aren.thewitnesspuzzle.puzzle.base.rules.SunRule;
import com.aren.thewitnesspuzzle.puzzle.walker.FastGridTreeWalker;
import com.aren.thewitnesspuzzle.render.PuzzleRenderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ChallengeSunHexagonPuzzleFactory extends PuzzleFactory {
    public ChallengeSunHexagonPuzzleFactory(Context context) {
        super(context);
    }

    @Override
    public PuzzleRenderer generate(Game game, Random random) {
        GridPuzzle puzzle = new GridPuzzle(PalettePreset.get("Challenge_1"), 4, 4);

        puzzle.addStartingPoint(0, 0);
        puzzle.addEndingPoint(4, 4);

        FastGridTreeWalker walker = FastGridTreeWalker.getLongest(puzzle, random, 10,0, 0, 4, 4);
        ArrayList<Vertex> vertexPositions = walker.getResult();

        Cursor cursor = new Cursor(puzzle, vertexPositions, null);

        GridAreaSplitter splitter = new GridAreaSplitter(cursor);

        BrokenLineRule.generate(cursor, random, 1.0f);
        // Make sure that only 8 broken lines exist
        List<Edge> brokenLines = new ArrayList<>();
        for (Edge edge : puzzle.getEdges()) {
            if (edge.getRule() instanceof BrokenLineRule) brokenLines.add(edge);
        }
        Collections.shuffle(brokenLines, random);
        int removeCount = brokenLines.size() - Math.min(brokenLines.size(), 8);
        for (int i = 0; i < removeCount; i++) {
            brokenLines.get(i).removeRule();
        }

        SunRule.generate(splitter, random, Arrays.asList(Color.PURPLE), 1f, 1f, 0);
        // Make sure that only 4 suns exist
        List<Area> sunApplied = new ArrayList<>();
        for (Area area : splitter.areaList) {
            for (Tile tile : area.tiles) {
                if (tile.getRule() instanceof SunRule) {
                    sunApplied.add(area);
                    break;
                }
            }
        }
        removeCount = sunApplied.size() - Math.min(sunApplied.size(), 2);
        Collections.shuffle(sunApplied, random);
        for (int i = 0; i < removeCount; i++) {
            Area area = sunApplied.get(i);
            for (Tile tile : area.tiles) {
                if (tile.getRule() instanceof SunRule) {
                    tile.removeRule();
                }
            }
        }

        HexagonRule.generate(cursor, random, 0.17f);

        return new PuzzleRenderer(game, puzzle);
    }

    @Override
    public Difficulty getDifficulty() {
        return Difficulty.NORMAL;
    }

    @Override
    public String getName() {
        return "Challenge #7";
    }
}
