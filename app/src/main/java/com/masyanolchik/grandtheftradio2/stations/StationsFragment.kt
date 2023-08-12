package com.masyanolchik.grandtheftradio2.stations

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.masyanolchik.grandtheftradio2.R

/**
 * A [Fragment] that displays stations for the given era.
 * Use the [StationsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StationsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stations, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val stationsLabelView = view.findViewById<TextView>(R.id.stations_label)
        stationsLabelView.text = arguments?.getString("eraName")
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment.
         *
         * @return A new instance of fragment StationsFragment.
         */
        @JvmStatic
        fun newInstance() = StationsFragment()
    }
}