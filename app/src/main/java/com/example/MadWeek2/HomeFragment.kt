package com.example.MadWeek2


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.MadWeek2.R


class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

}