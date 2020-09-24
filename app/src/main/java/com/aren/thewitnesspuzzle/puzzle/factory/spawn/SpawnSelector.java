package com.aren.thewitnesspuzzle.puzzle.factory.spawn;

import com.aren.thewitnesspuzzle.puzzle.cursor.area.Area;
import com.aren.thewitnesspuzzle.puzzle.cursor.area.GridAreaSplitter;
import com.aren.thewitnesspuzzle.puzzle.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.graph.GraphElement;
import com.aren.thewitnesspuzzle.puzzle.graph.Tile;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;

import java.util.List;
import java.util.Map;
import java.util.Random;

public interface SpawnSelector {

    <T> List<T> select(List<T> elements, Random random);

}
