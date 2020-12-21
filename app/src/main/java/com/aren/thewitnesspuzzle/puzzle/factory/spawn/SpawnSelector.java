package com.aren.thewitnesspuzzle.puzzle.factory.spawn;

import java.util.List;
import java.util.Random;

public interface SpawnSelector {

    <T> List<T> select(List<T> elements, Random random);

}
