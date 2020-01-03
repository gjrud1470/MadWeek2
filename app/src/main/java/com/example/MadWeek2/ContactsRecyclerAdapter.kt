package com.example.MadWeek2

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.ImageViewCompat
import kotlinx.android.synthetic.main.contacts_list_item.view.*
import kotlin.random.Random
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import androidx.core.content.ContextCompat
import com.example.MadWeek2.R
import com.mikhaellopez.circularimageview.CircularImageView


class ContactsRecyclerAdapter(private val context : Context,
                              private val listener : OnListItemSelectedInterface,
                              private val myDataset: ArrayList<ContactModel>) :
    RecyclerView.Adapter<ContactsRecyclerAdapter.MyViewHolder>() {

    interface OnListItemSelectedInterface {
        fun onItemSelected(view : View, position : Int)
        fun callOnClick(view : View, position : Int)
    }

    private var mListener : OnListItemSelectedInterface = listener
    private var mContext : Context = context
    //private var recyclerView : RecyclerView = RecyclerView(mContext)

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //var textView: TextView
        init {
            var call_view : CircularImageView = itemView.findViewById(R.id.button_call)
            call_view.setOnClickListener { v ->
                val position = adapterPosition
                mListener.callOnClick (v, position)

                Log.d("viewholder", "position = $adapterPosition")
            }
            itemView.setOnClickListener { v ->
                val position = adapterPosition
                mListener.onItemSelected(v, position)

                Log.d("test", "position = $adapterPosition")
            }
        }
    }

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
            val bitmapdrawable : BitmapDrawable = ContextCompat.getDrawable(context,
                R.drawable.default_profile
            ) as BitmapDrawable
            val bitmappic : Bitmap = bitmapdrawable.bitmap
            myDataset[position].photo = tintImage(bitmappic, randomcolor)
            holder.itemView.thumbnail1.setImageBitmap(myDataset[position].photo)
            //holder.itemView.thumbnail1.setImageResource(R.drawable.default_profile)
            //ImageViewCompat.setImageTintList(holder.itemView.thumbnail1, ColorStateList.valueOf(randomcolor))
            //var drawable : Drawable? = ContextCompat.getDrawable(context, R.drawable.default_profile)
            //drawable?.setColorFilter(PorterDuffColorFilter(randomcolor, PorterDuff.Mode.SRC_ATOP))
        }

    }

    private fun tintImage(bitmap: Bitmap, color: Int): Bitmap {
        val paint = Paint()
        paint.setColorFilter(PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP))
        val bitmapResult = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmapResult)
        val matrix = Matrix()
        canvas.drawBitmap(bitmap, matrix, paint)
        return bitmapResult
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}