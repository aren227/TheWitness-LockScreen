package com.aren.thewitnesspuzzle.puzzle;

import android.util.Log;

import com.aren.thewitnesspuzzle.puzzle.rules.BrokenLine;
import com.aren.thewitnesspuzzle.puzzle.rules.Rule;

import java.util.ArrayList;
import java.util.HashMap;

public class Solver {

    long hist;

    public Puzzle puzzle;

    int width, height;
    Rule[][] tileRules, hLineRules, vLineRules, cornerRules;

    public int maxLength = 0;

    public Solver(Puzzle puzzle){
        this.puzzle = puzzle;

        width = puzzle.getWidth();
        height = puzzle.getHeight();

        tileRules = puzzle.getTileRules();
        hLineRules = puzzle.getHLineRules();
        vLineRules = puzzle.getVLineRules();
        cornerRules = puzzle.getCornerRules();
    }

    public int dfs(int x, int y){
        if(x == width && y == height){
            maxLength = Math.max(maxLength, Long.bitCount(hist));
            return 1;
        }

        long bit = 1L << (x + y * (width + 1));
        if((hist & bit) > 0){
            return 0;
        }

        hist |= bit;

        int sum = 0;
        if(x < width){
            boolean go = true;
            if(hLineRules[x][y] != null){
                if(hLineRules[x][y] instanceof BrokenLine){
                    go = false;
                }
            }

            if(go) sum += dfs(x + 1, y);
        }
        if(x > 0){
            boolean go = true;
            if(hLineRules[x - 1][y] != null){
                if(hLineRules[x - 1][y] instanceof BrokenLine){
                    go = false;
                }
            }

            if(go) sum += dfs(x - 1, y);
        }
        if(y < height){
            boolean go = true;
            if(vLineRules[x][y] != null){
                if(vLineRules[x][y] instanceof BrokenLine){
                    go = false;
                }
            }

            if(go) sum += dfs(x, y + 1);
        }
        if(y > 0){
            boolean go = true;
            if(vLineRules[x][y - 1] != null){
                if(vLineRules[x][y - 1] instanceof BrokenLine){
                    go = false;
                }
            }

            if(go) sum += dfs(x, y - 1);
        }

        hist ^= bit;

        return sum;
    }

    public int solve(){
        hist = 0;
        return dfs(0, 0);
    }

}
