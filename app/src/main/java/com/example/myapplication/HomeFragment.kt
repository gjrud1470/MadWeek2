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



class HomeFragment : Fragment() {

    val DEBUG_TAG : String = "HomeFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)

        //val fireworks = Fireworks()

        var surface : FireworksView = rootView.findViewById(R.id.home_surface)
        /*
        surface.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                Log.v(DEBUG_TAG, "on touch")
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        fireworks.create(event.x, event.y)
                    }
                }
                fireworks.animate(canvas)
                return true
            }
        })*/

        val fab: FloatingActionButton = rootView.findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Log.wtf(DEBUG_TAG, "안녕")
            Snackbar.make(view, "안녕", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show()
        }

        return rootView
    }

}
