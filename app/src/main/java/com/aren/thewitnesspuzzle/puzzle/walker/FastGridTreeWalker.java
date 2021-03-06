package com.aren.thewitnesspuzzle.puzzle.walker;

import com.aren.thewitnesspuzzle.core.graph.Vertex;
import com.aren.thewitnesspuzzle.core.math.Vector2Int;
import com.aren.thewitnesspuzzle.core.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.core.puzzle.GridSymmetryPuzzle;
import com.aren.thewitnesspuzzle.core.rules.SymmetryType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class FastGridTreeWalker {

    public Random random;
    public int width, height;
    public int startX, startY;
    public int endX, endY;
    public boolean[][] visited;
    public int[][] dist;
    public int[][] direction;
    public boolean[][] hLines, vLines;
    public int[][] areaVisited;
    public int finalLength;
    public GridPuzzle gridPuzzle;
    public SymmetryType symmetryType;

    public int[][] travelOrder = {{0, 1, 2, 3}, {0, 1, 3, 2}, {0, 2, 1, 3}, {0, 2, 3, 1}, {0, 3, 1, 2}, {0, 3, 2, 1}, {1, 0, 2, 3}, {1, 0, 3, 2}, {1, 2, 0, 3}, {1, 2, 3, 0}, {1, 3, 0, 2}, {1, 3, 2, 0}, {2, 0, 1, 3}, {2, 0, 3, 1}, {2, 1, 0, 3}, {2, 1, 3, 0}, {2, 3, 0, 1}, {2, 3, 1, 0}, {3, 0, 1, 2}, {3, 0, 2, 1}, {3, 1, 0, 2}, {3, 1, 2, 0}, {3, 2, 0, 1}, {3, 2, 1, 0}};
    public int[][] delta = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}};
    public int[] opposite = {1, 0, 3, 2};

    public boolean[][] oneSideVisit;
    public Queue<Vector2Int> queue;
    public boolean[][] queueVisit;

    public boolean walk(int x, int y, int depth) {
        if(symmetryType == SymmetryType.VLINE && width % 2 == 0 && width / 2 == x)
            return false;
        if(symmetryType == SymmetryType.POINT && width % 2 == 0 && height % 2 == 0 && width / 2 == x && height / 2 == y)
            return false;

        visited[x][y] = true;
        if (oneSideVisit != null)
            oneSideVisit[x][y] = true;
        dist[x][y] = depth;
        direction[x][y] = 0;

        if(symmetryType == SymmetryType.VLINE)
            visited[width - x][y] = true;
        if(symmetryType == SymmetryType.POINT)
            visited[width - x][height - y] = true;

        // Check if opposite cursor blocks a route to exit
        // Not efficient
        if (symmetryType == SymmetryType.POINT) {
            boolean reachable = false;

            // Clear
            queue.clear();
            for (int i = 0; i <= width; i++) {
                for (int j = 0; j <= height; j++)
                    queueVisit[i][j] = false;
            }

            queue.offer(new Vector2Int(endX, endY));
            while (!queue.isEmpty()) {
                Vector2Int front = queue.poll();
                if (front.x < 0 || front.x > width || front.y < 0 || front.y > height)
                    continue;
                if (oneSideVisit[front.x][front.y]) {
                    reachable = true;
                    break;
                }
                if (visited[front.x][front.y] || queueVisit[front.x][front.y])
                    continue;

                queueVisit[front.x][front.y] = true;

                queue.offer(new Vector2Int(front.x - 1, front.y));
                queue.offer(new Vector2Int(front.x + 1, front.y));
                queue.offer(new Vector2Int(front.x, front.y - 1));
                queue.offer(new Vector2Int(front.x, front.y + 1));
            }

            // Retreat!
            if (!reachable) {
                visited[x][y] = false;
                oneSideVisit[x][y] = false;
                visited[width - x][height - y] = false;
                return false;
            }
        }

        if(x == endX && y == endY){
            finalLength = depth;
            return true;
        }

        int order = random.nextInt(24);
        for (int i = 0; i < 4; i++) {
            int nx = x + delta[travelOrder[order][i]][0];
            int ny = y + delta[travelOrder[order][i]][1];
            if (nx < 0 || nx > width || ny < 0 || ny > height || visited[nx][ny]) continue;

            direction[x][y] = travelOrder[order][i];
            if(walk(x + delta[travelOrder[order][i]][0], y + delta[travelOrder[order][i]][1], depth + 1))
                return true;
        }

        return false;
    }

    public FastGridTreeWalker(GridPuzzle gridPuzzle, Random random, int startX, int startY, int endX, int endY) {
        this.gridPuzzle = gridPuzzle;
        this.width = gridPuzzle.getWidth();
        this.height = gridPuzzle.getHeight();
        this.random = random;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;

        visited = new boolean[width + 1][height + 1];
        dist = new int[width + 1][height + 1];
        direction = new int[width + 1][height + 1];

        if(gridPuzzle instanceof GridSymmetryPuzzle) {
            symmetryType = ((GridSymmetryPuzzle) gridPuzzle).getSymmetry().getType();
            if (symmetryType == SymmetryType.POINT) {
                oneSideVisit = new boolean[width + 1][height + 1];
                queue = new LinkedList<>();
                queueVisit = new boolean[width + 1][height + 1];
            }
        }

        walk(startX, startY, 0);
    }

    public ArrayList<Vertex> getResult() {
        Vector2Int pos = new Vector2Int(startX, startY);
        ArrayList<Vertex> result = new ArrayList<>();
        while (true) {
            result.add(gridPuzzle.getVertexAt(pos.x, pos.y));
            if(pos.x == endX && pos.y == endY) break;
            pos = new Vector2Int(pos.x + delta[direction[pos.x][pos.y]][0], pos.y + delta[direction[pos.x][pos.y]][1]);
        }
        return result;
    }

    private void fill(int x, int y, int id){
        if(areaVisited[x][y] > 0) return;

        areaVisited[x][y] = id;
        if(x > 0 && !vLines[x][y]) fill(x - 1, y, id);
        if(x < width - 1 && !vLines[x + 1][y]) fill(x + 1, y, id);
        if(y > 0 && !hLines[x][y]) fill(x, y - 1, id);
        if(y < height - 1 && !hLines[x][y + 1]) fill(x, y + 1, id);
    }

    public int getAreaCount(){
        hLines = new boolean[width][height + 1];
        vLines = new boolean[width + 1][height];
        Vector2Int pos = new Vector2Int(startX, startY);
        while (true) {
            if(pos.x == endX && pos.y == endY) break;

            int nx = pos.x + delta[direction[pos.x][pos.y]][0];
            int ny = pos.y + delta[direction[pos.x][pos.y]][1];

            if(nx > pos.x)
                hLines[pos.x][pos.y] = true;
            else if(nx < pos.x)
                hLines[pos.x - 1][pos.y] = true;
            else if(ny > pos.y)
                vLines[pos.x][pos.y] = true;
            else
                vLines[pos.x][pos.y - 1] = true;

            pos = new Vector2Int(nx, ny);
        }

        areaVisited = new int[width][height];
        int count = 0;
        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                if(areaVisited[x][y] == 0){
                    fill(x, y, count + 1);
                    count++;
                }
            }
        }
        return count;
    }

    public static FastGridTreeWalker getLongest(GridPuzzle gridPuzzle, Random random, int iter, int startX, int startY, int endX, int endY) {
        FastGridTreeWalker longest = null;
        int length = 0;
        for (int i = 0; i < iter; i++) {
            FastGridTreeWalker walker = new FastGridTreeWalker(gridPuzzle, random, startX, startY, endX, endY);
            if (walker.finalLength > length) {
                longest = walker;
                length = walker.finalLength;
            }
        }
        return longest;
    }

    public static FastGridTreeWalker getMostAreas(GridPuzzle gridPuzzle, Random random, int iter, int startX, int startY, int endX, int endY) {
        FastGridTreeWalker ans = null;
        int count = 0;
        for (int i = 0; i < iter; i++) {
            FastGridTreeWalker walker = new FastGridTreeWalker(gridPuzzle, random, startX, startY, endX, endY);
            int c = walker.getAreaCount();
            if (c > count) {
                ans = walker;
                count = c;
            }
        }
        return ans;
    }
}
