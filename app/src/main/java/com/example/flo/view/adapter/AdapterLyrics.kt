package com.example.flo.view.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flo.model.Lyrics

import android.view.LayoutInflater
import com.example.flo.R
import com.example.flo.view.LyricsActivity
import com.example.flo.view.MainActivity
import kotlinx.android.synthetic.main.item_lyrics.view.*

class AdapterLyrics(private val context: Context, private val data: ArrayList<Lyrics>) :
    RecyclerView.Adapter<AdapterLyrics.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_lyrics, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setBinding(data[position]);
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun boldText(position: Int) {
        for (i in 0 until data.size) {
            data[i].running = false
        }
        data[position].running = true
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setBinding(lyrics: Lyrics) {
            itemView.txtLyrics.text = lyrics.context

            if (lyrics.running) {
                itemView.txtLyrics.setTypeface(null, Typeface.BOLD)
            }

            itemView.setOnClickListener {
                // TODO 가사 전체화면 띄워야 함
                /*var intent = Intent(context, LyricsActivity::class.java)
                context.startActivity(intent)*/
                (context as MainActivity).movePlayPosition(lyrics.milliseconds)
            }
        }
    }
}
