package com.aren.thewitnesspuzzle.puzzle.rules;

import android.graphics.Color;

import com.aren.thewitnesspuzzle.graphics.Hexagon;
import com.aren.thewitnesspuzzle.graphics.Rectangle;
import com.aren.thewitnesspuzzle.graphics.Shape;
import com.aren.thewitnesspuzzle.math.Vector2Int;
import com.aren.thewitnesspuzzle.math.Vector3;
import com.aren.thewitnesspuzzle.puzzle.Line;
import com.aren.thewitnesspuzzle.puzzle.Path;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class HexagonDots extends Rule {

    public static boolean eval(Puzzle puzzle) {
        return false;
    }

    public HexagonDots(Puzzle puzzle, int x, int y, Site site){
        super(puzzle, x, y, site);
    }

    @Override
    public Shape getShape() {
        if(site == Site.CORNER){
            return new Hexagon(new Vector3(x, y, 0), puzzle.getPathWidth() * 0.4f, Color.BLACK);
        }
        else if(site == Site.HLINE){
            return new Hexagon(new Vector3(x + 0.5f, y, 0), puzzle.getPathWidth() * 0.4f, Color.BLACK);
        }
        else if(site == Site.VLINE){
            return new Hexagon(new Vector3(x, y + 0.5f, 0), puzzle.getPathWidth() * 0.4f, Color.BLACK);
        }
        return null;
    }

    @Override
    public boolean validate(Path path){
        if(site == Site.CORNER){
            return path.hasPoint[x][y];
        }
        else if(site == Site.HLINE){
            return path.hasHLine[x][y];
        }
        else if(site == Site.VLINE){
            return path.hasVLine[x][y];
        }
        return true;
    }

    public static void generate(Path path, Random random){
        /*ArrayList<Line> pathLines = new ArrayList<>();

        for(int i = 0; i < path.puzzle.getWidth(); i++){
            for(int j = 0; j <= path.puzzle.getHeight(); j++){
                if(path.hasHLine[i][j]){
                    pathLines.add(new Line(path.puzzle, true, i, j));
                }
            }
        }
        for(int i = 0; i <= path.puzzle.getWidth(); i++){
            for(int j = 0; j < path.puzzle.getHeight(); j++){
                if(path.hasVLine[i][j]){
                    pathLines.add(new Line(path.puzzle, false, i, j));
                }
            }
        }

        //Hexagon Dots
        //모든 경로 상 엣지에 대하여
        int hexagonLinesCount = (int)(pathLines.size() * (random.nextFloat() * 0.2f + 0.1f));
        Collections.shuffle(pathLines);
        for(int i = 0; i < hexagonLinesCount; i++){
            Line line = pathLines.get(i);
            path.puzzle.addRule(new HexagonDots(path.puzzle, line.x, line.y, line.isHorizontal ? Rule.Site.HLINE : Rule.Site.VLINE));
        }*/
        ArrayList<Vector2Int> pathPoints = new ArrayList<>();

        for(int i = 0; i < path.puzzle.getWidth(); i++){
            for(int j = 0; j < path.puzzle.getHeight(); j++){
                if(i == 0 && j == 0 || i == path.puzzle.getWidth() && j == path.puzzle.getHeight()) continue;
                if(path.hasPoint[i][j]){
                    pathPoints.add(new Vector2Int(i, j));
                }
            }
        }

        //모든 경로 상 정점에 대하여
        int hexagonLinesCount = (int)(pathPoints.size() * (random.nextFloat() * 0.2f + 0.1f));
        Collections.shuffle(pathPoints);
        for(int i = 0; i < hexagonLinesCount; i++){
            path.puzzle.addRule(new HexagonDots(path.puzzle, pathPoints.get(i).x, pathPoints.get(i).y, Rule.Site.CORNER));
        }
    }
}
