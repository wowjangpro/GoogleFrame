package com.jangpro.googleframe

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*
import android.os.AsyncTask
import com.google.android.gms.auth.GoogleAuthException
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.common.Scopes
import android.content.SharedPreferences
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.api.Scope
import java.io.IOException


class LoginActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    var prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        configureGoogleSignIn()
        setupUI()
    }

    val RC_SIGN_IN: Int = 1
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mGoogleSignInOptions: GoogleSignInOptions

    private fun configureGoogleSignIn() {
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestScopes(Scope("https://www.googleapis.com/auth/photoslibrary.readonly"), Scope("https://www.googleapis.com/auth/photoslibrary"), Scope("https://www.googleapis.com/auth/photoslibrary.readonly.appcreateddata"))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)
    }

    private fun setupUI() {
        google_button.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                Log.d("Account", "Account=================== "+account)
                firebaseAuthWithGoogle(account!!)
                // 로그인 성공 했을때
                val personName = account.displayName
                val personEmail = account.email
                val personId = account.id
                val tokenKey = account.idToken

                Log.d("GoogleLogin", "personName=" + personName!!)
                Log.d("GoogleLogin", "personEmail=" + personEmail!!)
                Log.d("GoogleLogin", "personId=" + personId!!)
                Log.d("GoogleLogin", "tokenKey=" + tokenKey!!)

                val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
                if (result.isSuccess) {
                    val signInResult = result.signInAccount

                    val runnable = Runnable {
                        try {
                            val scope = "oauth2:" + Scopes.EMAIL + " " + Scopes.PROFILE
                            val accessToken =
                                GoogleAuthUtil.getToken(applicationContext, signInResult!!.account!!, scope, Bundle())
                            Log.d("Token", "accessToken:$accessToken") //accessToken:ya29.Gl...

                            prefs = this.getSharedPreferences(PREFS_FILENAME, 0)
                            val editor = prefs!!.edit()
                            editor.putString("access_token", accessToken)
                            editor.apply()
                            editor.commit()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        } catch (e: GoogleAuthException) {
                            e.printStackTrace()
                        }
                    }
                    AsyncTask.execute(runnable)

                } else {
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed:(", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d("getId", "firebaseAuthWithGoogle:" + acct.getId());

        firebaseAuth = FirebaseAuth.getInstance()
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                startActivity(getLaunchIntent(this))
            } else {
                Toast.makeText(this, "Google sign in failed:( :(", Toast.LENGTH_LONG).show()
            }
        }

    }

    companion object {
        fun getLaunchIntent(from: Context) = Intent(from, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }
}
