package gemstone.gemster;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private MainCoreManager mMainCoreManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Common.initSharedPrefData(this);
        mMainCoreManager = new MainCoreManager(this, this);
    }

    public void resetForDebug(View v) {
        mMainCoreManager.call(MainCoreManager.CallMode.RESET_FOR_DEBUG);
    }
}
