package com.mvip.kelimebilmeceoyunu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GirisActivity extends AppCompatActivity {

    private EditText editMail,EditSifre;
    private String txtMail,txtSifre;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris);
        editMail = findViewById(R.id.giris_editEmail);
        EditSifre = findViewById(R.id.giris_editSifre);
        mAuth = FirebaseAuth.getInstance();

    }
    public void girisYap(View v){
        txtMail = editMail.getText().toString();
        txtSifre = EditSifre.getText().toString();

        if(!TextUtils.isEmpty(txtMail) && !TextUtils.isEmpty(txtSifre)){
            mAuth.signInWithEmailAndPassword(txtMail,txtSifre).addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    mUser = mAuth.getCurrentUser();
                    System.out.println("Kullanici Adi : "+mUser.getDisplayName()+" Mail : "+mUser.getEmail());
                    Toast.makeText(GirisActivity.this, "Giris Basarili", Toast.LENGTH_SHORT).show();
                    Intent main = new Intent(GirisActivity.this,MainActivity.class);
                    main.putExtra("kullaniciadi2",txtMail);
                    startActivity(main);

                }
            }).addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(GirisActivity.this,"Kullanici adi veya Sifre hatali ! ",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void kayitOl(View v){
        Intent kayit = new Intent(this,KayitActivity.class);
        startActivity(kayit);
    }
}