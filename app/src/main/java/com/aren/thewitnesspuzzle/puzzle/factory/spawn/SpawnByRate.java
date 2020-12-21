package com.aren.thewitnesspuzzle.puzzle.factory.spawn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SpawnByRate implements SpawnSelector {

    private float rate;

    public SpawnByRate(float rate){
        this.rate = rate;
    }

    @Override
    public <T> List<T> select(List<T> elements, Random random) {
        Collections.shuffle(elements, random);

        int count = (int)(elements.size() * rate);
        List<T> list = new ArrayList<>();
        for(int i = 0; i < count; i++) {
            list.add(elements.get(i));
        }
        return list;
    }
}
