package com.example.myapplication

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar


class PaintFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
    ): View?    {
        // Inflate the layout for this fragment
        var v = inflater.inflate(R.layout.fragment_paint, container, false)

        var clearButton : Button = v.findViewById(R.id.clear)
        clearButton.setOnClickListener {
            Toast.makeText(getContext(), "clear the canvas", Toast.LENGTH_SHORT).show()
            //clearCanvas()
        }




        return v;

    }

}
