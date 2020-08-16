package com.aren.thewitnesspuzzle.puzzle.factory;

import com.aren.thewitnesspuzzle.puzzle.ColorFactory;
import com.aren.thewitnesspuzzle.puzzle.Game;
import com.aren.thewitnesspuzzle.puzzle.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.cursor.area.GridAreaSplitter;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.Color;
import com.aren.thewitnesspuzzle.puzzle.rules.EliminationRule;
import com.aren.thewitnesspuzzle.puzzle.rules.SquareRule;
import com.aren.thewitnesspuzzle.puzzle.rules.SunRule;
import com.aren.thewitnesspuzzle.puzzle.walker.RandomGridWalker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class TestPuzzleFactory implements PuzzleFactory{

    @Override
    public Puzzle generate(Game game, Random random){
        GridPuzzle gridPuzzle = new GridPuzzle(game, 4, 4);

        ColorFactory.setRandomColor(gridPuzzle);

        gridPuzzle.addStartingPoint(0, 0);
        gridPuzzle.addEndingPoint(gridPuzzle.getWidth(), gridPuzzle.getHeight());

        RandomGridWalker walker = new RandomGridWalker(gridPuzzle, random, 10, 0, 0, gridPuzzle.getWidth(), gridPuzzle.getHeight());
        ArrayList<Vertex> vertexPositions = walker.getResult();

        Cursor cursor = new Cursor(gridPuzzle, vertexPositions, null);

        GridAreaSplitter splitter = new GridAreaSplitter(cursor);
        splitter.assignAreaColorRandomly(random, Arrays.asList(Color.WHITE, Color.BLACK));

        gridPuzzle.getTileAt(0, 0).setRule(new EliminationRule());
        gridPuzzle.getTileAt(1, 0).setRule(new SquareRule(Color.WHITE));
        gridPuzzle.getTileAt(2, 0).setRule(new SunRule(Color.WHITE));
        gridPuzzle.getTileAt(0, 1).setRule(new SquareRule(Color.BLACK));
        gridPuzzle.getTileAt(1, 1).setRule(new SquareRule(Color.BLACK));
        gridPuzzle.getTileAt(2, 1).setRule(new SquareRule(Color.BLACK));
        gridPuzzle.getTileAt(0, 2).setRule(new SquareRule(Color.WHITE));
        gridPuzzle.getTileAt(1, 2).setRule(new SquareRule(Color.WHITE));
        gridPuzzle.getTileAt(2, 2).setRule(new SunRule(Color.WHITE));

        //SquareRule.generate(splitter, random, random.nextFloat() * 0.25f + 0.4f);
        //SunRule.generate(splitter, random, random.nextFloat() * 0.4f + 0.1f);

        /*gridPuzzle.getTileAt(0, 2).setRule(new BlocksRule(new boolean[][]{{true, true}, {false, true}, {false, true}}, gridPuzzle.getHeight(), true, false));
        gridPuzzle.getTileAt(1, 0).setRule(new BlocksRule(new boolean[][]{{true, false}, {true, true}, {true, false}}, gridPuzzle.getHeight(), true, false));
        gridPuzzle.getTileAt(1, 3).setRule(new BlocksRule(new boolean[][]{{true, true}, {true, true}}, gridPuzzle.getHeight(), true, false));
        gridPuzzle.getTileAt(1, 5).setRule(new BlocksRule(new boolean[][]{{false, true}, {false, true}, {true, true}}, gridPuzzle.getHeight(), true, false));
        gridPuzzle.getTileAt(3, 0).setRule(new BlocksRule(new boolean[][]{{false, true}, {true, true}, {true, false}}, gridPuzzle.getHeight(), true, false));
        gridPuzzle.getTileAt(3, 3).setRule(new BlocksRule(new boolean[][]{{false, true, true}, {true, true, false}}, gridPuzzle.getHeight(), true, false));
        gridPuzzle.getTileAt(4, 3).setRule(new BlocksRule(new boolean[][]{{true, true, true, true}}, gridPuzzle.getHeight(), true, false));
        gridPuzzle.getTileAt(0, 0).setRule(new EliminationRule());*/

        /*BrokenLineRule.generate(cursor, random, random.nextFloat() * 0.15f + 0.05f);
        HexagonRule.generate(cursor, random, random.nextFloat() * 0.2f + 0.1f);
        SquareRule.generate(splitter, random, random.nextFloat() * 0.25f + 0.4f);*/

        //TrianglesRule.generate(cursor, random, 0.5f);
        return gridPuzzle;
    }

    @Override
    public Difficulty getDifficulty() {
        return Difficulty.NORMAL;
    }
}