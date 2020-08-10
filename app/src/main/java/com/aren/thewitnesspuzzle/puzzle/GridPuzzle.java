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
import com.aren.thewitnesspuzzle.puzzle.cursor.area.GridAreaSplitter;
import com.aren.thewitnesspuzzle.puzzle.factory.TestPuzzleFactory;
import com.aren.thewitnesspuzzle.puzzle.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.graph.Tile;
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

    protected int width, height;

    protected Vertex[][] gridVerticies;
    protected Edge[][] gridHorizontalEdges;
    protected Edge[][] gridVerticalEdges;
    protected Tile[][] gridTiles;

    public GridPuzzle(Game game, int width, int height){
        super(game);

        this.width = width;
        this.height = height;
        pathWidth = Math.min(width, height) * 0.05f + 0.05f;

        gridVerticies = new Vertex[width + 1][height + 1];
        gridHorizontalEdges = new Edge[width][height + 1];
        gridVerticalEdges = new Edge[width + 1][height];
        gridTiles = new Tile[width][height];

        for(int i = 0; i <= width; i++){
            for(int j = 0; j <= height; j++){
                Vertex vertex = addVertex(new Vertex(this, i, j));
                vertex.gridPosition = new Vector2Int(i, j);
                gridVerticies[i][j] = vertex;
            }
        }

        // Horizontal lines
        for(int i = 0; i < width; i++){
            for(int j = 0; j <= height; j++){
                Edge edge = addEdge(new Edge(getVertexAt(i, j), getVertexAt(i + 1, j)));
                edge.gridPosition = new Vector2Int(i, j);
                edge.isHorizontal = true;
                gridHorizontalEdges[i][j] = edge;
            }
        }

        // Vertical lines
        for(int i = 0; i <= width; i++){
            for(int j = 0; j < height; j++){
                Edge edge = addEdge(new Edge(getVertexAt(i, j), getVertexAt(i, j + 1)));
                edge.gridPosition = new Vector2Int(i, j);
                edge.isHorizontal = false;
                gridVerticalEdges[i][j] = edge;
            }
        }

        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                Tile tile = addTile(new Tile(this, i + 0.5f, j + 0.5f));
                tile.gridPosition = new Vector2Int(i, j);
                gridTiles[i][j] = tile;
            }
        }
    }

    public Vertex getVertexAt(int x, int y){
        return gridVerticies[x][y];
    }

    public Edge getEdgeAt(int x, int y, boolean horizontal){
        if(horizontal) return gridHorizontalEdges[x][y];
        return gridVerticalEdges[x][y];
    }

    public Tile getTileAt(int x, int y){
        return gridTiles[x][y];
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

    public Edge addEndingPoint(int x, int y){
        Vertex vertex = null;
        if(y == 0){
            vertex = addVertex(new Vertex(this, x, y - getPathWidth()));
        }
        else if(y == height){
            vertex = addVertex(new Vertex(this, x, y + getPathWidth()));
        }
        else if(x == 0){
            vertex = addVertex(new Vertex(this, x - getPathWidth(), y));
        }
        else if(x == width){
            vertex = addVertex(new Vertex(this, x + getPathWidth(), y));
        }

        if(vertex != null){
            Edge edge = addEdge(new Edge(getVertexAt(x, y), vertex));
            vertex.setRule(new EndingPoint());
            return edge;
        }
        return null;
    }

    @Override
    public boolean validate(){
        super.validate();

        GridAreaSplitter splitter = new GridAreaSplitter(cursor);

        if(!Square.validateGlobally(splitter)) return false;

        return true;
    }

}
