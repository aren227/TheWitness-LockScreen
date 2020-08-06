package com.aren.thewitnesspuzzle.puzzle.rules;

import com.aren.thewitnesspuzzle.graphics.RoundSquare;
import com.aren.thewitnesspuzzle.graphics.Shape;
import com.aren.thewitnesspuzzle.math.Vector2Int;
import com.aren.thewitnesspuzzle.math.Vector3;
import com.aren.thewitnesspuzzle.puzzle.Path;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.graph.GraphElement;

import java.util.Collections;
import java.util.Random;

public class Square extends Rule {

    public Color color;

    public Square(Color color) {
        super();
        this.color = color;
    }

    @Override
    public Shape getShape() {
        /*if(site == Site.TILE){
            return new RoundSquare(new Vector3(x + 0.5f, y + 0.5f, 0), 0.18f, color.getRGB());
        }*/
        return null;
    }

    public static boolean validateGlobally(Path path){
        /*Color[] colors = new Color[path.areaCount];

        for(int i = 0; i < path.puzzle.getWidth(); i++){
            for(int j = 0; j < path.puzzle.getHeight(); j++){
                if(path.puzzle.getTileRules()[i][j] instanceof Square){
                    Square square = (Square)path.puzzle.getTileRules()[i][j];
                    if(colors[path.areaIds[i][j]] == null) colors[path.areaIds[i][j]] = square.color;
                    else if(colors[path.areaIds[i][j]] != square.color){
                        return false;
                    }
                }
            }
        }
        */
        return true;
    }

    public static void generate(Path path, Random random){
        /*
        //각 구역에 대해서 최소 하나 이상 표시
        for(int i = 0; i < path.areaCount; i++){
            int squareCount = Math.max((int)(path.areaSizes[i] * random.nextFloat() * 0.7f), 1);
            Collections.shuffle(path.tilesByAreaId.get(i));
            for(int j = 0; j < squareCount; j++){
                Vector2Int pos = path.tilesByAreaId.get(i).get(j);
                path.puzzle.addRule(new Square(path.puzzle, pos.x, pos.y, path.areaColorByAreaId[i]));
            }
        }
        */
    }

}
