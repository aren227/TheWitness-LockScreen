package com.aren.thewitnesspuzzle.puzzle.animation;

import com.aren.thewitnesspuzzle.puzzle.Puzzle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PuzzleAnimationManager {

    private List<Animation> animations;
    private List<Animation> tempAddQueue;
    private List<Animation> tempRemoveQueue;
    private boolean lock;

    public PuzzleAnimationManager(Puzzle puzzle){
        animations = new ArrayList<>();
        tempAddQueue = new ArrayList<>();
        tempRemoveQueue = new ArrayList<>();
        lock = false;
    }

    public void process(){
        lock = true;
        Iterator<Animation> iterator = animations.iterator();
        while(iterator.hasNext()){
            Animation animation = iterator.next();
            if(!animation.isDone() && !animation.process()){
                animation.remove();
                iterator.remove();
            }
        }
        lock = false;

        animations.addAll(tempAddQueue);
        tempAddQueue.clear();

        animations.removeAll(tempRemoveQueue);
        tempRemoveQueue.clear();
    }

    public boolean shouldUpdate(){
        for(Animation animation : animations){
            if(!animation.isDone()) return true;
        }
        return false;
    }

    public void reset(){
        for(Animation animation : animations){
            animation.remove();
        }
        animations.clear();
        tempAddQueue.clear();
        tempRemoveQueue.clear();
    }

    public void addAnimation(Animation animation){
        if(lock) tempAddQueue.add(animation);
        else animations.add(animation);
    }

    public boolean isPlaying(Class cls){
        for(Animation animation : animations){
            if(animation.getClass().equals(cls)) return true;
        }
        return false;
    }

    public List<Animation> getAnimations(Class cls){
        List<Animation> result = new ArrayList<>();
        for(Animation animation : animations){
            if(animation.getClass().equals(cls)) result.add(animation);
        }
        return result;
    }

    public void stopAnimation(Animation animation){
        animation.remove();
        if(lock) tempRemoveQueue.add(animation);
        else animations.remove(animation);
    }

    public void stopAnimation(Class cls){
        List<Animation> list = getAnimations(cls);
        for(Animation animation : animations) animation.remove();
        if(lock) tempRemoveQueue.addAll(list);
        else animations.removeAll(list);
    }

}
