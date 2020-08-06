package com.aren.thewitnesspuzzle.puzzle.rules;

import com.aren.thewitnesspuzzle.graphics.Rectangle;
import com.aren.thewitnesspuzzle.graphics.Shape;
import com.aren.thewitnesspuzzle.math.Vector3;
import com.aren.thewitnesspuzzle.puzzle.Line;
import com.aren.thewitnesspuzzle.puzzle.Path;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.graph.GraphElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class BrokenLine extends Rule {

    public BrokenLine() {
        super();
    }

    @Override
    public Shape getShape() {
        /*if(site == Site.HLINE){
            return new Rectangle(new Vector3(x + 0.5f, y, 0), getSize(), puzzle.getPathWidth(), puzzle.getBackgroundColor());
        }
        else if(site == Site.VLINE){
            return new Rectangle(new Vector3(x, y + 0.5f, 0), puzzle.getPathWidth(), getSize(), puzzle.getBackgroundColor());
        }*/
        return null;
    }

    public static float getSize(){
        return 0.3f;
    }

    public static void generate(Path path, Random random){
        /*ArrayList<Line> notPathLines = new ArrayList<>();

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
            path.puzzle.addRule(new BrokenLine());
        }*/
    }

    public static float getCollisionCircleRadius(){
        return getSize() / 2f;
    }

}
