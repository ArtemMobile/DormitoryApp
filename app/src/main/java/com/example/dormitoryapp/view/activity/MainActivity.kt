package com.example.dormitoryapp.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.dormitoryapp.R
import com.example.dormitoryapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, des, _ ->
            when (des.id) {
                R.id.userFragment -> hideBottomNav()
                else -> showBottomNav()
            }
        }
    }

    fun hideBottomNav() {
        binding.bottomNavigationView.clearAnimation()
        binding.bottomNavigationView.animate().translationY(
            binding.bottomNavigationView.getHeight()
                .toFloat()
        ).duration = 300;
    }

    fun showBottomNav() {
        binding.bottomNavigationView.clearAnimation();
        binding.bottomNavigationView.animate().translationY(0F).duration = 300;
    }
}