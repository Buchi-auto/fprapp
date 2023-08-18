//package com.example.login_as;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.os.Bundle;
//
//public class Despatcher_details extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_despatcher_details);
//    }
//}
package com.example.login_as;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Despatcher_details extends AppCompatActivity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_despatcher_details);
        ((TextView) findViewById(R.id.despatcher_details_name)).setText("Name: " + getSharedPreferences("DESPATCHERDETAILS", 0).getString("Name", ""));
    }

    public void OpenActivity_despatcher_entry_details(View v) {
        startActivity(new Intent(this, Despatcher_Entry_Details.class));
    }
}
