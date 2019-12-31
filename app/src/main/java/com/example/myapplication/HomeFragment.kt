package com.example.myapplication


import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.core.view.MotionEventCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import android.view.MotionEvent
import android.view.View.OnTouchListener
import android.widget.LinearLayout


class HomeFragment : Fragment() {

    val DEBUG_TAG : String = "HomeFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)

        val fab: FloatingActionButton = rootView.findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Log.wtf(DEBUG_TAG, "안녕")
            Snackbar.make(view, "안녕", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show()
        }

        return rootView
    }

}
