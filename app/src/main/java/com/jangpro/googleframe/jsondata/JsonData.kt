package com.jangpro.googleframe.jsondata

data class MyAlbum(val albums:List<Albums>)

data class Albums(
    val id:String,
    val title:String,
    val productUrl:String,
    val mediaItemsCount:String,
    val coverPhotoBaseUrl:String,
    val coverPhotoMediaItemId:String
)



