package ui.monsterbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import core.Common;
import core.gemster.R;

/**
 * Created by WONSEOK OH on 2016-12-10.
 */

public class MonsterBookImageAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Integer> mGemDrawableIdList;

    public MonsterBookImageAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mGemDrawableIdList = Common.getGemDrawableIdList(mContext);
    }

    @Override
    public int getCount() {
        return mGemDrawableIdList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        if (view == null) {
            view = mInflater.inflate(R.layout.layout_monster_book_item, parent, false);
        }

        MonsterBookCustomImageView imageViewThumbnail = (MonsterBookCustomImageView) view.findViewById(R.id.MBI_imageView_thumbnail);
        imageViewThumbnail.setImageResource(mGemDrawableIdList.get(position));

        return view;
    }
}
