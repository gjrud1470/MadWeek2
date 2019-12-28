package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.widget.AdapterView.OnItemClickListener
import android.widget.CursorAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.contacts_list_item.view.*


class ContactsRecyclerAdapter(private val myDataset: ArrayList<ContactModel>) :
    RecyclerView.Adapter<ContactsRecyclerAdapter.MyViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

    private var mListener : ContactsRecyclerAdapter.OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mListener = listener
    }

    inner class MyViewHolder(val view: View) :
        RecyclerView.ViewHolder(view) {
        init {
            itemView.setOnClickListener { v ->
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    // 리스너 객체의 메서드 호출.
                    //myDataset[pos] = "item clicked. pos=${pos}"
                    if (mListener != null)
                        mListener?.onItemClick(v, pos)
                    notifyItemChanged(pos)
                }
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyViewHolder {
        // create a new view
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.contacts_list_item, parent, false) as ConstraintLayout
        // set the view's size, margins, paddings and layout parameters

        return MyViewHolder(textView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.view.text1.text = myDataset[position].name
        holder.view.imageView1.src =
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}