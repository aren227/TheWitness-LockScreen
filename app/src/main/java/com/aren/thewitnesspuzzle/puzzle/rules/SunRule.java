package com.aren.thewitnesspuzzle.puzzle.rules;

import com.aren.thewitnesspuzzle.graphics.shape.Shape;
import com.aren.thewitnesspuzzle.graphics.shape.SunShape;
import com.aren.thewitnesspuzzle.math.Vector3;
import com.aren.thewitnesspuzzle.puzzle.cursor.area.Area;
import com.aren.thewitnesspuzzle.puzzle.cursor.area.GridAreaSplitter;
import com.aren.thewitnesspuzzle.puzzle.graph.Tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SunRule extends Colorable {

    public SunRule(Color color){
        super(color);
    }

    @Override
    public Shape generateShape(){
        if(!(getGraphElement() instanceof Tile)) return null;
        return new SunShape(new Vector3(getGraphElement().x, getGraphElement().y, 0), 0.2f, color.getRGB());
    }

    @Override
    public boolean canValidateLocally(){
        return false;
    }

    public static List<Rule> areaValidate(Area area){
        Map<Color, ArrayList<Rule>> sunColors = new HashMap<>();
        for(Tile tile : area.tiles){
            if(tile.getRule() instanceof SunRule){
                SunRule sun = (SunRule)tile.getRule();
                if(sun.eliminated) continue;
                if(!sunColors.containsKey(sun.color)) sunColors.put(sun.color, new ArrayList<Rule>());
                sunColors.get(sun.color).add(sun);
            }
        }

        List<Rule> areaErrors = new ArrayList<>();

        for(Color color : sunColors.keySet()){
            ArrayList<Rule> suns = sunColors.get(color);
            int count = 0;
            for(Tile tile : area.tiles){
                if(tile.getRule() instanceof Colorable){
                    if(((Colorable)tile.getRule()).eliminated) continue;
                    if(((SunRule)suns.get(0)).color == ((Colorable)tile.getRule()).color){
                        count++;
                        if(count > 2) break;
                    }
                }
            }
            if(count != 2){
                areaErrors.addAll(suns);
            }
        }

        return areaErrors;
    }

    public static void generate(GridAreaSplitter splitter, Random random, Color[] palette, float areaApplyRate, float spawnRate){
        List<Color> colors = new ArrayList<>(Arrays.asList(palette));

        List<Area> areas = new ArrayList<>(splitter.areaList);
        int applyAreaCount = (int)Math.ceil(areas.size() * areaApplyRate);
        Collections.shuffle(areas, random);
        for(int i = 0; i < applyAreaCount; i++){
            Area area = areas.get(i);
            ArrayList<Tile> tiles = new ArrayList<>();
            for(Tile tile : area.tiles){
                if(tile.getRule() == null) tiles.add(tile);
            }
            Collections.shuffle(tiles, random);
            //Collections.shuffle(colors, random);

            int spawned = 0;
            while(true){
                // Check if we can add more
                if(tiles.size() - spawned < 2) break;
                if(palette.length * 2 <= spawned) break;
                if((float)spawned / tiles.size() > spawnRate) break;

                tiles.get(spawned).setRule(new SunRule(palette[spawned / 2]));
                tiles.get(spawned + 1).setRule(new SunRule(palette[spawned / 2]));

                spawned += 2;
            }
        }
    }

}
