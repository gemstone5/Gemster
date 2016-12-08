package gemstone.gemster;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by WONSEOK OH on 2016-12-08.
 */

public class MonsterBookActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_monster_book);
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_hold);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_hold, R.anim.anim_slide_out_left);
    }
}
