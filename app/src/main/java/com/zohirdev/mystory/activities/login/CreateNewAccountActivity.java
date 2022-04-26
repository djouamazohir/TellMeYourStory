package com.zohirdev.mystory.activities.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;
import com.zohirdev.mystory.R;

import java.util.Objects;

public class CreateNewAccountActivity extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    EditText editTextFullName;
    CountryCodePicker ccpCountry;
    EditText editTextPhoneNumber;

    CardView cardGetOTP;
    Button buttonGetOTP;


    String fullName;
    String phoneNumber;

    String sex = "NO";

    CardView cardMan;
    CardView cardWoman;

    ImageView imageMan;
    ImageView imageWoman;

    TextView textSex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_account);

        Objects.requireNonNull(getSupportActionBar()).hide();
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        editTextFullName = findViewById(R.id.textFullName);
        ccpCountry = findViewById(R.id.ccp);
        editTextPhoneNumber = findViewById(R.id.textPhoneNumber);

        cardGetOTP = findViewById(R.id.cardGetOTP);
        buttonGetOTP = findViewById(R.id.buttonGetOTP);

        cardMan = findViewById(R.id.cardMan);
        cardWoman = findViewById(R.id.cardWoman);

        imageMan = findViewById(R.id.imageMan);
        imageWoman = findViewById(R.id.imageWoman);

        textSex = findViewById(R.id.textSex);

        cardMan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sex = "MAN";
                imageMan.setVisibility(View.VISIBLE);
                imageWoman.setVisibility(View.GONE);
                textSex.setText("MALE");
            }
        });

        cardWoman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sex = "WOMAN";
                imageMan.setVisibility(View.GONE);
                imageWoman.setVisibility(View.VISIBLE);
                textSex.setText("FEMALE");
            }
        });


        buttonGetOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fullName = editTextFullName.getText().toString();
                phoneNumber = editTextPhoneNumber.getText().toString();
                String phone = "+" + ccpCountry.getFullNumber() + phoneNumber;


                if (fullName.isEmpty() || phoneNumber.isEmpty() || sex.equals("NO")) {
                    Toast.makeText(CreateNewAccountActivity.this, "الرجاء إدخال كل المعلومات", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent;
                    intent = new Intent(CreateNewAccountActivity.this, EnterVerificationCodeActivity.class);
                    intent.putExtra("fullName", fullName);
                    intent.putExtra("phone", phone);
                    intent.putExtra("sex", sex);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}