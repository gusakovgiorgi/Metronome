package ua.com.gusakov.metronome;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

/**
 * Created by hasana on 6/27/2016.
 */
public class Starter {

    public static Indication getIndicator() {
        return indicator;
    }

    private static Indication indicator;
    private boolean isServiceRunning=false;
    Runnable setIndicator;
    Handler h;
    int bpm;
    Context ctx;
    int audioFile;
    Intent serviceIntent;

    Starter(Context ctx,Handler h,Runnable setIndicator, int audioFile){
        this.h=h;
        this.setIndicator=setIndicator;
        this.ctx=ctx;
        this.audioFile=audioFile;
        indicator=new Indication(h,setIndicator);
    }

    public void stop(){
        if(isServiceRunning) {
            stopMetronomeService();

        }

    }

    public void restart(int bpm){
        this.bpm=bpm;
        stop();
        start(bpm);


    }
    private void stopMetronomeService() {
        ctx.stopService(serviceIntent);
    }


    public void start(int bpm){
        isServiceRunning=true;
        this.bpm=bpm;
        startMetronomeService();
    }

    private void startMetronomeService() {
        serviceIntent =new Intent(ctx,MetronomeService.class);
        serviceIntent.putExtra("bpm", bpm);
        ctx.startService(serviceIntent);
    }

}
