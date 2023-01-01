package com.mvip.kelimebilmeceoyunu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.util.HashMap;
import java.util.Map;

public class SplashScreenActivity extends AppCompatActivity {

    private ProgressBar mProgress;
    private TextView mTextView;
   //private FirebaseFirestore mFireStore;
    //private DocumentReference mReference;
    private float maksimumProgress=100f,artacakProgress,progressMiktari=0;
    private int soruSayisi=10;
    public static MediaPlayer gameTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mProgress = (ProgressBar) findViewById(R.id.splash_screen_activity_progressBar);
        mTextView = (TextView) findViewById(R.id.splash_screen_activity_textViewState);

        gameTheme = MediaPlayer.create(this,R.raw.strangerthings);
        gameTheme.start();
        gameTheme.setLooping(true);

        try{

            artacakProgress = maksimumProgress/soruSayisi;

            mTextView.setText("Sorular Yükleniyor...");
            int sayac = 0;

            while(sayac<soruSayisi){
                progressMiktari += artacakProgress;
                mProgress.setProgress((int)progressMiktari);
                sayac++;
            }

            mTextView.setText("Sorular Alındı,Uygulama Başlatılıyor...");

            new CountDownTimer(1100,1000){
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    Intent mainIntent = new Intent(SplashScreenActivity.this,GirisActivity.class);
                    finish();
                    startActivity(mainIntent);
                }
            }.start();

        }catch(Exception e){

        }

    }
}