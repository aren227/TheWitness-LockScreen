package com.aren.thewitnesspuzzle.puzzle.base.rules;

import com.aren.thewitnesspuzzle.puzzle.base.cursor.area.Area;
import com.aren.thewitnesspuzzle.puzzle.base.cursor.area.GridAreaSplitter;
import com.aren.thewitnesspuzzle.puzzle.base.graph.Tile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SunRule extends Colorable {

    public static final String NAME = "sun";

    public SunRule(Color color) {
        super(color);
    }

    public SunRule(JSONObject jsonObject) throws JSONException {
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
        Map<Color, ArrayList<RuleBase>> sunColors = new HashMap<>();
        for (Tile tile : area.tiles) {
            if (tile.getRule() instanceof SunRule) {
                SunRule sun = (SunRule) tile.getRule();
                if (sun.eliminated) continue;
                if (!sunColors.containsKey(sun.color))
                    sunColors.put(sun.color, new ArrayList<RuleBase>());
                sunColors.get(sun.color).add(sun);
            }
        }

        List<RuleBase> areaErrors = new ArrayList<>();

        for (Color color : sunColors.keySet()) {
            ArrayList<RuleBase> suns = sunColors.get(color);
            int count = 0;
            for (Tile tile : area.tiles) {
                if (tile.getRule() instanceof Colorable) {
                    if (((Colorable) tile.getRule()).eliminated) continue;
                    if (((SunRule) suns.get(0)).color == ((Colorable) tile.getRule()).color) {
                        count++;
                        if (count > 2) break;
                    }
                }
            }
            if (count != 2) {
                areaErrors.addAll(suns);
            }
        }

        return areaErrors;
    }

    public static void generate(GridAreaSplitter splitter, Random random, List<Color> palette, float areaApplyRate, float spawnRate, float pairWithAnotherRate) {
        List<Area> areas = new ArrayList<>(splitter.areaList);
        int applyAreaCount = (int) Math.ceil(areas.size() * areaApplyRate);
        Collections.shuffle(areas, random);
        for (int i = 0; i < applyAreaCount; i++) {
            Area area = areas.get(i);
            ArrayList<Tile> tiles = new ArrayList<>();
            ArrayList<Color> availableColors = new ArrayList<>(palette);
            for (Tile tile : area.tiles) {
                if (tile.getRule() == null) tiles.add(tile);
                else if (tile.getRule() instanceof Colorable) {
                    availableColors.remove(((Colorable) tile.getRule()).color);
                }
            }
            Collections.shuffle(tiles, random);

            int spawned = 0;
            while (true) {
                // Check if we can add more
                if (tiles.size() - spawned < 2) break;
                if (availableColors.size() * 2 <= spawned) break;
                if ((float) spawned / tiles.size() > spawnRate) break;

                tiles.get(spawned).setRule(new SunRule(availableColors.get(spawned / 2)));
                tiles.get(spawned + 1).setRule(new SunRule(availableColors.get(spawned / 2)));

                spawned += 2;
            }
        }

        // Try to pair with another symbol
        int applyAreaCount2 = (int) Math.ceil(areas.size() * pairWithAnotherRate);
        Collections.shuffle(areas, random);
        for (int i = 0; i < applyAreaCount2; i++) {
            // Pick a random color to pair
            ArrayList<Color> availableColors = new ArrayList<>(palette);
            ArrayList<Tile> tiles = new ArrayList<>();
            HashSet<Color> colorSet = new HashSet<>();
            Area area = areas.get(i);
            for (Tile tile : area.tiles) {
                if (tile.getRule() == null) tiles.add(tile);
                else if (tile.getRule() instanceof Colorable && !(tile.getRule() instanceof SunRule) && availableColors.contains(((Colorable) tile.getRule()).color)) {
                    colorSet.add(((Colorable) tile.getRule()).color);
                }
            }

            ArrayList<Color> colors = new ArrayList<>(colorSet);
            Collections.shuffle(colors, random);

            // We will apply this rule to only one color
            for (Color color : colors) {
                ArrayList<Tile> removableTiles = new ArrayList<>(); // Can be removed without making any errors (SquareRule)
                ArrayList<Tile> nonRemovableTiles = new ArrayList<>();

                for (Tile tile : area.tiles) {
                    if (tile.getRule() instanceof Colorable && ((Colorable) tile.getRule()).color == color) {
                        //TODO: Maybe there are more rules that can be removable
                        if (tile.getRule() instanceof SquareRule) {
                            removableTiles.add(tile);
                        } else {
                            nonRemovableTiles.add(tile);
                        }
                    }
                }

                // Impossible
                if (nonRemovableTiles.size() > 1) {
                    continue;
                }

                // Remove rules except one
                Collections.shuffle(removableTiles, random);
                while (removableTiles.size() + nonRemovableTiles.size() > 1) {
                    removableTiles.get(removableTiles.size() - 1).removeRule();
                    tiles.add(removableTiles.get(removableTiles.size() - 1));
                    removableTiles.remove(removableTiles.size() - 1);
                }

                // Impossible
                if (tiles.size() == 0) {
                    continue;
                }

                Collections.shuffle(tiles, random);

                tiles.get(random.nextInt(tiles.size())).setRule(new SunRule(color));

                // Success!
                break;
            }
        }
    }
}
