package com.mvip.kelimebilmeceoyunu;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;

public class PlayActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private Map<String,Object> usersData;
    private DocumentReference docRef;
    private List<Integer> siklarList,soruList;
    private final int soruSayisi=30;
    private int rndSira,rndCvpSira,y=0,timerValue=20,soruNoCek=1,puan=0;
    private int ikix=0; // 0=Pasif , 1=Aktif ,2=Kullanıldı
    private TextView soruText, dogruCvp ,soruNo,sure,puanText,yanlisCvp,puanSon;
    private Random rndSoru,rndCvp;
    private Button cevapA,cevapB,cevapC,cevapD,doubleCvp,yuzdeElli,seyirci;
    private String puan2;
    private boolean d1=false,y1=false,y2=false,y3=false,x=false,yüzde50=false,seyirciKontrol=false;
    private CountDownTimer countDownTimer;
    private ProgressBar progressBar;
    private int[] cekmeDizisi;
    private Timer timer;
    private String kullaniciadi;
    private Dialog settingsDialog,seyirciDialog,dogruDialog,yanlisDialog;
    private ImageView settingsImgClose,seyirciImgClose,seyirciJoker,dogruCvpImgClose,yanlisCvpImgClose;

    private RadioButton settingsRadioOpen, settingsRadioClose;
    SplashScreenActivity muzik;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        usersData = new HashMap<>();
        //yanlisCvp = (TextView) findViewById(R.id.puanson);
        soruText =(TextView) findViewById(R.id.play_activity_textViewQuestion);
        dogruCvp =(TextView) findViewById(R.id.play_activity_dogrucvp);
        soruNo = (TextView) findViewById(R.id.play_activity_soruNo);
        puanText = (TextView) findViewById(R.id.play_activity_score);
        sure=(TextView) findViewById(R.id.play_activity_timers);
        cevapA = (Button) findViewById(R.id.play_activity_questionResultA);
        cevapB = (Button) findViewById(R.id.play_activity_questionResultB);
        cevapC = (Button) findViewById(R.id.play_activity_questionResultC);
        cevapD = (Button) findViewById(R.id.play_activity_questionResultD);
        yuzdeElli = (Button) findViewById(R.id.play_activity_fiftyPercent);
        doubleCvp = (Button) findViewById(R.id.play_activity_doubleAnswer);
        seyirci = (Button) findViewById(R.id.play_activity_spectator);
        progressBar = (ProgressBar) findViewById(R.id.play_activity_timer);
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        rndSoru = new Random();
        rndCvp = new Random();
        siklarList = new ArrayList<Integer>();
        soruList = new ArrayList<>();

        Intent gelenIntent = getIntent();
        y = gelenIntent.getIntExtra("RandomY", 0);
        soruNoCek = gelenIntent.getIntExtra("SoruNo", 1);
        if(soruNoCek>1) {
            soruNo.setText("SORU-" + soruNoCek);
            cekmeDizisi = gelenIntent.getIntArrayExtra("BenzersizSoru");
            puan=gelenIntent.getIntExtra("Puan",0);
            yüzde50 = gelenIntent.getBooleanExtra("YuzdeElliKontrol",false);
            seyirciKontrol = gelenIntent.getBooleanExtra("seyirciKontrol",false);
            ikix=gelenIntent.getIntExtra("DoubleAnswer",0);
            puanText.setText("Puan:"+puan);
            for (int i = 0; i < cekmeDizisi.length; i++) {
                soruList.add(cekmeDizisi[i]);
                System.out.println(soruList.get(i));
            }
        }

        for(int i=1;i<=4;i++) {
            siklarList.add(i);
        }

        if(yüzde50){
            yuzdeElli.setEnabled(false);
            yuzdeElli.setBackgroundResource(R.drawable.yariyariya2);
        }

        if(seyirciKontrol){
            seyirci.setEnabled(false);
            seyirci.setBackgroundResource(R.drawable.seyirci2);
        }

        if(ikix==2){
            doubleCvp.setEnabled(false);
            doubleCvp.setBackgroundResource(R.drawable.ciftcevap2);
        }

        if(soruNoCek==1) {
            for (int i = 1; i <= soruSayisi; i++) {
                soruList.add(i);
            }
        }
        rndSira = (rndSoru.nextInt(soruSayisi-y))+1;

        SoruCek(soruList.get(rndSira-1));

        CevaplariYerlestir();

        countDownTimer = new CountDownTimer(20000,1000) {
            @Override
            public void onTick(long l) {
                timerValue=timerValue-1;
                progressBar.setProgress(timerValue);
            }

            @Override
            public void onFinish() {
                if(x==false){
                    Dialog dialog = new Dialog(PlayActivity.this);
                    dialog.setCancelable(false);
                    dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
                    dialog.setContentView(R.layout.timer_out_dialog);

                    dialog.show();
                }
            }
        };

        countDownTimer.start();

        Intent mail = getIntent();
        kullaniciadi = mail.getStringExtra("name");

        getUsersData();

    }
    public void getUsersData(){
        firebaseFirestore.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                puan2 = (String) documentSnapshot.get("puan");
                usersData.put("puan",puan2);
                usersData.put("mail",kullaniciadi);
                usersData.put("user_id",FirebaseAuth.getInstance().getUid());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PlayActivity.this, "hata", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent mainIntent = new Intent(this,MainActivity.class);
        finish();
        startActivity(mainIntent);
        overridePendingTransition(R.anim.slide_out_up,R.anim.slide_in_down);
    }

    public void SoruCek(int rndSSayi){

        docRef = firebaseFirestore.collection("Sorular").document("4WlVdQksU73Z56IZ7qGd");
        docRef.get()
                .addOnSuccessListener(this, new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){

                            soruText.setText((String)documentSnapshot.get(String.valueOf(rndSSayi)));
                        }
                    }
                });
    }

    public void DogruCevapCek(int rndSSayi,int rndCCevap){
        DocumentReference docRefD = docRef.collection("DogruCevap").document("SNzV9KcoRCXiO2zNcFi6");
        docRefD.get()
                .addOnSuccessListener(this, new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){

                            switch(rndCCevap){
                                case 1:
                                    cevapA.setText("A: " + (String)documentSnapshot.get(String.valueOf(rndSSayi)));
                                    dogruCvp.setText(cevapA.getText());
                                    break;
                                case 2:
                                    cevapB.setText("B: " + (String)documentSnapshot.get(String.valueOf(rndSSayi)));
                                    dogruCvp.setText(cevapB.getText());
                                    break;
                                case 3:
                                    cevapC.setText("C: " + (String)documentSnapshot.get(String.valueOf(rndSSayi)));
                                    dogruCvp.setText(cevapC.getText());
                                    break;
                                case 4:
                                    cevapD.setText("D: " + (String)documentSnapshot.get(String.valueOf(rndSSayi)));
                                    dogruCvp.setText(cevapD.getText());
                                    break;
                            }
                        }
                    }
                });
    }

    public void YanlisCevapCek(int rndSSayi ,int rndCCevap){
        DocumentReference docRefY = docRef.collection("YanlisCevap").document("3E64ZsGv133vw8L4gVJe");
        docRefY.get()
                .addOnSuccessListener(this, new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){

                            switch(rndCCevap){
                                case 1:
                                    cevapA.setText("A: " + (String)documentSnapshot.get(String.valueOf(rndSSayi)));
                                    break;
                                case 2:
                                    cevapB.setText("B: " + (String)documentSnapshot.get(String.valueOf(rndSSayi)));
                                    break;
                                case 3:
                                    cevapC.setText("C: "+(String)documentSnapshot.get(String.valueOf(rndSSayi)));
                                    break;
                                case 4:
                                    cevapD.setText("D: "+(String)documentSnapshot.get(String.valueOf(rndSSayi)));
                                    break;
                            }
                        }
                    }
                });
    }

    public void YanlisCevapCek1(int rndSSayi ,int rndCCevap){
        DocumentReference docRefY1 = docRef.collection("YanlisCevap1").document("lliCoDjITH4jZWZewd0R");
        docRefY1.get()
                .addOnSuccessListener(this, new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){

                            switch(rndCCevap){
                                case 1:
                                    cevapA.setText("A: " + (String)documentSnapshot.get(String.valueOf(rndSSayi)));
                                    break;
                                case 2:
                                    cevapB.setText("B: " + (String)documentSnapshot.get(String.valueOf(rndSSayi)));
                                    break;
                                case 3:
                                    cevapC.setText("C: "+(String)documentSnapshot.get(String.valueOf(rndSSayi)));
                                    break;
                                case 4:
                                    cevapD.setText("D: "+(String)documentSnapshot.get(String.valueOf(rndSSayi)));
                                    break;
                            }
                        }
                    }
                });
    }
    //

    public void YanlisCevapCek2(int rndSSayi ,int rndCCevap){
        DocumentReference docRefY2 = docRef.collection("YanlisCevap2").document("agtVrrfcRvejVVOyUu74");
        docRefY2.get()
                .addOnSuccessListener(this, new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){

                            switch(rndCCevap){
                                case 1:
                                    cevapA.setText("A: " + (String)documentSnapshot.get(String.valueOf(rndSSayi)));
                                    break;
                                case 2:
                                    cevapB.setText("B: " + (String)documentSnapshot.get(String.valueOf(rndSSayi)));
                                    break;
                                case 3:
                                    cevapC.setText("C: "+(String)documentSnapshot.get(String.valueOf(rndSSayi)));
                                    break;
                                case 4:
                                    cevapD.setText("D: "+(String)documentSnapshot.get(String.valueOf(rndSSayi)));
                                    break;
                            }
                        }
                    }
                });
    }

    public void CevaplariYerlestir(){
        for(int i=0;i<4;i++) {
            rndCvpSira = (rndCvp.nextInt(4-i))+1;
            if(d1==false) {
                DogruCevapCek(soruList.get(rndSira-1), siklarList.get(rndCvpSira-1));
                siklarList.remove(rndCvpSira-1);
                d1 = true;
            }else if(y1==false){
                YanlisCevapCek(soruList.get(rndSira-1),siklarList.get(rndCvpSira-1));
                siklarList.remove(rndCvpSira-1);
                y1=true;
            }else if(y2==false){
                YanlisCevapCek1(soruList.get(rndSira-1),siklarList.get(rndCvpSira-1));
                siklarList.remove(rndCvpSira-1);
                y2=true;
            }else if(y3==false){
                YanlisCevapCek2(soruList.get(rndSira-1),siklarList.get(rndCvpSira-1));
                siklarList.remove(rndCvpSira-1);
                y3=true;
            }
        }
    }

    public void MainActivityBackUp(View v){
        Intent mainIntent = new Intent(this,MainActivity.class);
        finish();
        startActivity(mainIntent);
    }

    public void CvpSorgulaBtn(View v){
        String cvp =dogruCvp.getText().toString();
        String a =cevapA.getText().toString();
        String b =cevapB.getText().toString();
        String c =cevapC.getText().toString();
        String d =cevapD.getText().toString();

        if(v.getId()==R.id.play_activity_questionResultA){
            if(cvp.equals(a)) {
                soruList.remove(rndSira - 1);
                y++;
                if(soruNoCek<=3) {
                    puan += 5;
                }else if(soruNoCek<=7){
                    puan += 10;
                }else{
                    puan += 15;
                }
                soruNoCek++;
                x=true;
                int[] aktarmaDizisi = new int[soruList.size()];
                for (int i = 0; i < soruList.size(); i++) {
                    aktarmaDizisi[i] = soruList.get(i);
                }
                Intent repeatIntent = new Intent(this, PlayActivity.class);
                repeatIntent.putExtra("BenzersizSoru", aktarmaDizisi);
                repeatIntent.putExtra("RandomY", y);
                repeatIntent.putExtra("SoruNo", soruNoCek);
                repeatIntent.putExtra("YuzdeElliKontrol",yüzde50);
                repeatIntent.putExtra("DoubleAnswer",ikix);
                repeatIntent.putExtra("Puan",puan);
                repeatIntent.putExtra("seyirciKontrol",seyirciKontrol);
                repeatIntent.putExtra("name",kullaniciadi);
                finish();
                cevapA.setBackgroundResource(R.drawable.dogrubtn);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {


                    }
                }, 5000);
                startActivity(repeatIntent);
            }
            else{
                x=true;
                if(ikix==0 || ikix==2) {
                    yanlisCvp();

                }else{
                    ikix=2;
                    cevapA.setEnabled(false);
                }
            }
        }else if(v.getId()==R.id.play_activity_questionResultB){
            if(cvp.equals(b)) {
                soruList.remove(rndSira - 1);
                y++;
                if(soruNoCek<=3) {
                    puan += 5;
                }else if(soruNoCek<=7){
                    puan += 10;
                }else{
                    puan += 15;
                }
                soruNoCek++;
                x=true;
                int[] aktarmaDizisi = new int[soruList.size()];
                for (int i = 0; i < soruList.size(); i++) {
                    aktarmaDizisi[i] = soruList.get(i);
                }
                Intent repeatIntent = new Intent(this, PlayActivity.class);
                repeatIntent.putExtra("BenzersizSoru", aktarmaDizisi);
                repeatIntent.putExtra("RandomY", y);
                repeatIntent.putExtra("SoruNo", soruNoCek);
                repeatIntent.putExtra("YuzdeElliKontrol",yüzde50);
                repeatIntent.putExtra("DoubleAnswer",ikix);
                repeatIntent.putExtra("Puan",puan);
                repeatIntent.putExtra("seyirciKontrol",seyirciKontrol);
                repeatIntent.putExtra("name",kullaniciadi);
                finish();
                cevapB.setBackgroundResource(R.drawable.dogrubtn);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {


                    }
                }, 50000);
                startActivity(repeatIntent);
            }else{
                x=true;
                if(ikix==0 || ikix==2) {
                    yanlisCvp();

                }else{
                    ikix=2;
                    cevapB.setEnabled(false);
                }
            }
        }else if(v.getId()==R.id.play_activity_questionResultC){
            if(cvp.equals(c)) {
                soruList.remove(rndSira - 1);
                y++;
                if(soruNoCek<=3) {
                    puan += 5;
                }else if(soruNoCek<=7){
                    puan += 10;
                }else{
                    puan += 15;
                }
                soruNoCek++;
                x=true;
                int[] aktarmaDizisi = new int[soruList.size()];
                for (int i = 0; i < soruList.size(); i++) {
                    aktarmaDizisi[i] = soruList.get(i);
                }
                Intent repeatIntent = new Intent(this, PlayActivity.class);
                repeatIntent.putExtra("BenzersizSoru", aktarmaDizisi);
                repeatIntent.putExtra("RandomY", y);
                repeatIntent.putExtra("SoruNo", soruNoCek);
                repeatIntent.putExtra("YuzdeElliKontrol",yüzde50);
                repeatIntent.putExtra("DoubleAnswer",ikix);
                repeatIntent.putExtra("Puan",puan);
                repeatIntent.putExtra("seyirciKontrol",seyirciKontrol);
                repeatIntent.putExtra("name",kullaniciadi);
                finish();
                cevapC.setBackgroundResource(R.drawable.dogrubtn);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {


                    }
                }, 50000);
                startActivity(repeatIntent);
            }else{
                x=true;
                if(ikix==0 || ikix==2) {
                    yanlisCvp();

                }else{
                    ikix=2;
                    cevapC.setEnabled(false);
                }
            }
        }else if(v.getId()==R.id.play_activity_questionResultD){
            if(cvp.equals(d)){
                soruList.remove(rndSira - 1);
                y++;
                if(soruNoCek<=3) {
                    puan += 5;
                }else if(soruNoCek<=7){
                    puan += 10;
                }else{
                    puan += 15;
                }


                soruNoCek++;
                x=true;
                int[] aktarmaDizisi = new int[soruList.size()];
                for (int i = 0; i < soruList.size(); i++) {
                    aktarmaDizisi[i] = soruList.get(i);
                }
                Intent repeatIntent = new Intent(this, PlayActivity.class);
                repeatIntent.putExtra("BenzersizSoru", aktarmaDizisi);
                repeatIntent.putExtra("RandomY", y);
                repeatIntent.putExtra("SoruNo", soruNoCek);
                repeatIntent.putExtra("YuzdeElliKontrol",yüzde50);
                repeatIntent.putExtra("DoubleAnswer",ikix);
                repeatIntent.putExtra("Puan",puan);
                repeatIntent.putExtra("seyirciKontrol",seyirciKontrol);
                repeatIntent.putExtra("name",kullaniciadi);
                cevapD.setBackgroundResource(R.drawable.dogrubtn);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {


                    }
                }, 50000);
                startActivity(repeatIntent);
            }else{
                x=true;
                if(ikix==0 || ikix==2) {

                    yanlisCvp();

                }else{
                    ikix=2;
                    cevapD.setEnabled(false);
                }
            }
        }
    }

    public void JokerBtn(View v){
        String cvp =dogruCvp.getText().toString();
        String a =cevapA.getText().toString();
        String b =cevapB.getText().toString();
        String c =cevapC.getText().toString();
        String d =cevapD.getText().toString();
        int random1,random2;
        Boolean sorgu1=false,sorgu2=false;
        Random rndJoker = new Random();
        random1 = rndJoker.nextInt(3)+1;
        random2= rndJoker.nextInt(3)+1;

        while(random1==random2){
            random2= rndJoker.nextInt(3)+1;
        }

        if(v.getId()==R.id.play_activity_fiftyPercent){
            if(cvp.equals(a)){
                for(int i=0;i<2;i++) {
                    if ((random1 == 1 || random2 == 1) && !sorgu1) {
                        cevapB.setText("B:");
                        sorgu1=true;
                    } else if ((random1 == 2 || random2 == 2)&& !sorgu2) {
                        cevapC.setText("C:");
                        sorgu2=true;
                    } else if (random1 == 3 || random2 == 3) {
                        cevapD.setText("D:");
                    }
                }
            }else if(cvp.equals(b)){
                for(int i=0;i<2;i++) {
                    if ((random1 == 1 || random2 == 1) && !sorgu1) {
                        cevapA.setText("A:");
                        sorgu1=true;
                    } else if ((random1 == 2 || random2 == 2) && !sorgu2) {
                        cevapC.setText("C:");
                        sorgu2=true;
                    } else if (random1 == 3 || random2 == 3) {
                        cevapD.setText("D:");
                    }
                }
            }else if(cvp.equals(c)){
                for(int i=0;i<2;i++) {
                    if ((random1 == 1 || random2 == 1) && !sorgu1) {
                        cevapA.setText("A:");
                        sorgu1=true;
                    } else if ((random1 == 2 || random2 == 2) && !sorgu2) {
                        cevapB.setText("B:");
                        sorgu2=true;
                    } else if (random1 == 3 || random2 == 3) {
                        cevapD.setText("D:");
                    }
                }
            }else if(cvp.equals(d)){
                for(int i=0;i<2;i++) {
                    if ((random1 == 1 || random2 == 1) && !sorgu1) {
                        cevapA.setText("A:");
                        sorgu1=true;
                    } else if ((random1 == 2 || random2 == 2) && !sorgu2) {
                        cevapB.setText("B:");
                        sorgu2=true;
                    } else if (random1 == 3 || random2 == 3) {
                        cevapC.setText("C:");
                    }
                }
            }


            yüzde50 =true;
            yuzdeElli.setEnabled(false);
            yuzdeElli.setBackgroundResource(R.drawable.yariyariya2);


        }
        if(v.getId()==R.id.play_activity_doubleAnswer){
            ikix=1;
            doubleCvp.setEnabled(false);
            doubleCvp.setBackgroundResource(R.drawable.ciftcevap2);

        }

        if(v.getId()==R.id.play_activity_spectator){

            seyirciDialog = new Dialog(this);
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.copyFrom(seyirciDialog.getWindow().getAttributes());
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            seyirciDialog.setCancelable(false);
            seyirciDialog.setContentView(R.layout.custom_seyirci_jokeri);
            seyirciJoker = (ImageView) seyirciDialog.findViewById((R.id.seyirciJokeri));
            seyirciImgClose = (ImageView) seyirciDialog.findViewById(R.id.custom_dialog_settings_imageViewClose);
            seyirciImgClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    seyirciDialog.dismiss();
                }
            });

            if(cvp.equals(a)){
                seyirciJoker.setImageResource(R.drawable.seyirci_a);

            }else if(cvp.equals(b)){
                seyirciJoker.setImageResource(R.drawable.seyirci_b);

            }else if(cvp.equals(c)){
                seyirciJoker.setImageResource(R.drawable.seyirci_c);

            }else if(cvp.equals(d)){
                seyirciJoker.setImageResource(R.drawable.seyirci_d);

            }
            seyirciDialog.getWindow().setAttributes(params);
            seyirciDialog.show();
            seyirci.setEnabled(false);
            seyirciKontrol = true;
            seyirci.setBackgroundResource(R.drawable.seyirci2);

        }

    }


    public void yanlisCvp(){
        yanlisDialog = new Dialog(this);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(yanlisDialog.getWindow().getAttributes());
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        yanlisDialog.setCancelable(false);
        yanlisDialog.setContentView(R.layout.custom_cevap2);
        yanlisDialog.getWindow().setAttributes(params);
        yanlisDialog.show();
        yanlisCvp = yanlisDialog.findViewById(R.id.puanson);
        yanlisCvp.setText("Puan :"+ puan);

        yanlisDialog.findViewById(R.id.puanson).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                puanSon.setText("puan");
            }
        });

        if(puan>Integer.parseInt(puan2)){
        puan2 = Integer.toString(puan);
        usersData.put("puan",puan2);

        firebaseFirestore.collection("users").document(user.getUid()).set(usersData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        }


    }

    public void btnYanlis(View v){
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.putExtra("kullaniciadi2",kullaniciadi);
        yanlisDialog.dismiss();
        finish();
        startActivity(mainIntent);
    }
    public void anaMenu(View v){
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.putExtra("kullaniciadi2",kullaniciadi);
        finish();
        startActivity(mainIntent);
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
                muzik.gameTheme.start();

            }
        });
        settingsRadioClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                muzik.gameTheme.pause();
            }
        });

        settingsDialog.getWindow().setAttributes(params);
        settingsDialog.show();

    }
}


