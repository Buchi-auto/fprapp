//package com.example.login_as;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.os.Bundle;
//
//public class Login_BIO extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login_bio);
//    }
//}
package com.example.login_as;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Iterator;

public class Login_BIO extends AppCompatActivity {
    private FirebaseDatabase db;
    private Button loginButton;
    /* access modifiers changed from: private */
    public EditText password;
    /* access modifiers changed from: private */
    public String passwordString;
    /* access modifiers changed from: private */
    public EditText username;
    /* access modifiers changed from: private */
    public String usernameString;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_login_bio);
        this.username = (EditText) findViewById(R.id.username_bio);
        this.password = (EditText) findViewById(R.id.password_bio);
        this.loginButton = (Button) findViewById(R.id.loginbtn_bio);
        this.db = FirebaseDatabase.getInstance();
        ((Button) findViewById(R.id.back_to_main_bio)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Login_BIO.this.startActivity(new Intent(Login_BIO.this, MainActivity.class));
            }
        });
        this.loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Login_BIO login_BIO = Login_BIO.this;
                String unused = login_BIO.usernameString = login_BIO.username.getText().toString();
                Login_BIO login_BIO2 = Login_BIO.this;
                String unused2 = login_BIO2.passwordString = login_BIO2.password.getText().toString();
                Log.d("Shlok", Login_BIO.this.usernameString + " " + Login_BIO.this.passwordString);
                boolean isPasswordEmpty = true;
                boolean isUserEmpty = Login_BIO.this.usernameString == null || Login_BIO.this.usernameString.trim().isEmpty();
                if (Login_BIO.this.passwordString != null && !Login_BIO.this.passwordString.trim().isEmpty()) {
                    isPasswordEmpty = false;
                }
                if (isUserEmpty || isPasswordEmpty) {
                    Toast.makeText(Login_BIO.this, "Name or password is blank", Toast.LENGTH_SHORT).show();
                    return;
                }
                Login_BIO login_BIO3 = Login_BIO.this;
                login_BIO3.loginUser(login_BIO3.usernameString, Login_BIO.this.passwordString);
            }
        });
    }

    /* access modifiers changed from: private */
    public void loginUser(String username2, final String password2) {
        this.db.getReference().child("Bio").orderByChild("Username").equalTo(username2).addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                boolean flag = false;
                Intent intent = new Intent(Login_BIO.this, BioDetailUpload.class);
                String bioIden = "";
                String bioUserName = "";
                Iterator<DataSnapshot> it = snapshot.getChildren().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    DataSnapshot snap = it.next();
                    bioIden = snap.child("Name").getValue().toString();
                    bioUserName = snap.child("Username").getValue().toString();
                    if (snap.child("Password").getValue().toString().equals(password2)) {
                        flag = true;
                        break;
                    }
                }
                if (flag) {
                    SharedPreferences.Editor editor = Login_BIO.this.getSharedPreferences("BIODETAILS", 0).edit();
                    editor.putString("Username", bioUserName);
                    editor.putString("IdenNo", bioIden);
                    editor.apply();
                    Login_BIO.this.startActivity(intent);
                    Login_BIO.this.finish();
                }
                if (!flag) {
                    Toast.makeText(Login_BIO.this, "Invalid BIO credentials, please consult admin", Toast.LENGTH_SHORT).show();
                }
            }

            public void onCancelled(DatabaseError error) {
            }
        });
    }
}
