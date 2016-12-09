package ui;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import core.gemster.R;
import ui.monsterbook.MonsterBookFragment;
import ui.monstermain.MonsterMainFragment;

public class MainActivity extends Activity implements MonsterMainFragment.EventListener {

    FragmentManager mFragmentManager;
    MonsterMainFragment mMonsterMainFragment;
    MonsterBookFragment mMonsterBookFragment;

    private final String FRAGMENT_TAG_MONSTER_BOOK = "fragment_monster_book";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFragmentManager();
        initMainFragment();
        initMonsterBookFragment();
    }

    private void initFragmentManager() {
        mFragmentManager = getFragmentManager();
    }

    private void initMainFragment() {
        mMonsterMainFragment = (MonsterMainFragment) mFragmentManager.findFragmentById(R.id.fragment_main);
        mMonsterMainFragment.setEventListener(this);
    }

    private void initMonsterBookFragment() {
        mMonsterBookFragment = MonsterBookFragment.newInstance();
    }

    protected void openMonsterBook() {
        mMonsterMainFragment.setTouchable(false);
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.setCustomAnimations(R.animator.anim_slide_in_left, R.animator.anim_slide_out_left);
        ft.add(R.id.layout_main_monster_book, mMonsterBookFragment, FRAGMENT_TAG_MONSTER_BOOK);
        ft.commit();
    }

    @Override
    public void onMainFragmentEvent(MonsterMainFragment.EventMode mode) {
        if (MonsterMainFragment.EventMode.EVENT_OPEN_MONSTER_BOOK.equals(mode)) {
            openMonsterBook();
        }
    }

    private void removeMonsterBookFragment() {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.setCustomAnimations(R.animator.anim_slide_in_left, R.animator.anim_slide_out_left);
        ft.remove(mMonsterBookFragment).commit();
        mMonsterMainFragment.setTouchable(true);
    }

    @Override
    public void onBackPressed() {
        if (mMonsterBookFragment != null && mMonsterBookFragment.isVisible()) {
            removeMonsterBookFragment();
            return;
        }
        super.onBackPressed();
    }
}
