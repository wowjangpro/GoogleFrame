package com.jangpro.googleframe

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import com.google.android.gms.auth.GoogleAuthException
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.Scopes
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.io.IOException

class GetAccessToken {
    /*
    fun getAccessToken(context: Context) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val runnable = Runnable {
                try {
                    val acct = GoogleSignIn.getLastSignedInAccount(context)
                    val scope = "oauth2:" + Scopes.EMAIL + " " + Scopes.PROFILE
                    accessToken = GoogleAuthUtil.getToken(context, acct!!.account, scope, Bundle())
                    Log.d("Token-getaccesstoken", "accessToken:$accessToken") //accessToken:ya29.Gl...
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: GoogleAuthException) {
                    e.printStackTrace()
                }
            }
            AsyncTask.execute(runnable)
        }

    }
    */
    fun getAccessToken(context: Context): String {
        val user = FirebaseAuth.getInstance().currentUser
        var retAccessToken = ""
        user.let {
            try {
                val acct = GoogleSignIn.getLastSignedInAccount(context)
                val scope = "oauth2:" + Scopes.EMAIL + " " + Scopes.PROFILE
                retAccessToken = GoogleAuthUtil.getToken(context, acct!!.account, scope, Bundle())
                Log.d("Token-getaccesstoken", "accessToken:$retAccessToken") //accessToken:ya29.Gl...
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: GoogleAuthException) {
                e.printStackTrace()
            }
        }

        return retAccessToken
    }
}