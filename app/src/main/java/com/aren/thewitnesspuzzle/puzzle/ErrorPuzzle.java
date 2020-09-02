package com.aren.thewitnesspuzzle.puzzle;

import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.color.PuzzleColorPalette;

public class ErrorPuzzle extends Puzzle {
    public ErrorPuzzle(Game game) {
        super(game, new PuzzleColorPalette(0, 0, 0, 0, 0, 0), false);
    }
}
