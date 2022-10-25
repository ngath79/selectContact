package com.example.selectcontact;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.database.Cursor;
import android.net.Uri;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private ImageView thumbnailIv;
    private TextView contactTv;
    private FloatingActionButton addFab;

    private static final int CONTACT_PERMISSION_CODE = 1;
    private static final int CONTACT_PICK_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);

        thumbnailIv = findViewById (R.id.thumbnailIv);
        contactTv = findViewById (R.id.contactTV);
        addFab = findViewById(R.id.addFab);

        addFab.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                if(checkContactPermission()) {
                    pickContactIntent();
                }
                else {
                    requestContactPermission();
                }
            }
        });

    }

    private boolean checkContactPermission() {
        boolean result = ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_CONTACTS) == (PackageManager.PERMISSION_GRANTED
        );
        return result;
    }

    private void requestContactPermission() {
        String[] permission = {
                Manifest.permission.READ_CONTACTS
        };
        ActivityCompat.requestPermissions(this, permission, CONTACT_PERMISSION_CODE);
    }

    private void pickContactIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent,CONTACT_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == CONTACT_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickContactIntent();
            } else {
                Toast.makeText(this, "Permission denied...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("Range")
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            if(resultCode == CONTACT_PICK_CODE) {
                contactTv.setText("");
                Cursor cursor1, cursor2;
                Uri uri = data.getData();
                cursor1 = getContentResolver().query(uri,null,null,null,null);
                if(cursor1.moveToFirst()) {
                    String contactId;
                    contactId = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts._ID));
                    String contactName;
                    contactName = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String contactThumnail;
                    contactThumnail = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
                    String idResults;
                    idResults = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    int idResultHold = Integer.parseInt(idResults);

                    if(idResultHold == 1) {
                        cursor2 = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);

                        while (cursor2.moveToNext()) {
                            String contactNumber = cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                            contactTv.append(("\nPhone" + contactNumber));


                            if (contactThumnail != null) {
                                thumbnailIv.setImageURI(uri.parse(contactThumnail));
                            }
                            else {
                                thumbnailIv.setImageResource(R.drawable.ic_baseline_person_24);
                            }
                        }
                        cursor2.close();
                    }
                    cursor1.close();
                }
            }
        }
    }
}