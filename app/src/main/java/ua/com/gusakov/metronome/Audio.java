package ua.com.gusakov.metronome;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.io.IOException;

public class Audio {
    String tag = "mytag";
    SoundPool sp;
    int audioFile;
    Context ctx;

    public Audio(Context ctx) {
        this.ctx = ctx;

        //get audioStream
        sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        try {
            audioFile = sp.load(ctx.getAssets().openFd("metronome.wav"), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void playSound(int bpm) {

        sp.play(audioFile, 1, 1, 0, 0, 1);

    }

}
