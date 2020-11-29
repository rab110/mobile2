package com.example.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

import static java.security.AccessController.getContext;
import static maes.tech.intentanim.CustomIntent.customType;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    TextView userName, userEmail , changPass , txtEditPro;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    ImageView avatar;
    CardView EditeProfile;
    StorageReference storageReference;
    FirebaseUser user;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    Button delete;
    Button logout;
    Animation scale_up, scale_down;

    @SuppressLint("ClickableViewAccessibility")
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        userName = findViewById(R.id.user_name);
        userEmail = findViewById(R.id.user_Email);
        changPass = findViewById(R.id.chang_pass);
        EditeProfile = findViewById(R.id.editProfile);
        txtEditPro = findViewById(R.id.txtedit);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference avatarRed = storageReference.child("users/" + fAuth.getCurrentUser().getUid() + "/avatar.jpg");
        userID = fAuth.getCurrentUser().getUid();
         user = fAuth.getCurrentUser();
        delete = findViewById(R.id.delete);
        logout=findViewById(R.id.logout);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
       navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.profile);
        avatar = findViewById(R.id.user_avatar);
        scale_up= AnimationUtils.loadAnimation(this,R.anim.scale_up);
        scale_down= AnimationUtils.loadAnimation(this,R.anim.scale_down);
        avatarRed.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(avatar);
            }
        });
        final DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                userName.setText(documentSnapshot.getString("Name"));
                userEmail.setText(documentSnapshot.getString("Email"));

            }
        });

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGallery, 1000);
            }
        });

        txtEditPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), EditProfile.class);
                i.putExtra("Name", userName.getText().toString());
                i.putExtra("Email", userEmail.getText().toString());
                startActivity(i);
            }
        });

        EditeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), EditProfile.class);
                i.putExtra("Name", userName.getText().toString());
                i.putExtra("Email", userEmail.getText().toString());
                startActivity(i);
            }
        });

        changPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetPass = new EditText(v.getContext());
                AlertDialog.Builder passresetDialog = new AlertDialog.Builder(v.getContext(),R.style.AlertDialog);
                passresetDialog.setTitle("تغيير كلمة المرور");
                passresetDialog.setMessage("الرجاء ادخال كلمة المرور الجديدة");
                passresetDialog.setView(resetPass);

                passresetDialog.setNegativeButton("تغيير كلمة المرور", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newPass = resetPass.getText().toString();
                        user.updatePassword(newPass).addOnSuccessListener(new OnSuccessListener<Void>(){
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toasty.success(getApplicationContext(), "تم تغيير كلمة المرور", Toast.LENGTH_SHORT, true).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toasty.error(getApplicationContext(), "حدث خطأ، يرجى اعادة المحاولة", Toast.LENGTH_SHORT, true).show();
                            }
                        });
                    }
                });
                passresetDialog.setPositiveButton("إلغاء", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                passresetDialog.create().show();
            }
        });

        logout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    logout.startAnimation(scale_up);
                }else if(event.getAction()==MotionEvent.ACTION_UP){
                    logout.startAnimation(scale_down);
                }
                return false;
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext(),R.style.AlertDialog);
                dialog.setTitle("هل أنت متأكد أنك تريد تسجيل الخروج؟");
                dialog.setNegativeButton("تسجيل الخروج", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent it = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(it);
                        finish();
                        Toasty.success(getApplicationContext(), "تم تسجيل الخروج", Toast.LENGTH_SHORT, true).show();
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
        protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 1000) {
                if (resultCode == Activity.RESULT_OK) {
                    Uri avatarUri = data.getData();
                    avatar.setImageURI(avatarUri);
                    uploadAvatar(avatarUri);
                }
            }
        }

        private void uploadAvatar (Uri avatarUri){
            final StorageReference fileRef = storageReference.child("users/" + fAuth.getCurrentUser().getUid() + "/avatar.jpg");
            fileRef.putFile(avatarUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(avatar);
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


    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.table:
                Intent in = new Intent(Home.this, time_table.class);
                startActivity(in);
                customType(Home.this,"left-to-right");
                break;
            case R.id.task:
                Intent intent = new Intent(Home.this, Main_task.class);
                startActivity(intent);
                customType(Home.this,"left-to-right");
                break;
            case R.id.profile:
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}