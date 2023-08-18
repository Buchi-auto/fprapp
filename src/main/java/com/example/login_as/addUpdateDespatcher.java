//package com.example.login_as;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.os.Bundle;
//
//public class addUpdateDespatcher extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_add_update_despatcher);
//    }
//}
package com.example.login_as;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class addUpdateDespatcher extends AppCompatActivity {
    private Button button;
    /* access modifiers changed from: private */
    public FirebaseDatabase db;
    /* access modifiers changed from: private */
    public EditText despname;
    private ListView listView;
    /* access modifiers changed from: private */
    public EditText password;
    /* access modifiers changed from: private */
    public EditText username;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_add_update_despatcher);
        this.listView = (ListView) findViewById(R.id.bio_listview);
        final ArrayList<String> list = new ArrayList<>();
        final ArrayList<String> unames = new ArrayList<>();
        final ArrayAdapter adapter = new ArrayAdapter(this, R.layout.list_view, list);
        this.button = (Button) findViewById(R.id.bio_addbio);
        this.listView.setAdapter(adapter);
        this.username = (EditText) findViewById(R.id.bio_username);
        this.despname = (EditText) findViewById(R.id.bio_name);
        this.password = (EditText) findViewById(R.id.bio_password);
        FirebaseDatabase instance = FirebaseDatabase.getInstance();
        this.db = instance;
        instance.getReference().child("Despatcher").addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                list.clear();
                unames.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    String locString = "";
                    for (DataSnapshot s : snap.getChildren()) {
                        locString = locString + s.getKey().toString() + ": " + s.getValue().toString() + "\n";
                        if (s.getKey().toString().equals("Username")) {
                            unames.add(s.getValue().toString());
                        }
                    }
                    list.add(locString);
                }
//                Log.d("shlok", RegistryView$1$$ExternalSyntheticBackport0.m(" and ", list));
                adapter.notifyDataSetChanged();
            }

            public void onCancelled(DatabaseError error) {
            }
        });
        this.button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String uname = addUpdateDespatcher.this.username.getText().toString();
                String dname = addUpdateDespatcher.this.despname.getText().toString();
                String pass = addUpdateDespatcher.this.password.getText().toString();
                uname = uname.trim();
                dname = dname.trim();
                pass=pass.trim();
                boolean unameEmpty = uname == null || uname.trim().isEmpty();
                boolean dnameEmpty = dname == null || dname.trim().isEmpty();
                boolean passEmpty = pass == null || pass.trim().isEmpty();
                if (unameEmpty || dnameEmpty || passEmpty) {
                    Toast.makeText(addUpdateDespatcher.this, "Add non-empty details", Toast.LENGTH_SHORT).show();
                    return;
                }
                int flag = 1;
                Iterator it = unames.iterator();
                while (it.hasNext()) {
                    String kname = (String) it.next();
                    Log.d("dbnames", kname);
                    if (kname.equals(uname)) {
                        flag = 0;
                    }
                }
                if (flag == 1) {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("Username", uname);
                    hashMap.put("Name", dname);
                    hashMap.put("Password", pass);
                    addUpdateDespatcher.this.db.getReference().child("Despatcher").push().updateChildren(hashMap);
                    Toast.makeText(addUpdateDespatcher.this, "Despatcher User added to the database", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(addUpdateDespatcher.this, "DESPATCHER Username must be Unique to existing names", Toast.LENGTH_SHORT).show();
            }
        });
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                final String fin = ((String) list.get(position)).split("\\r?\\n")[2].split(" ")[1];
                Log.d("shlok", fin);
                new AlertDialog.Builder(addUpdateDespatcher.this).setTitle("Deleting the entry").setMessage("Confirm deletion of all despatchers with the Username: " + fin).setPositiveButton("delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        addUpdateDespatcher.this.db.getReference().child("Despatcher").orderByChild("Username").equalTo(fin).addListenerForSingleValueEvent(new ValueEventListener() {
                            public void onDataChange(DataSnapshot snapshot) {
                                for (DataSnapshot snap : snapshot.getChildren()) {
                                    snap.getRef().removeValue();
                                }
                                Log.d("shlok", snapshot.toString());
                            }

                            public void onCancelled(DatabaseError error) {
                            }
                        });
                        Toast.makeText(addUpdateDespatcher.this, "Entries deleted", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(addUpdateDespatcher.this, "User deletion cancelled", Toast.LENGTH_SHORT).show();
                    }
                }).show();
            }
        });
    }
}
