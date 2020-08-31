package com.aren.thewitnesspuzzle.puzzle.walker;

import android.util.Log;

import com.aren.thewitnesspuzzle.math.Vector2;
import com.aren.thewitnesspuzzle.math.Vector2Int;
import com.aren.thewitnesspuzzle.math.Vector3Int;
import com.aren.thewitnesspuzzle.math.Vector4Int;
import com.aren.thewitnesspuzzle.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class RandomGridTreeWalker {

    public Random random;
    public int width, height;
    public int startX, startY;
    public boolean[][] visited;
    public int[][] dist;
    public int[][] direction;
    public Vector2Int[][] prev;

    public int[][] travelOrder = {{0,1,2,3}, {0,1,3,2}, {0,2,1,3}, {0,2,3,1}, {0,3,1,2}, {0,3,2,1}, {1,0,2,3}, {1,0,3,2}, {1,2,0,3}, {1,2,3,0}, {1,3,0,2}, {1,3,2,0}, {2,0,1,3}, {2,0,3,1}, {2,1,0,3}, {2,1,3,0}, {2,3,0,1}, {2,3,1,0}, {3,0,1,2}, {3,0,2,1}, {3,1,0,2}, {3,1,2,0}, {3,2,0,1}, {3,2,1,0}};
    public int[][] delta = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}};
    public int[] opposite = {1, 0, 3, 2};

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

    public RandomGridTreeWalker(int width, int height, Random random, int startX, int startY, float newBranchProb){
        this.width = width;
        this.height = height;
        this.random = random;
        this.startX = startX;
        this.startY = startY;

        visited = new boolean[width + 1][height + 1];
        dist = new int[width + 1][height + 1];
        direction = new int[width + 1][height + 1];
        prev = new Vector2Int[width + 1][height + 1];

        List<Stack<Vector4Int>> stacks = new ArrayList<>(); // (x, y, dist, direction)
        stacks.add(new Stack<Vector4Int>());
        stacks.get(0).add(new Vector4Int(startX, startY, 0, 0));

        while(true){
            int check = 0;
            for(Stack<Vector4Int> stack : stacks) check += stack.size();
            if(check == 0) break;

            int idx = 0;
            while(idx < stacks.size()){
                Stack<Vector4Int> stack = stacks.get(idx++);

                if(stack.isEmpty()) continue;
                Vector4Int v = stack.peek();

                if(v.x < 0 || v.x > width || v.y < 0 || v.y > height || visited[v.x][v.y]){
                    stack.pop();
                    continue;
                }

                visited[v.x][v.y] = true;
                dist[v.x][v.y] = v.z;
                if(!(v.x == startX && v.y == startY)){
                    direction[v.x + delta[v.w][0]][v.y + delta[v.w][1]] |= 1 << opposite[v.w];
                    prev[v.x][v.y] = new Vector2Int(v.x + delta[v.w][0], v.y + delta[v.w][1]);
                }

                int order = random.nextInt(24);
                for(int i = 0; i < 4; i++){
                    int nx = v.x + delta[travelOrder[order][i]][0];
                    int ny = v.y + delta[travelOrder[order][i]][1];

                    // New branch
                    if(random.nextFloat() < newBranchProb){
                        Stack<Vector4Int> newStack = new Stack<>();
                        newStack.add(new Vector4Int(nx, ny, v.z + 1, opposite[travelOrder[order][i]]));
                        stacks.add(newStack);
                    }
                    else{
                        stack.add(new Vector4Int(nx, ny, v.z + 1, opposite[travelOrder[order][i]]));
                    }
                }
            }
        }

        //walk(startX, startY, 0);
    }

    public ArrayList<Vertex> getResult(GridPuzzle puzzle, int endX, int endY){
        Vector2Int pos = new Vector2Int(endX, endY);
        ArrayList<Vertex> result = new ArrayList<>();
        while(true){
            result.add(puzzle.getVertexAt(pos.x, pos.y));
            if(prev[pos.x][pos.y] == null) break;
            pos = prev[pos.x][pos.y];
        }
        Collections.reverse(result);
        return result;
    }

    public static ArrayList<Vertex> getLongestResult(GridPuzzle puzzle, Random random, int iter, int startX, int startY, int endX, int endY){
        return getLongestResult(puzzle, random, iter, startX, startY, endX, endY, 0f);
    }

    public static ArrayList<Vertex> getLongestResult(GridPuzzle puzzle, Random random, int iter, int startX, int startY, int endX, int endY, float newBranchProb){
        ArrayList<Vertex> longest = new ArrayList<>();
        for(int i = 0; i < iter; i++){
            RandomGridTreeWalker walker = new RandomGridTreeWalker(puzzle.getWidth(), puzzle.getHeight(), random, startX, startY, newBranchProb);
            ArrayList<Vertex> cand = walker.getResult(puzzle, endX, endY);
            if(cand.size() > longest.size()){
                longest = cand;
            }
        }
        return longest;
    }
}
