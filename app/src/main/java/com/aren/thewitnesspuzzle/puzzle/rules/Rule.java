package com.aren.thewitnesspuzzle.puzzle.rules;

import com.aren.thewitnesspuzzle.graphics.shape.Shape;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.graph.GraphElement;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class Rule {

    private GraphElement graphElement;

    public boolean eliminated;

    protected Shape shape;

    public Rule() {

    }

    public Rule(JSONObject jsonObject) {

    }

    public GraphElement getGraphElement() {
        return graphElement;
    }

    public void setGraphElement(GraphElement graphElement) {
        this.graphElement = graphElement;
    }

    public Shape generateShape() {
        return null;
    }

    public Shape getShape() {
        if (shape == null) {
            shape = generateShape();
        }
        return shape;
    }

    public boolean validateLocally(Cursor cursor) {
        return true;
    }

    public boolean canValidateLocally() {
        return true;
    }

    public abstract String getName();

    public JSONObject serialize() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        serialize(jsonObject);
        return jsonObject;
    }

    public void serialize(JSONObject jsonObject) throws JSONException {
        jsonObject.put("type", getName());
    }

    public static Rule deserialize(JSONObject jsonObject) throws JSONException {
        String type = jsonObject.getString("type");
        switch (type) {
            case BlocksRule.NAME:
                return new BlocksRule(jsonObject);
            case BrokenLineRule.NAME:
                return new BrokenLineRule(jsonObject);
            case EliminationRule.NAME:
                return new EliminationRule(jsonObject);
            case EndingPointRule.NAME:
                return new EndingPointRule(jsonObject);
            case HexagonRule.NAME:
                return new HexagonRule(jsonObject);
            case SquareRule.NAME:
                return new SquareRule(jsonObject);
            case SquareVertexRule.NAME:
                return new SquareVertexRule(jsonObject);
            case StartingPointRule.NAME:
                return new StartingPointRule(jsonObject);
            case SunRule.NAME:
                return new SunRule(jsonObject);
            case TrianglesRule.NAME:
                return new TrianglesRule(jsonObject);
        }
        return null;
    }

}
