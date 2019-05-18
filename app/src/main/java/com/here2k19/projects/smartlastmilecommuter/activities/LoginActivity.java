package com.here2k19.projects.smartlastmilecommuter.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.here2k19.projects.smartlastmilecommuter.Delivery.GetDeliveries;
import com.here2k19.projects.smartlastmilecommuter.R;
import com.here2k19.projects.smartlastmilecommuter.Routing.Positioning;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity{

    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final String[] RUNTIME_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE
    };

    private static final String TAG = "Login Activity";
    private TextInputEditText loginPhoneNumberEditText;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private TextView resultText;
    private AlertDialog otpDialog;
    private ProgressDialog progressDialog;
    EditText editText;
    public String name,mobile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (hasPermissions(this, RUNTIME_PERMISSIONS)) {
            setUpView();
        } else {
            ActivityCompat
                    .requestPermissions(this, RUNTIME_PERMISSIONS, REQUEST_CODE_ASK_PERMISSIONS);
        }
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS: {
                for (int index = 0; index < permissions.length; index++) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        if (!ActivityCompat
                                .shouldShowRequestPermissionRationale(this, permissions[index]))
                            Toast.makeText(this, "Required permission " + permissions[index]
                                            + " not granted. "
                                            + "Please go to settings and turn on for sample app",
                                    Toast.LENGTH_LONG).show();
                        else {
                            Toast.makeText(this, "Required permission " + permissions[index]
                                    + " not granted", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                setUpView();
                break;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void setUpView() {
        setContentView(R.layout.activity_login);
        loginPhoneNumberEditText= findViewById(R.id.login_mobile_number);
        editText=findViewById(R.id.usrname);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            finish();
            startActivity(new Intent(this,GetDeliveries.class));
        }
    }

    public void getOtp(View view) {
        name=editText.getText().toString();
        if ( loginPhoneNumberEditText.getText() != null ) {
            String phoneNumber = loginPhoneNumberEditText.getText().toString().trim();
            if (!TextUtils.isEmpty(phoneNumber) || phoneNumber.length() >= 10 ) {
                mobile = phoneNumber;
                progressDialog.show();
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber,
                        60,
                        TimeUnit.SECONDS,
                        this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                                signInWithPhoneAuthCredential(phoneAuthCredential);
                            }

                            @Override
                            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                mVerificationId = s;
                                mResendToken = forceResendingToken;
                                progressDialog.dismiss();
                                getOtpEntryPopUp();
                            }

                            @Override
                            public void onVerificationFailed(FirebaseException e) {
                                progressDialog.dismiss();
                                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                    Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                                } else if (e instanceof FirebaseTooManyRequestsException) {
                                    Toast.makeText(LoginActivity.this, "Firebase Quota Reached", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("err",e.getMessage());
                                }
                            }
                        });
            } else {
                Toast.makeText(this, "Enter a valid mobile number", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Enter Mobile Number", Toast.LENGTH_SHORT).show();
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            if(otpDialog!=null)
                            otpDialog.dismiss();
                            Log.e(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            finish();
                            Positioning positioning = new Positioning();
                            positioning.getPos(LoginActivity.this);
                            SharedPreferences sharedPreferences = getSharedPreferences("commuter",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("drivers").child(user.getUid());
                            Map<String,Object> map = new HashMap<>();
                                map.put("Name",name);
                                map.put("Mobile",mobile);
                                map.put("Address","Unknown");
                                map.put("VehicleType","Bike");
                                map.put("driverId",user.getUid());
                                map.put("inRide",false);
                                editor.putString("name",name);
                                editor.putString("mobile",mobile);
                                editor.apply();
                                Map<String,Double> liveLocation = new HashMap<>();
                                liveLocation.put("latitude",Positioning.latitude);
                                liveLocation.put("longitude",Positioning.longitude);

                            map.put("livelocation",liveLocation);
                            databaseReference.updateChildren(map);
                            startActivity(new Intent(getApplicationContext(), GetDeliveries.class));
                        } else {
                            progressDialog.dismiss();
                            Log.e(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                resultText.setError("Invalid code.");
                            }
                        }
                    }
                });
    }

    public void getOtpEntryPopUp(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        View view = getLayoutInflater().inflate(R.layout.popup_enter_otp,null);

        resultText = view.findViewById(R.id.result_text);

        Pinview otpView = view.findViewById(R.id.otp_view);

        otpView.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean fromUser) {
                Toast.makeText(LoginActivity.this, "OTP:"+pinview.getValue(), Toast.LENGTH_SHORT).show();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, pinview.getValue());
                signInWithPhoneAuthCredential(credential);
            }
        });

        builder.setView(view);
        otpDialog = builder.create();
        otpDialog.show();
    }

}
