package ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import core.Common;
import core.MainCoreManager;
import core.gemster.R;

/**
 * Created by WONSEOK OH on 2016-12-09.
 */

public class MainFragment extends Fragment {

    private Context mContext;
    private Activity mActivity;
    private MainCoreManager mMainCoreManager;

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
        return inflater.inflate(R.layout.layout_main_game, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMainCoreManager = new MainCoreManager(mContext, mActivity);
    }
}
