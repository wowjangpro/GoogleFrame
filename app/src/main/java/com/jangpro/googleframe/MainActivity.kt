package com.jangpro.googleframe

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.gms.auth.GoogleAuthException
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.common.Scopes
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.gson.Gson
import com.jangpro.googleframe.jsondata.MyAlbum
import com.jangpro.googleframe.restful.OkHttp3RetrofitManager
import com.jangpro.googleframe.restful.RetrofitInterface
import kotlinx.android.synthetic.main.activity_albums.*
import kotlinx.android.synthetic.main.content_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

const val PREFS_FILENAME = "com.jangpro.googleframe"

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_signout -> {
                signOut()
            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onStart() {
        super.onStart()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {

            val runnable = Runnable {
                try {
                    val acct = GoogleSignIn.getLastSignedInAccount(this)
                    val scope = "oauth2:" + Scopes.EMAIL + " " + Scopes.PROFILE
                    val accessToken = GoogleAuthUtil.getToken(applicationContext, acct!!.account, scope, Bundle())
                    Log.d("Token", "accessToken:$accessToken") //accessToken:ya29.Gl...
                    getAlbumList(accessToken)
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: GoogleAuthException) {
                    e.printStackTrace()
                }
            }
            AsyncTask.execute(runnable)

        }
        else {
            startActivity(getLaunchIntent(this))
        }
    }

    companion object {
        fun getLaunchIntent(from: Context) = Intent(from, LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

        }
        fun getLaunchIntentAlbums(from: Context, access_token: String) = Intent(from, AlbumsActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            putExtra("access_token", access_token)
        }
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut();
        startActivity(getLaunchIntent(this))
    }

    private fun getAlbumList(access_token:String) {
        val restClient: RetrofitInterface =
            OkHttp3RetrofitManager.getRetrofitService(RetrofitInterface::class.java)

        /*
         * Retrofit2을 사용하면 별도의 Thread(AsyncTask)를 만들필요없이
         * 비동기 방식으로 동작하도록 구성할 수 있다
         */
        val currentWeather = restClient.requestAlbumList(access_token, getString(R.string.google_api_key))
        currentWeather.enqueue(object : Callback<MyAlbum> {
            override fun onResponse(call: Call<MyAlbum>?, response: Response<MyAlbum>?) {
                if(response != null && response.isSuccessful) {
                    val gson = Gson()
                    val myAlbumList = gson.toJson(response.body())
                    Log.d("albumListResponse", "" + myAlbumList)
                    tv.setText(myAlbumList)
                }
            }
            override fun onFailure(call: Call<MyAlbum>?, t: Throwable?) {
                Log.d("errorResponse", ""+t.toString())
            }
        })
    }

}
