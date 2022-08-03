package com.example.managerappqlvt.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.managerappqlvt.R;
import com.example.managerappqlvt.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class ForgetResetPhone extends AppCompatActivity {
    private static final String TAG = ForgetResetPhone.class.getName();
    EditText edt_phonereset;
    AppCompatButton btnverifyPhoneNumber;
    ImageView phone_back;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_phone);

        phone_back = findViewById(R.id.phone_back);
        edt_phonereset = findViewById(R.id.edt_phonereset);
        btnverifyPhoneNumber = findViewById(R.id.btnverifyPhoneNumber);

        mAuth = FirebaseAuth.getInstance();

        phone_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnverifyPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phone = edt_phonereset.getText().toString().trim();

                verifyPhoneNumber(phone);
            }
        });
    }

    public void verifyPhoneNumber(String phone) {

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                                signInWithPhoneAuthCredential(phoneAuthCredential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {

                                Toast.makeText(ForgetResetPhone.this, "VerificationFailed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verifycationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(verifycationId, forceResendingToken);
                                goToEnterVerifyOTP(phone, verifycationId);
                            }
                        })          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // Update UI
                            goToVerifyOTP(user.getPhoneNumber());
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(ForgetResetPhone.this, "The verification code entered was invalid", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void goToVerifyOTP(String phoneNumber) {
        Intent intent = new Intent(getApplicationContext(), ForgetPasswordSuccessMessage.class);
        intent.putExtra("phonenumber", phoneNumber);
        startActivity(intent);
    }

    private void goToEnterVerifyOTP(String phone, String verifycationId) {
        Intent intent = new Intent(getApplicationContext(), VerifyOTP.class);
        intent.putExtra("phone", phone);
        intent.putExtra("whatToDo", "updateData");
        intent.putExtra("verifycationId", verifycationId);
        startActivity(intent);
    }


}