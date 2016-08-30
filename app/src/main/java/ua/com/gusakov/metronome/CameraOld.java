package ua.com.gusakov.metronome;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by hasana on 8/30/2016.
 */
public class CameraOld extends Camera{
    Context ctx;
    android.hardware.Camera camera;
    android.hardware.Camera.Parameters params;
    MediaPlayer mp;

    private boolean hasFlash;

    CameraOld(Context ctx){
        this.ctx=ctx;
        init();
    }

    @Override
    void turnOffFlashLight() {

        if (camera == null || params == null) {
            return;
        }

        params = camera.getParameters();
        params.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(params);
        camera.stopPreview();
    }

    @Override
    void turnOnFlashLight() {
            if (camera == null || params == null) {
                return;
            }

            params = camera.getParameters();
            params.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
            camera.startPreview();
    }

    private void init(){
        /*
 * First check if device is supporting flashlight or not
 */
        hasFlash = ctx.getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!hasFlash) {
            // device doesn't support flash
            // Show alert messag
            Toast.makeText(ctx,R.string.no_flash_message,Toast.LENGTH_LONG).show();
            return;
        }

        if (camera == null) {
            try {
                camera = android.hardware.Camera.open();
                params = camera.getParameters();
            } catch (RuntimeException e) {
                Log.e("Camera Error", e.getMessage());
            }
        }
    }

    @Override
    void close() {
        // on stop release the camera
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

}
