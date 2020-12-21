package com.aren.thewitnesspuzzle.puzzle.base.graph;

import com.aren.thewitnesspuzzle.math.Vector2;
import com.aren.thewitnesspuzzle.puzzle.base.PuzzleBase;
import com.aren.thewitnesspuzzle.puzzle.base.rules.EndingPointRule;

import org.json.JSONException;
import org.json.JSONObject;

public class Edge extends GraphElement {

    public Vertex from, to;

    public Edge(PuzzleBase puzzleBase, Vertex from, Vertex to) {
        super(puzzleBase, puzzleBase.getNextEdgeIndex(), (from.x + to.x) / 2, (from.y + to.y) / 2);
        this.from = from;
        this.to = to;

        from.adj.add(to);
        to.adj.add(from);
    }

    public Edge(PuzzleBase puzzleBase, JSONObject jsonObject) {
        super(puzzleBase, jsonObject);
        try {
            int from = jsonObject.getInt("from");
            int to = jsonObject.getInt("to");
            this.from = puzzleBase.getVertex(from);
            this.to = puzzleBase.getVertex(to);
        } catch (JSONException ignored) {

        }
    }

    public boolean containsVertex(Vertex vertex) {
        return from == vertex || to == vertex;
    }

    public Vertex getAnotherVertex(Vertex vertex) {
        if (vertex == from) return to;
        else return from;
    }

    public float getLength() {
        return (float) Math.sqrt((from.x - to.x) * (from.x - to.x) + (from.y - to.y) * (from.y - to.y));
    }

    public float getAngle() {
        return (float) Math.atan2(to.y - from.y, to.x - from.x);
    }

    public float getDistance(Vector2 point) {
        Vector2 startToEnd = new Vector2(to.x - from.x, to.y - from.y);
        Vector2 startToPoint = new Vector2(point.x - from.x, point.y - from.y);
        Vector2 endToPoint = new Vector2(point.x - to.x, point.y - to.y);

        if (startToEnd.dot(startToPoint) < 0) {
            return from.getPosition().distance(point);
        }
        if (startToEnd.dot(endToPoint) > 0) {
            return to.getPosition().distance(point);
        }

        Vector2 proj = startToPoint.projection(startToEnd);
        return proj.subtract(startToPoint).length();
    }

    public boolean isEndingEdge() {
        return from.getRule() instanceof EndingPointRule || to.getRule() instanceof EndingPointRule;
    }

    public boolean isHorizontal(){
        return Math.abs(from.y - to.y) < 1e-5;
    }

    @Override
    protected void serialize(JSONObject jsonObject) throws JSONException {
        super.serialize(jsonObject);
        jsonObject.put("from", from.index);
        jsonObject.put("to", to.index);
    }
}
