package com.aren.thewitnesspuzzle.puzzle;


public class Line {

    public GridPuzzle puzzle;

    public boolean isHorizontal;

    public int x, y;

    public Line(GridPuzzle puzzle, boolean isHorizontal, int x, int y){
        this.puzzle = puzzle;
        this.isHorizontal = isHorizontal;
        this.x = x;
        this.y = y;
    }

}
