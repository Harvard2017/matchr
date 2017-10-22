package com.matchr

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.matchr.activities.QuestionActivity
import com.matchr.data.User
import com.matchr.utils.L

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity(), View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private var googleApiClient: GoogleApiClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<View>(R.id.signin_button).setOnClickListener { signIn() }

        findViewById<View>(R.id.bn_logout).setOnClickListener { signOut() }

        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        googleApiClient = GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions).build()
        if (BuildConfig.DEBUG) signIn()
    }

    private fun signIn() {
        val intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
        startActivityForResult(intent, REQ_CODE)

    }

    private fun signOut() {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback {
            val context = applicationContext
            Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleResult(result: GoogleSignInResult) {
        // if successfully signed in with google
        if (googleApiClient != null && googleApiClient!!.isConnected) {
            val account = result.signInAccount
            if (account == null) {
                L.d("Account is null", null)
                return
            }
            // contains user ID
            val id = account.id ?: return
            val name = account.displayName ?: "Unnamed"
            val email = account.email ?: "NA"
            Firebase.saveUser(User(id, name, email))

            // LoginActivity.class will be changed with next activity
            val intent = Intent(this, QuestionActivity::class.java)
            //            intent.putExtra(Firebase.USER_ID, "u" + System.currentTimeMillis());
            intent.putExtra(Firebase.USER_ID, id)
            if (BuildConfig.DEBUG) Firebase.test(id)
            startActivity(intent)
        }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {

    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.bn_logout -> signOut()
            R.id.signin_button -> signIn()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQ_CODE) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            handleResult(result)
        }
    }

    companion object {

        private val REQ_CODE = 9001
    }
}