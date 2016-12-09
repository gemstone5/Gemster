package ui;

import android.app.Activity;
import android.os.Bundle;

import core.gemster.R;

public class MainActivity extends Activity {

    private MainManager mMainManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMainManager = new MainManager(this);
    }

    @Override
    public void onBackPressed() {
        if (mMainManager.backPressed()) {
            return;
        }
        super.onBackPressed();
    }
}
