package com.aren.thewitnesspuzzle.puzzle.graph;

import com.aren.thewitnesspuzzle.math.Vector2;
import com.aren.thewitnesspuzzle.math.Vector2Int;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.rules.Rule;

import org.json.JSONException;
import org.json.JSONObject;

public class GraphElement {

    public int index;
    public float x, y;

    private Rule rule;

    public void setRule(Rule rule) {
        if (rule == null) return;
        this.rule = rule;
        rule.setGraphElement(this);
    }

    public Rule getRule() {
        return rule;
    }

    public void removeRule() {
        rule = null;
    }

    public Vector2 getPosition() {
        return new Vector2(x, y);
    }

    public int getGridX(){
        return Math.round(x);
    }

    public int getGridY(){
        return Math.round(y);
    }

    public Vector2Int getGridPosition(){
        return new Vector2Int(getGridX(), getGridY());
    }

    public JSONObject serialize() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        serialize(jsonObject);
        return jsonObject;
    }

    protected void serialize(JSONObject jsonObject) throws JSONException {
        jsonObject.put("index", index);
        jsonObject.put("x", x);
        jsonObject.put("y", y);

        if(rule != null)
            jsonObject.put("rule", rule.serialize());
    }

    protected void baseDeserialize(JSONObject jsonObject) throws JSONException {
        index = jsonObject.getInt("index");
        x = (float)jsonObject.getDouble("x");
        y = (float)jsonObject.getDouble("y");

        if(jsonObject.has("rule"))
            rule = Rule.deserialize(jsonObject.getJSONObject("rule"));
    }

}
