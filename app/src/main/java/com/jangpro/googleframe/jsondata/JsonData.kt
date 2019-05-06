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

data class MyPhoto(val mediaitems:ArrayList<MediaItems>)

data class MediaItems(
    val id:String,
    val productUrl:String,
    val baseUrl:String,
    val mimeType:String,
    val mediaMetadata:List<MediaMetaData>
)

data class MediaMetaData(
    val creationTime:String,
    val width:String,
    val height:String
)