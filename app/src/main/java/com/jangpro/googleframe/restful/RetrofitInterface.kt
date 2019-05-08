package com.jangpro.googleframe.restful

import com.jangpro.googleframe.jsondata.MyAlbum
import com.jangpro.googleframe.jsondata.MyPhoto
import okhttp3.FormBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.internal.http.RealResponseBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitInterface {
    @GET("/v1/albums")
    fun requestAlbumList(
        @Query("access_token") access_token:String,
        @Query("key") key:String
    ) : Call<MyAlbum>
}
/*
interface GetPhotoInterface {
    @POST("/v1/mediaItems:search")
    fun requestPhotoList(
        @Field("access_token") access_token:String,
        @Field("key") key:String,
        @Field("data") data:String  //post 로 넘길 수 있는 방법?
    ) : Call<MyPhoto>
}
*/
interface GetPhotoInterface {
    @FormUrlEncoded
    @Headers("Content-Type: application/json")
    @POST("/v1/mediaItems:search")
    fun requestPhotoList(
        @Query("access_token") access_token: String,
        @Query("key") key: String,
        @Field("body") body: String
    ) : Call<MyPhoto>
}
