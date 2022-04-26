package com.zohirdev.mystory.activities.dashboard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.zohirdev.mystory.R;
import com.zohirdev.mystory.activities.HomeActivity;
import com.zohirdev.mystory.activities.login.AddProfilePictureActivity;

import java.util.HashMap;
import java.util.Objects;

public class NameConfirmationActivity extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;

    CardView cardImage;
    TextView text;
    Button buttonSend;
    ImageView image;

    Uri uri;

    StorageReference storageReference;
    String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_confirmation);

        Objects.requireNonNull(getSupportActionBar()).hide();
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        Button buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        storageReference = FirebaseStorage.getInstance().getReference("UsersDocument");
        cardImage = findViewById(R.id.cardImage);
        text = findViewById(R.id.text);
        buttonSend = findViewById(R.id.buttonSend);

        image = findViewById(R.id.image);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(NameConfirmationActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    openFileChooser();
                } else {
                    requestStoragePermission();
                }
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendDocument();
            }
        });
    }

    private void sendDocument() {
        final String imageName = uri + "." + getImageExtension(uri);
        storageReference = storageReference.child(imageName);
        final UploadTask imageUploadTask = storageReference.putFile(uri);

        imageUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) {
                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull final Task<Uri> task) {

                imageUrl = Objects.requireNonNull(task.getResult()).toString();

                HashMap<String, String> dataMap = new HashMap<>();
                dataMap.put("uid", firebaseAuth.getUid());
                dataMap.put("image", imageUrl);

                db.collection("USERS_DOCUMENT")
                        .document("" + firebaseAuth.getUid())
                        .set(dataMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent intent;
                                intent = new Intent(NameConfirmationActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });

            }

        });
    }


    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("")
                    .setPositiveButton(getString(R.string.button_allow_dialog_permission_not_accepted), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(NameConfirmationActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                        }
                    }).setNegativeButton(getString(R.string.button_deny_dialog_permission_not_accepted), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    private String getImageExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 30);
    }

    @SuppressLint("SetTextI18n")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            uri = data.getData();
            Picasso.get().load(uri).into(image);
            buttonSend.setClickable(true);
        } catch (Exception e) {
            uri = null;
        }

    }
}