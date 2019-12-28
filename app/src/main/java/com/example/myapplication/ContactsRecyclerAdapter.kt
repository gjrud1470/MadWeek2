package com.example.myapplication

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.ImageViewCompat
import kotlinx.android.synthetic.main.contacts_list_item.view.*
import kotlin.random.Random
import android.content.Context
import android.widget.TextView




class ContactsRecyclerAdapter(private val context : Context,
                              private val listener : OnListItemSelectedInterface,
                              private val myDataset: ArrayList<ContactModel>) :
    RecyclerView.Adapter<ContactsRecyclerAdapter.MyViewHolder>() {
/*
    interface OnItemClickListener {
        fun onItemClick(view : View?, data : ContactModel) {
            ContactsItemFragment (view, data)
        }
    }

    private var mListener : ContactsRecyclerAdapter.OnItemClickListener? = null

    public fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mListener = listener
    }
    */

    interface OnListItemSelectedInterface {
        fun onItemSelected(view : View, position : Int)
    }

    private var mListener : OnListItemSelectedInterface = listener
    private var mContext : Context = context
    //private var recyclerView : RecyclerView = RecyclerView(mContext)

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //var textView: TextView
        init {
            //this.textView = itemView.findViewById(R.id. )
            itemView.setOnClickListener { v ->
                val position = adapterPosition
                mListener.onItemSelected(v, position)

                Log.d("test", "position = $adapterPosition")
            }
        }
    }
    /*
    inner class MyViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        init {
            super.itemView
            itemView.setOnClickListener { v ->
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    // 리스너 객체의 메서드 호출.
                    ContactsItemFragment(itemView, myDataset[pos])
                    //myDataset[pos].name = "item clicked. pos=${pos}"
                    //if (mListener != null)
                    //    mListener?.onItemClick(view, myDataset[pos])
                    //notifyItemChanged(pos)
                }
            }
        }
    }*/

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyViewHolder {
        // create a new view
        val ConstraintView = LayoutInflater.from(mContext) //parent.context
            .inflate(R.layout.contacts_list_item, parent, false) as ConstraintLayout
        // set the view's size, margins, paddings and layout parameters

        return MyViewHolder(ConstraintView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.itemView.contactname1.text = myDataset[position].name
        holder.itemView.phonenumber1.text = myDataset[position].mobileNumber
        val rnd = Random
        val randomcolor : Int = Color.argb(20, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))

        if (myDataset[position].photo != null){
            holder.itemView.thumbnail1.setImageBitmap(myDataset[position].photo)
            ImageViewCompat.setImageTintList(holder.itemView.thumbnail1, null)
        }
        else {
            holder.itemView.thumbnail1.setImageResource(R.drawable.default_profile)
            ImageViewCompat.setImageTintList(holder.itemView.thumbnail1, ColorStateList.valueOf(randomcolor))
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}