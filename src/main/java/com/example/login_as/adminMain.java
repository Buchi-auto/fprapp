//package com.example.login_as;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.os.Bundle;
//
//public class adminMain extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_admin_main);
//    }
//}
package com.example.login_as;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class adminMain extends AppCompatActivity {
    /* access modifiers changed from: private */
    public FirebaseAuth auth;
    private Button bioBtn;
    /* access modifiers changed from: private */
    public FirebaseDatabase db;
    private Button despBtn;
    private Button logoutBtn;
    private Button regBtn;
    private Button thresBtn;
    /* access modifiers changed from: private */
    public EditText thresText;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_admin_main);
        this.logoutBtn = (Button) findViewById(R.id.logoutbtn_admin);
        this.regBtn = (Button) findViewById(R.id.registryBtn);
        this.bioBtn = (Button) findViewById(R.id.bioBtn);
        this.despBtn = (Button) findViewById(R.id.despBtn);
        this.thresBtn = (Button) findViewById(R.id.thresBtn);
        EditText editText = (EditText) findViewById(R.id.thresText);
        this.thresText = editText;
        editText.setText("45");
        this.auth = FirebaseAuth.getInstance();
        FirebaseDatabase instance = FirebaseDatabase.getInstance();
        this.db = instance;
        instance.getReference().child("Threshold").addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                adminMain.this.thresText.setText(snapshot.getValue().toString());
            }

            public void onCancelled(DatabaseError error) {
            }
        });
        this.logoutBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                adminMain.this.auth.signOut();
                adminMain.this.startActivity(new Intent(adminMain.this, MainActivity.class));
                adminMain.this.finish();
            }
        });
        this.regBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                adminMain.this.startActivity(new Intent(adminMain.this, RegistryView.class));
            }
        });
        this.bioBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                adminMain.this.startActivity(new Intent(adminMain.this, addUpdateBIO.class));
            }
        });
        this.despBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                adminMain.this.startActivity(new Intent(adminMain.this, addUpdateDespatcher.class));
            }
        });
        this.thresBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    float threshVal = Float.parseFloat(adminMain.this.thresText.getText().toString());
                    if (threshVal >= 5.0f) {
                        Toast.makeText(adminMain.this, "Enter Float between 0-5", Toast.LENGTH_SHORT).show();
                    } else if (threshVal > 0.0f) {
                        adminMain.this.db.getReference().child("Threshold").setValue(Float.toString(threshVal));
                        Toast.makeText(adminMain.this, "Threshold Set", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(adminMain.this, "Enter float between 0-5", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(adminMain.this, "Enter valid Integer", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
