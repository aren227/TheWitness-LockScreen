package com.aren.thewitnesspuzzle.puzzle;

import android.util.Log;

import com.aren.thewitnesspuzzle.math.Vector2Int;

import java.util.ArrayList;
import java.util.Random;

public class RandomWalker {

    int width, height;

    long hist; //지금까지 지나온 점들의 집합
    long deltaUpperBit; //각 점에 대해, 그 점에서 이동한 방향의 인덱스의 상위 비트 (4 cases = 2bit)
    long deltaLowerBit; //각 점에 대해, 그 점에서 이동한 방향의 인덱스의 하위 비트
    int x, y;

    int minimumVerticies = 0;
    boolean stopWalk = false;
    int touch = 0;

    Random random;

    int[][] travelOrder = {{0,1,2,3}, {0,1,3,2}, {0,2,1,3}, {0,2,3,1}, {0,3,1,2}, {0,3,2,1}, {1,0,2,3}, {1,0,3,2}, {1,2,0,3}, {1,2,3,0}, {1,3,0,2}, {1,3,2,0}, {2,0,1,3}, {2,0,3,1}, {2,1,0,3}, {2,1,3,0}, {2,3,0,1}, {2,3,1,0}, {3,0,1,2}, {3,0,2,1}, {3,1,0,2}, {3,1,2,0}, {3,2,0,1}, {3,2,1,0}};
    int[][] delta = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}};

    ArrayList<Vector2Int> result;

    boolean walk(){
        if(x < 0 || y < 0 || x > width || y > height) return false;

        if(x == width && y == height){
            return true;
        }

        int pos = (x + y * (width + 1));
        long bit = 1L << pos;

        if((hist & bit) > 0) return false;
        hist |= bit;

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

        return false;
    }

    public RandomWalker(int width, int height){
        this.width = width;
        this.height = height;
        random = new Random();
    }

    public void doWalkAsManyAsPossible(int iters){
        int i = 0;
        long start = System.currentTimeMillis();
        for(i = 0; i < iters; i++){
            if(stopWalk) break;

            hist = deltaUpperBit = deltaLowerBit = 0;
            x = y = 0;

            if(!walk()) break; //음

            if(Long.bitCount(hist) < minimumVerticies) continue;

            minimumVerticies = Long.bitCount(hist); //최고 정점 개수 갱신

            result = new ArrayList<>();
            int x = 0, y = 0;
            while(x != width || y != height){
                result.add(new Vector2Int(x, y));
                int pos = (x + y * (width + 1));
                int dir = (int)((((deltaUpperBit >> pos) & 1) << 1) | ((deltaLowerBit >> pos) & 1));
                x += delta[dir][0];
                y += delta[dir][1];
            }
            result.add(new Vector2Int(width, height));

            Log.i("WALKER", "" + (minimumVerticies + 1));
        }
        Log.i("WALKER", i + " iters in " + (System.currentTimeMillis() - start) + "ms");
        Log.i("WALKER", "Touch count is " + touch);
        Log.i("WALKER", "Length is " + (minimumVerticies + 1));
        Log.i("WALKER", "[" + (width + height + 1) + ", " + ((Math.pow(width+1, 2)-(1+Math.pow(-1, width+1))/2)) + "]");
    }

    public ArrayList<Vector2Int> getRandomWalk(){
        //시간이 허락하는 한 복잡한 경로를 찾아보자
        long wallTime = 100;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                doWalkAsManyAsPossible(10);
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

        return result;
    }

}
