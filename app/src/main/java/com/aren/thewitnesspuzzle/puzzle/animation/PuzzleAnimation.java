package com.aren.thewitnesspuzzle.puzzle.animation;

import com.aren.thewitnesspuzzle.puzzle.Puzzle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PuzzleAnimation {

    private List<Animation> animations;

    public PuzzleAnimation(Puzzle puzzle){
        animations = new ArrayList<>();
    }

    public void process(){
        Iterator<Animation> iterator = animations.iterator();
        while(iterator.hasNext()){
            Animation animation = iterator.next();
            if(!animation.process()){
                iterator.remove();
            }
        }
    }

    public boolean shouldUpdate(){
        return animations.size() > 0;
    }

    public void reset(){
        for(Animation animation : animations){
            animation.done();
        }
        animations.clear();
    }

    public void addAnimation(Animation animation){
        animations.add(animation);
    }

}
