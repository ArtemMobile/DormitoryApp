package com.example.dormitoryapp.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.dormitoryapp.R
import com.example.dormitoryapp.databinding.FragmentOnboardingBinding

class OnboardingFragment : Fragment() {

    private val binding: FragmentOnboardingBinding by lazy{
        FragmentOnboardingBinding.inflate(layoutInflater)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyClick()
    }

    private fun applyClick(){
        with(binding){
            tvSkip.setOnClickListener {
                findNavController().navigate(R.id.action_onboardingFragment_to_loginFragment)
            }
        }
    }
}