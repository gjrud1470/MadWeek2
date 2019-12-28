package com.example.myapplication

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.ImageViewCompat
import kotlinx.android.synthetic.main.contacts_list_item.view.*
import kotlin.random.Random


class ContactsRecyclerAdapter(private val myDataset: ArrayList<ContactModel>) :
    RecyclerView.Adapter<ContactsRecyclerAdapter.MyViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(view : View?, data : ContactModel) {
            ContactsItemFragment (view, data)
        }
    }

    private var mListener : ContactsRecyclerAdapter.OnItemClickListener? = null

    public fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mListener = listener
    }

    inner class MyViewHolder(val view: View) :
        RecyclerView.ViewHolder(view) {
        init {
            itemView.setOnClickListener { v ->
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    // 리스너 객체의 메서드 호출.
                    ContactsItemFragment(view, myDataset[pos])
                    //myDataset[pos].name = "item clicked. pos=${pos}"
                    //if (mListener != null)
                    //    mListener?.onItemClick(view, myDataset[pos])
                    //notifyItemChanged(pos)
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
        holder.view.contactname1.text = myDataset[position].name
        holder.view.phonenumber1.text = myDataset[position].mobileNumber
        val rnd = Random
        val randomcolor : Int = Color.argb(20, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))

        if (myDataset[position].photo != null){
            holder.view.thumbnail1.setImageBitmap(myDataset[position].photo)
            ImageViewCompat.setImageTintList(holder.view.thumbnail1, null)
        }
        else {
            holder.view.thumbnail1.setImageResource(R.drawable.default_profile)
            ImageViewCompat.setImageTintList(holder.view.thumbnail1, ColorStateList.valueOf(randomcolor))
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}