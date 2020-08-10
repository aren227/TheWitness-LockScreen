package com.aren.thewitnesspuzzle.puzzle.factory;

import com.aren.thewitnesspuzzle.puzzle.ColorFactory;
import com.aren.thewitnesspuzzle.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.puzzle.GridSymmetryPuzzle;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.cursor.area.GridAreaSplitter;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.BrokenLine;
import com.aren.thewitnesspuzzle.puzzle.rules.Color;
import com.aren.thewitnesspuzzle.puzzle.rules.HexagonDots;
import com.aren.thewitnesspuzzle.puzzle.rules.Square;
import com.aren.thewitnesspuzzle.puzzle.walker.RandomGridWalker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class TestSymmetryPuzzleFactory extends PuzzleFactory{

    public TestSymmetryPuzzleFactory(Puzzle puzzle) {
        super(puzzle);
    }

    @Override
    public void generate(){
        if(!(puzzle instanceof GridSymmetryPuzzle)) return;

        Random random = new Random();

        GridSymmetryPuzzle gridSymmetryPuzzle = (GridSymmetryPuzzle)puzzle;

        ColorFactory.setRandomColor(gridSymmetryPuzzle);

        gridSymmetryPuzzle.addStartingPoint(0, 0);
        //gridSymmetryPuzzle.addEndingPoint(gridSymmetryPuzzle.getWidth(), gridSymmetryPuzzle.getHeight());
        //gridSymmetryPuzzle.calcStaticShapes();
    }

}
