package com.aren.thewitnesspuzzle.puzzle;

import android.util.Log;

import com.aren.thewitnesspuzzle.math.Vector2Int;
import com.aren.thewitnesspuzzle.puzzle.rules.Color;

import java.util.ArrayList;
import java.util.Random;

public class Path {

    public GridPuzzle puzzle;

    public boolean[][] hasPoint;
    public boolean[][] hasHLine;
    public boolean[][] hasVLine;

    public int areaCount;
    public int[][] areaIds;
    public int[] areaSizes;
    public ArrayList<ArrayList<Vector2Int>> tilesByAreaId;

    public Color[][] areaColors;
    public Color[] areaColorByAreaId;
    public Color[] usedAreaColors;
    public int[] areaColorIdByAreaId;

    void fill(int x, int y, int idx){
        if(areaIds[x][y] != -1) return;
        areaIds[x][y] = idx;
        if(x > 0 && !hasVLine[x][y]) fill(x - 1, y, idx);
        if(x < puzzle.getWidth() - 1 && !hasVLine[x + 1][y]) fill(x + 1, y, idx);
        if(y > 0 && !hasHLine[x][y]) fill(x, y - 1, idx);
        if(y < puzzle.getHeight() - 1 && !hasHLine[x][y + 1]) fill(x, y + 1, idx);
    }

    void fillColor(int x, int y, int colorIdx){
        if(areaColors[x][y] != null) return;

        if(areaColorByAreaId[areaIds[x][y]] == null){
            colorIdx = (colorIdx + 1) % usedAreaColors.length;
            areaColorByAreaId[areaIds[x][y]] = usedAreaColors[colorIdx];
            areaColorIdByAreaId[areaIds[x][y]] = colorIdx;
        }
        colorIdx = areaColorIdByAreaId[areaIds[x][y]];
        areaColors[x][y] = areaColorByAreaId[areaIds[x][y]];

        if(x > 0) fillColor(x - 1, y, colorIdx);
        if(x < puzzle.getWidth() - 1) fillColor(x + 1, y, colorIdx);
        if(y > 0) fillColor(x, y - 1, colorIdx);
        if(y < puzzle.getHeight() - 1) fillColor(x, y + 1, colorIdx);
    }

    public Path(GridPuzzle puzzle, ArrayList<Vector2Int> path){
        this.puzzle = puzzle;

        hasPoint = new boolean[puzzle.getWidth() + 1][puzzle.getHeight() + 1];
        hasHLine = new boolean[puzzle.getWidth()][puzzle.getHeight() + 1];
        hasVLine = new boolean[puzzle.getWidth() + 1][puzzle.getHeight()];

        areaIds = new int[puzzle.getWidth()][puzzle.getHeight()];

        //초기화
        for(int i = 0; i < puzzle.getWidth(); i++){
            for(int j = 0; j < puzzle.getHeight(); j++){
                areaIds[i][j] = -1;
            }
        }

        //경로 엣지 미리 계산
        for(int i = 0; i < path.size() - 1; i++){
            if(path.get(i).y == path.get(i + 1).y){
                hasHLine[Math.min(path.get(i).x, path.get(i + 1).x)][path.get(i).y] = true;
            }
            else{
                hasVLine[path.get(i).x][Math.min(path.get(i).y, path.get(i + 1).y)] = true;
            }
        }
        for(int i = 0; i < path.size(); i++){
            hasPoint[path.get(i).x][path.get(i).y] = true;
        }

        //경로 구역 번호 매기기
        tilesByAreaId = new ArrayList<>(); //구역 인덱스에 따른 타일들
        for(int i = 0; i < puzzle.getWidth(); i++){
            for(int j = 0; j < puzzle.getHeight(); j++){
                if(areaIds[i][j] == -1) fill(i, j, areaCount++);
            }
        }
        areaSizes = new int[areaCount];
        for(int i = 0; i < areaCount; i++){
            tilesByAreaId.add(new ArrayList<Vector2Int>());
        }
        for(int i = 0; i < puzzle.getWidth(); i++){
            for(int j = 0; j < puzzle.getHeight(); j++){
                tilesByAreaId.get(areaIds[i][j]).add(new Vector2Int(i, j));
                areaSizes[areaIds[i][j]]++;
            }
        }
    }

    public void generateAreaColorsRandomly(Random random){
        areaColors = new Color[puzzle.getWidth()][puzzle.getHeight()];
        areaColorByAreaId = new Color[areaCount];
        usedAreaColors = new Color[]{Color.WHITE, Color.BLACK}; //TODO: More colors
        areaColorIdByAreaId = new int[areaCount];

        fillColor(random.nextInt(puzzle.getWidth()), random.nextInt(puzzle.getHeight()), random.nextInt(usedAreaColors.length));
    }

}
