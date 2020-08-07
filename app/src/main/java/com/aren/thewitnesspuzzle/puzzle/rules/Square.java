package com.aren.thewitnesspuzzle.puzzle.rules;

import com.aren.thewitnesspuzzle.graphics.RoundSquare;
import com.aren.thewitnesspuzzle.graphics.Shape;
import com.aren.thewitnesspuzzle.math.Vector2Int;
import com.aren.thewitnesspuzzle.math.Vector3;
import com.aren.thewitnesspuzzle.puzzle.Path;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.cursor.area.Area;
import com.aren.thewitnesspuzzle.puzzle.cursor.area.GridAreaSplitter;
import com.aren.thewitnesspuzzle.puzzle.graph.GraphElement;
import com.aren.thewitnesspuzzle.puzzle.graph.Tile;

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
        if(!(getGraphElement() instanceof Tile)) return null;
        return new RoundSquare(new Vector3(getGraphElement().x, getGraphElement().y, 0), 0.18f, color.getRGB());
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

    public static void generate(GridAreaSplitter splitter, Random random, float spawnRate){
        for(Area area : splitter.areaList){
            int squareCount = Math.max((int)(area.tiles.size() * spawnRate), 1);
            Collections.shuffle(area.tiles, random);
            for(int j = 0; j < squareCount; j++){
                area.tiles.get(j).setRule(new Square(area.color));
            }
        }
    }

}
