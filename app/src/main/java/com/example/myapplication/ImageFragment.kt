package com.example.myapplication


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat.getDrawable
import androidx.recyclerview.widget.*

class ImageFragment : Fragment() {

    val image_list : ArrayList<ImageItem> = ArrayList()
    lateinit var ImageRecyclerView : RecyclerView

    var isfirst : Boolean = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        if (isfirst) {
            add_init()
            isfirst = false
        }

        var rootView = inflater.inflate(R.layout.fragment_image, container, false)

        var insertButton : Button = rootView.findViewById(R.id.plusbutton)
        var deleteButton : Button = rootView.findViewById(R.id.minusbutton)

        ImageRecyclerView = rootView.findViewById(R.id.recyclerView!!)as RecyclerView
        ImageRecyclerView.adapter = ImageRecyclerAdapter(image_list)


        return rootView
    }

    fun add_init(){
        image_list.add(ImageItem(getResources().getDrawable(R.drawable.image01)!!, getString(R.string.title01)))
        image_list.add(ImageItem(getResources().getDrawable(R.drawable.image02)!!, getString(R.string.title02)))
        image_list.add(ImageItem(getResources().getDrawable(R.drawable.image03)!!, getString(R.string.title03)))
        image_list.add(ImageItem(getResources().getDrawable(R.drawable.image04)!!, getString(R.string.title04)))
        image_list.add(ImageItem(getResources().getDrawable(R.drawable.image05)!!, getString(R.string.title05)))
        image_list.add(ImageItem(getResources().getDrawable(R.drawable.image06)!!, getString(R.string.title06)))
        image_list.add(ImageItem(getResources().getDrawable(R.drawable.image07)!!, getString(R.string.title07)))
    }


}