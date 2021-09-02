package com.example.qrcodegeneratorcontacts;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.result.VCardResultParser;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.r0adkll.slidr.Slidr;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.provider.ContactsContract;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_READ_CONTACTS = 1;
    private static final int PICK_CONTACT = 1;
    private static boolean READ_CONTACTS_GRANTED = false;
    Context homeactivity;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeactivity = this;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        imageView = (ImageView)findViewById(R.id.imageview);

        int hasReadContactPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);

        if(hasReadContactPermission == PackageManager.PERMISSION_GRANTED){
            READ_CONTACTS_GRANTED = true;
        }
        else{
            // вызываем диалоговое окно для установки разрешений
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
            }
        });
        FloatingActionButton open_scanner = findViewById(R.id.open_scanner);
        open_scanner.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                IntentIntegrator integrator = new IntentIntegrator((Activity) homeactivity);

                integrator.initiateScan();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){

        switch (requestCode){
            case REQUEST_CODE_READ_CONTACTS:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    READ_CONTACTS_GRANTED = true;
                }
        }
        if(!READ_CONTACTS_GRANTED){
            Toast.makeText(this, "Требуется установить разрешения", Toast.LENGTH_LONG).show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == PICK_CONTACT) {
            getContactInfo(intent);
        }else {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
            if(result != null){
                if(result.getContents() != null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    final String str = result.getContents();
                    builder.setTitle("Scanning result");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    final String[] stroke = {""};
                    if(result.toString().contains("VCARD")){
                        String stroke_show = "";
                        String fio = "", tel = "", email = "";
                        String[] subStr = str.split(System.lineSeparator());

                        for(int i = 0; i < subStr.length; i++) {
                            if(subStr[i].contains("FN")) {
                                stroke_show = stroke_show + subStr[i].replace("FN", "ФИО") + System.lineSeparator();
                                fio = subStr[i].replace("FN:", "");
                            }
                            if(subStr[i].contains("TYPE=CELL")) {
                                stroke_show = stroke_show + subStr[i].replace("TEL;TYPE=CELL", "Телефон") + System.lineSeparator();
                                tel = subStr[i].replace("TEL;TYPE=CELL:", "");
                            }
                            if(subStr[i].contains("EMAIL;INTERNET")) {
                                stroke_show = stroke_show + subStr[i].replace("EMAIL;INTERNET", "EMAIL") + System.lineSeparator();
                                email = subStr[i].replace("EMAIL;INTERNET:", "");
                            }
                        }
                        final String finalFio = fio;
                        final String finalTel = tel;
                        final String finalEmail = email;
                        builder.setNegativeButton("Add to contact", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                try {
                                    /*File vcfFile = null;
                                    vcfFile = new File(homeactivity.getExternalCacheDir(), destinationContact.getName().replaceAll(" ", "") + ".vcf");
                                    FileWriter fw = new FileWriter(vcfFile);*/

                                    Intent i = new Intent(ContactsContract.Intents.Insert.ACTION);
                                    i.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                                    i.putExtra(ContactsContract.Intents.Insert.EMAIL, finalEmail)
                                            .putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                                            .putExtra(ContactsContract.Intents.Insert.PHONE, finalTel)
                                            .putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                                            .putExtra(ContactsContract.Intents.Insert.NAME, finalFio);

                                    startActivity(i);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        builder.setMessage(stroke_show);
                    }else{
                        builder.setMessage(result.getContents());
                    }

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {
                    Toast.makeText(this, "No result found", Toast.LENGTH_LONG).show();
                }
            }
            else {
                super.onActivityResult(requestCode, resultCode, intent);
            }
        }


    }//onActivityResult

    protected void getContactInfo(Intent intent)
    {
        imageView.setImageResource(android.R.color.transparent);
        if (intent == null)
        {
            return;
        }
        Cursor cursor =  managedQuery(intent.getData(), null, null, null, null);
        while (cursor.moveToNext()) {
            String str = "BEGIN:VCARD" + System.lineSeparator() +
                    "VERSION:3.0" + System.lineSeparator() +
                    "N:$name" + System.lineSeparator() +
                    "FN:$fullname" + System.lineSeparator() +
                    "ORG:" + System.lineSeparator() +
                    "ADR;WORK:;; ;;;" + System.lineSeparator() +
                    "TEL;WORK;VOICE:" + System.lineSeparator() +
                    "TEL;TYPE=CELL:$telnumber" + System.lineSeparator() +
                    "TEL;WORK;FAX:" + System.lineSeparator() +
                    "URL:" + System.lineSeparator() +
                    "EMAIL;INTERNET:$mail" + System.lineSeparator() +
                    "END:VCARD";
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));

            String newname = name.replace(' ', ';');
            str = str.replace("$name", newname);
            str = str.replace("$fullname", name);

            String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

            if (hasPhone.equalsIgnoreCase("1"))
                hasPhone = "true";
            else
                hasPhone = "false";

            if (Boolean.parseBoolean(hasPhone)) {
                Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                while (phones.moveToNext()) {
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    str = str.replace("$telnumber", phoneNumber);
                    break;
                }
                phones.close();
            }
            str = str.replace("$telnumber", "");

            // Find Email Addresses
            Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null);
            while (emails.moveToNext()) {
                String emailAddress = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                str = str.replace("$mail", emailAddress);
                break;
            }
            str = str.replace("$mail", "");
            emails.close();

            Cursor address = getContentResolver().query(
                    ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID + " = " + contactId,
                    null, null);
            while (address.moveToNext()) {
                String poBox = address.getString(address.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX));
                String street = address.getString(address.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                String city = address.getString(address.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                String state = address.getString(address.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
                String postalCode = address.getString(address.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
                String country = address.getString(address.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
                String type = address.getString(address.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
            }
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            try {
                Hashtable hints = new Hashtable();
                hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
                BitMatrix bitMatrix = multiFormatWriter.encode(str, BarcodeFormat.QR_CODE,1000,1000, hints);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                imageView.setImageBitmap(bitmap);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        cursor = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            View view = findViewById(R.id.fab);
            Snackbar.make(view, "Alexey Volodkovich", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}