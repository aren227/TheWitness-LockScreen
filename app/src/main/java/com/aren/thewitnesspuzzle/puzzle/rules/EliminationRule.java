package com.aren.thewitnesspuzzle.puzzle.rules;

import com.aren.thewitnesspuzzle.graphics.shape.EliminatorShape;
import com.aren.thewitnesspuzzle.graphics.shape.Shape;
import com.aren.thewitnesspuzzle.math.Vector3;
import com.aren.thewitnesspuzzle.puzzle.cursor.area.Area;
import com.aren.thewitnesspuzzle.puzzle.cursor.area.GridAreaSplitter;
import com.aren.thewitnesspuzzle.puzzle.graph.Tile;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class EliminationRule extends Rule {

    public static final int COLOR = android.graphics.Color.parseColor("#fafafa");

    public EliminationRule(){
        super();
    }

    @Override
    public Shape generateShape(){
        if(!(getGraphElement() instanceof Tile)) return null;
        return new EliminatorShape(new Vector3(getGraphElement().x, getGraphElement().y, 0), COLOR);
    }

    @Override
    public boolean canValidateLocally(){
        return false;
    }

    public static void generateFakeHexagon(GridAreaSplitter splitter, Random random){
        for(Area area : splitter.areaList){
            List<Tile> tiles = new ArrayList<>();
            for(Tile tile : area.tiles){
                if(tile.getRule() == null) tiles.add(tile);
            }
            if(tiles.size() == 0) continue;

            // There is no hexagon dot in the area. So we can add them all!
            List<Vertex> vertices = new ArrayList<>(area.getVertices(splitter.getCursor()));
            if(vertices.size() == 0) continue;

            // Add fake rule
            vertices.get(random.nextInt(vertices.size())).setRule(new HexagonRule());
            tiles.get(random.nextInt(tiles.size())).setRule(new EliminationRule());

            break;
        }
    }

    public static void generateFakeSquare(GridAreaSplitter splitter, Random random, List<Color> colors){
        for(Area area : splitter.areaList){
            List<Tile> tiles = new ArrayList<>();
            List<Tile> squareTiles = new ArrayList<>();
            Set<Color> availableColors = new HashSet<>(colors);
            for(Tile tile : area.tiles){
                if(tile.getRule() == null){
                    tiles.add(tile);
                }
                else if(tile.getRule() instanceof SquareRule){
                    squareTiles.add(tile);
                    availableColors.remove(((SquareRule)tile.getRule()).color);
                }
                else if(tile.getRule() instanceof SunRule){
                    availableColors.remove(((SunRule)tile.getRule()).color);
                }
            }
            if(squareTiles.size() < 1 || tiles.size() + squareTiles.size() < 3) continue;
            if(availableColors.size() == 0) continue;
            List<Color> availableColorList = new ArrayList<>(availableColors);

            Collections.shuffle(tiles, random);
            Collections.shuffle(squareTiles, random);

            List<Tile> wholeTiles = new ArrayList<>();
            wholeTiles.addAll(tiles);
            wholeTiles.addAll(squareTiles);

            // Empty tiles have high priority
            wholeTiles.get(0).setRule(new EliminationRule());
            wholeTiles.get(1).setRule(new SquareRule(availableColorList.get(random.nextInt(availableColorList.size()))));
            break;
        }
    }
}
