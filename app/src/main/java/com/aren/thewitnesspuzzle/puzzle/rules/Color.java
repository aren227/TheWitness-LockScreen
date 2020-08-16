package com.aren.thewitnesspuzzle.puzzle.rules;

public enum Color {
    BLACK, WHITE, ORANGE, LIME, PINK;

    public int getRGB(){
        if(this == BLACK){
            return android.graphics.Color.BLACK;
        }
        else if(this == WHITE){
            return android.graphics.Color.WHITE;
        }
        else if(this == ORANGE){
            return android.graphics.Color.rgb(255, 128, 0);
        }
        else if(this == LIME){
            return android.graphics.Color.rgb(128, 255, 0);
        }
        else if(this == PINK){
            return android.graphics.Color.rgb(255, 0, 255);
        }
        return 0;
    }
}