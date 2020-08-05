package com.aren.thewitnesspuzzle.puzzle.rules;

public enum Color {
    BLACK, WHITE;

    public int getRGB(){
        if(this == BLACK){
            return android.graphics.Color.BLACK;
        }
        else if(this == WHITE){
            return android.graphics.Color.WHITE;
        }
        return 0;
    }
}