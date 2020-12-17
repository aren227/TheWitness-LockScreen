package com.aren.thewitnesspuzzle.puzzle.base;

import com.aren.thewitnesspuzzle.math.Vector2;
import com.aren.thewitnesspuzzle.puzzle.color.PuzzleColorPalette;
import com.aren.thewitnesspuzzle.puzzle.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.graph.Tile;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.Rule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PuzzleBase {

    protected ArrayList<Vertex> vertices = new ArrayList<>();
    protected ArrayList<Edge> edges = new ArrayList<>();
    protected Edge[][] edgeTable; // Indexed by two vertices pair
    protected ArrayList<Tile> tiles = new ArrayList<>();

    protected PuzzleColorPalette color;

    public PuzzleBase(PuzzleColorPalette color){
        this.color = color;
    }

    public void setColorPalette(PuzzleColorPalette color) {
        this.color = color;
    }

    public PuzzleColorPalette getColorPalette() {
        return color;
    }

    public Vertex addVertex(Vertex vertex){
        vertex.index = vertices.size();
        vertices.add(vertex);
        return vertex;
    }

    public Vertex getVertex(int index) {
        for (Vertex vertex : vertices) {
            if (vertex.index == index) return vertex;
        }
        return null;
    }

    public List<Vertex> getConnectedVertices(Vertex vertex) {
        List<Vertex> result = new ArrayList<>();
        for (Edge edge : getEdges()) {
            if (edge.from == vertex) {
                result.add(edge.to);
            } else if (edge.to == vertex) {
                result.add(edge.from);
            }
        }
        return result;
    }

    public Edge addEdge(int va, int vb){
        Edge edge = new Edge(getVertex(va), getVertex(vb));
        return addEdge(edge);
    }

    public Edge addEdge(Edge edge) {
        edge.index = edges.size();
        edges.add(edge);
        edge.from.adj.add(edge.to);
        edge.to.adj.add(edge.from);
        return edge;
    }

    public Tile addTile(Tile tile) {
        tile.index = tiles.size();
        tiles.add(tile);
        return tile;
    }

    public Edge getNearestEdge(Vector2 pos) {
        float minDist = Float.MAX_VALUE;
        Edge minEdge = null;
        for (Edge edge : edges) {
            float dist = edge.getDistance(pos);
            if (dist < minDist) {
                minDist = dist;
                minEdge = edge;
            }
        }
        return minEdge;
    }

    public ArrayList<Vertex> getVertices() {
        return vertices;
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public ArrayList<Tile> getTiles() {
        return tiles;
    }

    private void calcEdgeTable() {
        edgeTable = new Edge[getVertices().size()][getVertices().size()];
        for (Edge edge : getEdges()) {
            edgeTable[edge.from.index][edge.to.index] = edge;
            edgeTable[edge.to.index][edge.from.index] = edge;
        }
    }

    public Edge getEdgeByVertex(Vertex from, Vertex to) {
        if (edgeTable == null) calcEdgeTable();
        return edgeTable[from.index][to.index];
    }

    public List<Rule> getAllRules() {
        List<Rule> rules = new ArrayList<>();
        for (Vertex vertex : vertices) {
            if (vertex.getRule() != null) {
                rules.add(vertex.getRule());
            }
        }
        for (Edge edge : edges) {
            if (edge.getRule() != null) {
                rules.add(edge.getRule());
            }
        }
        for (Tile tile : tiles) {
            if (tile.getRule() != null) {
                rules.add(tile.getRule());
            }
        }
        return rules;
    }

    public JSONObject serialize() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        JSONArray vertexArray = new JSONArray();
        for(Vertex vertex : vertices)
            vertexArray.put(vertex.serialize());
        jsonObject.put("vertices", vertexArray);

        JSONArray edgeArray = new JSONArray();
        for(Edge edge : edges)
            edgeArray.put(edge.serialize());
        jsonObject.put("edges", edgeArray);

        JSONArray tileArray = new JSONArray();
        for(Tile tile : tiles)
            tileArray.put(tile.serialize());
        jsonObject.put("tiles", tileArray);

        jsonObject.put("color", color.serialize());

        return jsonObject;
    }

    public static PuzzleBase deserialize(JSONObject jsonObject) throws JSONException {
        PuzzleColorPalette color = PuzzleColorPalette.deserialize(jsonObject.getJSONObject("color"));
        PuzzleBase puzzleBase = new PuzzleBase(color);

        JSONArray vertexArray = jsonObject.getJSONArray("vertices");
        for(int i = 0; i < vertexArray.length(); i++)
            puzzleBase.vertices.add(Vertex.deserialize(vertexArray.getJSONObject(i)));

        JSONArray edgeArray = jsonObject.getJSONArray("edges");
        for(int i = 0; i < edgeArray.length(); i++)
            puzzleBase.edges.add(Edge.deserialize(puzzleBase, edgeArray.getJSONObject(i)));

        JSONArray tileArray = jsonObject.getJSONArray("tiles");
        for(int i = 0; i < tileArray.length(); i++)
            puzzleBase.tiles.add(Tile.deserialize(tileArray.getJSONObject(i)));

        return puzzleBase;
    }

}
