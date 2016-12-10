package core;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import core.gemster.R;
import core.monsterbook.MonsterBookItem;

/**
 * Created by WONSEOK OH on 2016-12-04.
 */

public class Common {

    private static final String PREF_MAIN = "pref_main";
    public static final String MAIN_INIT = "main_init";
    public static final String MAIN_SPEC = "main_spec";
    public static final String MAIN_TIER = "main_tier";
    public static final String MAIN_DNA = "main_DNA";
    public static final String MAIN_DNA_USE = "main_DNA_use";
    public static final String MAIN_DATA_COLLECT = "main_data_collect"; // JSON Object

    public static final int DEBUG_DEFAULT = 900;
    public static final int DEBUG_CHECK_DNA_TIME = 901;
    public static final int DEBUG_CHECK_EVOLUTION_TIME = 902;

    public static final long TIME_DELAY = 10L;
    public static final long DNA_TIME = 0L;
    public static final long EVOLUTION_TIME = 1000; // 2sec

    public static void setPrefData(Context context, String key, String value) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_MAIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getMonsterKey(int spec, int tier) {
        return spec + "_" + tier;
    }

    public static void setPrefData(Context context, String key, HashMap<String, Boolean> map) {
        JSONObject jsonObject = new JSONObject(map);
        setPrefData(context, key, jsonObject.toString());
    }

    private static boolean isIntegerValue(String key) {
        return MAIN_SPEC.equals(key) || MAIN_TIER.equals(key) || MAIN_DNA.equals(key) || MAIN_DNA_USE.equals(key);
    }

    public static String getDefaultValue(String key) {
        int result;
        if (MAIN_INIT.equals(key) || MAIN_DNA_USE.equals(key)) result = 1;
        else result = 0;

        return String.valueOf(result);
    }

    public static Object getPrefData(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_MAIN, Context.MODE_PRIVATE);
        Object value = prefs.getString(key, "");

        if (isIntegerValue(key)) {
            if ("".equals(value)) return 0;
            return Integer.parseInt((String) value);
        } else {
            return value;
        }
    }

    public static void initSharedPrefData(Context context) {
        // init shared pref values if have not initialized yet
        if ("".equals(getPrefData(context, MAIN_INIT))) {
            setPrefData(context, MAIN_INIT, getDefaultValue(MAIN_INIT));
            setPrefData(context, MAIN_SPEC, getDefaultValue(MAIN_SPEC));
            setPrefData(context, MAIN_TIER, getDefaultValue(MAIN_TIER));
            setPrefData(context, MAIN_DNA, getDefaultValue(MAIN_DNA));
            setPrefData(context, MAIN_DNA_USE, getDefaultValue(MAIN_DNA_USE));
            setIsCollected(context, 0, 0);
        }
    }

    public static boolean isExceptionalTier(Context context) {
        int maxTier = getMaxTier(context);
        int tier = (int) Common.getPrefData(context, Common.MAIN_TIER);
        if (tier + 1 >= maxTier) {
            return true;
        }
        return false;
    }

    public static int getMaxTier(Context context) {
        int spec = (int) Common.getPrefData(context, Common.MAIN_SPEC);
        int[] arrMaxTier = context.getResources().getIntArray(R.array.array_max_tier_of_spec);
        return arrMaxTier[spec];
    }

    public static void setIsCollected(Context context, int spec, int tier) {
        try {
            HashMap<String, Boolean> map = new HashMap<>();
            String data = (String) getPrefData(context, MAIN_DATA_COLLECT);
            if (data != null && !data.equals("")) {
                JSONObject jsonObject = new JSONObject(data);
                Iterator<?> keys = jsonObject.keys();

                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    Boolean value = jsonObject.getBoolean(key);
                    map.put(key, value);
                }
            }

            String key = getMonsterKey(spec, tier);
            map.put(key, true);

            setPrefData(context, MAIN_DATA_COLLECT, map);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static boolean isCollected(Context context, String monsterKey) {
        boolean isCollected = false;
        try {
            JSONObject jsonObject = new JSONObject((String) getPrefData(context, MAIN_DATA_COLLECT));
            String jsonKey = monsterKey;
            isCollected = jsonObject.getBoolean(jsonKey);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return isCollected;
    }

    public static int getGemDrawableId(Context context, int spec, int tier) {
        String name = "gem_image_";
        name += getMonsterKey(spec, tier);
        int id = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
        return id;
    }

    public static int getDNAQuantity(int tier) {
        double base = 1.1f;
        double result = Math.pow(base, tier);
        return (int) result;
    }

    public static double getPerProb(int tier) {
        double base = 0.7f;
        double result = Math.pow(base, tier + 1);
        return result;
    }

    public static ArrayList<MonsterBookItem> getMonsterBookItemList(Context context) {
        ArrayList<MonsterBookItem> mListItem = new ArrayList<>();
        int[] arrMaxTier = context.getResources().getIntArray(R.array.array_max_tier_of_spec);
        for (int idxSpec = 0; idxSpec < arrMaxTier.length; idxSpec++) {
            int maxTier = arrMaxTier[idxSpec];
            for (int idxTier = 0; idxTier < maxTier; idxTier++) {
                Integer resourceId = getGemDrawableId(context, idxSpec, idxTier);
                mListItem.add(new MonsterBookItem(idxSpec, idxTier, resourceId));
            }
        }
        return mListItem;
    }

}
