package com.satmatgroup.safalsevakendra.activities_bbps.services_fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.satmatgroup.safalsevakendra.R
import com.satmatgroup.safalsevakendra.activities_bbps.BbpsBillConfirmationActivity
import kotlinx.android.synthetic.main.fragment_bbps_electricity.view.*


class BbpsElectricityFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bbps_electricity, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.btnShowDetails.setOnClickListener {
            val intent = Intent(context, BbpsBillConfirmationActivity::class.java)
            context?.startActivity(intent)
        }
    }


}