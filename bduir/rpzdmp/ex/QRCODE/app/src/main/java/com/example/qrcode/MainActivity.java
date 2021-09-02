package com.example.qrcode;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.content.ContentUris;

import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;





public class MainActivity extends AppCompatActivity {


    EditText first_name;
    EditText last_name;
    EditText phone;
    Button button1;
    Button create;
    ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        first_name = (EditText) findViewById(R.id.first_name);
        last_name = (EditText) findViewById(R.id.last_name);
        phone = (EditText) findViewById(R.id.phone_number);
        button1 = (Button) findViewById(R.id.generate_qr_btn);
        imageView = (ImageView) findViewById(R.id.image_view);
        create = (Button)findViewById(R.id.button_contacts);

      create.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              //Intent intent = new Intent(this, Contacts.class);
              //startActivity(intent);
          }
      });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initQRCode();
            }
        });

    }




    private void initQRCode() {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        try {
                    BitMatrix bitMatrix = multiFormatWriter.encode(first_name.getText().toString()+" "+last_name.getText().toString()+" "+phone.getText().toString(), BarcodeFormat.QR_CODE, 600, 600);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    imageView.setImageBitmap(bitmap);
                    imageView.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }

        }

    }




