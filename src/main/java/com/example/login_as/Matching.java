//package com.example.login_as;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.os.Bundle;
//
//public class Matching extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_matching);
//    }
//}
package com.example.login_as;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.internal.view.SupportMenu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Matching extends AppCompatActivity {
    /* access modifiers changed from: private */
    public FirebaseDatabase db;
    private TextView distText;
    /* access modifiers changed from: private */
    public String distance;
    private Button logOutButton;
    /* access modifiers changed from: private */
    public TextView matchText;
    private Button newEntryButton;
    /* access modifiers changed from: private */
    public TextView threshText;
    /* access modifiers changed from: private */
    public String threshold;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_matching);
        this.newEntryButton = (Button) findViewById(R.id.Make_New_entry);
        this.logOutButton = (Button) findViewById(R.id.Logout_match);
        this.distText = (TextView) findViewById(R.id.distance);
        this.threshText = (TextView) findViewById(R.id.threshold);
        this.matchText = (TextView) findViewById(R.id.result);
        SharedPreferences shrd_Desp = getSharedPreferences("DESPATCHERDETAILS", 0);
        SharedPreferences shrd = getSharedPreferences("DISTANCE", 0);
        SharedPreferences shrd_bio_ref = getSharedPreferences("BIOREFERENCE", 0);
        this.distance = shrd.getString("Distance", "");
        this.distText.setText("Distance " + String.valueOf(this.distance));
        final HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("BIO", shrd_bio_ref.getString("BioIden", "NA"));
        hashMap.put("COMMODITY", shrd_bio_ref.getString("commodity", "NA"));
        hashMap.put("DINUMBER", shrd_bio_ref.getString("diNumber", "NA"));
        hashMap.put("DIDATE", shrd_bio_ref.getString("diDate", "NA"));
        hashMap.put("LOTNUMBER", shrd_bio_ref.getString("lotNumber", "NA"));
        hashMap.put("FIRM", shrd_bio_ref.getString("Firm", "NA"));
        hashMap.put("LOCATION", shrd_bio_ref.getString("Location", "NA"));
        hashMap.put("DESPATCHER", shrd_Desp.getString("Username", "NA"));
        hashMap.put("DISTANCE", this.distance);
        hashMap.put("DATETIME", new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
        FirebaseDatabase instance = FirebaseDatabase.getInstance();
        this.db = instance;
        instance.getReference().child("Threshold").addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                String unused = Matching.this.threshold = snapshot.getValue().toString();
                Log.d("shlok", Matching.this.threshold);
                if (Float.parseFloat(Matching.this.threshold) < Float.parseFloat(Matching.this.distance)) {
                    Matching.this.matchText.setText("Rejected");
                    Matching.this.threshText.setText("The admin chose the threshold as " + String.valueOf(Matching.this.threshold) + ". The obtained distance exceeds the value. Please Retry.");
                    Matching.this.threshText.setTextColor(Color.RED);
                    hashMap.put("RESULT", "REJECTED");
                } else {
                    Matching.this.matchText.setText("Accepted");
                    Matching.this.threshText.setText("The admin chose the threshold as " + String.valueOf(Matching.this.threshold) + ". The obtained distance is under limit.");
                    Matching.this.threshText.setTextColor(Color.GREEN);
                    hashMap.put("RESULT", "ACCEPTED");
                }
                hashMap.put("THRESHOLD", Matching.this.threshold);
                Matching.this.db.getReference().child("Registry").push().updateChildren(hashMap);
            }

            public void onCancelled(DatabaseError error) {
                Toast.makeText(Matching.this, "No such value exists", Toast.LENGTH_SHORT).show();
            }
        });
        this.newEntryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Matching.this.startActivity(new Intent(Matching.this, Despatcher_details.class));
                Matching.this.finish();
            }
        });
        this.logOutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Matching.this.startActivity(new Intent(Matching.this, MainActivity.class));
                Matching.this.finish();
            }
        });
    }
}
