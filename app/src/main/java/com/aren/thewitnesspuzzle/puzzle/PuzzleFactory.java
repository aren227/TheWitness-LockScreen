package com.aren.thewitnesspuzzle.puzzle;

import android.util.Pair;

import com.aren.thewitnesspuzzle.math.Vector2Int;
import com.aren.thewitnesspuzzle.math.Vector3;
import com.aren.thewitnesspuzzle.puzzle.rules.BrokenLine;
import com.aren.thewitnesspuzzle.puzzle.rules.Color;
import com.aren.thewitnesspuzzle.puzzle.rules.EndingPoint;
import com.aren.thewitnesspuzzle.puzzle.rules.HexagonDots;
import com.aren.thewitnesspuzzle.puzzle.rules.Rule;
import com.aren.thewitnesspuzzle.puzzle.rules.Square;
import com.aren.thewitnesspuzzle.puzzle.rules.StartingPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class PuzzleFactory {

    GridPuzzle puzzle;

    public PuzzleFactory(GridPuzzle puzzle){
        this.puzzle = puzzle;
    }

    public void generatePuzzle(){
        puzzle.addStartingPoint(new StartingPoint(puzzle, 0, 0));
        puzzle.addEndingPoint(new EndingPoint(puzzle, puzzle.getWidth(), puzzle.getHeight()));

        RandomWalker walker = new RandomWalker(puzzle.getWidth(), puzzle.getHeight());
        ArrayList<Vector2Int> pathArr = walker.getRandomWalk();

        Path path = new Path(puzzle, pathArr);
        Random random = new Random();

        path.generateAreaColorsRandomly(random);

        BrokenLine.generate(path, random);
        HexagonDots.generate(path, random);
        Square.generate(path, random);

        //Visualize currently generated path
        /*puzzle.touching = true;
        puzzle.cursorPosition = new Vector3(pathArr.get(pathArr.size() - 1).x, pathArr.get(pathArr.size() - 1).y, 0);
        puzzle.cursorPositionStack = new ArrayList<>();
        for(Vector2Int vector2Int : pathArr){
            puzzle.cursorPositionStack.add(new Vector3(vector2Int.x, vector2Int.y, 0));
        }*/
    }

}
