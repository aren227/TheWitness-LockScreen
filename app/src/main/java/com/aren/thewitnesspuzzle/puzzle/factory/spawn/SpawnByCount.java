package com.aren.thewitnesspuzzle.puzzle.factory.spawn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SpawnByCount implements SpawnSelector {

    public int count;

    public SpawnByCount(int count){
        this.count = count;
    }

    @Override
    public <T> List<T> select(List<T> elements, Random random) {
        Collections.shuffle(elements, random);

        List<T> list = new ArrayList<>();
        for(int i = 0; i < Math.min(elements.size(), count); i++) {
            list.add(elements.get(i));
        }
        return list;
    }
}
