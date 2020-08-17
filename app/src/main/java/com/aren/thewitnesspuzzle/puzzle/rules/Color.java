package com.aren.thewitnesspuzzle.puzzle.rules;

public enum Color {
    BLACK, WHITE, ORANGE, LIME, PURPLE, CYAN, RED;

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
            return android.graphics.Color.rgb(79, 255, 79);
        }
        else if(this == PURPLE){
            return android.graphics.Color.rgb(210, 0, 255);
        }
        else if(this == CYAN){
            return android.graphics.Color.rgb(0, 255, 255);
        }
        else if(this == RED){
            return android.graphics.Color.rgb(255, 65, 0);
        }
        return 0;
    }
}