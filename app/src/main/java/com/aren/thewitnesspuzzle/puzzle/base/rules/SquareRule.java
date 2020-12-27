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
}
