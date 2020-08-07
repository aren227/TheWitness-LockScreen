package com.aren.thewitnesspuzzle.puzzle.factory;

import com.aren.thewitnesspuzzle.math.Vector2Int;
import com.aren.thewitnesspuzzle.puzzle.ColorFactory;
import com.aren.thewitnesspuzzle.puzzle.Cursor;
import com.aren.thewitnesspuzzle.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.BrokenLine;
import com.aren.thewitnesspuzzle.puzzle.walker.RandomGridWalker;

import java.util.ArrayList;
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
        Vertex lastVertex = gridPuzzle.addEndingPoint(gridPuzzle.getWidth(), gridPuzzle.getHeight());

        RandomGridWalker walker = new RandomGridWalker(gridPuzzle, random);
        ArrayList<Vertex> vertexPositions = walker.getResult();

        Cursor cursor = new Cursor(gridPuzzle, vertexPositions, new Edge(gridPuzzle.getVertexAt(gridPuzzle.getWidth(), gridPuzzle.getHeight()), lastVertex));

        BrokenLine.generate(cursor, random, random.nextFloat() * 0.15f + 0.05f);

        /*puzzle.addStartingPoint(new StartingPoint(puzzle, 0, 0));
        puzzle.addEndingPoint(new EndingPoint(puzzle, puzzle.getWidth(), puzzle.getHeight()));

        RandomWalker walker = new RandomWalker(puzzle.getWidth(), puzzle.getHeight());
        ArrayList<Vector2Int> pathArr = walker.getRandomWalk();

        Path path = new Path(puzzle, pathArr);
        Random random = new Random();

        path.generateAreaColorsRandomly(random);

        BrokenLine.generate(path, random);
        HexagonDots.generate(path, random);
        Square.generate(path, random);*/

        //Visualize currently generated path
        /*puzzle.touching = true;
        puzzle.cursorPosition = new Vector3(pathArr.get(pathArr.size() - 1).x, pathArr.get(pathArr.size() - 1).y, 0);
        puzzle.cursorPositionStack = new ArrayList<>();
        for(Vector2Int vector2Int : pathArr){
            puzzle.cursorPositionStack.add(new Vector3(vector2Int.x, vector2Int.y, 0));
        }*/
    }
}
