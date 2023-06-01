package com.example.dormitoryapp.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.dormitoryapp.R
import com.example.dormitoryapp.databinding.FragmentSplashBinding
import com.example.dormitoryapp.utils.PrefsManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private val binding: FragmentSplashBinding by lazy {
        FragmentSplashBinding.inflate(layoutInflater)
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
        with(binding) {
            ivLogo.animate()
                .alpha(1f)
                .duration = 1700
            lifecycleScope.launch {
                delay(2000)
                val prefsManager = PrefsManager(requireContext())
                if (prefsManager.getOnboardingPassed()) {
                    if (prefsManager.getLoginPassed()) {
                        if (prefsManager.getCreateProfilePassed()) {
                            findNavController().navigate(R.id.action_splashFragment_to_mainActivity)
                            requireActivity().finish()
                        } else {
                            findNavController().navigate(R.id.action_splashFragment_to_profileFragment)
                        }

                    } else {
                        findNavController().navigate(R.id.action_splashFragment_to_registerFragment)
                    }
                } else {
                    findNavController().navigate(R.id.action_splashFragment_to_onboardingFragment)
                }
            }
        }
    }

}