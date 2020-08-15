package com.aren.thewitnesspuzzle.puzzle.walker;

import android.util.Log;

import com.aren.thewitnesspuzzle.math.Vector2Int;
import com.aren.thewitnesspuzzle.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.puzzle.GridSymmetryPuzzle;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;

import java.util.ArrayList;
import java.util.Random;

public class RandomGridWalker {

    private GridPuzzle gridPuzzle;

    private int iters;
    private int startX, startY, endX, endY;

    private int width, height;

    private long hist; //지금까지 지나온 점들의 집합
    private long deltaUpperBit; //각 점에 대해, 그 점에서 이동한 방향의 인덱스의 상위 비트 (4 cases = 2bit)
    private long deltaLowerBit; //각 점에 대해, 그 점에서 이동한 방향의 인덱스의 하위 비트
    private int x, y;

    private int minimumVerticies = 0;
    private boolean stopWalk = false;
    private int touch = 0;

    private Random random;

    private int[][] travelOrder = {{0,1,2,3}, {0,1,3,2}, {0,2,1,3}, {0,2,3,1}, {0,3,1,2}, {0,3,2,1}, {1,0,2,3}, {1,0,3,2}, {1,2,0,3}, {1,2,3,0}, {1,3,0,2}, {1,3,2,0}, {2,0,1,3}, {2,0,3,1}, {2,1,0,3}, {2,1,3,0}, {2,3,0,1}, {2,3,1,0}, {3,0,1,2}, {3,0,2,1}, {3,1,0,2}, {3,1,2,0}, {3,2,0,1}, {3,2,1,0}};
    private int[][] delta = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}};

    private ArrayList<Vector2Int> result;

    private GridSymmetryPuzzle.SymmetryType symmetryType;

    private boolean walk(){
        if(x < 0 || y < 0 || x > width || y > height) return false;

        if(symmetryType == GridSymmetryPuzzle.SymmetryType.VLINE && width % 2 == 0 && width / 2 == x){
            return false;
        }

        if(symmetryType == GridSymmetryPuzzle.SymmetryType.POINT && width % 2 == 0 && width / 2 == x && height / 2 == y){
            return false;
        }

        int pos = (x + y * (width + 1));
        long bit = 1L << pos;

        if((hist & bit) > 0) return false;

        if(x == endX && y == endY){
            return true;
        }

        hist |= bit;

        long vSymBit = 0, pSymBit = 0;
        if(symmetryType == GridSymmetryPuzzle.SymmetryType.VLINE){
            vSymBit = 1L << ((width - x + y * (width + 1)));
            hist |= vSymBit;
        }
        else if(symmetryType == GridSymmetryPuzzle.SymmetryType.POINT){
            pSymBit = 1L << ((width - x + (height - y) * (width + 1)));
            hist |= pSymBit;
        }

        int tx = x, ty = y;

        int r = random.nextInt(24);
        for(int i = 0; i < 4; i++){
            deltaUpperBit &= ~(1L << pos);
            deltaLowerBit &= ~(1L << pos);
            deltaUpperBit |= ((((travelOrder[r][i] >> 1) > 0) ? 1L : 0L) << pos);
            deltaLowerBit |= ((((travelOrder[r][i] & 1) > 0) ? 1L : 0L) << pos);
            x = tx + delta[travelOrder[r][i]][0];
            y = ty + delta[travelOrder[r][i]][1];
            if(walk()) return true;
        }

        hist ^= bit;
        if(symmetryType == GridSymmetryPuzzle.SymmetryType.VLINE){
            hist ^= vSymBit;
        }
        else if(symmetryType == GridSymmetryPuzzle.SymmetryType.POINT){
            hist ^= pSymBit;
        }

        return false;
    }

    public RandomGridWalker(GridPuzzle gridPuzzle, Random random, int iters, int startX, int startY, int endX, int endY){
        this.gridPuzzle = gridPuzzle;
        this.width = gridPuzzle.getWidth();
        this.height = gridPuzzle.getHeight();
        if(this.width > 7 || this.height > 7){
            throw new RuntimeException("Width and height must be less than or equal to 7.");
        }
        this.random = random;
        this.iters = iters;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;

        if(gridPuzzle instanceof GridSymmetryPuzzle){
            this.symmetryType = ((GridSymmetryPuzzle)gridPuzzle).getSymmetryType();
        }
    }

    public void doWalkAsManyAsPossible(){
        int i = 0;
        long start = System.currentTimeMillis();
        for(i = 0; i < iters; i++){
            if(stopWalk) break;

            hist = deltaUpperBit = deltaLowerBit = 0;
            x = startX;
            y = startY;

            if(!walk()) break; //음

            if(Long.bitCount(hist) < minimumVerticies) continue;

            minimumVerticies = Long.bitCount(hist); //최고 정점 개수 갱신

            result = new ArrayList<>();
            int x = startX, y = startY;
            while(x != endX || y != endY){
                result.add(new Vector2Int(x, y));
                int pos = (x + y * (width + 1));
                int dir = (int)((((deltaUpperBit >> pos) & 1) << 1) | ((deltaLowerBit >> pos) & 1));
                x += delta[dir][0];
                y += delta[dir][1];
            }
            result.add(new Vector2Int(endX, endY));

            Log.i("WALKER", "" + (minimumVerticies + 1));
        }
        Log.i("WALKER", i + " iters in " + (System.currentTimeMillis() - start) + "ms");
        Log.i("WALKER", "Touch count is " + touch);
        Log.i("WALKER", "Length is " + (minimumVerticies + 1));
        Log.i("WALKER", "[" + (width + height + 1) + ", " + ((Math.pow(width+1, 2)-(1+Math.pow(-1, width+1))/2)) + "]");
    }

    public ArrayList<Vertex> getResult(){
        //시간이 허락하는 한 복잡한 경로를 찾아보자
        long wallTime = 100;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                doWalkAsManyAsPossible();
            }
        });

        thread.start(); //작업 시작

        try {
            Thread.sleep(wallTime); //작업하는 동안 잠시 낮잠
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        stopWalk = true; //이제 그만

        try {
            thread.join(); //곧 끝날거니까 잠깐만 기다리자
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ArrayList<Vertex> vertices = new ArrayList<>();
        for(Vector2Int pos : result){
            vertices.add(gridPuzzle.getVertexAt(pos.x, pos.y));
        }

        return vertices;
    }
}
