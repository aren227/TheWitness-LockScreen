package com.aren.thewitnesspuzzle.puzzle.rules;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class Colorable extends Rule {

    public Color color;

    public Colorable(Color color) {
        super();
        this.color = color;
    }

    public Colorable(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
        this.color = Color.fromString(jsonObject.getString("color"));
    }

    public void serialize(JSONObject jsonObject) throws JSONException {
        super.serialize(jsonObject);
        jsonObject.put("color", color.toString());
    }

}
