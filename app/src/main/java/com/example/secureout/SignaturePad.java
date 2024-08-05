package com.example.secureout;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignaturePad extends AppCompatActivity {
    com.github.gcacace.signaturepad.views.SignaturePad  signaturePad;
    Button btn_clear, btn_save;
    RetrofitAPI retrofitAPI;
    String doc;
    ProgressDialog progressDialog;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature_pad);

        signaturePad = findViewById(R.id.signature_pad);
        btn_clear = findViewById(R.id.btn_clear);
        btn_save = findViewById(R.id.btn_save);

        Intent intent = getIntent();
        doc = intent.getStringExtra("DOC_NUM");

        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signaturePad.clear();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap signatureBitmap = signaturePad.getSignatureBitmap();
                if (signatureBitmap != null) {
                    try {
                        progressDialog = new ProgressDialog(SignaturePad.this);
                        progressDialog.show();
                        progressDialog.setContentView(R.layout.progress_dialog);
                        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                        File file = saveBitmapToFile(signatureBitmap);
                        uploadImage(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Failed to save
                }
            }
        });
    }
    private File saveBitmapToFile(Bitmap bitmap) throws IOException {
        File file = new File(getCacheDir(), "signature1.jpg");
        FileOutputStream fos = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        fos.close();
        return file;
    }
    private void uploadImage(File imageFile1) {
        if (imageFile1 == null) {
            Toast.makeText(SignaturePad.this, "upload image is value NULL" , Toast.LENGTH_LONG).show();
            return;
        }
        //String doc = getIntent().getExtras().getString("DOC_NUM");

        RequestBody requestBody1 = RequestBody.create(MediaType.parse("image1/jpeg"), imageFile1);
        MultipartBody.Part filePart1 = MultipartBody.Part.createFormData("file1", imageFile1.getName(), requestBody1);

        RequestBody docRequestBody = RequestBody.create(MediaType.parse("text/plain"), doc);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://XX.XX.XX.X:XX/secure/")
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<ResponseBody> call =retrofitAPI.uploadSig1(filePart1,docRequestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SignaturePad.this, "บึนทึกสำเร็จ" , Toast.LENGTH_LONG).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("RESULT", "ลงชื่อแล้ว");
                    setResult(RESULT_OK, resultIntent);
                    progressDialog.dismiss();
                    finish();
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(SignaturePad.this, "API error: " + response.message(), Toast.LENGTH_LONG).show();
                    Log.e("API error", "API error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(SignaturePad.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("Network error", "Network error: " + t.getMessage());
            }
        });
    }
}
