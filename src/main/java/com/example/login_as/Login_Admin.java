//package com.example.login_as;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.os.Bundle;
//
//public class Login_Admin extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login_admin);
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login_Admin extends AppCompatActivity {
    private String EMAIL_DOMAIN = "@iitbfprapp.com";
    private FirebaseAuth auth;
    /* access modifiers changed from: private */
    public String passwordString;
    /* access modifiers changed from: private */
    public String usernameString;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_login_admin);
        final EditText username = (EditText) findViewById(R.id.username);
        final EditText password = (EditText) findViewById(R.id.password);
        this.auth = FirebaseAuth.getInstance();
        ((Button) findViewById(R.id.back_to_main_admin)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Login_Admin.this.startActivity(new Intent(Login_Admin.this, MainActivity.class));
            }
        });
        ((Button) findViewById(R.id.loginbtn)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String unused = Login_Admin.this.usernameString = username.getText().toString();
                String unused2 = Login_Admin.this.passwordString = password.getText().toString();
                boolean isPasswordEmpty = true;
                boolean isUserEmpty = Login_Admin.this.usernameString == null || Login_Admin.this.usernameString.trim().isEmpty();
                if (Login_Admin.this.passwordString != null && !Login_Admin.this.passwordString.trim().isEmpty()) {
                    isPasswordEmpty = false;
                }
                if (isUserEmpty || isPasswordEmpty) {
                    Toast.makeText(Login_Admin.this, "Name or password is blank", Toast.LENGTH_SHORT).show();
                    return;
                }
                Login_Admin login_Admin = Login_Admin.this;
                login_Admin.loginUser(login_Admin.usernameString, Login_Admin.this.passwordString);
            }
        });
    }

    /* access modifiers changed from: private */
    public void loginUser(String username, String password) {
        this.auth.signInWithEmailAndPassword(username + this.EMAIL_DOMAIN, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(Login_Admin.this, "Login successful", Toast.LENGTH_SHORT).show();
                Login_Admin.this.startActivity(new Intent(Login_Admin.this, adminMain.class));
                Login_Admin.this.finish();
            }
        });
        this.auth.signInWithEmailAndPassword(username + this.EMAIL_DOMAIN, password).addOnFailureListener(new OnFailureListener() {
            public void onFailure(Exception e) {
                Toast.makeText(Login_Admin.this, e.toString(), Toast.LENGTH_SHORT).show();
                Toast.makeText(Login_Admin.this, "Enter correct credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
