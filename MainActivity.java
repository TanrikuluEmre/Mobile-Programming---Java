package com.mvip.kelimebilmeceoyunu;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private String mail,puan;
    private TextView email;
    private String kullaniciAdi;

    private Dialog settingsDialog;
    private ImageView settingsImgClose;

    private RadioButton settingsRadioOpen, settingsRadioClose;
    private boolean muzikDurumu;
    SplashScreenActivity spActivity;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private Map<String,Object> usersData;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usersData = new HashMap<>();
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        email = findViewById(R.id.kullaniciAdi);
        Intent mail = getIntent();
        kullaniciAdi = mail.getStringExtra("kullaniciadi2");
        int x;
        x = kullaniciAdi.indexOf("@");
        String kullaniciAdi2 = "";
        for(int i = 0; i<x;i++){
            kullaniciAdi2 += kullaniciAdi.charAt(i);
        }
        email.setText(kullaniciAdi2);


    }

    public void mainBtnClick(View view) {
        switch(view.getId()){
            case R.id.main_activity_btnPlay:
                Intent playIntent = new Intent(MainActivity.this,PlayActivity.class);
                playIntent.putExtra("name",kullaniciAdi);
                finish();
                startActivity(playIntent);
                overridePendingTransition(R.anim.slide_out_up,R.anim.slide_in_down);
                break;

            case R.id.main_activity_btnExit:

                break;
            default:
        }
    }
    public void btnAyarlar(View v) {
        ayarlariGoster();
    }

    private void ayarlariGoster() {
        settingsDialog = new Dialog(this);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(settingsDialog.getWindow().getAttributes());
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        settingsDialog.setCancelable(false);
        settingsDialog.setContentView(R.layout.custom_dialog_settings);

        settingsImgClose = (ImageView) settingsDialog.findViewById(R.id.custom_dialog_settings_imageViewClose);
        settingsRadioClose = (RadioButton) settingsDialog.findViewById(R.id.custom_dialog_settings_radioBtnMusicClose);
        settingsRadioOpen = (RadioButton) settingsDialog.findViewById(R.id.custom_dialog_settings_radioBtnMusicOpen);

        settingsImgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsDialog.dismiss();
            }
        });
        settingsRadioOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spActivity.gameTheme.start();

            }
        });
        settingsRadioClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spActivity.gameTheme.pause();
            }
        });

        settingsDialog.getWindow().setAttributes(params);
        settingsDialog.show();
    }


    public void cikisBtnClick(View v){
        Intent giris = new Intent(this,GirisActivity.class);
        startActivity(giris);
    }

    public void btnSiralama(View v){
        Intent intentToSiralama=new Intent(MainActivity.this,SiralamaActivity.class);
        intentToSiralama.putExtra("kullaniciadi2",kullaniciAdi);
        startActivity(intentToSiralama);

    }


}