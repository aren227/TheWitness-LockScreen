package com.aren.thewitnesspuzzle.puzzle.rules;

import com.aren.thewitnesspuzzle.graphics.Rectangle;
import com.aren.thewitnesspuzzle.graphics.Shape;
import com.aren.thewitnesspuzzle.math.Vector3;
import com.aren.thewitnesspuzzle.puzzle.Line;
import com.aren.thewitnesspuzzle.puzzle.Path;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class BrokenLine extends Rule {

    public static boolean eval(Puzzle puzzle) {
        return true; //이미 길 찾기에서 걸러짐
    }

    public BrokenLine(Puzzle puzzle, int x, int y, Site site) {
        super(puzzle, x, y, site);
    }

    @Override
    public Shape getShape() {
        if(site == Site.HLINE){
            return new Rectangle(new Vector3(x + 0.5f, y, 0), getSize(), puzzle.getPathWidth(), puzzle.getBackgroundColor());
        }
        else if(site == Site.VLINE){
            return new Rectangle(new Vector3(x, y + 0.5f, 0), puzzle.getPathWidth(), getSize(), puzzle.getBackgroundColor());
        }
        return null;
    }

    public float getSize(){
        return 0.3f;
    }

    public static void generate(Path path, Random random){
        ArrayList<Line> notPathLines = new ArrayList<>();

        for(int i = 0; i < path.puzzle.getWidth(); i++){
            for(int j = 0; j <= path.puzzle.getHeight(); j++){
                if(!path.hasHLine[i][j]){
                    notPathLines.add(new Line(path.puzzle, true, i, j));
                }
            }
        }
        for(int i = 0; i <= path.puzzle.getWidth(); i++){
            for(int j = 0; j < path.puzzle.getHeight(); j++){
                if(!path.hasVLine[i][j]){
                    notPathLines.add(new Line(path.puzzle, false, i, j));
                }
            }
        }

        //Broken Lines
        //모든 경로가 아닌 엣지에 대하여 막을지 막지 않을지를 결정한다
        int brokenLinesCount = (int)(notPathLines.size() * (random.nextFloat() * 0.15f + 0.05f));
        Collections.shuffle(notPathLines);
        for(int i = 0; i < brokenLinesCount; i++){
            Line line = notPathLines.get(i);
            path.puzzle.addRule(new BrokenLine(path.puzzle, line.x, line.y, line.isHorizontal ? Rule.Site.HLINE : Rule.Site.VLINE));
        }
    }

}
