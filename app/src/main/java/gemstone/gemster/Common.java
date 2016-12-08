package gemstone.gemster;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;

/**
 * Created by WONSEOK OH on 2016-12-04.
 */

public class Common {

    private static final String PREF_MAIN = "pref_main";
    public static final String MAIN_INIT = "main_init";
    public static final String MAIN_TIER = "main_tier";
    public static final String MAIN_DNA = "main_DNA";
    public static final String MAIN_DNA_USE = "main_DNA_use";

    public static final int DEBUG_DEFAULT = 900;
    public static final int DEBUG_CHECK_DNA_TIME = 901;
    public static final int DEBUG_CHECK_EVOLUTION_TIME = 902;

    public static final long TIME_DELAY = 10L;
    public static final long DNA_TIME = 0L;
    public static final long EVOLUTION_TIME = 1000; // 2sec

    public static void setPrefData(Context context, String key, String value) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_MAIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, String.valueOf(value));
        editor.commit();
    }

    private static boolean isIntegerValue(String key) {
        return MAIN_TIER.equals(key) || MAIN_DNA.equals(key) || MAIN_DNA_USE.equals(key);
    }

    public static String getDefaultValue(String key) {
        int result;
        if (MAIN_INIT.equals(key) || MAIN_DNA_USE.equals(key)) result = 1;
        else result = 0;

        return String.valueOf(result);
    }

    public static Object getPrefData(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_MAIN, Context.MODE_PRIVATE);
        String value = prefs.getString(key, "");

        if (isIntegerValue(key)) {
            if ("".equals(value)) return 0;
            return Integer.parseInt(value);
        } else {
            return value;
        }
    }

    public static void initSharedPrefData(Context context) {
        // init shared pref values if have not initialized yet
        if ("".equals(getPrefData(context, MAIN_INIT))) {
            setPrefData(context, MAIN_INIT, getDefaultValue(MAIN_INIT));
            setPrefData(context, MAIN_TIER, getDefaultValue(MAIN_TIER));
            setPrefData(context, MAIN_DNA, getDefaultValue(MAIN_DNA));
            setPrefData(context, MAIN_DNA_USE, getDefaultValue(MAIN_DNA_USE));
        }
    }

    public static boolean isMaxTier(Context context) {
        int tier = (int) Common.getPrefData(context, Common.MAIN_TIER);
        TypedArray arrPerProb = context.getResources().obtainTypedArray(R.array.array_evol_name);
        if (tier + 1 >= arrPerProb.length()) {
            return true;
        }
        return false;
    }

}
