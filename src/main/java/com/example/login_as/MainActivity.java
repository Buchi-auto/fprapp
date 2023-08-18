//package com.example.login_as;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.os.Bundle;
//
//public class MainActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//    }
//}
package com.example.login_as;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_main);
        SharedPreferences.Editor editor = getSharedPreferences("DESPATCHERDETAILS", 0).edit();
        editor.clear();
        editor.apply();
        SharedPreferences.Editor editor2 = getSharedPreferences("BIODETAILS", 0).edit();
        editor2.clear();
        editor2.apply();
        SharedPreferences.Editor editor3 = getSharedPreferences("BIOREFERENCE", 0).edit();
        editor3.clear();
        editor3.apply();
        SharedPreferences.Editor editor4 = getSharedPreferences("BIOREFERENCE", 0).edit();
        editor4.clear();
        editor4.apply();
        Toast.makeText(this, "The app requires active INTERNET connectivity for proper functionality", Toast.LENGTH_SHORT).show();
    }

    public void OpenActivity_admin(View v) {
        startActivity(new Intent(this, Login_Admin.class));
    }

    public void OpenActivity_despatcher(View v) {
        startActivity(new Intent(this, Login_Despatcher.class));
    }

    public void OpenActivity_BIO(View v) {

        startActivity(new Intent(this, Login_BIO.class));
    }
}
