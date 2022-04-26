package com.zohirdev.mystory.activities.login;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.zohirdev.mystory.R;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class EnterVerificationCodeActivity extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    String fullName;
    String phone;
    String sex;
    String imageProfile;
    String imageCover;
    String deviceID;
    String verificationCodeBySystem = null;
    String codeByUser;

    Button buttonSignIn;
    CardView cardViewSignIn;
    ProgressBar progressSignIn;

    EditText editTextInput1;
    EditText editTextInput2;
    EditText editTextInput3;
    EditText editTextInput4;
    EditText editTextInput5;
    EditText editTextInput6;

    TextView textMobile;
    TextView textResendCode;


    @SuppressLint({"HardwareIds", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_verification_code);

        Objects.requireNonNull(getSupportActionBar()).hide();

        deviceID = Settings.Secure.getString(EnterVerificationCodeActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);


        buttonSignIn = findViewById(R.id.buttonSignIn);
        cardViewSignIn = findViewById(R.id.cardSignIn);
        progressSignIn = findViewById(R.id.progressSignIn);

        editTextInput1 = findViewById(R.id.inputCode1);
        editTextInput2 = findViewById(R.id.inputCode2);
        editTextInput3 = findViewById(R.id.inputCode3);
        editTextInput4 = findViewById(R.id.inputCode4);
        editTextInput5 = findViewById(R.id.inputCode5);
        editTextInput6 = findViewById(R.id.inputCode6);


        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        fullName = getIntent().getStringExtra("fullName");
        phone = getIntent().getStringExtra("phone");
        sex = getIntent().getStringExtra("sex");

        textResendCode = findViewById(R.id.textResendCode);

        long duration = TimeUnit.MINUTES.toMillis(1);

        new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long l) {
                String sDuration = String.format(Locale.ENGLISH, "%02d : %02d",
                        TimeUnit.MILLISECONDS.toMinutes(l),
                        TimeUnit.MILLISECONDS.toSeconds(l) - (TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l))));
                textResendCode.setText(sDuration);
            }

            @Override
            public void onFinish() {
                textResendCode.setText("RESEND CODE");
                textResendCode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SendVerificationCodeToUser(phone);
                        new CountDownTimer(duration, 1000) {
                            @Override
                            public void onTick(long l) {
                                String sDuration = String.format(Locale.ENGLISH, "%02d : %02d",
                                        TimeUnit.MILLISECONDS.toMinutes(l),
                                        TimeUnit.MILLISECONDS.toSeconds(l) - (TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l))));
                                textResendCode.setText(sDuration);
                            }

                            @Override
                            public void onFinish() {
                                textResendCode.setText("RESEND CODE");
                                textResendCode.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        SendVerificationCodeToUser(phone);
                                        textResendCode.setVisibility(View.GONE);
                                    }
                                });

                            }
                        }.start();
                    }
                });

            }
        }.start();


        textMobile = findViewById(R.id.textMobile);
        textMobile.setText("Enter the OTP sent to  " + phone);

        if (sex.equals("MAN")) {
            imageProfile = "https://firebasestorage.googleapis.com/v0/b/story-91b4a.appspot.com/o/DefaultPictures%2Fboy.png?alt=media&token=506a291b-272a-43da-af77-f00450b91abf";
        } else {
            imageProfile = "https://firebasestorage.googleapis.com/v0/b/story-91b4a.appspot.com/o/DefaultPictures%2Fgirl.png?alt=media&token=2ba3873e-730a-486c-9262-7f9557e42edf";
        }
        imageCover = "https://firebasestorage.googleapis.com/v0/b/story-91b4a.appspot.com/o/DefaultPictures%2Fcover.jpg?alt=media&token=61213bfe-29d3-4890-b736-5cfa4b9118e9";

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editTextInput1.getText().toString().isEmpty() ||
                        editTextInput2.getText().toString().isEmpty() ||
                        editTextInput3.getText().toString().isEmpty() ||
                        editTextInput4.getText().toString().isEmpty() ||
                        editTextInput5.getText().toString().isEmpty() ||
                        editTextInput6.getText().toString().isEmpty()
                ) {
                    Toast.makeText(EnterVerificationCodeActivity.this, "Please enter a valid code", Toast.LENGTH_SHORT).show();
                } else {
                    codeByUser = editTextInput1.getText().toString() +
                            editTextInput2.getText().toString() +
                            editTextInput3.getText().toString() +
                            editTextInput4.getText().toString() +
                            editTextInput5.getText().toString() +
                            editTextInput6.getText().toString();

                    if (verificationCodeBySystem != null) {
                        progressSignIn.setVisibility(View.VISIBLE);
                        buttonSignIn.setVisibility(View.GONE);
                        verifyCode(codeByUser);
                    } else {
                        Toast.makeText(EnterVerificationCodeActivity.this, "Please wait!", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

        setupOTPInputs();
        SendVerificationCodeToUser(phone);
    }

    private void SendVerificationCodeToUser(String phone) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                  // OnVerificationStateChangedCallbacks
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationCodeBySystem = s;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                editTextInput1.setText("" + code.charAt(0));
                editTextInput2.setText("" + code.charAt(1));
                editTextInput3.setText("" + code.charAt(2));
                editTextInput4.setText("" + code.charAt(3));
                editTextInput5.setText("" + code.charAt(4));
                editTextInput6.setText("" + code.charAt(5));

                progressSignIn.setVisibility(View.VISIBLE);
                buttonSignIn.setVisibility(View.GONE);
                verifyCode(code);
            }

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(EnterVerificationCodeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void verifyCode(String codeByUser) {
        progressSignIn.setVisibility(View.VISIBLE);
        buttonSignIn.setVisibility(View.GONE);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, codeByUser);
        signInTheUserByCredentials(credential);
    }

    private void setupOTPInputs() {
        editTextInput1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (!s.toString().trim().isEmpty()) {
                    editTextInput2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        editTextInput2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (!s.toString().trim().isEmpty()) {
                    editTextInput3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        editTextInput3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (!s.toString().trim().isEmpty()) {
                    editTextInput4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        editTextInput4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (!s.toString().trim().isEmpty()) {
                    editTextInput5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        editTextInput5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (!s.toString().trim().isEmpty()) {
                    editTextInput6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void signInTheUserByCredentials(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(EnterVerificationCodeActivity.this, new OnCompleteListener<AuthResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            long username = new Date().getTime();
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            assert user != null;
                            user.getIdToken(true)
                                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                                            if (task.isSuccessful()) {
                                                String token = Objects.requireNonNull(task.getResult()).getToken();
                                                HashMap<String, String> dataMap = new HashMap<>();
                                                dataMap.put("fullName", fullName);
                                                dataMap.put("username", "user" + username);
                                                dataMap.put("uid", firebaseAuth.getUid());
                                                dataMap.put("token", token);
                                                dataMap.put("phone", phone);
                                                dataMap.put("imageProfile", imageProfile);
                                                dataMap.put("imageCover", imageCover);
                                                dataMap.put("links", "");
                                                dataMap.put("bio", "");
                                                dataMap.put("sex", sex);
                                                dataMap.put("isBlocked", "NO");
                                                dataMap.put("deviceID", deviceID);
                                                dataMap.put("verified", "NO");

                                                db.collection("USERS")
                                                        .document("" + firebaseAuth.getUid())
                                                        .set(dataMap)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Intent intent;
                                                                intent = new Intent(EnterVerificationCodeActivity.this, CreateUsernameActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        });

                                            } else {
                                                Toast.makeText(EnterVerificationCodeActivity.this, "حدث خطأ", Toast.LENGTH_SHORT).show();
                                                progressSignIn.setVisibility(View.GONE);
                                                buttonSignIn.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    });


                        } else {
                            Toast.makeText(EnterVerificationCodeActivity.this, "حدث خطأ غير معروف", Toast.LENGTH_SHORT).show();
                            progressSignIn.setVisibility(View.GONE);
                            buttonSignIn.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }
}