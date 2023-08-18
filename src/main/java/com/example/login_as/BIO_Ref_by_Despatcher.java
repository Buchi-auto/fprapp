//package com.example.login_as;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.os.Bundle;
//
//public class BIO_Ref_by_Despatcher extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_bio_ref_by_despatcher);
//    }
//}
package com.example.login_as;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class BIO_Ref_by_Despatcher extends AppCompatActivity {
    Button CheckImage;
    private final int GALLERY_REQ_CODE = 1000;
    private TextView fileIden;
    ImageView imgGallery;
    Uri returnData;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_bio_ref_by_despatcher);
        this.imgGallery = (ImageView) findViewById(R.id.Ref_Image);
        this.fileIden = (TextView) findViewById(R.id.File_name_TV);
        this.CheckImage = (Button) findViewById(R.id.Check_Ref);
    }

    public void Check_Image(View v) {
        Toast.makeText(this, "Please make sure finger print contained in the box", Toast.LENGTH_SHORT).show();
        Intent iGallery = new Intent("android.intent.action.PICK");
        iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityIfNeeded(iGallery, 1000);
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 && requestCode == 1000) {
            Uri data2 = data.getData();
            this.returnData = data2;
            this.imgGallery.setImageURI(data2);
            Cursor fileName = getContentResolver().query(this.returnData, (String[]) null, (String) null, (String[]) null, (String) null);
            int nameIndex = fileName.getColumnIndex("_display_name");
            fileName.moveToFirst();
            this.fileIden.setText(fileName.getString(nameIndex));
            this.CheckImage.setEnabled(true);
            return;
        }
        this.CheckImage.setEnabled(false);
    }

    public static String removeExtension(String fname) {
        int pos = fname.lastIndexOf(46);
        if (pos > -1) {
            return fname.substring(0, pos);
        }
        return fname;
    }

    public void Validate_Ref_Image(View v) {
        String currFileName;
        try {
            currFileName = removeExtension(this.fileIden.getText().toString().split("__")[1]);
        } catch (Exception e) {
            e.printStackTrace();
            currFileName = "";
        }
        SharedPreferences sharedPreferences = getSharedPreferences("DESPATCHERDETAILS", 0);
        if (currFileName.equals(getSharedPreferences("BIOREFERENCE", 0).getString("BioIden", ""))) {
            Toast.makeText(this, "Identity Numbers matched", Toast.LENGTH_SHORT).show();
            this.CheckImage.setEnabled(false);
            Intent intent = new Intent(this, Capute_Test.class);
            intent.setData(this.returnData);
            startActivity(intent);
            return;
        }
        Toast.makeText(this, "The image details does not match entered Identity number", Toast.LENGTH_SHORT).show();
    }
}

