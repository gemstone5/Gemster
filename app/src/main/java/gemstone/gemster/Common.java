package gemstone.gemster;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by WONSEOK OH on 2016-12-04.
 */

public class Common {

    private static final String PREF_MAIN = "pref_main";
    public static final String MAIN_INIT = "main_init";
    public static final String MAIN_TIER = "main_tier";
    public static final String MAIN_FEED = "main_feed";

    public static final String INTEGER_STRING_DEFAULT_VALUE = "0";

    public static final int DEBUG_DEFAULT = 900;
    public static final int DEBUG_CHECK_FEED_TIME = 901;
    public static final int DEBUG_CHECK_EVOLUTION_TIME = 902;

    public static final long TIME_DELAY = 10L;
    public static final long FEED_TIME = 500L; // 3sec
    public static final long EVOLUTION_TIME = 1000; // 2sec

    public static void setPrefData(Context context, String key, String value) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_MAIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, String.valueOf(value));
        boolean result = editor.commit();
    }

    public static Object getPrefData(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_MAIN, Context.MODE_PRIVATE);
        String value = prefs.getString(key, "");

        if (MAIN_TIER.equals(key) || MAIN_FEED.equals(key)) {
            if ("".equals(value)) return 0;
            return Integer.parseInt(value);
        } else {
            return value;
        }
    }

    public static void initSharedPrefData(Context context) {
        // init shared pref values if have not initialized yet
        if (!"".equals(Common.getPrefData(context, Common.MAIN_INIT))) {
            Common.setPrefData(context, Common.MAIN_INIT, "1");
            Common.setPrefData(context, Common.MAIN_TIER, "0");
            Common.setPrefData(context, Common.MAIN_FEED, "0");
        }
    }

}
