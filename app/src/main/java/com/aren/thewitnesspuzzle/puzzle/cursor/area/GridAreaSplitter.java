package com.aren.thewitnesspuzzle.puzzle.cursor.area;

import com.aren.thewitnesspuzzle.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.puzzle.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GridAreaSplitter {

    private Cursor cursor;
    private GridPuzzle puzzle;

    public boolean[][] hasVertex;
    public boolean[][] hasHorizontalEdge;
    public boolean[][] hasVerticalEdge;

    public ArrayList<Area> areaList;
    public Area[][] areas;

    // Temp vars
    private boolean[][] visited;

    public GridAreaSplitter(Cursor cursor){
        this.cursor = cursor;
        if(!(cursor.getPuzzle() instanceof GridPuzzle)){
            throw new RuntimeException("Only grid puzzles are supported.");
        }
        puzzle = (GridPuzzle)cursor.getPuzzle();

        hasVertex = new boolean[puzzle.getWidth() + 1][puzzle.getHeight() + 1];
        hasHorizontalEdge = new boolean[puzzle.getWidth()][puzzle.getHeight() + 1];
        hasVerticalEdge = new boolean[puzzle.getWidth() + 1][puzzle.getHeight()];

        areaList = new ArrayList<>();
        areas = new Area[puzzle.getWidth()][puzzle.getHeight()];

        for(Edge edge : cursor.getVisitedEdges(true)){
            if(edge.isHorizontal) hasHorizontalEdge[edge.gridPosition.x][edge.gridPosition.y] = true;
            else hasVerticalEdge[edge.gridPosition.x][edge.gridPosition.y] = true;
        }

        for(Vertex vertex : cursor.getVisitedVertices(true)){
            hasVertex[vertex.gridPosition.x][vertex.gridPosition.y] = true;
        }

        for(int i = 0; i < puzzle.getWidth(); i++){
            for(int j = 0; j < puzzle.getHeight(); j++){
                if(areas[i][j] == null){
                    Area area = new Area(puzzle);
                    area.id = areaList.size();
                    areaList.add(area);
                    fill(i, j, area);
                }
            }
        }
    }

    public void assignAreaColorRandomly(Random random, List<Color> colors){
        visited = new boolean[puzzle.getWidth()][puzzle.getHeight()];

        fillColor(0, 0, colors, random.nextInt(colors.size()));
    }

    private void fill(int x, int y, Area area){
        if(areas[x][y] != null) return;

        areas[x][y] = area;
        area.tiles.add(puzzle.getTileAt(x, y));

        if(x > 0 && !hasVerticalEdge[x][y]) fill(x - 1, y, area);
        if(x < puzzle.getWidth() - 1 && !hasVerticalEdge[x + 1][y]) fill(x + 1, y, area);
        if(y > 0 && !hasHorizontalEdge[x][y]) fill(x, y - 1, area);
        if(y < puzzle.getHeight() - 1 && !hasHorizontalEdge[x][y + 1]) fill(x, y + 1, area);
    }

    private void fillColor(int x, int y, List<Color> colors, int index){
        if(visited[x][y]) return;

        if(areas[x][y].color == null){
            index = (index + 1) % colors.size();
            areas[x][y].color = colors.get(index);
            areas[x][y].colorIndex = index;
        }

        visited[x][y] = true;
        index = areas[x][y].colorIndex;

        if(x > 0) fillColor(x - 1, y, colors, index);
        if(x < puzzle.getWidth() - 1) fillColor(x + 1, y, colors, index);
        if(y > 0) fillColor(x, y - 1, colors, index);
        if(y < puzzle.getHeight() - 1) fillColor(x, y + 1, colors, index);
    }

}
