package com.aren.thewitnesspuzzle.puzzle.base.rules;

import com.aren.thewitnesspuzzle.puzzle.base.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.base.cursor.SymmetryCursor;
import com.aren.thewitnesspuzzle.puzzle.factory.spawn.SpawnByRate;
import com.aren.thewitnesspuzzle.puzzle.factory.spawn.SpawnSelector;
import com.aren.thewitnesspuzzle.puzzle.base.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.base.graph.Vertex;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HexagonRule extends SymmetricColorable {

    public static final String NAME = "hexagon";

    // Manipulate color
    private int overrideColor = 0;

    public HexagonRule() {
        super();
    }

    public HexagonRule(SymmetryColor symmetryColor) {
        super(symmetryColor);
    }

    public HexagonRule(JSONObject jsonObject) throws JSONException {
        super(jsonObject);

        overrideColor = jsonObject.getInt("overrideColor");
    }

    @Override
    public boolean validateLocally(Cursor cursor) {
        if(eliminated) return true;

        if (getGraphElement() instanceof Edge) {
            if (!cursor.containsEdge((Edge) getGraphElement())) return false;
            if (hasSymmetricColor() && cursor instanceof SymmetryCursor && ((SymmetryCursor) cursor).hasSymmetricColor()) {
                return ((SymmetryCursor) cursor).getSymmetricColor((Edge) getGraphElement()) == getSymmetricColor();
            }
            return true;
        }
        if (getGraphElement() instanceof Vertex) {
            if (!cursor.containsVertex((Vertex) getGraphElement())) return false;
            if (hasSymmetricColor() && cursor instanceof SymmetryCursor && ((SymmetryCursor) cursor).hasSymmetricColor()) {
                return ((SymmetryCursor) cursor).getSymmetricColor((Vertex) getGraphElement()) == getSymmetricColor();
            }
            return true;
        }
        return true;
    }

    @Override
    public String getName() {
        return NAME;
    }

    public void setOverrideColor(int color){
        this.overrideColor = color;
    }

    public int getOverrideColor() {
        return overrideColor;
    }

    public void serialize(JSONObject jsonObject) throws JSONException {
        super.serialize(jsonObject);

        jsonObject.put("overrideColor", overrideColor);
    }
}
