package com.aren.thewitnesspuzzle.puzzle.base.rules;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class SymmetricColorable extends RuleBase {

    public SymmetryColor color;

    public SymmetricColorable() {
        super();
        this.color = SymmetryColor.NONE;
    }

    public SymmetricColorable(SymmetryColor color) {
        super();
        this.color = color;
    }

    public SymmetricColorable(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
        color = SymmetryColor.fromString(jsonObject.getString("color"));
    }

    public SymmetryColor getSymmetricColor() {
        return color;
    }

    public void setSymmetricColor(SymmetryColor color) {
        this.color = color;
    }

    public boolean hasSymmetricColor() {
        return color != SymmetryColor.NONE;
    }

    @Override
    public void serialize(JSONObject jsonObject) throws JSONException {
        jsonObject.put("color", color.toString());
    }

}
