package com.aren.thewitnesspuzzle.puzzle.rules;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class SymmetricColorable extends Rule {

    public SymmetricColor color;

    public SymmetricColorable() {
        super();
        this.color = null;
    }

    public SymmetricColorable(SymmetricColor color) {
        super();
        this.color = color;
    }

    public SymmetricColor getSymmetricColor() {
        return color;
    }

    public void setSymmetricColor(SymmetricColor color) {
        this.color = color;
    }

    public boolean hasSymmetricColor() {
        return color != null;
    }

    @Override
    public void serialize(JSONObject jsonObject) throws JSONException {
        jsonObject.put("color", color.toString());
    }

}
