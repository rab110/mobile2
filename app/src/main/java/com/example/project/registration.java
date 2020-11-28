package com.example.project;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.TextUtilsCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.LoginFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class registration extends AppCompatActivity {
    EditText Rusername, Rpass, email ;
    Button register ;
    TextView login ;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fStor;
    String user_ID;
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    Animation scale_up, scale_down;
    public static final String TAG = "TAG";



    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        Rusername = findViewById(R.id.re_user);
        Rpass = findViewById(R.id.re_pass);
        email = findViewById(R.id.Email);
        register = findViewById(R.id.btn_create);
        fAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        login = findViewById(R.id.loginACTV);
        fStor = FirebaseFirestore.getInstance();
        scale_up= AnimationUtils.loadAnimation(this,R.anim.scale_up);
        scale_down= AnimationUtils.loadAnimation(this,R.anim.scale_down);

        register.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    register.startAnimation(scale_up);
                }else if(event.getAction()==MotionEvent.ACTION_UP){
                    register.startAnimation(scale_down);
                }
                return false;
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                final String Femail = email.getText().toString();
                final String Fpass = Rpass.getText().toString().trim();
                final String fname = Rusername.getText().toString();


                if (TextUtils.isEmpty(Femail)){
                    email.setError("الرجاء إدخال إيميل فعال");
                    return;
                }
                if (TextUtils.isEmpty(Fpass)){
                    Rpass.setError("الرجاء ادخال كلمة مرور");
                    return;
                }
                if(Fpass.length() < 8){
                    Rpass.setError("يجب على كلمة المرور ان تكون مكونه من 8 خانات او أكثر");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                fAuth.createUserWithEmailAndPassword(Femail,Fpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toasty.success(getApplicationContext(), "تم إنشاء الحساب", Toast.LENGTH_SHORT, true).show();
                            user_ID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStor.collection("users").document(user_ID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("Name",fname);
                            user.put("Email",Femail);

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: تم إنشاء الحساب ");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG,"onFailure: " + e.toString());
                                }
                            });

                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            overridePendingTransition(R.anim.slide_in, R.anim.slide_left_out);
                        } else {
                            Toasty.error(getApplicationContext(), "حدث خطأ الرجاء اعادة المحاولة", Toast.LENGTH_SHORT, true).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });


            }
        });

        login.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    login.startAnimation(scale_up);
                }else if(event.getAction()==MotionEvent.ACTION_UP){
                    login.startAnimation(scale_down);
                }
                return false;
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_left_out);
            }
        });


} }
