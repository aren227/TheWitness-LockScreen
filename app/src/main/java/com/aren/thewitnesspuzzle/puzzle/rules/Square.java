package com.aren.thewitnesspuzzle.puzzle.rules;

import com.aren.thewitnesspuzzle.graphics.RoundSquare;
import com.aren.thewitnesspuzzle.graphics.Shape;
import com.aren.thewitnesspuzzle.math.Vector2Int;
import com.aren.thewitnesspuzzle.math.Vector3;
import com.aren.thewitnesspuzzle.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.puzzle.Path;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.cursor.area.Area;
import com.aren.thewitnesspuzzle.puzzle.cursor.area.GridAreaSplitter;
import com.aren.thewitnesspuzzle.puzzle.graph.GraphElement;
import com.aren.thewitnesspuzzle.puzzle.graph.Tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    @Override
    public boolean canValidateLocally(){
        return false;
    }

    public static List<Rule> areaValidate(Area area){
        Color color = null;
        for(Tile tile : area.tiles){
            if(tile.getRule() instanceof Square){
                Square square = (Square)tile.getRule();
                if(color == null) color = square.color;
                else if(color != square.color){
                    List<Rule> areaErrors = new ArrayList<>();
                    for(Tile t : area.tiles){
                        if(t.getRule() instanceof Square) areaErrors.add(t.getRule());
                    }
                    return areaErrors;
                }
            }
        }
        return new ArrayList<>();
    }

    public static void generate(GridAreaSplitter splitter, Random random, float spawnRate){
        for(Area area : splitter.areaList){
            ArrayList<Tile> tiles = new ArrayList<>(area.tiles);
            int squareCount = Math.max((int)(tiles.size() * spawnRate), 1);
            Collections.shuffle(tiles, random);
            for(int j = 0; j < squareCount; j++){
                tiles.get(j).setRule(new Square(area.color));
            }
        }
    }

}
