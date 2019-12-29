package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_imageitem.view.*

class ImageRecyclerAdapter(private val items: ArrayList<ImageItem>) :
    RecyclerView.Adapter<ImageRecyclerAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ImageRecyclerAdapter.ViewHolder, position: Int) {
        val item = items[position]
        val listener = View.OnClickListener {it ->
            Toast.makeText(it.context, "Clicked: ${item.title}", Toast.LENGTH_SHORT).show()
        }
        holder.apply {
            bind(listener, item)
            itemView.tag = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ImageRecyclerAdapter.ViewHolder {
        /*val inflatedView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_imageitem, parent, false)*/
        return ImageRecyclerAdapter.ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.list_imageitem, parent, false))
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        private var view: View = v

        fun bind(listener: View.OnClickListener, item: ImageItem) {
            view.thumbnail.setImageBitmap(item.image)
            view.title.text = item.title
            view.setOnClickListener(listener)
        }
    }
}