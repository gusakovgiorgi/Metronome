package ua.com.gusakov.metronome;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;

/**
 * Created by hasana on 6/24/2016.
 */
public class Initialization  implements SeekBar.OnSeekBarChangeListener {
    private Activity activity;
    private ImageView vibrationImageVew;
    private ImageView flashImageVew;
    private ImageView soundImageVew;
    private LinearLayout mainLinearLayout;
    boolean indicatorIsOn=false;
    SoundPool audioStream;

    public static Camera getCamera() {
        return camera;
    }

    private static Camera camera;
    int audioFile;                                  //audiofile id from assets folder
    ImageView minusImageView;
    ImageView plusImageView;
    private EditText bmpEditText;
    private ImageView indicatorImageView;
    final int MAX_BPM_NUMBER=200;
    int bpm;                                        //beats per minute

    SeekBar seekbar;

    String fontMuseoSans500 = "fonts/museosans500.otf";
    String fontMuseoSans100 = "fonts/museosans100.otf";
    String fontMuseoSans300 = "fonts/museosans300.otf";
    private Starter starter;
    private static boolean vibrationButtonPressed = false;

    public static boolean isFlashButtonPressed() {
        return flashButtonPressed;
    }

    public static boolean isNoSoundImageButtonPressed() {
        return noSoundImageButtonPressed;
    }


    public static boolean isVibrationButtonPressed() {
        return vibrationButtonPressed;
    }

    private static boolean flashButtonPressed = false;
    private static boolean noSoundImageButtonPressed = false;
    private boolean startButtonPressed=false;
    Handler h=new Handler();
    //run indicator in new thread
    Runnable setIndicator = new Runnable() {
        @Override
        public void run() {


            /*if (startButtonPressed) {
                indicatorImageView.setImageResource(R.drawable.indicator_no);
                indicatorIsOn = false;
            } else*/ if (indicatorIsOn) {
                indicatorImageView.setImageResource(R.drawable.indicator_no);
                indicatorIsOn = false;
            } else {
                indicatorImageView.setImageResource(R.drawable.indicator_yes);
                indicatorIsOn = true;
            }
        }
    };

    public Initialization(Activity activity){
        this.activity=activity;
    }

    public void start(){
        initial();
    }
    public  void stop(){
        camera.close();
        starter.stop();
    }




    private void initial(){
        //initial layout objects
        vibrationImageVew = (ImageView)activity.findViewById(R.id.vibration_image_view_id);
        flashImageVew = (ImageView) activity.findViewById(R.id.flash_image_view_id);
        soundImageVew = (ImageView) activity.findViewById(R.id.sound_image_view_id);
        plusImageView = (ImageView) activity.findViewById(R.id.plus_image_view_id);
        minusImageView = (ImageView) activity.findViewById(R.id.minus_image_view_id);
        seekbar = (SeekBar) activity.findViewById(R.id.seekBar);
        bmpEditText = (EditText) activity.findViewById(R.id.bmp_number_id);
        final ImageView imageButton = (ImageView) activity.findViewById(R.id.start_button_id);
        indicatorImageView = (ImageView) activity.findViewById(R.id.indicator_id);
        mainLinearLayout=(LinearLayout)activity.findViewById(R.id.main_linear_layout_id);
        seekbar.setOnSeekBarChangeListener(this);
        starter=new Starter(activity.getApplicationContext(),h,setIndicator,audioFile);
        setSerifs();
        initialAudio();
        initialCamera();

        flashImageVew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flashButtonPressed) {
                    flashActive(false);
                } else {
                    flashActive(true);
                }
            }
        });

        vibrationImageVew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vibrationButtonPressed){
                    vibrationActive(false);
                }else{
                    vibrationActive(true);
                }
            }
        });

        soundImageVew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(noSoundImageButtonPressed){
                    soundnActive(false);
                }else{
                    soundnActive(true);
                }
            }
        });

        //start button listener
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startButtonPressed) {
//                    stopService(intent);

                    imageButton.setImageResource(R.drawable.start);
                    startButtonPressed = false;
                    starter.stop();
//                    indicatorStopFlag = true;
                } else {
                    bpm = seekbar.getProgress();
//                    intent.putExtra("bpm", bpm);
//                    startService(intent);
                    starter.start(bpm);
                    imageButton.setImageResource(R.drawable.stop);
                    //start green indicator
//                    startIndication();
                    startButtonPressed = true;

                }
            }
        });

        bmpEditText.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                        if (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            return true;
                        }
                        //for android 5.0
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            setNewBPM(v);
                        }
                        if (event != null && event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            setNewBPM(v);
                            return true;
                        }
                        return false;
                    }
                });

        //plus and minus imageViews listeners
        plusImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get current number
                int bmpNumber = Integer.valueOf(bmpEditText.getText().toString());
                //increase by 1
                bmpNumber++;
                //call onProgressChangedListenre fromUser parameter is false;
                onProgressChanged(seekbar, bmpNumber, false);
            }
        });

        minusImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get current number
                int bmpNumber = Integer.valueOf(bmpEditText.getText().toString());
                //decrease by 1
                bmpNumber--;
                //call onProgressChangedListenre fromUser parameter is false;
                onProgressChanged(seekbar, bmpNumber, false);
            }
        });

    }



    private void setNewBPM(TextView v) {
        bmpEditText.clearFocus();
        mainLinearLayout.requestFocus();
        InputMethodManager in = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        String text=v.getText().toString();
        if(NumberUtils.isDigits(text)) {
            int bpmNumber = Integer.valueOf(text);
            if (bpmNumber > 0 && bpmNumber < MAX_BPM_NUMBER) {
                bpm = bpmNumber;
                seekbar.setProgress(bpm);
                if (startButtonPressed){
                    starter.restart(bpm);
                }
            }else{
                bpm=seekbar.getProgress();
                v.setText(String.valueOf(bpm));
            }
        }else{
            bpm=seekbar.getProgress();
            v.setText(String.valueOf(bpm));
        }
    }

    private void initialAudio() {
        //get audStream
        //1 audio
        audioStream = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        //getAudioFile
        try {
            audioFile = audioStream.load(activity.getAssets().openFd("metronome.wav"), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void vibrationActive(boolean flag) {
        if (flag) {
            vibrationImageVew.setImageResource(R.drawable.v_light);
            vibrationButtonPressed=true;
        } else {
            vibrationImageVew.setImageResource(R.drawable.v_dark);
            vibrationButtonPressed=false;
        }
    }
    public void flashActive(boolean flag) {
        if (flag) {
            flashImageVew.setImageResource(R.drawable.flash_light);
            flashButtonPressed=true;
        } else {
            flashImageVew.setImageResource(R.drawable.flash_dark);
            flashButtonPressed=false;
            camera.turnOffFlashLight();
        }
    }

    public void soundnActive(boolean flag) {
        if (flag) {
            soundImageVew.setImageResource(R.drawable.sound_light);
            noSoundImageButtonPressed=true;
        } else {
            soundImageVew.setImageResource(R.drawable.sound_dark);
            noSoundImageButtonPressed=false;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser) {
            if(progress==0){
                progress++;
                Log.v("progress", "changed");
            }
            seekBar.setProgress(progress);
        }
        if(progress==0){
            progress++;
            seekBar.setProgress(progress);
            Log.v("progress", "changed");
        }
        bmpEditText.setText(String.valueOf(seekBar.getProgress()));
        if(startButtonPressed){
            starter.restart(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void initialCamera() {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP){
            camera=new Camera2(activity);
        } else{
            // do something for phones running an SDK before lollipop
        }
    }
    private void setSerifs(){
        //set museoSans 500 font
        TextView manualModeText = (TextView) activity.findViewById(R.id.manual_id_text_view);
        manualModeText.requestFocus();
        TextView indicatorText = (TextView) activity.findViewById(R.id.indicator_text_view_id);
        // Font Face
        Typeface muSeoSans500Typeface = Typeface.createFromAsset(activity.getAssets(), fontMuseoSans500);
        // Applying font
        manualModeText.setTypeface(muSeoSans500Typeface);
        indicatorText.setTypeface(muSeoSans500Typeface);

        //set museoSans 100 font
        TextView setBmpText = (TextView) activity.findViewById(R.id.set_bmp_text_view_id);
        // Font Face
        Typeface museoSans100Typeface = Typeface.createFromAsset(activity.getAssets(), fontMuseoSans100);
        // Applying font
        setBmpText.setTypeface(museoSans100Typeface);

        //set museoSans 300 font
        TextView setBmpNumberText = (TextView) activity.findViewById(R.id.bmp_number_id);
        TextView setBmpNameText = (TextView) activity.findViewById(R.id.bmp_name_text_view_id);
        // Font Face
        Typeface museoSans300Typeface = Typeface.createFromAsset(activity.getAssets(), fontMuseoSans300);
        // Applying font
        setBmpNumberText.setTypeface(museoSans300Typeface);
        setBmpNameText.setTypeface(museoSans300Typeface);
    }
}
