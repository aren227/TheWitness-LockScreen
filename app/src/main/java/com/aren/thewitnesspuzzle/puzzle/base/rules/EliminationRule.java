package com.aren.thewitnesspuzzle.puzzle.base.rules;

import com.aren.thewitnesspuzzle.math.Vector2Int;
import com.aren.thewitnesspuzzle.puzzle.base.cursor.area.Area;
import com.aren.thewitnesspuzzle.puzzle.base.cursor.area.GridAreaSplitter;
import com.aren.thewitnesspuzzle.puzzle.base.graph.Tile;
import com.aren.thewitnesspuzzle.puzzle.base.graph.Vertex;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class EliminationRule extends Colorable {

    public static final String NAME = "elimination";

    public static final int COLOR = android.graphics.Color.parseColor("#fafafa");

    public EliminationRule(){
        this(Color.WHITE);
    }

    public EliminationRule(Color color) {
        super(color);
    }

    public EliminationRule(JSONObject jsonObject) throws JSONException {
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

}
