package com.example.secureout;

import static android.content.ContentValues.TAG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    TextView txt_name, txt_company, txt_item, txt_cartype, txt_carreg, txt_draw1, txt_draw2, txt_draw3,
            txt_image1, txt_image2, txt_image3, txt_image4, txt_image5;
    EditText txt_doc;
    ImageButton btn_qr, btn_draw1, btn_draw2, btn_draw3, btn_image1, btn_image2, btn_image3, btn_image4, btn_image5;
    Button btn_save;
    ProgressDialog progressDialog;
    RetrofitAPI retrofitAPI;
    String currentPhotoPath1, currentPhotoPath2, currentPhotoPath3, currentPhotoPath4, currentPhotoPath5,
            currentPhotoName1, currentPhotoName2, currentPhotoName3, currentPhotoName4, currentPhotoName5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt_doc = findViewById(R.id.txt_doc);
        txt_name = findViewById(R.id.txt_name);
        txt_company = findViewById(R.id.txt_company);
        txt_item = findViewById(R.id.txt_item);
        txt_cartype = findViewById(R.id.txt_cartype);
        txt_carreg = findViewById(R.id.txt_carreg);
        txt_draw1 = findViewById(R.id.txt_draw1);
        txt_draw2 = findViewById(R.id.txt_draw2);
        txt_draw3 = findViewById(R.id.txt_draw3);

        txt_image1 = findViewById(R.id.txt_image1);
        txt_image2 = findViewById(R.id.txt_image2);
        txt_image3 = findViewById(R.id.txt_image3);
        txt_image4 = findViewById(R.id.txt_image4);
        txt_image5 = findViewById(R.id.txt_image5);

        btn_qr = findViewById(R.id.btn_qr);
        btn_save = findViewById(R.id.btn_save);
        btn_draw1 = findViewById(R.id.btn_draw1);
        btn_draw2 = findViewById(R.id.btn_draw2);
        btn_draw3 = findViewById(R.id.btn_draw3);
        btn_image1 = findViewById(R.id.btn_image1);
        btn_image2 = findViewById(R.id.btn_image2);
        btn_image3 = findViewById(R.id.btn_image3);
        btn_image4 = findViewById(R.id.btn_image4);
        btn_image5 = findViewById(R.id.btn_image5);

        btn_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txt_doc != null) {
                    String docText = txt_doc.getText().toString();
                    if (docText.isEmpty()) {
                        StartScaning();
                    } else {
                        Log.d("doc_num", "doc: " + docText);
                        try {
                            GetDocById(docText);
                        } catch (Exception e) {
                            Log.e("Error", "Exception in GetDocById: " + e.getMessage(), e);
                            // Optionally show a user-friendly message
                            Toast.makeText(getApplicationContext(), "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Log.e("Error", "txt_doc is null");
                }
            }
        });

        btn_draw1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txt_doc.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "โปรดแสกนหมายเลขเอกสารก่อน", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    Intent intent = new Intent(MainActivity.this, SignaturePad.class);
                    intent.putExtra("DOC_NUM", txt_doc.getText().toString());
                    startActivityForResult(intent, 200);
                }
            }
        });

        btn_draw2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txt_doc.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "โปรดแสกนหมายเลขเอกสารก่อน", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    Intent intent2 = new Intent(MainActivity.this, SignaturePad2.class);
                    intent2.putExtra("DOC_NUM", txt_doc.getText().toString());
                    startActivityForResult(intent2, 300);
                }
            }
        });

        btn_draw3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txt_doc.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "โปรดแสกนหมายเลขเอกสารก่อน", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    Intent intent3 = new Intent(MainActivity.this, SignaturePad3.class);
                    intent3.putExtra("DOC_NUM", txt_doc.getText().toString());
                    startActivityForResult(intent3, 400);
                }
            }
        });

        btn_image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txt_doc.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "โปรดแสกนหมายเลขเอกสารก่อน", Toast.LENGTH_LONG).show();
                    return;
                }  else if (txt_draw1.getText().toString() != "ลงชื่อแล้ว"){
                    Toast.makeText(MainActivity.this, "โปรดลงชื่อให้ครบ", Toast.LENGTH_LONG).show();
                    return;
                }else if (txt_draw2.getText().toString() != "ลงชื่อแล้ว"){
                    Toast.makeText(MainActivity.this, "โปรดลงชื่อให้ครบ", Toast.LENGTH_LONG).show();
                    return;
                }else if (txt_draw3.getText().toString() != "ลงชื่อแล้ว") {
                    Toast.makeText(MainActivity.this, "โปรดลงชื่อให้ครบ", Toast.LENGTH_LONG).show();
                    return;
                } else if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    }, 500);
                }
                dispatchTakePictureIntent1();
                Log.d("photo", "Response successful, items: " + currentPhotoName1);
                //Log.d("photo", "Response successful, items: " + currentPhotoPath1);
            }
        });

        btn_image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txt_doc.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "โปรดแสกนหมายเลขเอกสารก่อน", Toast.LENGTH_LONG).show();
                    return;
                }  else if (txt_draw1.getText().toString() != "ลงชื่อแล้ว"){
                    Toast.makeText(MainActivity.this, "โปรดลงชื่อให้ครบ", Toast.LENGTH_LONG).show();
                    return;
                }else if (txt_draw2.getText().toString() != "ลงชื่อแล้ว"){
                    Toast.makeText(MainActivity.this, "โปรดลงชื่อให้ครบ", Toast.LENGTH_LONG).show();
                    return;
                }else if (txt_draw3.getText().toString() != "ลงชื่อแล้ว") {
                    Toast.makeText(MainActivity.this, "โปรดลงชื่อให้ครบ", Toast.LENGTH_LONG).show();
                    return;
                } else if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    }, 600);
                }
                dispatchTakePictureIntent2();
            }
        });

        btn_image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txt_doc.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "โปรดแสกนหมายเลขเอกสารก่อน", Toast.LENGTH_LONG).show();
                    return;
                }  else if (txt_draw1.getText().toString() != "ลงชื่อแล้ว"){
                    Toast.makeText(MainActivity.this, "โปรดลงชื่อให้ครบ", Toast.LENGTH_LONG).show();
                    return;
                }else if (txt_draw2.getText().toString() != "ลงชื่อแล้ว"){
                    Toast.makeText(MainActivity.this, "โปรดลงชื่อให้ครบ", Toast.LENGTH_LONG).show();
                    return;
                }else if (txt_draw3.getText().toString() != "ลงชื่อแล้ว") {
                    Toast.makeText(MainActivity.this, "โปรดลงชื่อให้ครบ", Toast.LENGTH_LONG).show();
                    return;
                } else if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    }, 700);
                }
                dispatchTakePictureIntent3();
            }
        });

        btn_image4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txt_doc.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "โปรดแสกนหมายเลขเอกสารก่อน", Toast.LENGTH_LONG).show();
                    return;
                }  else if (txt_draw1.getText().toString() != "ลงชื่อแล้ว"){
                    Toast.makeText(MainActivity.this, "โปรดลงชื่อให้ครบ", Toast.LENGTH_LONG).show();
                    return;
                }else if (txt_draw2.getText().toString() != "ลงชื่อแล้ว"){
                    Toast.makeText(MainActivity.this, "โปรดลงชื่อให้ครบ", Toast.LENGTH_LONG).show();
                    return;
                }else if (txt_draw3.getText().toString() != "ลงชื่อแล้ว") {
                    Toast.makeText(MainActivity.this, "โปรดลงชื่อให้ครบ", Toast.LENGTH_LONG).show();
                    return;
                } else if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    }, 800);
                }
                dispatchTakePictureIntent4();
            }
        });

        btn_image5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txt_doc.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "โปรดแสกนหมายเลขเอกสารก่อน", Toast.LENGTH_LONG).show();
                    return;
                }  else if (txt_draw1.getText().toString() != "ลงชื่อแล้ว"){
                    Toast.makeText(MainActivity.this, "โปรดลงชื่อให้ครบ", Toast.LENGTH_LONG).show();
                    return;
                }else if (txt_draw2.getText().toString() != "ลงชื่อแล้ว"){
                    Toast.makeText(MainActivity.this, "โปรดลงชื่อให้ครบ", Toast.LENGTH_LONG).show();
                    return;
                }else if (txt_draw3.getText().toString() == "ลงชื่อแล้ว") {
                    Toast.makeText(MainActivity.this, "โปรดลงชื่อให้ครบ", Toast.LENGTH_LONG).show();
                    return;
                } else if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    }, 900);
                }
                dispatchTakePictureIntent5();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(txt_doc.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "โปรดแสกนหมายเลขเอกสารก่อน", Toast.LENGTH_LONG).show();
                    return;
                }
                if(txt_image1.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "ต้องถ่ายอย่างน้อย3รูป", Toast.LENGTH_LONG).show();
                    return;
                }
                if(txt_image2.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "ต้องถ่ายอย่างน้อย3รูป", Toast.LENGTH_LONG).show();
                    return;
                }
                if(txt_image3.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "ต้องถ่ายอย่างน้อย3รูป", Toast.LENGTH_LONG).show();
                    return;
                }
                if(txt_image1.getText().toString() == "ถ่ายรูปแล้ว" && txt_image2.getText().toString() == "ถ่ายรูปแล้ว" &&
                        txt_image3.getText().toString() == "ถ่ายรูปแล้ว" ){
                    Toast.makeText(MainActivity.this, "เอกสารข้อมูลครบหมดแล้ว", Toast.LENGTH_LONG).show();
                    return;
                }

                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.show();
                progressDialog.setContentView(R.layout.progress_dialog);
                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                File imageFile1 = new File(currentPhotoPath1);
                File imageFile2 = new File(currentPhotoPath2);
                File imageFile3 = new File(currentPhotoPath3);
                File imageFile4 = null;
                File imageFile5 = null;

                if (currentPhotoPath4 != null) {
                    imageFile4 = new File(currentPhotoPath4);
                }
                if (currentPhotoPath5 != null) {
                    imageFile5 = new File(currentPhotoPath5);
                }

                uploadImage(imageFile1, imageFile2, imageFile3, imageFile4, imageFile5);
                btn_image4.setVisibility(View.INVISIBLE);
                btn_image5.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void StartScaning() {
        Intent intent = new Intent(MainActivity.this, Zxing_scanner.class);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            try {
                if (resultCode == RESULT_OK) {
                    String strText0 = data.getStringExtra("DOC_NUM");
                    txt_doc.setText(strText0);

                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.show();
                    progressDialog.setContentView(R.layout.progress_dialog);
                    progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                    GetDocById(txt_doc.getText().toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (requestCode == 200) {
            try {
                if (resultCode == RESULT_OK) {
                    //GetDocById(txt_doc.getText().toString());
                    String StingSig1 = data.getStringExtra("RESULT");
                    txt_draw1.setText(StingSig1);
                    if (txt_draw1.getText().toString().equals("ลงชื่อแล้ว")) {
                        txt_draw1.setBackgroundColor(Color.GREEN);
                        btn_draw1.setVisibility(View.INVISIBLE);
                    }

                    GetDocById(txt_doc.getText().toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (requestCode == 300) {
            try {
                if (resultCode == RESULT_OK) {
                    //GetDocById(txt_doc.getText().toString());
                    String StingSig1 = data.getStringExtra("RESULT");
                    txt_draw2.setText(StingSig1);
                    if (txt_draw2.getText().toString().equals("ลงชื่อแล้ว")) {
                        txt_draw2.setBackgroundColor(Color.GREEN);
                        btn_draw2.setVisibility(View.INVISIBLE);
                    }

                    GetDocById(txt_doc.getText().toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (requestCode == 400) {
            try {
                if (resultCode == RESULT_OK) {
                    //GetDocById(txt_doc.getText().toString());
                    String StingSig1 = data.getStringExtra("RESULT");
                    txt_draw3.setText(StingSig1);
                    if (txt_draw3.getText().toString().equals("ลงชื่อแล้ว")) {
                        txt_draw3.setBackgroundColor(Color.GREEN);
                        btn_draw3.setVisibility(View.INVISIBLE);
                    }

                    GetDocById(txt_doc.getText().toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (requestCode == 500 && resultCode == RESULT_OK) {
            try {
                if (currentPhotoPath1 != null) {
                    txt_image1.setText(currentPhotoName1);
                    btn_image1.setVisibility(View.INVISIBLE);
                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (requestCode == 600 && resultCode == RESULT_OK) {
            try {
                if (currentPhotoPath2 != null) {
                    txt_image2.setText(currentPhotoName2);
                    btn_image2.setVisibility(View.INVISIBLE);
                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (requestCode == 700 && resultCode == RESULT_OK) {
            try {
                if (currentPhotoPath3 != null) {
                    txt_image3.setText(currentPhotoName3);
                    btn_image3.setVisibility(View.INVISIBLE);
                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (requestCode == 800 && resultCode == RESULT_OK) {
            try {
                if (currentPhotoPath4 != null) {
                    txt_image4.setText(currentPhotoName4);
                    btn_image4.setVisibility(View.INVISIBLE);
                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (requestCode == 900 && resultCode == RESULT_OK) {
            try {
                if (currentPhotoPath5 != null) {
                    txt_image5.setText(currentPhotoName5);
                    btn_image5.setVisibility(View.INVISIBLE);
                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void GetDocById(String Doc_num) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://XX.XX.XX.X:XX/secure/")
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<List<Model_Doc>> Doc_item = retrofitAPI.getDocById(Doc_num);
        Doc_item.enqueue(new Callback<List<Model_Doc>>() {
            @Override
            public void onResponse(Call<List<Model_Doc>> call, Response<List<Model_Doc>> response) {
                if (response.isSuccessful()) {
                    List<Model_Doc> model_items = response.body();
                    Log.d("GetDocById", "Response successful, items: " + model_items);
                    if (model_items != null && !model_items.isEmpty()) {
                        Model_Doc model_item = model_items.get(0);
                        Log.d("GetDocById", "Response successful, getDraw1: " + model_item.getDraw1());
                        Log.d("GetDocById", "Response successful, getDraw2: " + model_item.getDraw2());
                        Log.d("GetDocById", "Response successful, getDraw3: " + model_item.getDraw3());
                        Log.d("GetDocById", "Response successful, getImage1: " + model_item.getImage1());
                        Log.d("GetDocById", "Response successful, getImage2: " + model_item.getImage2());
                        Log.d("GetDocById", "Response successful, getImage3: " + model_item.getImage3());
                        Log.d("GetDocById", "Response successful, getImage4: " + model_item.getImage4());
                        Log.d("GetDocById", "Response successful, getImage5: " + model_item.getImage5());
                        txt_doc.setText(model_item.getDoc_num());
                        txt_name.setText(model_item.getName_out());
                        txt_company.setText(model_item.getCompany());
                        txt_cartype.setText(model_item.getCar_type());
                        txt_carreg.setText(model_item.getCar_reg());
                        if (model_item.getJob_qty() != null && model_item.getContainer_qty() == null && model_item.getContainer_um() == null
                                && model_item.getTool_qty() == null && model_item.getComputer_qty() == null && model_item.getMeasuringtools_qty() == null) {
                            txt_item.setText("ชิ้นงาน " + model_item.getJob_qty() + " ชิ้น");
                        }
                        if (model_item.getJob_qty() == null && model_item.getContainer_qty() != null && model_item.getContainer_um() != null
                                && model_item.getTool_qty() == null && model_item.getComputer_qty() == null && model_item.getMeasuringtools_qty() == null) {
                            txt_item.setText("ภาชนะ " + model_item.getContainer_qty() + " " + model_item.getContainer_um());
                        }
                        if (model_item.getJob_qty() == null && model_item.getContainer_qty() == null && model_item.getContainer_um() == null
                                && model_item.getTool_qty() != null && model_item.getComputer_qty() == null && model_item.getMeasuringtools_qty() == null) {
                            txt_item.setText("JIG/GAUGE " + model_item.getTool_qty() + " ชิ้น");
                        }
                        if (model_item.getJob_qty() == null && model_item.getContainer_qty() == null && model_item.getContainer_um() == null
                                && model_item.getTool_qty() == null && model_item.getComputer_qty() != null && model_item.getMeasuringtools_qty() == null) {
                            txt_item.setText("คอม " + model_item.getComputer_qty() + " เครื่อง");
                        }
                        if (model_item.getJob_qty() == null && model_item.getContainer_qty() == null && model_item.getContainer_um() == null
                                && model_item.getTool_qty() == null && model_item.getComputer_qty() == null && model_item.getMeasuringtools_qty() != null) {
                            txt_item.setText("เครื่องมือวัด " + model_item.getMeasuringtools_qty() + " เครื่อง");
                        }
                        if (model_item.getDraw1() != null) {
                            txt_draw1.setText("ลงชื่อแล้ว");
                            txt_draw1.setBackgroundColor(Color.GREEN);
                            btn_draw1.setVisibility(View.INVISIBLE);
                        }else {
                            txt_draw1.setText("ยังไม่ได้ลงชื่อ");
                            txt_draw1.setBackgroundColor(Color.parseColor("#FB144C"));
                            btn_draw1.setVisibility(View.VISIBLE);
                        }

                        if (model_item.getDraw2() != null) {
                            txt_draw2.setText("ลงชื่อแล้ว");
                            txt_draw2.setBackgroundColor(Color.GREEN);
                            btn_draw2.setVisibility(View.INVISIBLE);
                        }
                        else {
                            txt_draw2.setText("ยังไม่ได้ลงชื่อ");
                            txt_draw2.setBackgroundColor(Color.parseColor("#FB144C"));
                            btn_draw2.setVisibility(View.VISIBLE);
                        }

                        if (model_item.getDraw3() != null) {
                            txt_draw3.setText("ลงชื่อแล้ว");
                            txt_draw3.setBackgroundColor(Color.GREEN);
                            btn_draw3.setVisibility(View.INVISIBLE);
                        }else {
                            txt_draw3.setText("ยังไม่ได้ลงชื่อ");
                            txt_draw3.setBackgroundColor(Color.parseColor("#FB144C"));
                            btn_draw3.setVisibility(View.VISIBLE);
                        }

                        if (model_item.getImage1() != null) {
                            txt_image1.setText("ถ่ายรูปแล้ว");
                            txt_image1.setBackgroundColor(Color.GREEN);
                            btn_image1.setVisibility(View.INVISIBLE);
                        }else {
                            txt_image1.setText("");
                            txt_image1.setBackgroundColor(Color.parseColor("#CFD3D0"));
                            btn_image1.setVisibility(View.VISIBLE);
                        }

                        if (model_item.getImage2() != null) {
                            txt_image2.setText("ถ่ายรูปแล้ว");
                            txt_image2.setBackgroundColor(Color.GREEN);
                            btn_image2.setVisibility(View.INVISIBLE);
                        }else {
                            txt_image2.setText("");
                            txt_image2.setBackgroundColor(Color.parseColor("#CFD3D0"));
                            btn_image2.setVisibility(View.VISIBLE);
                        }

                        if (model_item.getImage3() != null) {
                            txt_image3.setText("ถ่ายรูปแล้ว");
                            txt_image3.setBackgroundColor(Color.GREEN);
                            btn_image3.setVisibility(View.INVISIBLE);
                        }else {
                            txt_image3.setText("");
                            txt_image3.setBackgroundColor(Color.parseColor("#CFD3D0"));
                            btn_image3.setVisibility(View.VISIBLE);
                        }

                        if (model_item.getImage4() != null) {
                            txt_image4.setText("ถ่ายรูปแล้ว");
                            txt_image4.setBackgroundColor(Color.GREEN);
                            btn_image4.setVisibility(View.INVISIBLE);
                        }else if(model_item.getImage3() != null) {
                            txt_image4.setText("");
                            txt_image4.setBackgroundColor(Color.parseColor("#CFD3D0"));
                            btn_image4.setVisibility(View.INVISIBLE);
                        }else {
                            txt_image4.setText("");
                            txt_image4.setBackgroundColor(Color.parseColor("#CFD3D0"));
                            btn_image4.setVisibility(View.VISIBLE);
                        }

                        if (model_item.getImage5() != null) {
                            txt_image5.setText("ถ่ายรูปแล้ว");
                            txt_image5.setBackgroundColor(Color.GREEN);
                            btn_image5.setVisibility(View.INVISIBLE);
                        }else if(model_item.getImage3() != null) {
                            txt_image5.setText("");
                            txt_image5.setBackgroundColor(Color.parseColor("#CFD3D0"));
                            btn_image5.setVisibility(View.INVISIBLE);
                        }else {
                            txt_image5.setText("");
                            txt_image5.setBackgroundColor(Color.parseColor("#CFD3D0"));
                            btn_image5.setVisibility(View.VISIBLE);
                        }

                        progressDialog.dismiss();

                    } else {
                        Log.d("GetDocById", "No document found");
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "No document found", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.e("GetDocById", "Response error: " + response.message());
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Response error: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Model_Doc>> call, Throwable t) {
                Log.e("GetDocById", "Get doc error: " + t.getMessage());
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "get doc error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    private void dispatchTakePictureIntent1() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create a file to store the image
            File photoFile1 = null;
            try {
                photoFile1 = createImageFile1();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile1 != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.secureout.fileprovider", photoFile1);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 500);
            }
        }
    }
    private void dispatchTakePictureIntent2() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create a file to store the image
            File photoFile2 = null;
            try {
                photoFile2 = createImageFile2();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile2 != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.secureout.fileprovider", photoFile2);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 600);
            }
        }
    }
    private void dispatchTakePictureIntent3() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create a file to store the image
            File photoFile3 = null;
            try {
                photoFile3 = createImageFile3();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile3 != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.secureout.fileprovider", photoFile3);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 700);
            }
        }
    }
    private void dispatchTakePictureIntent4() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create a file to store the image
            File photoFile4 = null;
            try {
                photoFile4 = createImageFile4();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile4 != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.secureout.fileprovider", photoFile4);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 800);
            }
        }
    }
    private void dispatchTakePictureIntent5() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create a file to store the image
            File photoFile5 = null;
            try {
                photoFile5 = createImageFile5();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile5 != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.secureout.fileprovider", photoFile5);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 900);
            }
        }
    }
    private File createImageFile1() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        String imageFileName = "PHOTO1_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile1 = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath1 = imageFile1.getAbsolutePath();
        currentPhotoName1 = imageFile1.getName();
        return imageFile1;
    }
    private File createImageFile2() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "PHOTO2_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile2 = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath2 = imageFile2.getAbsolutePath();
        currentPhotoName2 = imageFile2.getName();
        return imageFile2;
    }
    private File createImageFile3() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "PHOTO3_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile3 = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath3 = imageFile3.getAbsolutePath();
        currentPhotoName3 = imageFile3.getName();
        return imageFile3;
    }
    private File createImageFile4() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "PHOTO4_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile4 = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath4 = imageFile4.getAbsolutePath();
        currentPhotoName4 = imageFile4.getName();
        return imageFile4;
    }
    private File createImageFile5() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "PHOTO5_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile5 = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath5 = imageFile5.getAbsolutePath();
        currentPhotoName5 = imageFile5.getName();
        return imageFile5;
    }
    private void uploadImage(File imageFile1, File imageFile2, File imageFile3, File imageFile4, File imageFile5) {
        if (txt_image1.getText().toString().isEmpty()) {
            Toast.makeText(MainActivity.this, "ต้องถ่ายอย่างน้อย3รูป" , Toast.LENGTH_LONG).show();
            return;
        }
        if (txt_image2.getText().toString().isEmpty()) {
            Toast.makeText(MainActivity.this, "ต้องถ่ายอย่างน้อย3รูป" , Toast.LENGTH_LONG).show();
            return;
        }
        if (txt_image3.getText().toString().isEmpty()) {
            Toast.makeText(MainActivity.this, "ต้องถ่ายอย่างน้อย3รูป" , Toast.LENGTH_LONG).show();
            return;
        }

        String doc_num = txt_doc.getText().toString();
        RequestBody docRequestBody = RequestBody.create(MediaType.parse("text/plain"), doc_num);

        RequestBody requestBody1 = RequestBody.create(MediaType.parse("image1/jpeg"), imageFile1);
        MultipartBody.Part filePart1 = MultipartBody.Part.createFormData("file1", imageFile1.getName(), requestBody1);

        RequestBody requestBody2 = RequestBody.create(MediaType.parse("image2/jpeg"), imageFile2);
        MultipartBody.Part filePart2 = MultipartBody.Part.createFormData("file2", imageFile2.getName(), requestBody2);

        RequestBody requestBody3 = RequestBody.create(MediaType.parse("image3/jpeg"), imageFile3);
        MultipartBody.Part filePart3 = MultipartBody.Part.createFormData("file3", imageFile3.getName(), requestBody3);

        MultipartBody.Part filePart4 = null;
        MultipartBody.Part filePart5 = null;

        if(imageFile4 != null) {
            RequestBody requestBody4 = RequestBody.create(MediaType.parse("image4/jpeg"), imageFile4);
            filePart4 = MultipartBody.Part.createFormData("file4", imageFile4.getName(), requestBody4);
        }
        if(imageFile5 != null) {
            RequestBody requestBody5 = RequestBody.create(MediaType.parse("image5/jpeg"), imageFile5);
            filePart5 = MultipartBody.Part.createFormData("file5", imageFile5.getName(), requestBody5);
        }

        Call<ResponseBody> call = retrofitAPI.uploadPicture(filePart1, filePart2, filePart3, filePart4, filePart5, docRequestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "บันทึกข้อมูลสำเร็จ", Toast.LENGTH_LONG).show();
                    currentPhotoPath1 = null;
                    currentPhotoPath2 = null;
                    currentPhotoPath3 = null;
                    currentPhotoPath4 = null;
                    currentPhotoPath5 = null;
                    currentPhotoName1 = null;
                    currentPhotoName2 = null;
                    currentPhotoName3 = null;
                    currentPhotoName4 = null;
                    currentPhotoName5 = null;

                    GetDocById(txt_doc.getText().toString());
                    progressDialog.dismiss();
                }else {
                    // Handle API error
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "API error: " + response.message(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "API error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Network error: " + t.getMessage());
            }
        });
    }
}
