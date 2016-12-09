package ui.monstermain;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import core.Common;
import core.gemster.R;
import core.monstermain.MonsterMainCoreManager;

/**
 * Created by WONSEOK OH on 2016-12-09.
 */

public class MonsterMainFragment extends Fragment implements MonsterMainCoreManager.EventListener {

    private Context mContext;
    private Activity mActivity;
    private MonsterMainCoreManager mMonsterMainCoreManager;

    public enum EventMode {
        EVENT_OPEN_MONSTER_BOOK, EVENT_EVOLUTION_SUCCESS
    }

    private EventListener mListener;

    public interface EventListener {
        void onMainFragmentEvent(EventMode mode);
    }

    public void setEventListener(EventListener listener) {
        mListener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mActivity = getActivity();
        Common.initSharedPrefData(mContext);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_monster_main, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMonsterMainCoreManager = new MonsterMainCoreManager(mContext, mActivity);
        mMonsterMainCoreManager.setEventListener(this);
    }

    public void setTouchable(boolean state) {
        mMonsterMainCoreManager.setTouchable(state);
    }

    private void openMonsterBook() {
        mListener.onMainFragmentEvent(EventMode.EVENT_OPEN_MONSTER_BOOK);
    }

    private void updateMonsterBook() {
        mListener.onMainFragmentEvent(EventMode.EVENT_EVOLUTION_SUCCESS);
    }

    @Override
    public void onMainFragmentEvent(MonsterMainCoreManager.EventMode mode) {
        if (MonsterMainCoreManager.EventMode.EVENT_OPEN_MONSTER_BOOK.equals(mode)) {
            openMonsterBook();
        } else if (MonsterMainCoreManager.EventMode.EVENT_EVOLUTION_SUCCESS.equals(mode)) {
            updateMonsterBook();
        }
    }
}
