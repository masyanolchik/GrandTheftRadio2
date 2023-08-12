package com.masyanolchik.grandtheftradio2.assetimport

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.masyanolchik.grandtheftradio2.R

/**
 * A [Fragment] which is used for importing serialized list of stations.
 * Use the [ImportFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ImportFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_import, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment.
         *
         * @return A new instance of fragment ImportFragment.
         */
        @JvmStatic
        fun newInstance() = ImportFragment()
    }
}