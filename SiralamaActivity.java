package com.mvip.kelimebilmeceoyunu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mvip.kelimebilmeceoyunu.adapter.ScoreBoardAdapter;
import com.mvip.kelimebilmeceoyunu.adapter.ScoreInfo;

import java.util.ArrayList;
import java.util.Map;

public class SiralamaActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    ArrayList<ScoreInfo> scoreArray;
    ScoreBoardAdapter adapter;
    RecyclerView recyclerView;
    String kullaniciadi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_siralama);





        recyclerView = findViewById(R.id.score_board);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firebaseFirestore = FirebaseFirestore.getInstance();

        scoreArray = new ArrayList<ScoreInfo>();

        adapter = new ScoreBoardAdapter(SiralamaActivity.this,scoreArray);

        recyclerView.setAdapter(adapter);

        Intent mail = getIntent();
        kullaniciadi = mail.getStringExtra("kullaniciadi2");



        siralama();

    }
    public void siralama(){
        firebaseFirestore.collection("users").orderBy("puan", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error!=null){
                            Toast.makeText(SiralamaActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                        if(value !=null){
                            for(DocumentSnapshot snapshot:value.getDocuments()){
                                Map<String,Object> data = snapshot.getData();
                                String mail=(String) data.get("mail");
                                String puan = (String) data.get("puan");

                                ScoreInfo scoreInfo=new ScoreInfo(mail,puan);

                                scoreArray.add(scoreInfo);



                            }
                            adapter.notifyDataSetChanged();

                        }

                    }
                });



    }
    public void anaMenu(View v){
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.putExtra("kullaniciadi2",kullaniciadi);
        finish();
        startActivity(mainIntent);
    }
}
