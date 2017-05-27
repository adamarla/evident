package com.gradians.evident.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.JsonObject;
import com.gradians.evident.R;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class Launch extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build() ;

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build() ;

        SignInButton btnSignIn = (SignInButton)findViewById(R.id.sign_in_button);
        btnSignIn.setOnClickListener(this);
        btnSignIn.setColorScheme(SignInButton.COLOR_DARK);
        btnSignIn.setBackgroundResource(R.color.colorPrimary);
        btnSignIn.setSize(SignInButton.SIZE_WIDE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("profile", Context.MODE_PRIVATE);
        int userId = prefs.getInt("userId", 0);
        if (userId != 0) nextActivity();
    }

    @Override
    public void onClick(View view) {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient) ;
        startActivityForResult(intent, SIGN_IN_CODE) ;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SIGN_IN_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data) ;
            handleResult(result) ;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        errorMessage = connectionResult.getErrorMessage();
    }

    private void nextActivity() {
        Intent intent = new Intent(this, SelectChapter.class) ;
        startActivity(intent);
        finish();
    }

    private void handleResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount() ;
            final String firstName = account.getGivenName(),
                    lastName = account.getFamilyName(),
                    email = account.getEmail() ;

            JsonObject params = new JsonObject() ;

            params.addProperty("email", email);
            params.addProperty("first_name", firstName);
            params.addProperty("last_name", lastName);

            Ion.with(this)
                    .load("http://www.gradians.com/user/ping")
                    .setJsonObjectBody(params)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (result == null) {
                                Log.e("EvidentApp", "Error: " + e.getMessage());
                            } else {
                                int pid = result.get("id").getAsInt();
                                saveId(pid, firstName);
                                nextActivity();
                            }
                        }
                    }) ;
        } else {
            if (errorMessage == null)
                errorMessage = "Sorry, error signing in, please try again!";
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    private void saveId(int id, String name) {
        SharedPreferences prefs = getSharedPreferences("profile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("userId", id);
        editor.putString("name", name);
        editor.commit();
    }

    private String errorMessage;

    private GoogleApiClient mGoogleApiClient;
    private final int SIGN_IN_CODE = 3141 ;

}
