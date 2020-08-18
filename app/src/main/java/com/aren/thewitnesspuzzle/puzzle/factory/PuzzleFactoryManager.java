package com.aren.thewitnesspuzzle.puzzle.factory;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PuzzleFactoryManager {

    private Context context;
    private Map<UUID, PuzzleFactory> factories = new HashMap<>();

    public PuzzleFactoryManager(Context context){
        this.context = context;

        register(new MultipleSunColorsPuzzleFactory());
        register(new RotatableBlocksPuzzleFactory());
        register(new SecondPuzzleFactory());
        register(new SimpleBlocksPuzzleFactory());
        register(new SimpleHexagonEliminationPuzzleFactory());
        register(new SimpleHexagonPuzzleFactory());
        register(new SimpleMazePuzzleFactory());
        register(new SimplePSymmetryPuzzleFactory());
        register(new SimpleSquareEliminationPuzzleFactory());
        register(new SimpleSquarePuzzleFactory());
        register(new SimpleSunPuzzleFactory());
        register(new SimpleSunSquarePuzzleFactory());
        register(new SimpleVSymmetryPuzzleFactory());
        register(new SlidePuzzleFactory());
        register(new SunPairWithSquarePuzzleFactory());
        register(new SymmetryHexagonPuzzleFactory());
    }

    public PuzzleFactory getPuzzleFactoryByUuid(UUID uuid){
        if(factories.containsKey(uuid)) return factories.get(uuid);
        return null;
    }

    public List<PuzzleFactory> getAllPuzzleFactories(){
        List<PuzzleFactory> list = new ArrayList<>(factories.values());
        sort(list);
        return list;
    }

    public List<PuzzleFactory> getActivatedPuzzleFactories(){
        List<PuzzleFactory> list = new ArrayList<>();
        for(UUID uuid : factories.keySet()){
            if(isActivated(uuid)){
                list.add(factories.get(uuid));
            }
        }
        return list;
    }

    public boolean isActiavted(PuzzleFactory factory){
        return isActivated(factory.getUuid());
    }

    public boolean isActivated(UUID uuid){
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.aren.thewitnesspuzzle.puzzle.factory", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(uuid.toString(), false);
    }

    public void setActivated(PuzzleFactory factory, boolean activated){
        setActivated(factory.getUuid(), activated);
    }

    public void setActivated(UUID uuid, boolean activated){
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.aren.thewitnesspuzzle.puzzle.factory", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(uuid.toString(), activated);
        editor.commit();
    }

    //TODO: Sorting options
    private void sort(List<PuzzleFactory> factories){
        Collections.sort(factories, new Comparator<PuzzleFactory>() {
            @Override
            public int compare(PuzzleFactory o1, PuzzleFactory o2) {
                return Integer.compare(o1.getDifficulty().ordinal(), o2.getDifficulty().ordinal());
            }
        });
    }

    public void register(PuzzleFactory puzzleFactory){
        factories.put(puzzleFactory.getUuid(), puzzleFactory);
    }
}
