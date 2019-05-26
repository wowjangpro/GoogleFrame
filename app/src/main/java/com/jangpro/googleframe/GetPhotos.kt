package com.jangpro.googleframe

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.jangpro.googleframe.jsondata.*
import com.jangpro.googleframe.restful.GetPhotoInterface
import com.jangpro.googleframe.restful.OkHttp3RetrofitManager
import com.jangpro.googleframe.restful.ReturnInterface
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetPhotos {
    //var access_token: String?= null
    //var album_id: String?= null
    var returnInterface: ReturnInterface? = null
    /*
    constructor(access_token: String?, album_id: String?, mediaItems: List<MediaItems>?) : this(access_token, album_id){
        //this.mediaItems = mediaItems
    }
    */
    internal fun getPhotoList(context: Context, access_token:String?, album_id: String?, page_token: String?) {
        if (album_id == "") {
            Log.d("access_token", "" + access_token)
            Log.d("album_id", "" + album_id)
        } else {
            //Toast.makeText(this, "전달된 이름이 없습니다", Toast.LENGTH_SHORT).show()
        }
        val restClient: GetPhotoInterface =
            OkHttp3RetrofitManager.getRetrofitService(GetPhotoInterface::class.java)

        var searchString = SearchString(
            albumId = album_id.toString(),
            pageSize = 50,
            pageToken = page_token!!
        )
        val gsonSearchString = Gson().toJson(searchString)
        val searchStringBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), gsonSearchString)
        Log.d("myAlbumsObj", "" + gsonSearchString)

        val currentWeather = restClient.requestPhotoList(
            "/v1/mediaItems:search?access_token=$access_token&key="+context.getString(R.string.google_api_key),
            searchStringBody
        )

        currentWeather.enqueue(object : Callback<MyPhoto> {
            override fun onResponse(call: Call<MyPhoto>?, response: Response<MyPhoto>?) {
                Log.d("photoListResponse=====", ""+response)
                if(response != null && response.isSuccessful) {
                    val gson = Gson()
                    val myPhotoList = gson.toJson(response.body())
                    Log.d("photoListResponse", "" + myPhotoList)

                    val gsonObj = GsonBuilder().setPrettyPrinting().create()
                    val mediaList: MyPhoto = gsonObj.fromJson(myPhotoList, object : TypeToken<MyPhoto>() {}.type)
                    returnInterface?.MyPhotoCallback(mediaList)
                    //Log.d("mediaList", "" + (mediaItems as List<MediaItems>))
                }
                else {
                    Log.d("photoListResponse", "Error")
                }
            }
            override fun onFailure(call: Call<MyPhoto>?, t: Throwable?) {
                Log.d("errorResponse", ""+t.toString())
            }
        })
    }

}