package com.aren.thewitnesspuzzle.puzzle;

import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.widget.Toast;

import com.aren.thewitnesspuzzle.graphics.Circle;
import com.aren.thewitnesspuzzle.graphics.Rectangle;
import com.aren.thewitnesspuzzle.graphics.Shape;
import com.aren.thewitnesspuzzle.math.Vector2Int;
import com.aren.thewitnesspuzzle.math.Vector3;
import com.aren.thewitnesspuzzle.puzzle.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.BrokenLine;
import com.aren.thewitnesspuzzle.puzzle.rules.EndingPoint;
import com.aren.thewitnesspuzzle.puzzle.rules.HexagonDots;
import com.aren.thewitnesspuzzle.puzzle.rules.Rule;
import com.aren.thewitnesspuzzle.puzzle.rules.Square;
import com.aren.thewitnesspuzzle.puzzle.rules.StartingPoint;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class GridPuzzle extends Puzzle {

    private int width, height;

    public GridPuzzle(Game game, int width, int height){
        super(game);

        this.width = width;
        this.height = height;
        pathWidth = Math.min(width, height) * 0.05f + 0.05f;

        /*tileRules = new Rule[width][height];
        hLineRules = new Rule[width][height + 1];
        vLineRules = new Rule[width + 1][height];
        cornerRules = new Rule[width + 1][height + 1];
        startingPoints = new ArrayList<>();
        endingPoints = new ArrayList<>();

        appliedRules = new HashSet<>();

        //tileRules[0][0] = new Square(this, 0, 0, Rule.Where.TILE, Color.BLACK);
        //tileRules[1][0] = new Square(this, 1, 0, Rule.Where.TILE, Color.WHITE);
        //hLineRules[0][0] = new BrokenLine(this, 0, 0, Rule.Where.HLINE);
        //hLineRules[2][1] = new BrokenLine(this, 2, 1, Rule.Where.HLINE);
        //vLineRules[2][3] = new BrokenLine(this, 2, 3, Rule.Where.VLINE);
        //vLineRules[0][1] = new HexagonDots(this, 0, 1, Rule.Where.VLINE);
        //cornerRules[2][2] = new HexagonDots(this, 2, 2, Rule.Where.CORNER);

        PuzzleFactory factory = new PuzzleFactory(this);
        factory.generatePuzzle();

        /*
        Solver solver = new Solver(this);
        long start = System.currentTimeMillis();
        Log.i("SOLUTIONS", "" + solver.solve());
        Log.i("MAX_LENGTH", "" + (solver.maxLength + 1));
        Log.i("TIME ELAPSED", (System.currentTimeMillis() - start) + " ms");
        */
        ColorFactory.setRandomColor(this);

        for(int i = 0; i <= width; i++){
            for(int j = 0; j <= height; j++){
                addVertex(new Vertex(this, i, j));
            }
        }

        // Horizontal lines
        for(int i = 0; i < width; i++){
            for(int j = 0; j <= height; j++){
                addEdge(new Edge(getVertexAt(i, j), getVertexAt(i + 1, j)));
            }
        }

        // Vertical lines
        for(int i = 0; i <= width; i++){
            for(int j = 0; j < height; j++){
                addEdge(new Edge(getVertexAt(i, j), getVertexAt(i, j + 1)));
            }
        }

        addStartingPoint(0, 0);
        addEndingPoint(width, height);

        calcStaticShapes();
    }

    public Vertex getVertexAt(int x, int y){
        return vertices.get(x * (height + 1) + y);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void addStartingPoint(int x, int y){
        getVertexAt(x, y).setRule(new StartingPoint());
    }

    public void addEndingPoint(int x, int y){
        if(y == 0){
            Vertex vertex = addVertex(new Vertex(this, x, y - getPathWidth()));
            addEdge(new Edge(getVertexAt(x, y), vertex));
        }
        else if(y == height){
            Vertex vertex = addVertex(new Vertex(this, x, y + getPathWidth()));
            addEdge(new Edge(getVertexAt(x, y), vertex));
        }
        else if(x == 0){
            Vertex vertex = addVertex(new Vertex(this, x - getPathWidth(), y));
            addEdge(new Edge(getVertexAt(x, y), vertex));
        }
        else if(x == width){
            Vertex vertex = addVertex(new Vertex(this, x + getPathWidth(), y));
            addEdge(new Edge(getVertexAt(x, y), vertex));
        }
    }
}
