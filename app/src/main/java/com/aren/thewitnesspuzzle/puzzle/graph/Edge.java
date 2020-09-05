package com.aren.thewitnesspuzzle.puzzle.graph;

import com.aren.thewitnesspuzzle.math.Vector2;
import com.aren.thewitnesspuzzle.puzzle.rules.EndingPointRule;

public class Edge extends GraphElement {

    public Vertex from, to;
    public boolean isHorizontal; // Only for grid puzzle

    public Edge(Vertex from, Vertex to) {
        super(from.puzzle);
        this.from = from;
        this.to = to;
        this.x = getMiddlePoint().x;
        this.y = getMiddlePoint().y;
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

    public Vector2 getMiddlePoint() {
        return new Vector2((from.x + to.x) / 2, (from.y + to.y) / 2);
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

    @Override
    public Vector2 getPosition() {
        return getMiddlePoint();
    }

    public boolean isEndingEdge() {
        return from.getRule() instanceof EndingPointRule || to.getRule() instanceof EndingPointRule;
    }

}
