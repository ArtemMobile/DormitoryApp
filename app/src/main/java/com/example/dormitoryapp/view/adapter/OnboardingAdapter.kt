package com.example.dormitoryapp.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.dormitoryapp.view.fragment.OnboardingItemFragment

class OnboardingAdapter(
    private val fragmentActivity: FragmentActivity,
) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return OnboardingItemFragment.newInstance(position)
    }
}