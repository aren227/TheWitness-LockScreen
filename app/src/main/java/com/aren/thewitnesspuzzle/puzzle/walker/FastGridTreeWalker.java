package com.aren.thewitnesspuzzle.puzzle.walker;

import com.aren.thewitnesspuzzle.math.Vector2Int;
import com.aren.thewitnesspuzzle.math.Vector3;
import com.aren.thewitnesspuzzle.math.Vector4Int;
import com.aren.thewitnesspuzzle.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class FastGridTreeWalker {

    public Random random;
    public int width, height;
    public int startX, startY;
    public int endX, endY;
    public boolean[][] visited;
    public int[][] dist;
    public int[][] direction;
    public int finalLength;

    public int[][] travelOrder = {{0, 1, 2, 3}, {0, 1, 3, 2}, {0, 2, 1, 3}, {0, 2, 3, 1}, {0, 3, 1, 2}, {0, 3, 2, 1}, {1, 0, 2, 3}, {1, 0, 3, 2}, {1, 2, 0, 3}, {1, 2, 3, 0}, {1, 3, 0, 2}, {1, 3, 2, 0}, {2, 0, 1, 3}, {2, 0, 3, 1}, {2, 1, 0, 3}, {2, 1, 3, 0}, {2, 3, 0, 1}, {2, 3, 1, 0}, {3, 0, 1, 2}, {3, 0, 2, 1}, {3, 1, 0, 2}, {3, 1, 2, 0}, {3, 2, 0, 1}, {3, 2, 1, 0}};
    public int[][] delta = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}};
    public int[] opposite = {1, 0, 3, 2};

    public void walk(int x, int y, int depth) {
        visited[x][y] = true;
        dist[x][y] = depth;
        direction[x][y] = 0;

        if(x == endX && y == endY){
            finalLength = depth;
            return;
        }

        int order = random.nextInt(24);
        for (int i = 0; i < 4; i++) {
            int nx = x + delta[travelOrder[order][i]][0];
            int ny = y + delta[travelOrder[order][i]][1];
            if (nx < 0 || nx > width || ny < 0 || ny > height || visited[nx][ny]) continue;

            direction[x][y] = travelOrder[order][i];
            walk(x + delta[travelOrder[order][i]][0], y + delta[travelOrder[order][i]][1], depth + 1);
        }
    }

    public FastGridTreeWalker(int width, int height, Random random, int startX, int startY, int endX, int endY) {
        this.width = width;
        this.height = height;
        this.random = random;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;

        visited = new boolean[width + 1][height + 1];
        dist = new int[width + 1][height + 1];
        direction = new int[width + 1][height + 1];

        walk(startX, startY, 0);
    }

    public ArrayList<Vertex> getResult(GridPuzzle puzzle) {
        Vector2Int pos = new Vector2Int(startX, startY);
        ArrayList<Vertex> result = new ArrayList<>();
        while (true) {
            result.add(puzzle.getVertexAt(pos.x, pos.y));
            if(pos.x == endX && pos.y == endY) break;
            pos = new Vector2Int(pos.x + delta[direction[pos.x][pos.y]][0], pos.y + delta[direction[pos.x][pos.y]][1]);
        }
        return result;
    }

    public static FastGridTreeWalker getLongest(int width, int height, Random random, int iter, int startX, int startY, int endX, int endY) {
        FastGridTreeWalker longest = null;
        int length = 0;
        for (int i = 0; i < iter; i++) {
            FastGridTreeWalker walker = new FastGridTreeWalker(width, height, random, startX, startY, endX, endY);
            if (walker.finalLength > length) {
                longest = walker;
                length = walker.finalLength;
            }
        }
        return longest;
    }
}
