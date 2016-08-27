package ua.com.gusakov.metronome;

import android.content.Context;
import android.os.Vibrator;

/**
 * Created by hasana on 6/28/2016.
 */
public class Vibration {
    private  Vibrator vibrator;
    private  Context ctx;
    private  long[] pattern = {
            0,  // Start immediately
            100
    };

    Vibration(Context ctx){
        this.ctx=ctx;
        //get vibrator
        vibrator = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void vibrate(){
        //vibrate for 120 miliseconds once
        vibrator.vibrate(pattern,-1);
    }
}
