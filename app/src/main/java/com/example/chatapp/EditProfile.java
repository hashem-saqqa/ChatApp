package com.example.chatapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {
    EditText phoneET, nameET;
    String phone, name, photo;
    Uri profileAvatar = null;
    CircleImageView profileImage;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        phoneET = findViewById(R.id.phoneET);
        nameET = findViewById(R.id.nameET);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void uploadPhoto(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "select imagee"), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {

            if (data == null) {
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
            } else {
                profileAvatar = data.getData();
                profileImage.setImageURI(profileAvatar);

            }
        }
    }

    public void updateData(View view) {
        name = nameET.getText().toString();
        phone = phoneET.getText().toString();

        if (!phone.equals("")) {
            databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("name").setValue(name);
            Toast.makeText(this, "Data updated", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (!name.equals("")) {
            databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("phone").setValue(phone);
            Toast.makeText(this, "Data updated", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (profileAvatar != null) {
            storageReference = FirebaseStorage.getInstance().getReference(firebaseAuth.getCurrentUser().getUid());
            storageReference.putFile(profileAvatar).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            photo = uri.toString();
                            databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("photo").setValue(photo);

                        }
                    });
                }
            });
            Toast.makeText(this, "Data updated", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (phone.equals("") & name.equals("") & profileAvatar != null) {

            Toast.makeText(this, "Data is missing", Toast.LENGTH_SHORT).show();
        }

    }
}