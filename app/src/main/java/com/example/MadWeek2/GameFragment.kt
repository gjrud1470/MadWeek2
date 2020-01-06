package com.example.MadWeek2


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.MadWeek2.Game.GameActivity

class GameFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_game, container, false)

        rootView.setOnClickListener {
            var intent = Intent(context, GameActivity::class.java)
            startActivity(intent)
        }

        return rootView
    }


}
