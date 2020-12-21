package com.aren.thewitnesspuzzle.puzzle.base.rules;

import org.json.JSONException;
import org.json.JSONObject;

public class Symmetry {

    public SymmetryType type;
    public SymmetryColor color;

    public Symmetry(SymmetryType type) {
        this(type, SymmetryColor.NONE);
    }

    public Symmetry(SymmetryType type, SymmetryColor color) {
        this.type = type;
        this.color = color;
    }

    public Symmetry(JSONObject jsonObject) throws JSONException {
        type = SymmetryType.fromString(jsonObject.getString("type"));
        color = SymmetryColor.fromString(jsonObject.getString("color"));
    }

    public SymmetryType getType() {
        return type;
    }

    public boolean hasColor() {
        return color != SymmetryColor.NONE;
    }

    public SymmetryColor getPrimaryColor() {
        return color;
    }

    public SymmetryColor getSecondaryColor() {
        if (color == SymmetryColor.CYAN2) {
            return SymmetryColor.YELLOW2;
        }
        return SymmetryColor.YELLOW;
    }

    public JSONObject serialize() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", type.toString());
        jsonObject.put("color", color.toString());
        return jsonObject;
    }
}
