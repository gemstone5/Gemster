package ui.monsterbook;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by WONSEOK OH on 2016-12-10.
 */

public class MonsterBookCustomImageView extends ImageView {

    public MonsterBookCustomImageView(Context context) {
        super(context);
    }

    public MonsterBookCustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MonsterBookCustomImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec); // This is the key that will make the height equivalent to its width
    }
}
