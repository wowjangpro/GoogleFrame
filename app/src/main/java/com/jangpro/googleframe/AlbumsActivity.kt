package com.jangpro.googleframe

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.jangpro.googleframe.jsondata.MyAlbum
import com.jangpro.googleframe.restful.OkHttp3RetrofitManager
import com.jangpro.googleframe.restful.RetrofitInterface
import kotlinx.android.synthetic.main.activity_albums.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AlbumsActivity : AppCompatActivity() {
    var accessToken: String?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_albums)
        getAlbumList()
    }

    private fun getAlbumList() {
        if (intent.hasExtra("access_token")) {
            accessToken = intent.getStringExtra("access_token")
        } else {
            Toast.makeText(this, "전달된 이름이 없습니다", Toast.LENGTH_SHORT).show()
        }
        /*
          * 요청을 보낼 객체를 리턴 받는다
          */
        val restClient: RetrofitInterface =
            OkHttp3RetrofitManager.getRetrofitService(RetrofitInterface::class.java)

        /*
         * Retrofit2을 사용하면 별도의 Thread(AsyncTask)를 만들필요없이
         * 비동기 방식으로 동작하도록 구성할 수 있다
         */
        val currentWeather = restClient.requestAlbumList(accessToken!!, getString(R.string.google_api_key))
        currentWeather.enqueue(object : Callback<MyAlbum> {
            override fun onResponse(call: Call<MyAlbum>?, response: Response<MyAlbum>?) {
                if(response != null && response.isSuccessful) {
                    val gson = Gson()
                    val myAlbumList = gson.toJson(response.body())
                    Log.e("albumListResponse", "" + myAlbumList)
                    textView2.setText(myAlbumList)
                }
            }
            override fun onFailure(call: Call<MyAlbum>?, t: Throwable?) {
                Log.e("errorResponse", ""+t.toString())
            }
        })
    }

}