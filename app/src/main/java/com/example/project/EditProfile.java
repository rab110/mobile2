package com.example.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class EditProfile extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText Ename , Eemail ;
    ImageView Eavatar;
    FirebaseAuth fAuth;
    FirebaseFirestore fstore;
    Button Save;
    FirebaseUser user ;
    LinearLayout back;
    RelativeLayout deleteimg;
    StorageReference storageReference;
    FirebaseFirestore fStore;
    String userID;
    Button delete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Intent data = getIntent();
        final String name = data.getStringExtra("Name");
        final String email = data.getStringExtra("Email");
        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        Ename = findViewById(R.id.EditName);
        Eemail = findViewById(R.id.EditEmail);
        Eavatar = findViewById(R.id.user_avatar);
        Save = findViewById(R.id.save);
        user = fAuth.getCurrentUser();
        userID = fAuth.getCurrentUser().getUid();
        back = findViewById(R.id.back);
        delete = findViewById(R.id.delete);
        Ename.setText(name);
        Eemail.setText(email);
        fStore = FirebaseFirestore.getInstance();
        deleteimg=findViewById(R.id.deleteimg);
        Log.d(TAG, "onCreate: " + name + " " + email);

        storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference avatarRed = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/avatar.jpg");
        final DocumentReference documentReference = fStore.collection("users").document(userID);

        avatarRed.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(Eavatar);
            }
        });
       
        Eavatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGallery,1000);
            }
        });

        deleteimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avatarRed.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Eavatar.setImageResource(R.drawable.img_9148);
                        Toasty.success(getApplicationContext(), "تم حذف الصورة", Toast.LENGTH_SHORT, true).show();
                    }
                });
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(EditProfile.this, Home.class);
                startActivity(in);
            }
        });

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Ename.getText().toString().isEmpty() || Eemail.getText().toString().isEmpty()
                || Ename.getText().toString().equals(name)&&Eemail.getText().toString().equals(email) ){
                    Toasty.error(getApplicationContext(), "لم يتم تحديث البيانات", Toast.LENGTH_SHORT, true).show();
                    return;
                }
                final String name = Ename.getText().toString();
                final String email = Eemail.getText().toString();
                user.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DocumentReference docRef =fstore.collection("users").document(user.getUid());
                        Map<String,Object> edited = new HashMap<>();
                        edited.put("Name",Ename.getText().toString());
                        edited.put("Email",email);
                        docRef.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toasty.success(getApplicationContext(), "تم تعديل البيانات", Toast.LENGTH_SHORT, true).show();
                                startActivities(new Intent[]{new Intent(getApplicationContext(), Home.class)});
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toasty.error(getApplicationContext(), "حدث خطأ، يرجى اعادة المحاولة", Toast.LENGTH_SHORT, true).show();
                            }
                        });

                    }
                });
            }
        });


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                androidx.appcompat.app.AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext(),R.style.AlertDialog);
                dialog.setTitle("هل انت متأكد؟");
                dialog.setMessage("حذف الحساب سيؤدي الى حذف شامل لجميع بياناتك");
                dialog.setNegativeButton("حذف", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Intent it = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(it);
                                            finish();
                                            Toasty.success(getApplicationContext(), "تم حذف الحساب", Toast.LENGTH_SHORT, true).show();
                                        }
                                        else {
                                            Toasty.error(getApplicationContext(), "حدث خطأ، يرجى اعادة المحاولة", Toast.LENGTH_SHORT, true).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
                dialog.setPositiveButton("إلغاء", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.create().show();
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK){
                Uri avatarUri = data.getData();
                Eavatar.setImageURI(avatarUri);
                uploadAvatar(avatarUri);
            }
        }
    }
    private void uploadAvatar(Uri avatarUri) {
        final StorageReference fileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/avatar.jpg");
        fileRef.putFile(avatarUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(Eavatar);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toasty.error(getApplicationContext(), "حدث خطأ، يرجى اعادة المحاولة", Toast.LENGTH_SHORT, true).show();
            }
        });
    }

}