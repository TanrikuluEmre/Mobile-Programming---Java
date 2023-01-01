package com.mvip.kelimebilmeceoyunu;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class KayitActivity extends AppCompatActivity {

    private EditText editMail,EditSifre,EditSifreOnay;
    private String txtMail,txtSifre,txtSifreOnay;
    private FirebaseAuth mAuth;
    private String kullaniciAdi2;
    FirebaseFirestore db ;
    int x;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kayit);
        editMail =  findViewById(R.id.kayit_ol_editEmail);
        EditSifre = findViewById(R.id.kayit_ol_editSifre);
        EditSifreOnay = findViewById(R.id.kayit_ol_editSifreKontrol);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();




    }



    public void kayitOl(View v){

        txtMail = editMail.getText().toString();
        txtSifre = EditSifre.getText().toString();
        txtSifreOnay = EditSifreOnay.getText().toString();

        x = txtMail.indexOf("@");
        kullaniciAdi2 = "";
        for(int i = 0; i<x;i++){
            kullaniciAdi2 += txtMail.charAt(i);
        }

        if(!TextUtils.isEmpty(txtMail) && !TextUtils.isEmpty(txtSifre) && !TextUtils.isEmpty(txtSifreOnay)){
            if(TextUtils.equals(txtSifre,txtSifreOnay)){

                mAuth.createUserWithEmailAndPassword(txtMail,txtSifre).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        setFireBaseUser();
                        Toast.makeText(KayitActivity.this, "Kayit Yapildi", Toast.LENGTH_SHORT).show();
                        Toast.makeText(KayitActivity.this, "Giris Yapabilirsiniz", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(KayitActivity.this,"Kayit Yapilamadi",Toast.LENGTH_SHORT).show();
                    }
                });

            }
            else{
                Toast.makeText(this, "Sifreler Uyusmuyor !", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, "Bosluklari Doldurunuz !", Toast.LENGTH_SHORT).show();
        }
    }
    public void geriDon(View v){
        Intent geri = new Intent(this,GirisActivity.class);
        startActivity(geri);
    }
    public void setFireBaseUser(){
        Map<String, Object> users = new HashMap<>();
        users.put("mail",txtMail);
        users.put("user_id",FirebaseAuth.getInstance().getUid());
        users.put("puan","0");


        db.collection("users").document(FirebaseAuth.getInstance().getUid()).set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(KayitActivity.this, "User olusmadi", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}