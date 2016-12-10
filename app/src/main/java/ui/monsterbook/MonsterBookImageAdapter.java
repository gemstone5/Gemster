package ui.monsterbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import core.Common;
import core.gemster.R;
import core.monsterbook.MonsterBookItem;

/**
 * Created by WONSEOK OH on 2016-12-10.
 */

public class MonsterBookImageAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<MonsterBookItem> mMonsterBookItemList;

    public MonsterBookImageAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mMonsterBookItemList = Common.getMonsterBookItemList(mContext);
    }

    @Override
    public int getCount() {
        return mMonsterBookItemList.size() * 20;
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

        MonsterBookItem item = mMonsterBookItemList.get(position % mMonsterBookItemList.size());

        if (view == null) {
            view = mInflater.inflate(R.layout.layout_monster_book_item, parent, false);
        }

        MonsterBookCustomImageView imageViewThumbnail = (MonsterBookCustomImageView) view.findViewById(R.id.MBI_imageView_thumbnail);
        if (!Common.isCollected(mContext, item.mMonsterKey)) {
            Glide.with(mContext).load(R.drawable.ic_mbi_question_mark).into(imageViewThumbnail);
        } else {
            int resourceId = mMonsterBookItemList.get(position % mMonsterBookItemList.size()).mResourceId;
            Glide.with(mContext).load(resourceId).into(imageViewThumbnail);
        }

        return view;
    }
}
