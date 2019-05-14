package com.jangpro.googleframe

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jangpro.googleframe.jsondata.Albums
import com.jangpro.googleframe.jsondata.MyAlbum
import kotlinx.android.synthetic.main.card_item.view.*


class RecyclerViewAdapter(val myalbum: MyAlbum) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    //아이템의 갯수
    override fun getItemCount(): Int {
        return myalbum.albums.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_item, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: RecyclerViewAdapter.ViewHolder, position: Int) {
        holder.bindItems(myalbum.albums.get(position))
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindItems(data: Albums) {
            //이미지표시
            Glide.with(itemView.context).load(data.coverPhotoBaseUrl).into(itemView.imageView_photo)
            itemView.textView_name.text = data.title
            itemView.textView_email.text = data.mediaItemsCount
            //itemView.imageView_photo.setImageBitmap(data.photo)
            //각각의 아이템 클릭시
            itemView.setOnClickListener({
                Log.d("cardviewClick", "Click!")
                Toast.makeText(itemView.context, "아이템 '${data.title}'를 클릭했습니다.", Toast.LENGTH_LONG).show()
                itemView.getContext().startActivity(MainActivity.getLaunchIntentPhoto(itemView.context, data.id));
            })


        }
    }

    /*
companion object {
        fun getLaunchIntent(from: Context, album_id: String) = Intent(from, Slideshow::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            putExtra("access_token", MainActivity.)
            putExtra("album_id", album_id)
        }
    }
    */
}