package com.aren.thewitnesspuzzle.puzzle.walker;

import android.util.Log;

import java.util.Random;

public class RandomGridTreeWalker {

    public Random random;
    public int width, height;
    public int startX, startY;
    public boolean[][] visited;
    public int[][] dist;
    public int[][] direction;

    public int[][] travelOrder = {{0,1,2,3}, {0,1,3,2}, {0,2,1,3}, {0,2,3,1}, {0,3,1,2}, {0,3,2,1}, {1,0,2,3}, {1,0,3,2}, {1,2,0,3}, {1,2,3,0}, {1,3,0,2}, {1,3,2,0}, {2,0,1,3}, {2,0,3,1}, {2,1,0,3}, {2,1,3,0}, {2,3,0,1}, {2,3,1,0}, {3,0,1,2}, {3,0,2,1}, {3,1,0,2}, {3,1,2,0}, {3,2,0,1}, {3,2,1,0}};
    public int[][] delta = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}};

    public void walk(int x, int y, int depth){
        visited[x][y] = true;
        dist[x][y] = depth;
        direction[x][y] = 0;

        int order = random.nextInt(24);
        for(int i = 0; i < 4; i++){
            int nx = x + delta[travelOrder[order][i]][0];
            int ny = y + delta[travelOrder[order][i]][1];
            if(nx < 0 || nx > width || ny < 0 || ny > height || visited[nx][ny]) continue;

            direction[x][y] |= 1 << travelOrder[order][i];
            walk(x + delta[travelOrder[order][i]][0], y + delta[travelOrder[order][i]][1], depth + 1);
        }
    }

    public RandomGridTreeWalker(int width, int height, Random random, int startX, int startY){
        this.width = width;
        this.height = height;
        this.random = random;
        this.startX = startX;
        this.startY = startY;

        visited = new boolean[width + 1][height + 1];
        dist = new int[width + 1][height + 1];
        direction = new int[width + 1][height + 1];

        walk(startX, startY, 0);
    }

}
