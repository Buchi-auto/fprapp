//package com.example.login_as;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.os.Bundle;
//
//public class Login_Despatcher extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login_despatcher);
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

public class Login_Despatcher extends AppCompatActivity {
    public static final String MSG = "com.example.login_as.DESPATCHER_DETAILS";
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
        setContentView((int) R.layout.activity_login_despatcher);
        this.username = (EditText) findViewById(R.id.username_despatcher);
        this.password = (EditText) findViewById(R.id.password_despatcher);
        this.loginButton = (Button) findViewById(R.id.loginbtn_despatcher);
        this.db = FirebaseDatabase.getInstance();
        ((Button) findViewById(R.id.back_to_main_desp)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Login_Despatcher.this.startActivity(new Intent(Login_Despatcher.this, MainActivity.class));
                Login_Despatcher.this.finish();
            }
        });
        this.loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Login_Despatcher login_Despatcher = Login_Despatcher.this;
                String unused = login_Despatcher.usernameString = login_Despatcher.username.getText().toString();
                Login_Despatcher login_Despatcher2 = Login_Despatcher.this;
                String unused2 = login_Despatcher2.passwordString = login_Despatcher2.password.getText().toString();
                Log.d("Shlok", Login_Despatcher.this.usernameString + " " + Login_Despatcher.this.passwordString);
                boolean isPasswordEmpty = true;
                boolean isUserEmpty = Login_Despatcher.this.usernameString == null || Login_Despatcher.this.usernameString.trim().isEmpty();
                if (Login_Despatcher.this.passwordString != null && !Login_Despatcher.this.passwordString.trim().isEmpty()) {
                    isPasswordEmpty = false;
                }
                if (isUserEmpty || isPasswordEmpty) {
                    Toast.makeText(Login_Despatcher.this, "Name or password is blank", Toast.LENGTH_SHORT).show();
                    return;
                }
                Login_Despatcher login_Despatcher3 = Login_Despatcher.this;
                login_Despatcher3.loginUser(login_Despatcher3.usernameString, Login_Despatcher.this.passwordString);
            }
        });
    }

    /* access modifiers changed from: private */
    public void loginUser(String username2, final String password2) {
        this.db.getReference().child("Despatcher").orderByChild("Username").equalTo(username2).addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                boolean flag = false;
                Intent intent = new Intent(Login_Despatcher.this, Despatcher_details.class);
                String despName = "";
                String despUserName = "";
                Iterator<DataSnapshot> it = snapshot.getChildren().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    DataSnapshot snap = it.next();
                    despName = snap.child("Name").getValue().toString();
                    despUserName = snap.child("Username").getValue().toString();
                    if (!snap.child("Password").getValue().toString().equals(password2)) {
                        if (0 == 1) {
                            break;
                        }
                    } else {
                        flag = true;
                        break;
                    }
                }
                if (flag) {
                    SharedPreferences.Editor editor = Login_Despatcher.this.getSharedPreferences("DESPATCHERDETAILS", 0).edit();
                    editor.putString("Name", despName);
                    editor.putString("Username", despUserName);
                    editor.apply();
                    Login_Despatcher.this.startActivity(intent);
                    Login_Despatcher.this.finish();
                }
                if (!flag) {
                    Toast.makeText(Login_Despatcher.this, "Invalid despatcher credentials, please consult admin", Toast.LENGTH_SHORT).show();
                }
            }

            public void onCancelled(DatabaseError error) {
            }
        });
    }
}
