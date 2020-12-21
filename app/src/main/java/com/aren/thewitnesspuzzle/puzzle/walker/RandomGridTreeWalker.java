package com.aren.thewitnesspuzzle.puzzle.walker;

import com.aren.thewitnesspuzzle.math.Vector2Int;
import com.aren.thewitnesspuzzle.math.Vector4Int;
import com.aren.thewitnesspuzzle.puzzle.base.GridPuzzle;
import com.aren.thewitnesspuzzle.puzzle.base.graph.Vertex;

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
    public int[][] bidirection;
    public Vector2Int[][] prev;
    public boolean[][] branchCreated;

    List<Integer> stackMaxSizes;

    public int[][] travelOrder = {{0, 1, 2, 3}, {0, 1, 3, 2}, {0, 2, 1, 3}, {0, 2, 3, 1}, {0, 3, 1, 2}, {0, 3, 2, 1}, {1, 0, 2, 3}, {1, 0, 3, 2}, {1, 2, 0, 3}, {1, 2, 3, 0}, {1, 3, 0, 2}, {1, 3, 2, 0}, {2, 0, 1, 3}, {2, 0, 3, 1}, {2, 1, 0, 3}, {2, 1, 3, 0}, {2, 3, 0, 1}, {2, 3, 1, 0}, {3, 0, 1, 2}, {3, 0, 2, 1}, {3, 1, 0, 2}, {3, 1, 2, 0}, {3, 2, 0, 1}, {3, 2, 1, 0}};
    public int[][] delta = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}};
    public int[] opposite = {1, 0, 3, 2};

    public void walk(int x, int y, int depth) {
        visited[x][y] = true;
        dist[x][y] = depth;
        direction[x][y] = 0;

        int order = random.nextInt(24);
        for (int i = 0; i < 4; i++) {
            int nx = x + delta[travelOrder[order][i]][0];
            int ny = y + delta[travelOrder[order][i]][1];
            if (nx < 0 || nx > width || ny < 0 || ny > height || visited[nx][ny]) continue;

            direction[x][y] |= 1 << travelOrder[order][i];
            walk(x + delta[travelOrder[order][i]][0], y + delta[travelOrder[order][i]][1], depth + 1);
        }
    }

    public RandomGridTreeWalker(int width, int height, Random random, int startX, int startY, List<Vector2Int> branchPositions) {
        this.width = width;
        this.height = height;
        this.random = random;
        this.startX = startX;
        this.startY = startY;

        visited = new boolean[width + 1][height + 1];
        dist = new int[width + 1][height + 1];
        direction = new int[width + 1][height + 1];
        bidirection = new int[width + 1][height + 1];
        prev = new Vector2Int[width + 1][height + 1];
        branchCreated = new boolean[width + 1][height + 1];

        List<Stack<Vector4Int>> stacks = new ArrayList<>(); // (x, y, dist, branch delay)
        stacks.add(new Stack<Vector4Int>());
        stacks.get(0).add(new Vector4Int(startX, startY, 0, 0));

        stackMaxSizes = new ArrayList<>();
        stackMaxSizes.add(0);

        visited[startX][startY] = true;

        while (true) {
            int check = 0;
            for (Stack<Vector4Int> stack : stacks) check += stack.size();
            if (check == 0) break;

            int idx = 0;
            while (idx < stacks.size()) {
                Stack<Vector4Int> stack = stacks.get(idx++);

                if (stack.isEmpty()) continue;
                Vector4Int v = stack.peek();

                List<Integer> avail = new ArrayList<>();
                int order = random.nextInt(24);
                for (int i = 0; i < 4; i++) {
                    int nx = v.x + delta[travelOrder[order][i]][0];
                    int ny = v.y + delta[travelOrder[order][i]][1];

                    if (nx < 0 || nx > width || ny < 0 || ny > height || visited[nx][ny]) {
                        continue;
                    }

                    avail.add(i);
                }

                if (avail.size() < 1) {
                    stack.pop();
                    continue;
                }

                // Forward
                int nx = v.x + delta[travelOrder[order][avail.get(0)]][0];
                int ny = v.y + delta[travelOrder[order][avail.get(0)]][1];
                visited[nx][ny] = true;
                dist[nx][ny] = v.z + 1;
                direction[v.x][v.y] |= 1 << travelOrder[order][avail.get(0)];
                bidirection[v.x][v.y] |= 1 << travelOrder[order][avail.get(0)];
                bidirection[nx][ny] |= 1 << opposite[travelOrder[order][avail.get(0)]];
                prev[nx][ny] = new Vector2Int(v.x, v.y);
                Vector4Int nv = new Vector4Int(nx, ny, v.z + 1, v.w - 1);

                // Branch
                //if(avail.size() > 1 && random.nextFloat() < newBranchProb && !branchCreated[v.x][v.y]){
                if (branchPositions != null && branchPositions.contains(new Vector2Int(v.x, v.y)) && avail.size() > 1 && !branchCreated[v.x][v.y] && v.w <= 0) {
                    nx = v.x + delta[travelOrder[order][avail.get(1)]][0];
                    ny = v.y + delta[travelOrder[order][avail.get(1)]][1];
                    visited[nx][ny] = true;
                    dist[nx][ny] = v.z + 1;
                    direction[v.x][v.y] |= 1 << travelOrder[order][avail.get(1)];
                    bidirection[v.x][v.y] |= 1 << travelOrder[order][avail.get(1)];
                    bidirection[nx][ny] |= 1 << opposite[travelOrder[order][avail.get(1)]];
                    prev[nx][ny] = new Vector2Int(v.x, v.y);

                    Stack<Vector4Int> newStack = new Stack<>();
                    newStack.add(new Vector4Int(nx, ny, v.z + 1, random.nextInt(width + height) + width + height));
                    stacks.add(newStack);
                    stackMaxSizes.add(1);
                    branchCreated[v.x][v.y] = true;

                    // Reset Delay
                    //nv.w = random.nextInt(width + height) + width + height;
                }
                stack.add(nv);
                stackMaxSizes.set(idx - 1, stack.size());
            }
        }

        //walk(startX, startY, 0);
    }

    public ArrayList<Vertex> getResult(GridPuzzle puzzle, int endX, int endY) {
        Vector2Int pos = new Vector2Int(endX, endY);
        ArrayList<Vertex> result = new ArrayList<>();
        while (true) {
            result.add(puzzle.getVertexAt(pos.x, pos.y));
            if (prev[pos.x][pos.y] == null) break;
            pos = prev[pos.x][pos.y];
        }
        Collections.reverse(result);
        return result;
    }

    public int getPathLength(int endX, int endY) {
        Vector2Int pos = new Vector2Int(endX, endY);
        int count = 0;
        while (true) {
            count++;
            if (prev[pos.x][pos.y] == null) break;
            pos = prev[pos.x][pos.y];
        }
        return count;
    }

    // ?????
    public static RandomGridTreeWalker getMaziest(int width, int height, Random random, int iter, int startX, int startY, int endX, int endY, int branches) {
        int length = 0;
        RandomGridTreeWalker maziest = null;
        for (int i = 0; i < iter; i++) {
            List<Vector2Int> branchPositions = new ArrayList<>();
            for (int j = 0; j < branches; j++) {
                float fx = random.nextFloat();
                float fy = random.nextFloat();
                int x = Math.min((int) (Math.pow(fx, 3) * (width + 1)), width);
                int y = Math.min((int) (Math.pow(fy, 3) * (height + 1)), height);
                branchPositions.add(new Vector2Int(x, y));
            }

            RandomGridTreeWalker walker = new RandomGridTreeWalker(width, height, random, startX, startY, branchPositions);
            int newLength = walker.getPathLength(endX, endY);
            if (newLength > length) {
                maziest = walker;
                length = newLength;
            }
        }
        return maziest;
    }

    /*public static RandomGridTreeWalker getLongest(GridPuzzle puzzle, Random random, int iter, int startX, int startY, int endX, int endY){
        return getLongest(puzzle, random, iter, startX, startY, endX, endY, 0);
    }*/

    public static RandomGridTreeWalker getLongest(int width, int height, Random random, int iter, int startX, int startY, int endX, int endY) {
        RandomGridTreeWalker longest = null;
        int length = 0;
        for (int i = 0; i < iter; i++) {
            RandomGridTreeWalker walker = new RandomGridTreeWalker(width, height, random, startX, startY, null);
            int newLength = walker.getPathLength(endX, endY);
            if (newLength > length) {
                longest = walker;
                length = newLength;
            }
        }
        return longest;
    }
}
