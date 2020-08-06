package com.aren.thewitnesspuzzle.puzzle;

public class SlidePuzzle extends Puzzle {

    public SlidePuzzle(Game game){
        super(game);

        ColorFactory.setRandomColor(this);

        calcStaticShapes();
    }
}
