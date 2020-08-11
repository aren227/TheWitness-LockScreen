package com.aren.thewitnesspuzzle.puzzle.rules;

import com.aren.thewitnesspuzzle.R;
import com.aren.thewitnesspuzzle.graphics.Shape;
import com.aren.thewitnesspuzzle.graphics.SunSquare;
import com.aren.thewitnesspuzzle.math.Vector3;
import com.aren.thewitnesspuzzle.puzzle.cursor.area.Area;
import com.aren.thewitnesspuzzle.puzzle.cursor.area.GridAreaSplitter;
import com.aren.thewitnesspuzzle.puzzle.graph.Tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Sun extends Colorable {

    public Sun(Color color){
        super(color);
    }

    @Override
    public Shape getShape(){
        if(!(getGraphElement() instanceof Tile)) return null;
        return new SunSquare(new Vector3(getGraphElement().x, getGraphElement().y, 0), 0.2f, color.getRGB());
    }

    @Override
    public boolean canValidateLocally(){
        return false;
    }

    public static List<Rule> areaValidate(Area area){
        Map<Color, ArrayList<Rule>> sunColors = new HashMap<>();
        for(Tile tile : area.tiles){
            if(tile.getRule() instanceof Sun){
                Sun sun = (Sun)tile.getRule();
                if(!sunColors.containsKey(sun.color)) sunColors.put(sun.color, new ArrayList<Rule>());
                sunColors.get(sun.color).add(sun);
            }
        }

        List<Rule> areaErrors = new ArrayList<>();

        for(Color color : sunColors.keySet()){
            ArrayList<Rule> suns = sunColors.get(color);
            // Pair with another color symbol
            if(suns.size() == 1){
                boolean paired = false;
                for(Tile tile : area.tiles){
                    if(tile.getRule() instanceof Colorable && !(tile.getRule() instanceof Sun)){
                        if(((Sun)suns.get(0)).color == ((Colorable)tile.getRule()).color){
                            paired = true;
                            break;
                        }
                    }
                }
                if(!paired) areaErrors.addAll(suns);
            }
            else if(suns.size() > 2){
                areaErrors.addAll(suns);
            }
        }

        return areaErrors;
    }

    public static void generate(GridAreaSplitter splitter, Random random, float applyRate){
        ArrayList<Area> areas = new ArrayList<>(splitter.areaList);
        int applyAreaCount = (int)Math.ceil(areas.size() * applyRate);
        Collections.shuffle(areas, random);
        for(int i = 0; i < applyAreaCount; i++){
            Area area = areas.get(i);
            ArrayList<Tile> tiles = new ArrayList<>();
            for(Tile tile : area.tiles){
                if(tile.getRule() == null) tiles.add(tile);
            }
            if(tiles.size() < 2) continue;
            Collections.shuffle(tiles, random);
            tiles.get(0).setRule(new Sun(Color.BLACK));
            tiles.get(1).setRule(new Sun(Color.BLACK));
        }
    }

}
