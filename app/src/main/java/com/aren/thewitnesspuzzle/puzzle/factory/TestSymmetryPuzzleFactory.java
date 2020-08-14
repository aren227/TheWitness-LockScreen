package com.aren.thewitnesspuzzle.puzzle.factory;

import com.aren.thewitnesspuzzle.puzzle.ColorFactory;
import com.aren.thewitnesspuzzle.puzzle.GridSymmetryPuzzle;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;

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
