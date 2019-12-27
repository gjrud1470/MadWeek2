package com.example.myapplication


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getDrawable
import androidx.recyclerview.widget.*

class ImageFragment : Fragment() {

    val image_list : ArrayList<ImageItem> = ArrayList()
    lateinit var ImageRecyclerView : RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        image_list.add(ImageItem(getResources().getDrawable(R.drawable.image01)!!, getString(R.string.title01)))
        image_list.add(ImageItem(getResources().getDrawable(R.drawable.image02)!!, getString(R.string.title02)))
        image_list.add(ImageItem(getResources().getDrawable(R.drawable.image03)!!, getString(R.string.title03)))

        var rootView = inflater.inflate(R.layout.fragment_image, container, false)

        ImageRecyclerView = rootView.findViewById(R.id.recyclerView!!)as RecyclerView
        ImageRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        ImageRecyclerView.adapter = ImageRecyclerAdapter(image_list)

        return rootView

    }


}