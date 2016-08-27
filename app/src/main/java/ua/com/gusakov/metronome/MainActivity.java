package ua.com.gusakov.metronome;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity{
    final int DIALOG_EXIT = 1;
    Initialization initial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3664161384009406~4477305577");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        initial=new Initialization(this);
        initial.start();

//        Toast.makeText(this,"version 12",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        Log.v("MyTag", "Back Pressed");
//        showDialog(DIALOG_EXIT);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.exit)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        initial.stop();
                        finish();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

//    DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
//        public void onClick(DialogInterface dialog, int which) {
//            switch (which) {
//                // положительная кнопка
//                case Dialog.BUTTON_POSITIVE:
////                    finish();
//                    break;
//                // негаитвная кнопка
//                case Dialog.BUTTON_NEGATIVE:
//                    //NOT
//                    break;
//            }
//        }
//    };


}
