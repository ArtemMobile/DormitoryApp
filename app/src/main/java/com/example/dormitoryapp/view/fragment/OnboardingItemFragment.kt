package com.example.dormitoryapp.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.dormitoryapp.R
import com.example.dormitoryapp.databinding.FragmentOnboardingItemBinding
import com.example.dormitoryapp.viewmodel.OnBoardingViewModel

class OnboardingItemFragment : Fragment() {

    private val viewModel: OnBoardingViewModel by viewModels()

    private val binding: FragmentOnboardingItemBinding by lazy{
        FragmentOnboardingItemBinding.inflate(layoutInflater)
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
            val position = arguments?.getInt("position") ?: 0
            val item = viewModel.onBoardingList[position]
            tvDescription.text = item.description
            tvTitle.text = item.title
            onboardingImage.setImageResource(item.image)
        }
    }

    companion object {
        fun newInstance(position: Int): OnboardingItemFragment {
            return OnboardingItemFragment().apply {
                arguments = Bundle().apply { putInt("position", position) }
            }
        }
    }
}