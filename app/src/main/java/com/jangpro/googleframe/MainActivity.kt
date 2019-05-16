package com.jangpro.googleframe

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.auth.GoogleAuthException
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.Scopes
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jangpro.googleframe.jsondata.MyAlbum
import com.jangpro.googleframe.restful.OkHttp3RetrofitManager
import com.jangpro.googleframe.restful.RetrofitInterface
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

const val PREFS_FILENAME = "com.jangpro.googleframe"
var accessToken: String? = null

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

    private var first_time : Long = 0
    private var second_time : Long = 0
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            second_time = System.currentTimeMillis()
            if(second_time - first_time < 2000){
                super.onBackPressed()
                finish()
            }else Toast.makeText(this,"뒤로가기 버튼을 한 번 더 누르시면 종료!", Toast.LENGTH_SHORT).show()
            first_time = System.currentTimeMillis()
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

    }

    override fun onResume() {
        super.onResume()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {

            val runnable = Runnable {
                try {
                    val acct = GoogleSignIn.getLastSignedInAccount(this)
                    val scope = "oauth2:" + Scopes.EMAIL + " " + Scopes.PROFILE
                    accessToken = GoogleAuthUtil.getToken(applicationContext, acct!!.account, scope, Bundle())
                    Log.d("Token", "accessToken:$accessToken") //accessToken:ya29.Gl...
                    getAlbumList(accessToken.toString())
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: GoogleAuthException) {
                    e.printStackTrace()
                }
            }
            AsyncTask.execute(runnable)

        } else {
            startActivity(getLaunchIntent(this))
        }
    }

    companion object {
        fun getLaunchIntent(from: Context) = Intent(from, LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

        }

        fun getLaunchIntentAlbums(from: Context, access_token: String) =
            Intent(from, AlbumsActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                putExtra("access_token", access_token)
            }

        fun getLaunchIntentPhoto(from: Context, album_id: String) = Intent(from, Slideshow::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            putExtra("access_token", accessToken)
            putExtra("album_id", album_id)
        }
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut();
        startActivity(getLaunchIntent(this))
    }

    private fun getAlbumList(access_token: String) {
        val restClient: RetrofitInterface =
            OkHttp3RetrofitManager.getRetrofitService(RetrofitInterface::class.java)

        val currentWeather = restClient.requestAlbumList(access_token, getString(R.string.google_api_key))
        currentWeather.enqueue(object : Callback<MyAlbum> {
            override fun onResponse(call: Call<MyAlbum>?, response: Response<MyAlbum>?) {
                if (response != null && response.isSuccessful) {
                    val gson = Gson()
                    val myAlbumList = gson.toJson(response.body())
                    //Log.d("albumListResponse", response.body().toString())
                    //tv.setText(myAlbumList)

                    /*
                    val userList = arrayListOf<Albums>(
                        Albums("AFfJaJYJxwe3HzBIcbRI_d8LZC9yb4g08-Se1_Mnq3eYtudCpcBBN34ye1RClsL7c9FMF3zhQ_6F","펜타스톰","https://photos.google.com/lr/album/AFfJaJYJxwe3HzBIcbRI_d8LZC9yb4g08-Se1_Mnq3eYtudCpcBBN34ye1RClsL7c9FMF3zhQ_6F", "489", "https://lh3.googleusercontent.com/lr/AGWb-e46vznuUMkiAxbsAW-_fI-gnr-0leYEk2Vt4wpbWPt5_KM5oeOQvdh-owBhIAzOouqRxwlM4JzrPYboMssfytoKFQlfdYuyypJjEaGzBZGkWDZITuGT33AKOCD3JiGITH4xXCQ034aJFMdxH7O_uOtiJA7Z0bYJPjvZaUh2xrkXOQ5G6fdiAZAlbPk7-xR7xfzCsji0jahaU29cUT_cOEbY3atLuQlgk6iQX9LfTjsge_WUYLfa40QkhZEcI1Di04LpG4ZyW9On6sHip1G0kjF3DMDKk_k5g3xYTZxv-IezrINYuFlxp4KuUrmrbwKYG_Zokr6aum7XoOnHUc11PN4rn8qYqwdU-WCRkLUq3BqucLL9O_60uHn3xdL2t5Q9hpHpp38HhsGScaqNKuaBq1-g_Te1iGKw_Qr6iPM6FAAysBhkpA3EYKCaefFwaCpN708uqKLTjB4fDF9vl8HM-b0rLZ5GT3JrT_ei_93Zjpa1qF_TLskEEF4GPGaGdHbsqaqBmbEqjNTIgmqqhJ5FdflNQK-J3NV6vKNaTDIJiaSAr__MGptKhABnB5Oacg02rub6zjKx30L3cUq8BaqimB9azJ0EW0Ui1ewo_kjrGvsrmR9LLs98qIWLe8REEZfnj6LktZdQIuNqmUpaXwv1DqAIXP32bhoOQxs116t7ASxJunL6APsPnL8MRl7UPYaum8Dxb_dUWqQOGSel85qMa2GbBy1AGBN1p2yeYrO1fJizRkdJwoafgXPVV9hrup1qrBcRwT-hwMHAIr3FigXkGmTqkNyVCIpUc7_2AN2G6dio2iLgxvBhAWLZ3h3XPH-8y7VQJQiCaECrb-ehKTRlZlWylbTkqeMtd7wH7Rzt6Z0RVoYuIwCXs4bhzuXpSiTUUAgg7f6CIgNgckTlytb08y6w7A31r-ANQ1w", ""),
                        Albums("AFfJaJZBV4TVCnkjAx5SpcWFLERFW6HcnoOK9x-1chUjFcglHYVDleTKjSYXOLWnZkUav75BQEIA","정선의 추억","https://photos.google.com/lr/album/AFfJaJZBV4TVCnkjAx5SpcWFLERFW6HcnoOK9x-1chUjFcglHYVDleTKjSYXOLWnZkUav75BQEIA", "38", "https://lh3.googleusercontent.com/lr/AGWb-e55pVKKl1qiymuCmQfOiU_kR0MtnOfkbaL4aqvFrquqmLUJ8eznOVX1rhi5Zs4Npr7qKWAI6C0CvrNZr2hTFHpU4k-p1QA-a3iqHSPt41SlDY8cmF9qzUFCvwXL2dJv9vKfZR_C5KO6bDCgRMoDV3Vt8PeLLpj-N43up1S2hIR9KegJ8KchzDKu4tQnq0EZIvXFfPKQGqjX1w87pbJbP_Yt8cybcq7z31lKq4YbtytQUrFzu1DuA2mmRQ2QQ25SZsUfXmGknzLPSsxd-bsaTd-m1s9dlhBKpLRS0BGtTIbeLrK5QSWXEoakIFS6ke_bnAkBH1sbclCEl67W6Q4v0b_5291cPTwVi5ppKB-V3CNlZ59Ym4jFAo0Ffx3vQrWaphmXgQXFyGUxQW0PlRYCj266YLzcaT1qxFkScd_KeKH_Z14mKS3wYZ3LrAmJLOu97kheO6uPKsoN35J-Z37-lNMyUyXRoLkIsC1Z9544UABWrTaICSY_P0ffTJWdPwidUTBKwTgIeKun2epKFN3FqQkDvu-Qjywq6KDM6X0iFxJAEUAYoNJBiUXVIs0jlAnp3w6sx6_UalU1ksd0H_zQillANjPoVlktODIZwLd6rTRaMcTSs18QzeZYQl-6_csq27YkOJe1VxnNDCBQPUBZOjdgETNkw6zUThsJAwcQ6PO2Ba9OGNWQWb1qgCdSp-TJLHO5BHgwSrUpXisXXt5Oz3hTsk3jZZKcswfBYNeT1ceo_wemCo9UUVbKyrBs-G-YZTeEHxzw3Mp8Guf4f_xWBg_rtNjc34Yrpugi_JlLGQ5tWcUKulyBTGrSBCxcJWTcTzF35KvplXonNw3rmpZz16WszYfveE0yaIYeOSrWn-dIANHUnr5svAEd2NguXzS0Zsam5qxrLZGzRDOpwtgwbUpL5yMTp8Acq-I", ""),
                        Albums("AFfJaJZJ6rgxqvVzdmAnfigVVfRx964DEt-8HZb_eAMaNJ40WzLpCzXHsSU-dxGcHqRhTfNSrxlV","속초시의 추석","https://photos.google.com/lr/album/AFfJaJZJ6rgxqvVzdmAnfigVVfRx964DEt-8HZb_eAMaNJ40WzLpCzXHsSU-dxGcHqRhTfNSrxlV", "14", "https://lh3.googleusercontent.com/lr/AGWb-e4emj2H480tHekQfui2b8Fo5YA-1dIO0n5AsB0P-zHBjALkRFtjRQ2wZwpRBkd48R__IdeSv1DA1RFmpNnZaxJlZZl7fVWUebq54fb17aSX0wh8P9QKTi4hN22GNzzmXGGIU6-u5V5xu4m8MBQdcuB8WMP7LSmVE2iy17mWA91AMrav010z_6aGETPL0MZ5M_PWTnoLOTKTSeY9OZPn_qZH0uB_FibtvrDWJacJO8Lp6tzijzfAlB8kdylF2E22ggB-c9XWG3YWA6gLLFaeWvxdnp07wors0UW5GDzOcg_aiJmW28cn5tRIx0WY2naCYRFqP-3jw38pE5upZjqDAB3er-E3AKSxyIgdbV3HcrGOiiDe4prSIkhwKxnsllmFGLB4t9wPBPc0ytVIN4sBy1A1_ibST1VpZSZrw7-YADsTU5Hs991fQ2jQTdhCh3xeqr9sDxAVFzWO7VJXlSCvMv5BJAmSYpCFh8eVvz23Y2DydvAvykJS9kFihLsTw-htBtks1J72dI5DIhsfdRq3SwHDqze3oIzqu8vRV8SPLKD2WIYGzz7VLZKngKKFSrgTTDjsjvVCi0REFmTtCNpgb6f1Iishnqf1lH6Pggrc80OapIYHRQzAnCbwK5Cky7LAJ0eYrjAbm9Z4j0WwZLp4uUBrIoFcRvGzsUMhjZgPGN6m5PF5pxj2rt3KKPhaAgT-lzfD8j6BUdfKXL0eiWsurUFjU2TnYKo9Z3WuBOZWPInfUpXS_L6-zUcA9E9ilBactwMZ12-0Afjkepu6gkRKMieTHFBBjPSnuoBYYWVjqScRQH22pAUISlQgf2dZOFQGd-pEM6kUtqwAhjZ6feWnnL1GE95cHNk3XO9pn1kD3-ApBjnEVDps1ckGWC6J4Uo4jxWqt1ZLoV89Q1clN0anxsOzp6Q3pNR6rX8", ""),
                        Albums("AFfJaJbtHaQNa9aHidleXqEzE3SsacIFcp3ijyKXWsgZNZtD0IB4AexjHvCttKmwgHPmQilBVTsW","동유럽","https://photos.google.com/lr/album/AFfJaJbtHaQNa9aHidleXqEzE3SsacIFcp3ijyKXWsgZNZtD0IB4AexjHvCttKmwgHPmQilBVTsW", "960", "", "")
                    )
                    */
                    //레이아웃매니저 설정
                    //recyclerView.layoutManager = LinearLayoutManager(applicationContext, LinearLayout.HORIZONTAL, false)
                    recyclerView.layoutManager = GridLayoutManager(applicationContext, 2)
                    recyclerView.setHasFixedSize(true)

                    //어답터 설정
                    //recyclerView.adapter = RecyclerViewAdapter(userList)
                    val gsonObj = GsonBuilder().create()

                    val myAlbumsObj = gsonObj.fromJson(myAlbumList, MyAlbum::class.java)
                    Log.d("albumListResponse", "" + myAlbumsObj)

                    runOnUiThread {
                        recyclerView.adapter = RecyclerViewAdapter(myAlbumsObj)
                    }

                }
            }

            override fun onFailure(call: Call<MyAlbum>?, t: Throwable?) {
                Log.d("errorResponse", "" + t.toString())
            }
        })
    }
}

class JsonObject() {

}