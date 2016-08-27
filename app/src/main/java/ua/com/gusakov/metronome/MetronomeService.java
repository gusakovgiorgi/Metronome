package ua.com.gusakov.metronome;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.google.android.gms.ads.InterstitialAd;

import java.util.concurrent.TimeUnit;

public class MetronomeService extends Service {
    Thread serviceThread;
    Indication indicator;
    Audio audio;
    Vibration vibrator;
    Camera camera;
    int bpm;
    boolean lightIsOn=false;
    public MetronomeService() {
    }

    @Override
    public void onDestroy() {
        serviceThread.interrupt();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        bpm=intent.getIntExtra("bpm",100);
        indicator=Starter.getIndicator();
        audio=new Audio(getApplicationContext());
        vibrator=new Vibration(getApplicationContext());
        camera=Initialization.getCamera();
        serviceThread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(true) {
                        TimeUnit.MILLISECONDS.sleep(60000 / bpm);
                        indicator.indicate();
                        if(Initialization.isVibrationButtonPressed()){
                            vibrator.vibrate();
                        }
                        if(!Initialization.isNoSoundImageButtonPressed()) {
                            audio.playSound(bpm);
                        }
                        if(Initialization.isFlashButtonPressed()){
                            if(lightIsOn) {
                                camera.turnOnFlashLight();
                                lightIsOn=false;
                            }else{
                                camera.turnOffFlashLight();
                                lightIsOn=true;

                            }
                        }
                    }
                } catch (InterruptedException e) {
                    camera.turnOffFlashLight();
                    e.printStackTrace();
                }

            }
        });
        serviceThread.start();
        return super.onStartCommand(intent, flags, startId);
    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
