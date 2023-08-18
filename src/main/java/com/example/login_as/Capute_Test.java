//package com.example.login_as;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.os.Bundle;
//
//public class Capute_Test extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_capute_test);
//    }
//}
package com.example.login_as;

import android.app.Activity;
import android.content.Context;
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
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.android.volley.DefaultRetryPolicy;
//import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
//import retrofit2.Retrofit;

public class Capute_Test extends AppCompatActivity {
//    private static String BASE_URL = "https://shlok1278.pythonanywhere.com";
//    private static final int REQUEST_CAMERA_PERMISSION_CODE = 1;
//    private static final int REQUEST_IMAGE_CAPTURE = 2;
//    private static Retrofit retrofit;
//    private final int CAMERA_REQ_CODE = 69;
    private ArrayList<HashMap<String, String>> arraylist;
    Button captureButton;
    ImageView capturedFp;
    String currentPhotoPath;
    Bitmap imageBitmap;
    ImageView imgCamera;
    ProgressBar progressBar;
//    private RequestQueue rQueue;
    Uri refImageData;
    Button uploadFromMedia;
    String url = "https://Shlok1278.pythonanywhere.com/match";
    Button verifyButton;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_capute_test);
        this.imgCamera = (ImageView) findViewById(R.id.Capture_Image);
        Uri data = getIntent().getData();
        this.refImageData = data;
        this.imgCamera.setImageURI(data);
        this.capturedFp = (ImageView) findViewById(R.id.fp_image);
        this.captureButton = (Button) findViewById(R.id.Upload_Test);
        this.verifyButton = (Button) findViewById(R.id.Verify);
        ProgressBar progressBar2 = (ProgressBar) findViewById(R.id.showProgress);
        this.progressBar = progressBar2;
        progressBar2.setVisibility(View.GONE);
        this.uploadFromMedia = (Button) findViewById(R.id.uploadFromMedia);
        this.verifyButton.setVisibility(View.GONE);
        final ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Uri uri = result.getData().getData();
                    Context context = Capute_Test.this;
//                    Log.d("shlok dmdnmd", "I am here");
                    try {
                        Capute_Test.this.imageBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                        Capute_Test.this.capturedFp.setImageURI(uri);
//                        Log.d("shlok dmdnmd", "I am here too");

                        verifyButton.setVisibility(View.VISIBLE);
//                        Log.d("shlok dmdnmd", "I am here too ngl");
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(Capute_Test.this, e.toString(), Toast.LENGTH_SHORT).show();
                        Capute_Test.this.verifyButton.setVisibility(View.GONE);
//                        Log.d("shlok dmdnmd", "I am here somehow");
                    }
                } else {
                    Capute_Test.this.verifyButton.setVisibility(View.GONE);
//                    Log.d("shlok dmdnmd", "Oh, I am here");
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
        this.verifyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bitmap refBitmap;
                Bitmap refBitmap2;
                try {
                    refBitmap = MediaStore.Images.Media.getBitmap(Capute_Test.this.getContentResolver(), Capute_Test.this.refImageData);
                } catch (IOException e) {
                    e.printStackTrace();
                    refBitmap = Capute_Test.this.imageBitmap;
                    Toast.makeText(Capute_Test.this, "Some issue in receiving image from last activity, try again", Toast.LENGTH_SHORT).show();
                }
                try {
                    Capute_Test capute_Test = Capute_Test.this;
                    capute_Test.imageBitmap = Bitmap.createScaledBitmap(capute_Test.imageBitmap, 400, 400, true);
                    refBitmap2 = Bitmap.createScaledBitmap(refBitmap, 400, 400, true);
                } catch (Exception e2) {
                    e2.printStackTrace();
                    refBitmap2 = refBitmap;
                }
                Capute_Test.this.captureButton.setVisibility(View.GONE);
                Capute_Test.this.uploadFromMedia.setVisibility(View.GONE);
                Capute_Test capute_Test2 = Capute_Test.this;
                String imageByteArrayStr = capute_Test2.BitMapToString(capute_Test2.imageBitmap);
                final String str = imageByteArrayStr;
                final String BitMapToString = Capute_Test.this.BitMapToString(refBitmap2);
                StringRequest request = new StringRequest(1, Capute_Test.this.url, new Response.Listener<String>() {
                    public void onResponse(String response) {
                        Capute_Test.this.progressBar.setVisibility(View.GONE);
                        Log.d("shlok", "it also here");
                        try {
                            String data = new JSONObject(response).getString("distance");
                            Log.d("shlok d", data);
                            Intent intent = new Intent(Capute_Test.this, Matching.class);
                            SharedPreferences.Editor editor = Capute_Test.this.getSharedPreferences("DISTANCE", 0).edit();
                            editor.putString("Distance", data);
                            editor.apply();
                            Capute_Test.this.startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Capute_Test.this.progressBar.setVisibility(View.GONE);
                        Log.d("shlok error", "error");
                        try {
                            Toast.makeText(Capute_Test.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("shlok", "" + error.getMessage());
                        } catch (Exception e) {
                            Log.d("shlok nerror", e.toString());
                            e.printStackTrace();
                        }
                    }
                }) {
                    /* access modifiers changed from: protected */
                    public Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("bm1", str);
                        params.put("bm2", BitMapToString);
                        return params;
                    }
                };
                request.setRetryPolicy(new DefaultRetryPolicy(60000, 5, 3.0f));
                Volley.newRequestQueue(Capute_Test.this).add(request);
                Capute_Test.this.verifyButton.setVisibility(View.GONE);
                Capute_Test.this.progressBar.setVisibility(View.VISIBLE);
                Log.d("shlok", "part 3 done");
            }
        });
    }

    public String BitMapToString(Bitmap bitmap) {
//        Log.d("shlok", String.valueOf(bitmap.getByteCount()));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bu = baos.toByteArray();
//        Log.d("shlok", String.valueOf(bu.length));
        return Base64.encodeToString(bu, 0);
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
            File newFile = null;
            try {
                newFile = getImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (newFile != null) {
                intent.putExtra("output", FileProvider.getUriForFile(this, "com.example.login_as.fileprovider", newFile));
                startActivityIfNeeded(intent, 2);
            }
        }
    }

    private File getImageFile() throws IOException {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String fileName = "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        Log.d("shlok", fileName.toString());
        File imageFile = File.createTempFile(fileName, ".jpg", storageDir);
        Log.d("shlok", imageFile.toString());
        this.currentPhotoPath = imageFile.getAbsolutePath();
        return imageFile;
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
        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
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
            this.capturedFp.setImageBitmap(rotatedBitmap);
            this.verifyButton.setVisibility(View.VISIBLE);
            return;
        }
        this.verifyButton.setVisibility(View.GONE);
    }
}
