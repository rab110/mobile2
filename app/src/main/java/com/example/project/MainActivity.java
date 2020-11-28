package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import es.dmoral.toasty.Toasty;

import static maes.tech.intentanim.CustomIntent.customType;


public class MainActivity extends AppCompatActivity {
    private static final String Tag = "project";
    Button login,registerBtn;
    EditText Lemail, Lpass;
    TextView forgetBtn;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    Animation scale_up, scale_down;
   //private int counter = 7;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(Tag,"OnCreate Called");
        login=findViewById(R.id.btnlogin);
        Lemail=findViewById(R.id.lo_Email);
        Lpass=findViewById(R.id.lo_pass);
        progressBar = findViewById(R.id.progressBar2);
        fAuth = FirebaseAuth.getInstance();
        registerBtn = findViewById(R.id.registerACT);
        forgetBtn = findViewById(R.id.forgetPass);

        scale_up= AnimationUtils.loadAnimation(this,R.anim.scale_up);
        scale_down= AnimationUtils.loadAnimation(this,R.anim.scale_down);

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
                String email = Lemail.getText().toString().trim();
                String pass = Lpass.getText().toString().trim();
                if (TextUtils.isEmpty(email)){
                    Lemail.setError("الرجاء إدخال إيميل فعال");
                    return;
                }
                if (TextUtils.isEmpty(pass)){
                    Lpass.setError("الرجاء ادخال كلمة مرور");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                fAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            progressBar.setVisibility(View.GONE);
                            Toasty.success(getApplicationContext(), "", Toast.LENGTH_SHORT, true).show();
                            Intent intent = new Intent(MainActivity.this, time_table.class);
                            startActivity(intent);
                        }
                        else {
                            Toasty.error(getApplicationContext(), "حدث خطأ، يرجى اعادة المحاولة", Toast.LENGTH_SHORT, true).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }

        });

        forgetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetMail = new EditText(v.getContext());
                AlertDialog.Builder passresetDialog = new AlertDialog.Builder(v.getContext(),R.style.AlertDialog);
                passresetDialog.setTitle("إعاده تعيين كلمة المرور؟");
                passresetDialog.setMessage("ادخل إيميلك للإستراد كلمة المرور");
                passresetDialog.setView(resetMail);
                passresetDialog.setNegativeButton("أعاده تعيين", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mail = resetMail.getText().toString();
                        fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toasty.success(getApplicationContext(), "تم إرسال رابط إعاده التعيين", Toast.LENGTH_SHORT, true).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toasty.error(getApplicationContext(), "حدث خطأ الرجاء اعادة المحاولة", Toast.LENGTH_SHORT, true).show();

                            }
                        });
                        
                    }
                });
                passresetDialog.setPositiveButton("إلغاء", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                passresetDialog.create().show();
            }
        });
        registerBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    registerBtn.startAnimation(scale_up);
                }else if(event.getAction()==MotionEvent.ACTION_UP){
                    registerBtn.startAnimation(scale_down);
                }
                return false;
            }
        });
     registerBtn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             startActivity(new Intent(getApplicationContext(), registration.class));
             overridePendingTransition(R.anim.slide_left_in, R.anim.slide_out);
         }
     });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(Tag,"OnStart Called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(Tag,"onResume Called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(Tag,"onStop Called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(Tag,"onPause Called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(Tag,"onDestroy Called");
    }
}