package com.aren.thewitnesspuzzle.puzzle.factory;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PuzzleFactoryManager {

    public static final String sharedPreferenceConfigKey = "com.aren.thewitnesspuzzle.puzzle.factory.config";
    public static final String sharedPreferenceProfilesKey = "com.aren.thewitnesspuzzle.puzzle.factory.profiles";

    public static final UUID defaultProfileUuid = new UUID(0, 0);

    private Context context;
    private static Map<UUID, PuzzleFactory> factories = new HashMap<>();

    private Runnable onUpdate;

    public PuzzleFactoryManager(Context context){
        this.context = context;
        updateFactoryList();
    }

    private void updateFactoryList(){
        registerBuiltInFactories();
        registerUserDefinedFactories();
    }

    public void setOnUpdate(Runnable runnable){
        onUpdate = runnable;
    }

    public List<PuzzleFactory> getAllPuzzleFactories(){
        updateFactoryList();

        List<PuzzleFactory> list = new ArrayList<>(factories.values());
        sort(list);
        return list;
    }

    /*public List<PuzzleFactory> getActivatedPuzzleFactories(){
        updateFactoryList();

        List<PuzzleFactory> list = new ArrayList<>();
        for(UUID uuid : factories.keySet()){
            if(isActivated(uuid)){
                list.add(factories.get(uuid));
            }
        }
        return list;
    }*/

    public PuzzleFactory getPuzzleFactoryByUuid(UUID uuid){
        updateFactoryList();

        if(!factories.containsKey(uuid)) return null;
        return factories.get(uuid);
    }

    /*public boolean isActiavted(PuzzleFactory factory){
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
    }*/

    //TODO: Sorting options
    private void sort(List<PuzzleFactory> factories){
        Collections.sort(factories, new Comparator<PuzzleFactory>() {
            @Override
            public int compare(PuzzleFactory o1, PuzzleFactory o2) {
                if(o1.isCreatedByUser() && o2.isCreatedByUser()){
                    return -Long.compare(o1.getConfig().getCreationTimestamp(), o2.getConfig().getCreationTimestamp());
                }
                else if(o1.isCreatedByUser() != o2.isCreatedByUser()){
                    return Integer.compare(o1.isCreatedByUser() ? 0 : 1, o2.isCreatedByUser() ? 0 : 1);
                }
                else{
                    int a = -1;
                    if(o1.getDifficulty() != null) a = o1.getDifficulty().ordinal();
                    int b = -1;
                    if(o2.getDifficulty() != null) b = o2.getDifficulty().ordinal();
                    return Integer.compare(a, b);
                }
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
        if(factories.containsKey(puzzleFactory.getUuid())) return;
        factories.put(puzzleFactory.getUuid(), puzzleFactory);
    }

    public void remove(PuzzleFactory puzzleFactory){
        if(!puzzleFactory.isCreatedByUser()) return;
        factories.remove(puzzleFactory.getUuid());

        SharedPreferences sharedPreferences = context.getSharedPreferences(PuzzleFactoryManager.sharedPreferenceConfigKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(puzzleFactory.getUuid().toString());
        editor.commit();
    }

    public PuzzleFactory getPuzzleFactoryByName(String name){
        for(PuzzleFactory factory : factories.values()){
            if(factory.getName().equals(name)) return factory;
        }
        return null;
    }

    public Profile getLastViewedProfile(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.aren.thewitnesspuzzle.puzzle.factory", Context.MODE_PRIVATE);
        UUID uuid = UUID.fromString(sharedPreferences.getString("last", defaultProfileUuid.toString()));
        return new Profile(context, uuid);
    }

    public Profile getLockProfile(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.aren.thewitnesspuzzle.puzzle.factory", Context.MODE_PRIVATE);
        UUID uuid = UUID.fromString(sharedPreferences.getString("lock", defaultProfileUuid.toString()));
        return new Profile(context, uuid);
    }

    public Profile getPlayProfile(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.aren.thewitnesspuzzle.puzzle.factory", Context.MODE_PRIVATE);
        UUID uuid = UUID.fromString(sharedPreferences.getString("play", defaultProfileUuid.toString()));
        return new Profile(context, uuid);
    }

    public Profile getDefaultProfile(){
        return new Profile(context, defaultProfileUuid);
    }

    public List<Profile> getProfiles(){
        Set<String> defaultSet = new HashSet<>();
        defaultSet.add(defaultProfileUuid.toString());

        SharedPreferences sharedPreferences = context.getSharedPreferences("com.aren.thewitnesspuzzle.puzzle.factory", Context.MODE_PRIVATE);
        List<Profile> list = new ArrayList<>();
        for(String strUuid : sharedPreferences.getStringSet("profiles", defaultSet)){
            list.add(new Profile(context, UUID.fromString(strUuid)));
        }
        return list;
    }

    public Profile createProfile(String name){
        Profile profile = new Profile(context, UUID.randomUUID());
        profile.setName(name);
        return profile;
    }

    public void removeProfile(Profile profile){
        if(profile.uuid.equals(defaultProfileUuid)) return;

        if(getLockProfile().equals(profile)){
            getDefaultProfile().assignToLock();
        }
        if(getPlayProfile().equals(profile)){
            getDefaultProfile().assignToPlay();
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences("com.aren.thewitnesspuzzle.puzzle.factory", Context.MODE_PRIVATE);
        Set<String> set = sharedPreferences.getStringSet("profiles", new HashSet<String>());
        set.remove(profile.uuid.toString());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("profiles", set);
        editor.commit();

        sharedPreferences = context.getSharedPreferences(PuzzleFactoryManager.sharedPreferenceProfilesKey, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.remove(profile.uuid.toString() + "/name");
        editor.remove(profile.uuid.toString() + "/activated");
        editor.commit();
    }

    public boolean isRemovedProfile(Profile profile){
        return !getProfiles().contains(profile);
    }

    public class Profile{

        private Context context;
        private SharedPreferences sharedPreferences;
        private UUID uuid;

        public Profile(Context context, UUID uuid){
            this.context = context;
            this.sharedPreferences = context.getSharedPreferences(PuzzleFactoryManager.sharedPreferenceProfilesKey, Context.MODE_PRIVATE);
            this.uuid = uuid;

            SharedPreferences sharedPreferences = context.getSharedPreferences("com.aren.thewitnesspuzzle.puzzle.factory", Context.MODE_PRIVATE);
            Set<String> set = sharedPreferences.getStringSet("profiles", new HashSet<String>());
            set.add(uuid.toString());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet("profiles", set);
            editor.commit();
        }

        public String getName(){
            if(uuid.equals(defaultProfileUuid)){
                return sharedPreferences.getString(uuid.toString() + "/name", "Default");
            }
            return sharedPreferences.getString(uuid.toString() + "/name", "No Name");
        }

        public void setName(String name){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(uuid.toString() + "/name", name);
            editor.commit();
        }

        public List<PuzzleFactory> getActivatedPuzzleFactories(){
            List<PuzzleFactory> list = new ArrayList<>();
            for(String strUuid : sharedPreferences.getStringSet(uuid.toString() + "/activated", new HashSet<String>())){
                PuzzleFactory factory = getPuzzleFactoryByUuid(UUID.fromString(strUuid));
                if(factory != null) list.add(factory);
            }
            return list;
        }

        public void setActivated(PuzzleFactory factory, boolean activated){
            Set<String> set = sharedPreferences.getStringSet(uuid.toString() + "/activated", new HashSet<String>());
            if(activated) set.add(factory.getUuid().toString());
            else set.remove(factory.getUuid().toString());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet(uuid.toString() + "/activated", set);
            editor.commit();

            if(onUpdate != null) onUpdate.run();
        }

        public boolean isActivated(PuzzleFactory factory){
            Set<String> set = sharedPreferences.getStringSet(uuid.toString() + "/activated", new HashSet<String>());
            return set.contains(factory.getUuid().toString());
        }

        public void assignToLock(){
            SharedPreferences sharedPreferences = context.getSharedPreferences("com.aren.thewitnesspuzzle.puzzle.factory", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("lock", uuid.toString());
            editor.commit();
        }

        public void assignToPlay(){
            SharedPreferences sharedPreferences = context.getSharedPreferences("com.aren.thewitnesspuzzle.puzzle.factory", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("play", uuid.toString());
            editor.commit();
        }

        public void markAsLastViewed(){
            SharedPreferences sharedPreferences = context.getSharedPreferences("com.aren.thewitnesspuzzle.puzzle.factory", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("last", uuid.toString());
            editor.commit();
        }

        @Override
        public boolean equals(Object obj){
            if(obj instanceof Profile){
                return ((Profile)obj).uuid.equals(uuid);
            }
            return false;
        }
    }
}
