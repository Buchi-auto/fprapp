//package com.example.login_as;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.os.Bundle;
//
//public class BioDetailUpload extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_bio_detail_upload);
//    }
//}
package com.example.login_as;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BioDetailUpload extends AppCompatActivity {
    private static final int REQUEST_CAMERA_PERMISSION_CODE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private String IdenNo;
    private TextView bioIden;
    private TextView bioName;
    private String currentPhotoPath;
    /* access modifiers changed from: private */
    public Bitmap imageBitmap;
    private File imageFile;
    /* access modifiers changed from: private */
    public ImageView imageView;
    private String name;
    Button saveButton;
    Button uploadFromMedia;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_bio_detail_upload);
        SharedPreferences shrd = getSharedPreferences("BIODETAILS", 0);
        this.name = shrd.getString("Username", "");
        this.IdenNo = shrd.getString("IdenNo", "");
        this.bioName = (TextView) findViewById(R.id.bio_name_TV);
        this.bioIden = (TextView) findViewById(R.id.bio_iden_TV);
        this.uploadFromMedia = (Button) findViewById(R.id.upload_media_bio);
        this.bioName.setText("Name: " + this.name);
        this.bioIden.setText("Identity No.: " + this.IdenNo);
        this.imageView = (ImageView) findViewById(R.id.bio_capture_image);
        Button button = (Button) findViewById(R.id.save_button_bio);
        this.saveButton = button;
        button.setEnabled(false);
        final ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == -1) {
                    Uri uri = result.getData().getData();
                    try {
                        Bitmap unused = BioDetailUpload.this.imageBitmap = MediaStore.Images.Media.getBitmap(BioDetailUpload.this.getContentResolver(), uri);
                        BioDetailUpload.this.imageView.setImageURI(uri);
                        BioDetailUpload.this.saveButton.setEnabled(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        this.uploadFromMedia.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent("android.intent.action.GET_CONTENT");
                intent.setType("image/jpg");
                intent.putExtra("android.intent.extra.LOCAL_ONLY", true);
                someActivityResultLauncher.launch(intent);
            }
        });
    }

    public void captureImage(View view) {
        if (ContextCompat.checkSelfPermission(this, "android.permission.CAMERA") != 0) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.CAMERA"}, 1);
        }
        if (ContextCompat.checkSelfPermission(this, "android.permission.CAMERA") != 0) {
            Toast.makeText(this, "Denied permissions; check settings", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (intent.resolveActivity(getPackageManager()) != null) {
            File imageFile2 = null;
            try {
                imageFile2 = getImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (imageFile2 != null) {
                Uri imageUri = FileProvider.getUriForFile(this, "com.example.login_as.fileprovider", imageFile2);
                intent.putExtra("output", imageUri);
                Log.d("shlok intent: ", imageFile2.toString());
                Log.d("shlok uri: ", imageUri.toString());
                startActivityIfNeeded(intent, 2);
            }
        }
    }

    private File getImageFile() throws IOException {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String fileName = "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        Log.d("shlok", fileName.toString());
        File createTempFile = File.createTempFile(fileName, ".jpg", storageDir);
        this.imageFile = createTempFile;
        Log.d("shlok", createTempFile.toString());
        this.currentPhotoPath = this.imageFile.getAbsolutePath();
        return this.imageFile;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap rotatedBitmap;
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == -1) {
            this.imageBitmap = BitmapFactory.decodeFile(this.currentPhotoPath);
            ExifInterface ei = null;
            try {
                ei = new ExifInterface(this.currentPhotoPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            switch (ei.getAttributeInt("Orientation", 0)) {
                case 3:
                    rotatedBitmap = rotateImage(this.imageBitmap, 180.0f);
                    break;
                case 6:
                    rotatedBitmap = rotateImage(this.imageBitmap, 90.0f);
                    break;
                case 8:
                    rotatedBitmap = rotateImage(this.imageBitmap, 270.0f);
                    break;
                default:
                    rotatedBitmap = this.imageBitmap;
                    break;
            }
            this.imageBitmap = rotatedBitmap;
            this.imageView.setImageBitmap(rotatedBitmap);
            this.saveButton.setEnabled(true);
            return;
        }
        this.saveButton.setEnabled(false);
    }

    public void saveImage(View v) {
        saveImageToGallery(this.imageBitmap);
        this.saveButton.setEnabled(false);
    }

    private void saveImageToGallery(Bitmap imageBitmap2) {
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String fileName = "IMG_" + new SimpleDateFormat("ddMMyyyyHHmm", Locale.getDefault()).format(new Date()) + "__" + this.IdenNo + ".jpg";
        Log.d("shlok", fileName.toString());
        File imageFileSave = new File(storageDir, fileName);
        try {
            FileOutputStream outputStream = new FileOutputStream(imageFileSave);
            imageBitmap2.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
            mediaScanIntent.setData(Uri.fromFile(imageFileSave));
            sendBroadcast(mediaScanIntent);
            Toast.makeText(this, "Image Saved Successully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save Image", Toast.LENGTH_SHORT).show();
        }
    }
}
