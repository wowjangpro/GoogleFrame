package com.jangpro.googleframe.restful

import com.jangpro.googleframe.jsondata.MyAlbum
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitInterface {
    @GET("/v1/albums")
    fun  requestAlbumList(
        @Query("access_token") access_token:String,
        @Query("key") key:String
    ) : Call<MyAlbum>
}