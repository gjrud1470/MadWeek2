package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.*
import android.media.ThumbnailUtils
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_imageitem.view.*

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

        var imageWidth = item.image!!.getWidth()
        var imageHeight = item.image!!.getHeight()

        view.measure(View.MeasureSpec.makeMeasureSpec(imageWidth, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(imageHeight, View.MeasureSpec.EXACTLY))
        var siz = view.measuredWidth

        var bitmap = item.image
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, siz, siz)

        //if(imageWidth>siz || imageHeight>siz)
        //    bitmap = Bitmap.createScaledBitmap(bitmap, siz, siz, true)

        view.thumbnail.setImageBitmap(bitmap)
        view.title.text = item.title
        view.tag = item
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ImageRecyclerAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.list_imageitem, parent, false))
    }
}