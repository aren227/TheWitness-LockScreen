package com.aren.thewitnesspuzzle.puzzle.rules;

import com.aren.thewitnesspuzzle.graphics.Shape;
import com.aren.thewitnesspuzzle.math.Vector3;
import com.aren.thewitnesspuzzle.puzzle.Path;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;

public class Rule {

    public enum Site { TILE, HLINE, VLINE, CORNER }

    public Puzzle puzzle;
    public int x, y;
    public Site site;

    public Rule(Puzzle puzzle, int x, int y, Site site){
        this.puzzle = puzzle;
        this.x = x;
        this.y = y;
        this.site = site;
    }

    public Shape getShape(){
        return null;
    }

    public boolean validate(Path path){
        return true;
    }

}
