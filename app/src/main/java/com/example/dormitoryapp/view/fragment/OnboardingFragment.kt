package com.example.dormitoryapp.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.dormitoryapp.R
import com.example.dormitoryapp.databinding.FragmentOnboardingBinding
import com.example.dormitoryapp.utils.PrefsManager
import com.example.dormitoryapp.view.adapter.OnboardingAdapter
import com.example.dormitoryapp.viewmodel.OnBoardingViewModel

class OnboardingFragment : Fragment() {

    private val binding: FragmentOnboardingBinding by lazy{
        FragmentOnboardingBinding.inflate(layoutInflater)
    }

    private val viewModel: OnBoardingViewModel by viewModels()
    private lateinit var onBoardingAdapter: OnboardingAdapter
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
        setObservers()
        initViewPager()
    }

    private fun applyClick(){
        with(binding){
            tvSkip.setOnClickListener {
                if (vpOnboarding.currentItem == 2) {
                    viewModel.setOnBoardingPassed()
                } else {
                    vpOnboarding.currentItem = vpOnboarding.currentItem + 1
                }
            }
        }
    }

    private fun initViewPager() {
        binding.vpOnboarding.apply {
            onBoardingAdapter = OnboardingAdapter(requireActivity())
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            adapter = onBoardingAdapter

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    when (position) {
                        0 -> {
                            binding.indicator1.setImageResource(R.drawable.selected_indicator)
                            binding.indicator2.setImageResource(R.drawable.unselected_indicator)
                            binding.indicator3.setImageResource(R.drawable.unselected_indicator)
                            binding.indicator1.setPadding(0, 0, 0, 0)
                            viewModel.setIsLastPage(false)
                        }
                        1 -> {
                            binding.indicator1.setImageResource(R.drawable.unselected_indicator)
                            binding.indicator2.setImageResource(R.drawable.selected_indicator)
                            binding.indicator3.setImageResource(R.drawable.unselected_indicator)
                            viewModel.setIsLastPage(false)
                        }
                        2 -> {
                            binding.indicator1.setImageResource(R.drawable.unselected_indicator)
                            binding.indicator2.setImageResource(R.drawable.unselected_indicator)
                            binding.indicator3.setImageResource(R.drawable.selected_indicator)
                            viewModel.setIsLastPage(true)
                        }
                    }
                }
            })
        }
        (binding.vpOnboarding[0] as RecyclerView).apply {
            overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        }
    }

    private fun setObservers() {
        viewModel.isLastPage.observe(viewLifecycleOwner) {
            if (it) {
                binding.tvSkip.text = resources.getString(R.string.skip)
            } else {
                binding.tvSkip.text = resources.getString(R.string.next)
            }
        }
        viewModel.isOnBoardingPassed.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigate(R.id.action_onboardingFragment_to_loginFragment)
            }
        }
    }
}