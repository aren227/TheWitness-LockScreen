package com.aren.thewitnesspuzzle.puzzle.animation;

import com.aren.thewitnesspuzzle.puzzle.Puzzle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PuzzleAnimation {

    private List<Animation> animations;
    private List<Animation> tempQueue;
    private boolean lock;

    public PuzzleAnimation(Puzzle puzzle){
        animations = new ArrayList<>();
        tempQueue = new ArrayList<>();
        lock = false;
    }

    public void process(){
        lock = true;
        Iterator<Animation> iterator = animations.iterator();
        while(iterator.hasNext()){
            Animation animation = iterator.next();
            if(!animation.process()){
                iterator.remove();
            }
        }
        lock = false;

        animations.addAll(tempQueue);
        tempQueue.clear();
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
        if(lock) tempQueue.add(animation);
        else animations.add(animation);
    }

}
