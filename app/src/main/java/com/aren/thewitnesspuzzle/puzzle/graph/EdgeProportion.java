package com.aren.thewitnesspuzzle.puzzle.graph;

import com.aren.thewitnesspuzzle.math.MathUtils;
import com.aren.thewitnesspuzzle.math.Vector2;
import com.aren.thewitnesspuzzle.puzzle.cursor.Cursor;

public class EdgeProportion {

    public Edge edge;
    public float proportion; // edge.from -> edge.to [0, 1]
    public boolean reverse; // If it's true, edge.to -> edge.from

    public EdgeProportion(Edge edge) {
        this.edge = edge;
    }

    public void reverse() {
        proportion = 1 - proportion;
        reverse = !reverse;
    }

    public void updateProportion(Cursor cursor, float proportion) {
        cursor.updateProportionWithCollision(this, this.proportion, proportion);
    }

    public Vector2 getProportionPoint() {
        if (reverse)
            return new Vector2(MathUtils.lerp(edge.to.x, edge.from.x, proportion), MathUtils.lerp(edge.to.y, edge.from.y, proportion));
        return new Vector2(MathUtils.lerp(edge.from.x, edge.to.x, proportion), MathUtils.lerp(edge.from.y, edge.to.y, proportion));
    }

    public Vector2 getProportionMiddlePoint() {
        if (reverse)
            return new Vector2(MathUtils.lerp(edge.to.x, edge.from.x, proportion * 0.5f), MathUtils.lerp(edge.to.y, edge.from.y, proportion * 0.5f));
        return new Vector2(MathUtils.lerp(edge.from.x, edge.to.x, proportion * 0.5f), MathUtils.lerp(edge.from.y, edge.to.y, proportion * 0.5f));
    }

    public float getProportionFromPointOutside(Vector2 point) {
        Vector2 startToEnd = new Vector2(edge.to.x - edge.from.x, edge.to.y - edge.from.y);
        Vector2 startToPoint = new Vector2(point.x - edge.from.x, point.y - edge.from.y);

        if (startToEnd.dot(startToPoint) < 0) return reverse ? 1 : 0;

        Vector2 proj = startToPoint.projection(startToEnd);

        if (reverse) return Math.max(1 - proj.length() / startToEnd.length(), 0);
        return Math.min(proj.length() / startToEnd.length(), 1);
    }

    public float getProportionFromVertex(Vertex vertex) {
        if (vertex == edge.from) return reverse ? 1 : 0;
        return reverse ? 0 : 1;
    }

    public float getProportionLength() {
        return edge.getLength() * proportion;
    }

    public Vertex from() {
        return reverse ? edge.to : edge.from;
    }

    public Vertex to() {
        return reverse ? edge.from : edge.to;
    }

}
