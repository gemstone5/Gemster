package core;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;

import com.gemstone5.gemster.R;

/**
 * Created by WONSEOK OH on 2016-12-11.
 */

public class SoundManager {

    private static MediaPlayer mBGM;
    private static SoundPool mSoundPool;
    private static int mSoundIdClick;

    public enum SoundEnum {CLICK}

    public static void init(Activity activity) {
        mBGM = MediaPlayer.create(activity, R.raw.background_sound);
        mBGM.setVolume(0.5f, 0.5f);
        mBGM.setLooping(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSoundPool = new SoundPool.Builder()
                    .setMaxStreams(10)
                    .build();
        } else {
            mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);
        }
        mSoundIdClick = mSoundPool.load(activity, R.raw.sm_click, 1);

    }

    public static void startBGM() {
        mBGM.start();
    }

    public static void pauseBGM() {
        mBGM.pause();
    }

    public static void stopBGM() {
        mBGM.stop();
    }

    public static void startSound(SoundEnum sound) {
        if (SoundEnum.CLICK.equals(sound)) {
            mSoundPool.play(mSoundIdClick, 1, 1, 1, 0, 1);
        }
    }

}
