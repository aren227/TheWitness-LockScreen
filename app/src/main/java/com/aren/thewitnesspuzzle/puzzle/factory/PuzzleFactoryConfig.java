package com.aren.thewitnesspuzzle.puzzle.factory;

import android.content.Context;
import android.content.SharedPreferences;

import com.aren.thewitnesspuzzle.puzzle.base.color.PuzzleColorPalette;
import com.aren.thewitnesspuzzle.puzzle.base.rules.Color;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PuzzleFactoryConfig {

    SharedPreferences sharedPreferences;
    UUID factoryUuid;
    JSONObject jsonObject = new JSONObject();

    String factoryType;

    public PuzzleFactoryConfig(Context context, UUID factoryUuid) {
        sharedPreferences = context.getSharedPreferences(PuzzleFactoryManager.sharedPreferenceConfigKey, Context.MODE_PRIVATE);
        this.factoryUuid = factoryUuid;

        if (sharedPreferences.contains(factoryUuid.toString())) {
            try {
                String jsonStr = sharedPreferences.getString(factoryUuid.toString(), "{}");
                jsonObject = new JSONObject(jsonStr);
                factoryType = jsonObject.getString("factoryType");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            setLong("created_at", System.currentTimeMillis() / 1000L);
        }
    }

    public void setFactoryType(String type) {
        factoryType = type;
        setString("factoryType", type);
    }

    public String getFactoryType() {
        return factoryType;
    }

    public long getCreationTimestamp() {
        return getLong("created_at", 0);
    }

    public void setString(String key, String val) {
        try {
            jsonObject.put(key, val);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getString(String key, String def) {
        try {
            return jsonObject.getString(key);
        } catch (JSONException ignored) {

        }
        return def;
    }

    public void setInt(String key, int val) {
        try {
            jsonObject.put(key, val);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getInt(String key, int def) {
        try {
            return jsonObject.getInt(key);
        } catch (JSONException ignored) {

        }
        return def;
    }

    public void setLong(String key, long val) {
        try {
            jsonObject.put(key, val);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public long getLong(String key, long def) {
        try {
            return jsonObject.getLong(key);
        } catch (JSONException ignored) {

        }
        return def;
    }


    public void setBoolean(String key, boolean val) {
        try {
            jsonObject.put(key, val);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean getBoolean(String key, boolean def) {
        try {
            return jsonObject.getBoolean(key);
        } catch (JSONException ignored) {

        }
        return def;
    }

    public void setFloat(String key, float val) {
        try {
            jsonObject.put(key, val);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public float getFloat(String key, float def) {
        try {
            return (float) jsonObject.getDouble(key);
        } catch (JSONException ignored) {

        }
        return def;
    }

    public void setIntList(String key, List<Integer> val) {
        try {
            JSONArray arr = new JSONArray(val.toArray());
            jsonObject.put(key, arr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<Integer> getIntList(String key, List<Integer> def) {
        try {
            JSONArray array = jsonObject.getJSONArray(key);
            List<Integer> result = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                result.add(array.getInt(i));
            }
            return result;
        } catch (JSONException ignored) {

        }
        return def;
    }

    public void setColorList(String key, List<Color> val) {
        try {
            List<String> strList = new ArrayList<>();
            for (Color color : val) strList.add(color.toString());
            JSONArray arr = new JSONArray(strList.toArray());
            jsonObject.put(key, arr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<Color> getColorList(String key, List<Color> def) {
        try {
            JSONArray array = jsonObject.getJSONArray(key);
            List<Color> result = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                Color color = Color.fromString(array.getString(i));
                if (color != null) result.add(color);
            }
            return result;
        } catch (JSONException ignored) {

        }
        return def;
    }

    public void setColorPalette(String key, PuzzleColorPalette val) {
        try {
            jsonObject.put(key, val.serialize());
        } catch (JSONException ignored) {

        }
    }

    public PuzzleColorPalette getColorPalette(String key, PuzzleColorPalette def) {
        try {
            JSONObject colorObj = jsonObject.getJSONObject(key);
            def.set(PuzzleColorPalette.deserialize(colorObj));
        } catch (JSONException ignored) {

        }
        return def;
    }

    public boolean containsKey(String key) {
        return jsonObject.has(key);
    }

    public UUID getUuid() {
        return factoryUuid;
    }

    public void save() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(factoryUuid.toString(), jsonObject.toString());
        editor.commit();
    }

}
