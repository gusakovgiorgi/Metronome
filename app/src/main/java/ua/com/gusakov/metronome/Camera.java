package ua.com.gusakov.metronome;

/**
 * Created by hasana on 6/28/2016.
 */
abstract class Camera {
    abstract void turnOffFlashLight();
    abstract void turnOnFlashLight();
    private void init(){};
    abstract void close();
}
