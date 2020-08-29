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

    public static final String sharedPreferenceConfigKey = "com.aren.thewitnesspuzzle.puzzle.factory.config";

    private Context context;
    private Map<UUID, PuzzleFactory> factories = new HashMap<>();

    private Runnable onUpdate;

    public PuzzleFactoryManager(Context context){
        this.context = context;
        registerBuiltInFactories();
        registerUserDefinedFactories();
    }

    public void setOnUpdate(Runnable runnable){
        onUpdate = runnable;
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

        if(onUpdate != null) onUpdate.run();
    }

    //TODO: Sorting options
    private void sort(List<PuzzleFactory> factories){
        Collections.sort(factories, new Comparator<PuzzleFactory>() {
            @Override
            public int compare(PuzzleFactory o1, PuzzleFactory o2) {
                if(o1.isCreatedByUser() != o2.isCreatedByUser()){
                    return Integer.compare(o1.isCreatedByUser() ? 0 : 1, o2.isCreatedByUser() ? 0 : 1);
                }

                int a = -1;
                if(o1.getDifficulty() != null) a = o1.getDifficulty().ordinal();
                int b = -1;
                if(o2.getDifficulty() != null) b = o2.getDifficulty().ordinal();
                return Integer.compare(a, b);
            }
        });
    }

    private void registerBuiltInFactories(){
        register(new BlocksEliminationPuzzleFactory(context));
        register(new BlocksRotatableBlocksPuzzleFactory(context));
        register(new ChallengeSunBlocksPuzzleFactory(context));
        register(new ChallengeSunHexagonPuzzleFactory(context));
        register(new ChallengeTrianglesPuzzleFactory(context));
        register(new EntryAreaMazePuzzleFactory(context));
        register(new FirstPuzzleFactory(context));
        register(new MultipleSunColorsPuzzleFactory(context));
        register(new RotatableBlocksPuzzleFactory(context));
        register(new SecondPuzzleFactory(context));
        register(new SimpleBlocksPuzzleFactory(context));
        register(new SimpleHexagonEliminationPuzzleFactory(context));
        register(new SimpleHexagonPuzzleFactory(context));
        register(new SimpleMazePuzzleFactory(context));
        register(new SimplePSymmetryPuzzleFactory(context));
        register(new SimpleSquareEliminationPuzzleFactory(context));
        register(new SimpleSquarePuzzleFactory(context));
        register(new SimpleSunPuzzleFactory(context));
        register(new SimpleSunSquarePuzzleFactory(context));
        register(new SimpleTrianglesPuzzleFactory(context));
        register(new SimpleVSymmetryPuzzleFactory(context));
        register(new SlidePuzzleFactory(context));
        register(new SunBlockPuzzleFactory(context));
        register(new SunEliminationPuzzleFactory(context));
        register(new SunPairWithSquarePuzzleFactory(context));
        register(new SymmetryHexagonPuzzleFactory(context));
    }

    private void registerUserDefinedFactories(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PuzzleFactoryManager.sharedPreferenceConfigKey, Context.MODE_PRIVATE);
        List<String> factoryUuidStrList = new ArrayList<>(sharedPreferences.getAll().keySet());

        for(String uuidStr : factoryUuidStrList){
            try{
                UUID uuid = UUID.fromString(uuidStr);
                PuzzleFactoryConfig config = new PuzzleFactoryConfig(context, uuid);

                if(config.getFactoryType().equals("pattern")){
                    CustomPatternPuzzleFactory factory = new CustomPatternPuzzleFactory(context, uuid);
                    register(factory);
                }
                else if(config.getFactoryType().equals("random")){
                    CustomRandomPuzzleFactory factory = new CustomRandomPuzzleFactory(context, uuid);
                    register(factory);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void register(PuzzleFactory puzzleFactory){
        factories.put(puzzleFactory.getUuid(), puzzleFactory);
    }

    public PuzzleFactory getPuzzleFactoryByName(String name){
        for(PuzzleFactory factory : factories.values()){
            if(factory.getName().equals(name)) return factory;
        }
        return null;
    }
}
