package com.example.reto_final.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.reto_final.R
import com.example.reto_final.databinding.UserFragmentBinding

class UserFragment : Fragment() {

    private lateinit var binding: UserFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.user_fragment, container, false)

        binding.closeFragment.setOnClickListener {

        }

        return view
    }
}