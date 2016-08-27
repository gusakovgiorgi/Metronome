package ua.com.gusakov.metronome;

import android.os.Handler;

/**
 * Created by hasana on 6/26/2016.
 */
public class Indication{

    Runnable setIndicator;
    Handler h;
    Indication(Handler h,Runnable setIndicator){
        this.h=h;
        this.setIndicator=setIndicator;
    }

    public void indicate() {
                h.post(setIndicator);

    }
}
