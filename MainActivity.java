package com.Mathmeister.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView txtMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtMessage = (TextView)findViewById(R.id.textMessage); // id'si textMessage olan textViewi bulmaya yarar
        txtMessage.setText("Merhabalar"); // textin içeriğini değiştirir
        txtMessage.setTextColor(Color.RED); // textin rengini değiştirir
        System.out.println(txtMessage.getText().toString()); // text içerisindeki yazıyı konsola yazar
        txtMessage.setVisibility(); // txtMessage'nin görünürlüğünü ayarlar
        txtMessage.setTextSize(); // size'ını ayarlar


    }
}