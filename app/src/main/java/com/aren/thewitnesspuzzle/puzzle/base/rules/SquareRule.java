package com.aren.thewitnesspuzzle.puzzle.base.rules;

import com.aren.thewitnesspuzzle.puzzle.base.cursor.area.Area;
import com.aren.thewitnesspuzzle.puzzle.base.cursor.area.GridAreaSplitter;
import com.aren.thewitnesspuzzle.puzzle.factory.spawn.SpawnByCount;
import com.aren.thewitnesspuzzle.puzzle.factory.spawn.SpawnByRate;
import com.aren.thewitnesspuzzle.puzzle.factory.spawn.SpawnSelector;
import com.aren.thewitnesspuzzle.puzzle.base.graph.Tile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class SquareRule extends Colorable {

    public static final String NAME = "square";

    public SquareRule(Color color) {
        super(color);
    }

    public SquareRule(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    @Override
    public boolean canValidateLocally() {
        return false;
    }

    @Override
    public String getName() {
        return NAME;
    }

    public static List<RuleBase> areaValidate(Area area) {
        Map<Color, ArrayList<RuleBase>> squareColors = new HashMap<>();
        for (Tile tile : area.tiles) {
            if (tile.getRule() instanceof SquareRule) {
                SquareRule square = (SquareRule) tile.getRule();
                if (square.eliminated) continue;
                if (!squareColors.containsKey(square.color))
                    squareColors.put(square.color, new ArrayList<RuleBase>());
                squareColors.get(square.color).add(square);
            }
        }

        if (squareColors.keySet().size() <= 1) return new ArrayList<>();

        List<Integer> sizes = new ArrayList<>();
        for (Color color : squareColors.keySet()) {
            sizes.add(squareColors.get(color).size());
        }
        Collections.sort(sizes);
        int errorMaxSize = sizes.get(sizes.size() - 2);

        List<RuleBase> areaErrors = new ArrayList<>();
        for (Color color : squareColors.keySet()) {
            if (squareColors.get(color).size() <= errorMaxSize) {
                areaErrors.addAll(squareColors.get(color));
            }
        }
        return areaErrors;
    }

    public static void generate(GridAreaSplitter splitter, Random random, float spawnRate) {
        generate(splitter, random, new SpawnByRate(spawnRate));
    }

    public static void generate(GridAreaSplitter splitter, Random random, SpawnSelector spawnSelector) {
        /*for (Area area : splitter.areaList) {
            ArrayList<Tile> tiles = new ArrayList<>();
            for (Tile tile : area.tiles) {
                if (tile.getRule() == null) tiles.add(tile);
            }
            if (tiles.size() == 0) continue;

            for(Tile tile : spawnSelector.select(tiles, random)){
                tile.setRule(new SquareRule(area.color));
            }
        }*/
        ArrayList<Tile> tiles = new ArrayList<>();
        for(Tile tile : splitter.getPuzzle().getTiles()){
            if(tile.getRule() == null) tiles.add(tile);
        }

        for(Tile tile : spawnSelector.select(tiles, random)){
            tile.setRule(new SquareRule(splitter.areas[tile.getGridX()][tile.getGridY()].color));
        }
    }

    public static void generate(GridAreaSplitter splitter, Random random, List<SpawnByCount> spawnSelectors) {
        Set<Color> colorSet = new HashSet<>();
        for(Area area : splitter.areaList){
            colorSet.add(area.color);
        }

        final Map<Color, List<Tile>> tiles = new HashMap<>();
        for(Color color : colorSet){
            tiles.put(color, new ArrayList<Tile>());
        }

        for(Area area : splitter.areaList){
            for(Tile tile : area.tiles){
                if(tile.getRule() == null){
                    tiles.get(area.color).add(tile);
                }
            }
        }

        // Sort colors by tile size
        List<Color> colorList = new ArrayList<>(colorSet);
        Collections.sort(colorList, new Comparator<Color>() {
            @Override
            public int compare(Color o1, Color o2) {
                return -Integer.compare(tiles.get(o1).size(), tiles.get(o2).size());
            }
        });

        // Sort spawnSelectors
        Collections.sort(spawnSelectors, new Comparator<SpawnByCount>() {
            @Override
            public int compare(SpawnByCount o1, SpawnByCount o2) {
                return -Integer.compare(o1.count, o2.count);
            }
        });

        for(int i = 0; i < colorList.size(); i++){
            for(Tile tile : spawnSelectors.get(i).select(tiles.get(colorList.get(i)), random)){
                tile.setRule(new SquareRule(colorList.get(i)));
            }
        }
    }

}
