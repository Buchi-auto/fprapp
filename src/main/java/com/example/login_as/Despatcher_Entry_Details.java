//package com.example.login_as;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.os.Bundle;
//
//public class Despatcher_Entry_Details extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_despatcher_entry_details);
//    }
//}
package com.example.login_as;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Despatcher_Entry_Details extends AppCompatActivity {
    EditText BioIden;
    String BioIdenStr = "";
    EditText commodity;
    String commodityStr = "";
    EditText diDate;
    String diDateStr = "";
    EditText diNumber;
    String diNumberStr = "";
    EditText firm;
    String firmStr = "";
    EditText location;
    String locationStr = "";
    EditText lotNumber;
    String lotNumberStr = "";
    Button submitEntryDetails;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_despatcher_entry_details);
        this.BioIden = (EditText) findViewById(R.id.BIO_Iden);
        this.commodity = (EditText) findViewById(R.id.Commodity);
        this.diNumber = (EditText) findViewById(R.id.DI_Number);
        this.diDate = (EditText) findViewById(R.id.DI_Date);
        this.lotNumber = (EditText) findViewById(R.id.Lot_Number);
        this.firm = (EditText) findViewById(R.id.Firm);
        this.location = (EditText) findViewById(R.id.Location);
        this.submitEntryDetails = (Button) findViewById(R.id.submit_entry_details);
    }

    public void OpenActivity_BIO_ref_by_despatcher(View v) {
        this.BioIdenStr = this.BioIden.getText().toString();
        this.commodityStr = this.commodity.getText().toString();
        this.diNumberStr = this.diNumber.getText().toString();
        this.diDateStr = this.diDate.getText().toString();
        this.lotNumberStr = this.lotNumber.getText().toString();
        this.firmStr = this.firm.getText().toString();
        this.locationStr = this.location.getText().toString();
        if (!areFieldsFilled().booleanValue()) {
            SharedPreferences.Editor editor = getSharedPreferences("BIOREFERENCE", 0).edit();
            editor.putString("BioIden", this.BioIdenStr);
            editor.putString("commodity", this.commodityStr);
            editor.putString("diNumber", this.diNumberStr);
            editor.putString("diDate", this.diDateStr);
            editor.putString("lotNumber", this.lotNumberStr);
            editor.putString("firm", this.firmStr);
            editor.putString("location", this.locationStr);
            editor.apply();
            startActivity(new Intent(this, BIO_Ref_by_Despatcher.class));
            return;
        }
        Toast.makeText(this, "Please fill all the details", Toast.LENGTH_SHORT).show();
    }

    private Boolean areFieldsFilled() {
        String str = this.BioIdenStr;
        boolean z = false;
        boolean isBioIdenEmpty = str == null || str.trim().isEmpty();
        String str2 = this.commodityStr;
        boolean iscommodityEmpty = str2 == null || str2.trim().isEmpty();
        String str3 = this.diNumberStr;
        boolean isdiNumberEmpty = str3 == null || str3.trim().isEmpty();
        String str4 = this.diDateStr;
        boolean isdiDateEmpty = str4 == null || str4.trim().isEmpty();
        String str5 = this.lotNumberStr;
        boolean islotNumberEmpty = str5 == null || str5.trim().isEmpty();
        String str6 = this.firmStr;
        boolean isfirmEmpty = str6 == null || str6.trim().isEmpty();
        String str7 = this.locationStr;
        boolean islocationEmpty = str7 == null || str7.trim().isEmpty();
        if (isBioIdenEmpty || iscommodityEmpty || isdiNumberEmpty || isdiDateEmpty || islotNumberEmpty || isfirmEmpty || islocationEmpty) {
            z = true;
        }
        return Boolean.valueOf(z);
    }
}
