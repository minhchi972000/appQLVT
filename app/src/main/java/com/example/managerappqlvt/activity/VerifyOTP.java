package com.example.managerappqlvt.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.TaskExecutor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.example.managerappqlvt.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerifyOTP extends AppCompatActivity {
    private static final String TAG = VerifyOTP.class.getName();
    TextView optMobile, resendBtn;
    EditText optET1, optET2, optET3, optET4, optET5, optET6;
    String mVerification, mPhone,whatToDo;
    Button verifyBtn;
    private boolean resendEnabled = false;
    private int resendTime = 60;
    private int selectedETPositon = 0;

    FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mForceResendingToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        optMobile = findViewById(R.id.optMobile);
        resendBtn = findViewById(R.id.resendBtn);
        optET1 = findViewById(R.id.optET1);
        optET2 = findViewById(R.id.optET2);
        optET3 = findViewById(R.id.optET3);
        optET4 = findViewById(R.id.optET4);
        optET5 = findViewById(R.id.optET5);
        optET6 = findViewById(R.id.optET6);
        verifyBtn = findViewById(R.id.verifyBtn);

        mAuth = FirebaseAuth.getInstance();

        mPhone = getIntent().getStringExtra("phone");
        mVerification = getIntent().getStringExtra("verifycationId");
        whatToDo = getIntent().getStringExtra("whatToDo");


        optMobile.setText(mPhone);

        optET1.addTextChangedListener(textWatcher);
        optET2.addTextChangedListener(textWatcher);
        optET3.addTextChangedListener(textWatcher);
        optET4.addTextChangedListener(textWatcher);

        showKeyboard(optET1);
        startCountDownTimer();

        resendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (resendEnabled) {
                    startCountDownTimer();
                    sendOtpAgain();
                }
            }
        });
        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String generateotp = optET1.getText().toString().trim()
                        + optET2.getText().toString().trim()
                        + optET3.getText().toString().trim()
                        + optET4.getText().toString().trim()
                        + optET5.getText().toString().trim()
                        + optET6.getText().toString().trim();
                if (generateotp.length() == 6) {
                    onClickSendOtp(generateotp);
                }
            }
        });

    }

    private void sendOtpAgain() {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(mPhone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)// Activity (for callback binding)
                        .setForceResendingToken(mForceResendingToken)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                                signInWithPhoneAuthCredential(phoneAuthCredential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Log.d("VerificationFailed",e.getMessage());
                                Toast.makeText(VerifyOTP.this, "VerificationFailed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verifycationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(verifycationId, forceResendingToken);
                                mVerification = verifycationId;
                                mForceResendingToken = forceResendingToken;
                            }
                        })          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void onClickSendOtp(String strOtp) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerification, strOtp);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e(TAG, "signInWithCredential:success");

                            if (whatToDo.equals("updateData")) {
                                updateOldUsersData();
                            }
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(VerifyOTP.this, "The verification code entered was invalid", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void updateOldUsersData() {
        Intent intent = new Intent(getApplicationContext(),SetNewPassword.class);
        intent.putExtra("phone",mPhone);
        startActivity(intent);
        finish();
    }

    private void showKeyboard(EditText optET) {
        optET.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(optET, InputMethodManager.SHOW_IMPLICIT);

    }

    private void startCountDownTimer() {

        resendEnabled = false;
        resendBtn.setTextColor(Color.parseColor("#99000000"));

        new CountDownTimer(resendTime * 1000, 1000) {
            @Override
            public void onTick(long l) {
                resendBtn.setText("Resend code (" + (l / 1000) + ")");
            }

            @Override
            public void onFinish() {
                resendEnabled = true;
                resendBtn.setText("Resend Code");
                resendBtn.setTextColor(getResources().getColor(R.color.teal));

            }

        }.start();
    }


    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            if (s.length() > 0) {
                if (selectedETPositon == 0) {
                    selectedETPositon = 1;
                    showKeyboard(optET2);
                } else if (selectedETPositon == 1) {
                    selectedETPositon = 2;
                    showKeyboard(optET3);
                } else if (selectedETPositon == 2) {
                    selectedETPositon = 3;
                    showKeyboard(optET4);
                } else if (selectedETPositon == 3) {
                    selectedETPositon = 4;
                    showKeyboard(optET5);
                } else if (selectedETPositon == 4) {
                    selectedETPositon = 5;
                    showKeyboard(optET6);
                }
            }
        }
    };

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            if (selectedETPositon == 5) {
                selectedETPositon = 4;
                showKeyboard(optET5);
            } else if (selectedETPositon == 4) {
                selectedETPositon = 3;
                showKeyboard(optET4);
            } else if (selectedETPositon == 3) {
                selectedETPositon = 2;
                showKeyboard(optET3);
            } else if (selectedETPositon == 2) {
                selectedETPositon = 1;
                showKeyboard(optET2);
            } else if (selectedETPositon == 1) {
                selectedETPositon = 0;
                showKeyboard(optET1);
            }
            return true;
        } else {
            return super.onKeyUp(keyCode, event);
        }

    }
}