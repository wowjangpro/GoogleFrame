package com.jangpro.googleframe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jangpro.googleframe.jsondata.Albums
import com.jangpro.googleframe.jsondata.MyAlbum
import kotlinx.android.synthetic.main.content_main.view.*

class RecyclerViewAdapter(val myalbum:MyAlbum): RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    //아이템의 갯수
    override fun getItemCount(): Int {
        return myalbum.albums.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.content_main, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: RecyclerViewAdapter.ViewHolder, position: Int) {
        holder.bindItems(myalbum.albums.get(position))
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bindItems(data : Albums){
            //이미지표시
            Glide.with(itemView.context).load(data.coverPhotoBaseUrl).into(itemView.imageView_photo)
            itemView.textView_name.text = data.title
            itemView.textView_email.text = data.mediaItemsCount
            //itemView.imageView_photo.setImageBitmap(data.photo)

            //각각의 아이템 클릭시
            itemView.setOnClickListener({
                //여기서 토스터를 어떻게?
                Toast.makeText(itemView.context, "아이템 '${data.title}'를 클릭했습니다.", Toast.LENGTH_LONG).show()
            })
        }
    }
}