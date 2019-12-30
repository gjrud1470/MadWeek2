package com.example.myapplication

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.content.*
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_imageitem.view.*
import java.security.AccessController.getContext

class ImageRecyclerAdapter(private val context: Context, private val listener : OnListItemSelectedInterface, private val items: ArrayList<ImageItem>) :
    RecyclerView.Adapter<ImageRecyclerAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return items.size
    }

    interface OnListItemSelectedInterface {
        fun onItemSelected(view : View, position : Int)
    }

    private var mListener : OnListItemSelectedInterface = listener

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener { v ->
                val position = adapterPosition
                mListener.onItemSelected(v, position)
                Log.d("test", "position = $adapterPosition")
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        var view :View = holder.itemView

        view.thumbnail.setImageBitmap(item.image)
        view.title.text = item.title
        view.tag = item
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ImageRecyclerAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.list_imageitem, parent, false))
    }
}