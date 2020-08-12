package com.aren.thewitnesspuzzle.puzzle.factory;

import com.aren.thewitnesspuzzle.puzzle.ColorFactory;
import com.aren.thewitnesspuzzle.puzzle.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.cursor.area.GridAreaSplitter;
import com.aren.thewitnesspuzzle.puzzle.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.Block;
import com.aren.thewitnesspuzzle.puzzle.rules.BrokenLine;
import com.aren.thewitnesspuzzle.puzzle.rules.Color;
import com.aren.thewitnesspuzzle.puzzle.rules.HexagonDots;
import com.aren.thewitnesspuzzle.puzzle.rules.Square;
import com.aren.thewitnesspuzzle.puzzle.rules.Sun;
import com.aren.thewitnesspuzzle.puzzle.rules.Triangle;
import com.aren.thewitnesspuzzle.puzzle.walker.RandomGridWalker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class TestPuzzleFactory extends PuzzleFactory{

    public TestPuzzleFactory(Puzzle puzzle) {
        super(puzzle);
    }

    @Override
    public void generate(){
        if(!(puzzle instanceof GridPuzzle)) return;

        Random random = new Random();

        GridPuzzle gridPuzzle = (GridPuzzle)puzzle;

        ColorFactory.setRandomColor(gridPuzzle);

        gridPuzzle.addStartingPoint(0, 0);
        gridPuzzle.addEndingPoint(gridPuzzle.getWidth(), gridPuzzle.getHeight());

        RandomGridWalker walker = new RandomGridWalker(gridPuzzle, random);
        ArrayList<Vertex> vertexPositions = walker.getResult();

        Cursor cursor = new Cursor(gridPuzzle, vertexPositions, null);

        GridAreaSplitter splitter = new GridAreaSplitter(cursor);
        splitter.assignAreaColorRandomly(random, Arrays.asList(Color.WHITE, Color.BLACK));

        //Square.generate(splitter, random, random.nextFloat() * 0.25f + 0.4f);
        //Sun.generate(splitter, random, random.nextFloat() * 0.4f + 0.1f);

        gridPuzzle.getTileAt(0, 2).setRule(new Block(new boolean[][]{{true, true}, {false, true}, {false, true}}, gridPuzzle.getHeight(), true, false));
        gridPuzzle.getTileAt(1, 0).setRule(new Block(new boolean[][]{{true, false}, {true, true}, {true, false}}, gridPuzzle.getHeight(), true, false));
        gridPuzzle.getTileAt(1, 3).setRule(new Block(new boolean[][]{{true, true}, {true, true}}, gridPuzzle.getHeight(), true, false));
        gridPuzzle.getTileAt(1, 5).setRule(new Block(new boolean[][]{{false, true}, {false, true}, {true, true}}, gridPuzzle.getHeight(), true, false));
        gridPuzzle.getTileAt(3, 0).setRule(new Block(new boolean[][]{{false, true}, {true, true}, {true, false}}, gridPuzzle.getHeight(), true, false));
        gridPuzzle.getTileAt(3, 3).setRule(new Block(new boolean[][]{{false, true, true}, {true, true, false}}, gridPuzzle.getHeight(), true, false));
        gridPuzzle.getTileAt(4, 3).setRule(new Block(new boolean[][]{{true, true, true, true}}, gridPuzzle.getHeight(), true, false));

        /*BrokenLine.generate(cursor, random, random.nextFloat() * 0.15f + 0.05f);
        HexagonDots.generate(cursor, random, random.nextFloat() * 0.2f + 0.1f);
        Square.generate(splitter, random, random.nextFloat() * 0.25f + 0.4f);*/

        //Triangle.generate(cursor, random, 0.5f);
    }
}
