package ui;

import android.app.Activity;
import android.os.Bundle;

import com.gemstone5.gemster.R;

public class MainActivity extends Activity {

    private MainManager mMainManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMainManager = new MainManager(this);
    }

    @Override
    protected void onUserLeaveHint() {
        mMainManager.onUserLeaveHint();
        super.onUserLeaveHint();
    }

    @Override
    protected void onResume() {
        mMainManager.resume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mMainManager.pause();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (mMainManager.backPressed()) {
            return;
        }
        super.onBackPressed();
    }
}
