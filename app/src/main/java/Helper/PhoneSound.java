package Helper;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * Created by 王彦鹏 on 2017-09-02.
 */

public class PhoneSound {


    public static void play(Context ctx, int soundID) {
        SoundPool mSoundPlayer = new SoundPool(10, AudioManager.STREAM_SYSTEM, 0);
        mSoundPlayer.load(ctx,soundID, 1);
        mSoundPlayer.setOnLoadCompleteListener(
                new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                        soundPool.play(sampleId, 1, 1, 1, 0, 1);
                    }
                }
        );
    }
}
