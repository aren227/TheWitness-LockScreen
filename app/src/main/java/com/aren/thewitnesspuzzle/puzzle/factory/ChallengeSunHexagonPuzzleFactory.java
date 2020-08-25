package com.aren.thewitnesspuzzle.puzzle.factory;

import android.util.Log;

import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.color.PalettePreset;
import com.aren.thewitnesspuzzle.puzzle.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.cursor.area.Area;
import com.aren.thewitnesspuzzle.puzzle.cursor.area.GridAreaSplitter;
import com.aren.thewitnesspuzzle.puzzle.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.graph.Tile;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.BrokenLineRule;
import com.aren.thewitnesspuzzle.puzzle.rules.Color;
import com.aren.thewitnesspuzzle.puzzle.rules.HexagonRule;
import com.aren.thewitnesspuzzle.puzzle.rules.SunRule;
import com.aren.thewitnesspuzzle.puzzle.walker.RandomGridWalker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ChallengeSunHexagonPuzzleFactory extends PuzzleFactory {
    @Override
    public Puzzle generate(Game game, Random random) {
        GridPuzzle puzzle = new GridPuzzle(game, PalettePreset.get("Challenge_1"), 4, 4);

        puzzle.addStartingPoint(0, 0);
        puzzle.addEndingPoint(4, 4);

        RandomGridWalker walker = new RandomGridWalker(puzzle, random, 7, 0, 0, 4, 4);
        ArrayList<Vertex> vertexPositions = walker.getResult();

        Cursor cursor = new Cursor(puzzle, vertexPositions, null);

        GridAreaSplitter splitter = new GridAreaSplitter(cursor);

        BrokenLineRule.generate(cursor, random, 1.0f);
        // Make sure that only 8 broken lines exist
        List<Edge> brokenLines = new ArrayList<>();
        for(Edge edge : puzzle.getEdges()){
            if(edge.getRule() instanceof BrokenLineRule) brokenLines.add(edge);
        }
        Collections.shuffle(brokenLines, random);
        int removeCount = brokenLines.size() - Math.min(brokenLines.size(), 8);
        for(int i = 0; i < removeCount; i++){
            brokenLines.get(i).removeRule();
        }

        SunRule.generate(splitter, random, new Color[]{Color.PURPLE}, 1f, 1f, 0);
        // Make sure that only 4 suns exist
        List<Area> sunApplied = new ArrayList<>();
        for(Area area : splitter.areaList){
            for(Tile tile : area.tiles){
                if(tile.getRule() instanceof SunRule){
                    sunApplied.add(area);
                    break;
                }
            }
        }
        removeCount = sunApplied.size() - Math.min(sunApplied.size(), 2);
        Collections.shuffle(sunApplied, random);
        for(int i = 0; i < removeCount; i++){
            Area area = sunApplied.get(i);
            for(Tile tile : area.tiles){
                if(tile.getRule() instanceof SunRule){
                    tile.removeRule();
                }
            }
        }

        HexagonRule.generate(cursor, random, 0.17f);

        return puzzle;
    }

    @Override
    public Difficulty getDifficulty() {
        return Difficulty.NORMAL;
    }

    @Override
    public String getName(){
        return "Challenge #1";
    }
}
