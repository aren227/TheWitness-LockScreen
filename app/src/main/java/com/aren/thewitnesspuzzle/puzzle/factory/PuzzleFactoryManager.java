package com.aren.thewitnesspuzzle.puzzle.factory;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class PuzzleFactoryManager {

    public static final String sharedPreferenceConfigKey = "com.aren.thewitnesspuzzle.puzzle.factory.config";
    public static final String sharedPreferenceProfilesKey = "com.aren.thewitnesspuzzle.puzzle.factory.profiles";

    public static final UUID defaultProfileUuid = new UUID(0, 0);

    private Context context;
    private static Map<UUID, PuzzleFactory> factories = new HashMap<>();

    private Runnable onUpdate;

    public PuzzleFactoryManager(Context context) {
        this.context = context;
        updateFactoryList();
    }

    private void updateFactoryList() {
        registerBuiltInFactories();
        registerUserDefinedFactories();
    }

    public void setOnUpdate(Runnable runnable) {
        onUpdate = runnable;
    }

    public List<PuzzleFactory> getAllPuzzleFactories() {
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

    public PuzzleFactory getPuzzleFactoryByUuid(UUID uuid) {
        // updateFactoryList();

        if (!factories.containsKey(uuid)) return null;
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
    private void sort(List<PuzzleFactory> factories) {
        Collections.sort(factories, new Comparator<PuzzleFactory>() {
            @Override
            public int compare(PuzzleFactory o1, PuzzleFactory o2) {
                if (o1.isCreatedByUser() && o2.isCreatedByUser()) {
                    return -Long.compare(o1.getConfig().getCreationTimestamp(), o2.getConfig().getCreationTimestamp());
                } else if (o1.isCreatedByUser() != o2.isCreatedByUser()) {
                    return Integer.compare(o1.isCreatedByUser() ? 0 : 1, o2.isCreatedByUser() ? 0 : 1);
                } else {
                    int a = -1;
                    if (o1.getDifficulty() != null) a = o1.getDifficulty().ordinal();
                    int b = -1;
                    if (o2.getDifficulty() != null) b = o2.getDifficulty().ordinal();
                    if(a == b) return o1.getName().compareTo(o2.getName());
                    return Integer.compare(a, b);
                }
            }
        });
    }

    private void registerBuiltInFactories() {
        register(new BlocksEliminationPuzzleFactory(context));
        register(new BlocksRotatableBlocksPuzzleFactory(context));
        register(new ChallengeMaze1PuzzleFactory(context));
        register(new ChallengeMaze2PuzzleFactory(context));
        register(new ChallengeMaze3PuzzleFactory(context));
        register(new ChallengeTwoHexPuzzleFactory(context));
        register(new ChallengeMazePuzzleFactory(context));
        register(new ChallengeSymmetryPuzzleFactory(context));
        register(new ChallengeSunBlocksPuzzleFactory(context));
        register(new ChallengeSunHexagonPuzzleFactory(context));
        register(new ChallengeTwoSquarePuzzleFactory(context));
        register(new ChallengeThreeSquarePuzzleFactory(context));
        register(new ChallengeTrianglesPuzzleFactory(context));
        register(new EntryAreaMazePuzzleFactory(context));
        register(new FirstPuzzleFactory(context));
        register(new MultipleSunColorsPuzzleFactory(context));
        register(new RotatableBlocksPuzzleFactory(context));
        register(new SecondPuzzleFactory(context));
        register(new BlocksPuzzleFactory(context));
        register(new HexagonEliminationPuzzleFactory(context));
        register(new HexagonPuzzleFactory(context));
        register(new MazePuzzleFactory(context));
        register(new PSymmetryPuzzleFactory(context));
        register(new SquareEliminationPuzzleFactory(context));
        register(new SquarePuzzleFactory(context));
        register(new SunPuzzleFactory(context));
        register(new SunSquarePuzzleFactory(context));
        register(new SwampLockPuzzleFactory(context));
        register(new TriangleLockPuzzleFactory(context));
        register(new TrianglesPuzzleFactory(context));
        register(new VSymmetryPuzzleFactory(context));
        register(new SlidePuzzleFactory(context));
        register(new SunBlockPuzzleFactory(context));
        register(new SunEliminationPuzzleFactory(context));
        register(new SunPairWithSquarePuzzleFactory(context));
        register(new SymmetryHexagonPuzzleFactory(context));
    }

    private void registerUserDefinedFactories() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PuzzleFactoryManager.sharedPreferenceConfigKey, Context.MODE_PRIVATE);
        List<String> factoryUuidStrList = new ArrayList<>(sharedPreferences.getAll().keySet());

        for (String uuidStr : factoryUuidStrList) {
            try {
                UUID uuid = UUID.fromString(uuidStr);
                PuzzleFactoryConfig config = new PuzzleFactoryConfig(context, uuid);

                if (config.getFactoryType().equals("pattern")) {
                    CustomPatternPuzzleFactory factory = new CustomPatternPuzzleFactory(context, uuid);
                    register(factory);
                } else if (config.getFactoryType().equals("random")) {
                    CustomRandomPuzzleFactory factory = new CustomRandomPuzzleFactory(context, uuid);
                    register(factory);
                } else if (config.getFactoryType().equals("fixed")) {
                    CustomFixedPuzzleFactory factory = new CustomFixedPuzzleFactory(context, uuid);
                    register(factory);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void register(PuzzleFactory puzzleFactory) {
        if (factories.containsKey(puzzleFactory.getUuid())) return;
        factories.put(puzzleFactory.getUuid(), puzzleFactory);
    }

    public void remove(PuzzleFactory puzzleFactory) {
        if (!puzzleFactory.isCreatedByUser()) return;
        factories.remove(puzzleFactory.getUuid());

        SharedPreferences sharedPreferences = context.getSharedPreferences(PuzzleFactoryManager.sharedPreferenceConfigKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(puzzleFactory.getUuid().toString());
        editor.commit();
    }

    public PuzzleFactory getPuzzleFactoryByName(String name) {
        for (PuzzleFactory factory : factories.values()) {
            if (factory.getName().equals(name)) return factory;
        }
        return null;
    }

    public Profile getLastViewedProfile() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.aren.thewitnesspuzzle.puzzle.factory", Context.MODE_PRIVATE);
        UUID uuid = UUID.fromString(sharedPreferences.getString("last", defaultProfileUuid.toString()));
        return new Profile(context, uuid);
    }

    public Profile getLockProfile() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.aren.thewitnesspuzzle.puzzle.factory", Context.MODE_PRIVATE);
        UUID uuid = UUID.fromString(sharedPreferences.getString("lock", defaultProfileUuid.toString()));
        return new Profile(context, uuid);
    }

    public Profile getPlayProfile() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.aren.thewitnesspuzzle.puzzle.factory", Context.MODE_PRIVATE);
        UUID uuid = UUID.fromString(sharedPreferences.getString("play", defaultProfileUuid.toString()));
        return new Profile(context, uuid);
    }

    public Profile getDefaultProfile() {
        return new Profile(context, defaultProfileUuid);
    }

    public List<Profile> getProfiles() {
        Set<String> defaultSet = new HashSet<>();
        defaultSet.add(defaultProfileUuid.toString());

        SharedPreferences sharedPreferences = context.getSharedPreferences("com.aren.thewitnesspuzzle.puzzle.factory", Context.MODE_PRIVATE);
        List<Profile> list = new ArrayList<>();
        for (String strUuid : sharedPreferences.getStringSet("profiles", defaultSet)) {
            list.add(new Profile(context, UUID.fromString(strUuid)));
        }

        Collections.sort(list, new Comparator<Profile>() {
            @Override
            public int compare(Profile o1, Profile o2) {
                return -Long.compare(o1.getCreationTime(), o2.getCreationTime());
            }
        });
        return list;
    }

    public Profile createProfile(String name, ProfileType type) {
        Profile profile = new Profile(context, UUID.randomUUID());
        profile.setName(name);
        profile.setCreationTime(System.currentTimeMillis());
        profile.setType(type);
        return profile;
    }

    public void removeProfile(Profile profile) {
        if (profile.uuid.equals(defaultProfileUuid)) return;

        if (getLockProfile().equals(profile)) {
            getDefaultProfile().assignToLock();
        }
        if (getPlayProfile().equals(profile)) {
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
        editor.remove(profile.uuid.toString() + "/sequence");
        editor.commit();

        getProfiles().get(0).markAsLastViewed();
    }

    public boolean isRemovedProfile(Profile profile) {
        return !getProfiles().contains(profile);
    }

    public enum ProfileType{
        DEFAULT, SEQUENCE;

        public static ProfileType fromString(String str) {
            for (ProfileType type : ProfileType.values()) {
                if (type.toString().equalsIgnoreCase(str)) {
                    return type;
                }
            }
            return null;
        }
    }

    public class Profile {

        private Context context;
        private UUID uuid;

        public Profile(Context context, UUID uuid) {
            this.context = context;
            this.uuid = uuid;

            SharedPreferences sharedPreferences = context.getSharedPreferences("com.aren.thewitnesspuzzle.puzzle.factory", Context.MODE_PRIVATE);
            // Must clone it before use
            Set<String> set = new HashSet<>(sharedPreferences.getStringSet("profiles", new HashSet<String>()));
            set.add(uuid.toString());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet("profiles", set);
            editor.commit();
        }

        public String getName() {
            if (uuid.equals(defaultProfileUuid)) {
                return getSharedPreferences().getString(uuid.toString() + "/name", "Default");
            }
            return getSharedPreferences().getString(uuid.toString() + "/name", "No Name");
        }

        public void setName(String name) {
            SharedPreferences.Editor editor = getSharedPreferences().edit();
            editor.putString(uuid.toString() + "/name", name);
            editor.commit();
        }

        public long getCreationTime() {
            return getSharedPreferences().getLong(uuid.toString() + "/creation_time", 0);
        }

        public void setCreationTime(long time) {
            SharedPreferences.Editor editor = getSharedPreferences().edit();
            editor.putLong(uuid.toString() + "/creation_time", time);
            editor.commit();
        }

        public ProfileType getType(){
            return ProfileType.fromString(getSharedPreferences().getString(uuid.toString() + "/type", "DEFAULT"));
        }

        public void setType(ProfileType type){
            SharedPreferences.Editor editor = getSharedPreferences().edit();
            editor.putString(uuid.toString() + "/type", type.toString());
            editor.commit();
        }

        public File getMusicFile(){
            File file = new File(context.getFilesDir(), uuid.toString() + "_music");
            return file;
        }

        public String getMusicName(){
            return getSharedPreferences().getString(uuid.toString() + "/music", "No Name");
        }

        public boolean hasMusic(){
            return getSharedPreferences().contains(uuid.toString() + "/music");
        }

        public void removeMusic(){
            SharedPreferences.Editor editor = getSharedPreferences().edit();
            editor.remove(uuid.toString() + "/music");

            File file = new File(context.getFilesDir(), uuid.toString() + "_music");
            if(file.exists()) file.delete();

            editor.commit();
        }

        public void setMusic(InputStream stream, String name){
            try{
                // Write to file
                BufferedInputStream bis = new BufferedInputStream(stream);

                File file = new File(context.getFilesDir(), uuid.toString() + "_music");
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buf = new byte[1024];
                int len;
                while((len = bis.read(buf)) > 0){
                    fos.write(buf, 0,len);
                }
                fos.close();
                bis.close();

                SharedPreferences.Editor editor = getSharedPreferences().edit();
                editor.putString(uuid.toString() + "/music", name);
                editor.commit();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        public List<PuzzleFactory> getActivatedPuzzleFactories() {
            List<PuzzleFactory> list = new ArrayList<>();
            for (String strUuid : getSharedPreferences().getStringSet(uuid.toString() + "/activated", new HashSet<String>())) {
                PuzzleFactory factory = getPuzzleFactoryByUuid(UUID.fromString(strUuid));
                if (factory != null) list.add(factory);
            }
            return list;
        }

        public PuzzleFactory getRandomPuzzleFactory(Random random) {
            List<PuzzleFactory> activated = getActivatedPuzzleFactories();
            if (activated.size() == 0) return null;

            String lastUuid = getSharedPreferences().getString(uuid.toString() + "/last", "");

            List<PuzzleFactory> candidates = new ArrayList<>();
            for (PuzzleFactory factory : activated) {
                if (!factory.getUuid().toString().equals(lastUuid)) {
                    candidates.add(factory);
                }
            }

            if (candidates.size() == 0) return activated.get(0);

            PuzzleFactory selected = candidates.get(random.nextInt(candidates.size()));

            SharedPreferences.Editor editor = getSharedPreferences().edit();
            editor.putString(uuid.toString() + "/last", selected.getUuid().toString());
            editor.commit();

            return selected;
        }

        public void setActivated(PuzzleFactory factory, boolean activated) {
            // Must clone it before use
            Set<String> set = new HashSet<>(getSharedPreferences().getStringSet(uuid.toString() + "/activated", new HashSet<String>()));
            if (activated) set.add(factory.getUuid().toString());
            else set.remove(factory.getUuid().toString());

            SharedPreferences.Editor editor = getSharedPreferences().edit();
            editor.putStringSet(uuid.toString() + "/activated", set);
            editor.commit();

            if (onUpdate != null) onUpdate.run();
        }

        public boolean isActivated(PuzzleFactory factory) {
            Set<String> set = getSharedPreferences().getStringSet(uuid.toString() + "/activated", new HashSet<String>());
            return set.contains(factory.getUuid().toString());
        }

        public List<PuzzleFactory> getSequence(){
            List<PuzzleFactory> list = new ArrayList<>();
            String[] strs = getSharedPreferences().getString(uuid.toString() + "/sequence", "").split(",");
            for (String strUuid : strs) {
                try{
                    PuzzleFactory factory = getPuzzleFactoryByUuid(UUID.fromString(strUuid));
                    if (factory != null) list.add(factory);
                }
                catch (Exception e){

                }
            }
            return list;
        }

        public void setSequence(List<PuzzleFactory> seq){
            StringBuilder builder = new StringBuilder();
            for(int i = 0; i < seq.size(); i++){
                if(i > 0){
                    builder.append(",");
                }
                builder.append(seq.get(i).getUuid());
            }

            SharedPreferences.Editor editor = getSharedPreferences().edit();
            editor.putString(uuid.toString() + "/sequence", builder.toString());
            editor.commit();
        }

        public int getTimeLength(){
            return getSharedPreferences().getInt(uuid.toString() + "/time_length", 90);
        }

        public void setTimeLength(int length){
            SharedPreferences.Editor editor = getSharedPreferences().edit();
            editor.putInt(uuid.toString() + "/time_length", length);
            editor.commit();
        }

        public void assignToLock() {
            SharedPreferences sharedPreferences = context.getSharedPreferences("com.aren.thewitnesspuzzle.puzzle.factory", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("lock", uuid.toString());
            editor.commit();
        }

        public void assignToPlay() {
            SharedPreferences sharedPreferences = context.getSharedPreferences("com.aren.thewitnesspuzzle.puzzle.factory", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("play", uuid.toString());
            editor.commit();
        }

        public void markAsLastViewed() {
            SharedPreferences sharedPreferences = context.getSharedPreferences("com.aren.thewitnesspuzzle.puzzle.factory", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("last", uuid.toString());
            editor.commit();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Profile) {
                return ((Profile) obj).uuid.equals(uuid);
            }
            return false;
        }

        public SharedPreferences getSharedPreferences() {
            return context.getSharedPreferences(PuzzleFactoryManager.sharedPreferenceProfilesKey, Context.MODE_PRIVATE);
        }
    }
}
