package core.monsterbook;

import core.Common;

/**
 * Created by WONSEOK OH on 2016-12-10.
 */

public class MonsterBookItem {
    public String mMonsterKey;
    public int mResourceId;
    public int mSpec;
    public int mTier;

    public MonsterBookItem(int spec, int tier, int resourceId) {
        mSpec = spec;
        mTier = tier;
        mResourceId = resourceId;
        mMonsterKey = Common.getMonsterKey(spec, tier);
    }
}
