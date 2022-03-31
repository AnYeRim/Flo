package com.example.flo.view.adapter

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flo.model.Lyrics

import android.view.LayoutInflater
import com.example.flo.R
import com.example.flo.view.LyricsActivity
import kotlinx.android.synthetic.main.item_lyrics.view.*

class AdapterLyrics(private val context : Context, private val data : ArrayList<Lyrics>) : RecyclerView.Adapter<AdapterLyrics.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_lyrics, parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setBinding(data[position]);
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setBinding(lyrics: Lyrics) {
            itemView.txtLyrics.text = lyrics.context

            itemView.setOnClickListener {
                var intent = Intent(context, LyricsActivity::class.java)
                //intent.putExtra("song", data)
                context.startActivity(intent)
            }
        }

    }
}
