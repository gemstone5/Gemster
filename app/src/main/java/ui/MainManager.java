package ui;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import core.gemster.R;
import ui.monsterbook.MonsterBookFragment;
import ui.monstermain.MonsterMainFragment;

/**
 * Created by WONSEOK OH on 2016-12-09.
 */

public class MainManager implements MonsterMainFragment.EventListener {

    private Activity mActivity;

    private FragmentManager mFragmentManager;
    private MonsterMainFragment mMonsterMainFragment;
    private MonsterBookFragment mMonsterBookFragment;

    private final String FRAGMENT_TAG_MONSTER_BOOK = "fragment_monster_book";

    public MainManager(Activity activity) {
        mActivity = activity;

        initFragmentManager();
        initMainFragment();
        initMonsterBookFragment();
    }

    private void initFragmentManager() {
        mFragmentManager = mActivity.getFragmentManager();
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

    public boolean backPressed() {
        if (mMonsterBookFragment != null && mMonsterBookFragment.isVisible()) {
            removeMonsterBookFragment();
            return true;
        }
        return false;
    }
}
