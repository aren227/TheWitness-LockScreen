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
}
