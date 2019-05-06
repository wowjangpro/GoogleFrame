package com.jangpro.googleframe.restful

import com.jangpro.googleframe.jsondata.MyAlbum
import com.jangpro.googleframe.jsondata.MyPhoto
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RetrofitInterface {
    @GET("/v1/albums")
    fun requestAlbumList(
        @Query("access_token") access_token:String,
        @Query("key") key:String
    ) : Call<MyAlbum>
}

interface GetPhotoInterface {
    @POST("/v1/mediaItems:search")
    fun requestPhotoList(
        @Query("access_token") access_token:String,
        @Query("key") key:String,
        @Query("data") data:String  //post 로 넘길 수 있는 방법?
    ) : Call<MyPhoto>

}
