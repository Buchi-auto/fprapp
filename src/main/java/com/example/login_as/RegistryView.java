//package com.example.login_as;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.os.Bundle;
//
//public class RegistryView extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_registry_view);
//    }
//}
package com.example.login_as;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class RegistryView extends AppCompatActivity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_registry_view);
        final ArrayList<String> list = new ArrayList<>();
        final ArrayAdapter adapter = new ArrayAdapter(this, R.layout.list_view, list);
        ((ListView) findViewById(R.id.registry_listview)).setAdapter(adapter);
        FirebaseDatabase.getInstance().getReference().child("Registry").addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    String locString = "";
                    for (DataSnapshot s : snap.getChildren()) {
                        locString = locString + s.getKey().toString() + ": " + s.getValue().toString() + "\n";
                    }
                    list.add(locString);
                }
//                Log.d("shlok", RegistryView$1$$ExternalSyntheticBackport0.m(" and ", list));
                adapter.notifyDataSetChanged();
            }

            public void onCancelled(DatabaseError error) {
            }
        });
    }
}
