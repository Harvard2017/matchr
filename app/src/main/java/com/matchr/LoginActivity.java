package com.matchr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.matchr.activities.QuestionActivity;
import com.matchr.data.Response;
import com.matchr.utils.L;

import java.util.ArrayList;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQ_CODE = 9001;
    private GoogleApiClient googleApiClient;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.signin_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();

            }
        });

        findViewById(R.id.bn_logout).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();

            }
        });

        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions).build();
        ArrayList<String> test = new ArrayList<>();
        test.add("hi");
        test.add("bye");
        Firebase.INSTANCE.saveResponse("abcd", new Response(25, test));
        Firebase.INSTANCE.test();
        signIn();
    }

    private void signIn() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, REQ_CODE);

    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Context context = getApplicationContext();
                        Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleResult(GoogleSignInResult result) {
        // if successfully signed in with google
        if (googleApiClient != null && googleApiClient.isConnected()) {
            GoogleSignInAccount account = result.getSignInAccount();
            if (account == null) {
                L.INSTANCE.d("Account is null", null);
                return;
            }
            // contains user ID
            String personId = account.getId();
            String personName = account.getDisplayName();
            String personEmail = account.getEmail();
            String givenName = account.getGivenName();
            String comb = "u" + givenName;

            myRef.child(comb + "/UserId").setValue(personId);
            myRef.child(comb + "/Name").setValue(personName);
            myRef.child(comb + "/Email").setValue(personEmail);

            // LoginActivity.class will be changed with next activity
            Intent intent = new Intent(this, QuestionActivity.class);
            intent.putExtra(Firebase.USER_ID, personId);
            startActivity(intent);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bn_logout:
                signOut();
                break;
            case R.id.signin_button:
                signIn();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(result);
        }
    }
}