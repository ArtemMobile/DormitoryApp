package com.example.dormitoryapp.view.activity

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.dormitoryapp.R
import com.example.dormitoryapp.databinding.ActivityMainBinding
import com.example.dormitoryapp.databinding.AgendaLayoutBinding

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val notificationBody = intent?.extras?.getString("body")
        if (notificationBody == null) {
            showNewsDialog()
        } else {
            intent?.extras?.let {
                val body = it.getString("body")
                val title = it.getString("title")
                AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(body)
                    .setNeutralButton("OK", { _, _ -> })
                    .setPositiveButton("Посмотреть профиль", { _, _ -> })
                    .show()
            }
        }


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

    private fun showNewsDialog(){
        val newsDialogBinding = AgendaLayoutBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this)
            .setView(newsDialogBinding.root)
            .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        newsDialogBinding.tvTitle.text = "Генеральная уборка"
        newsDialogBinding.tvContent.text =
            "В понедельник 05.06.2023 состоится генеральная уборка. Мы ждём от вас вовлеченности в процесс и чистоты во время проверки во вторник^_^"
        newsDialogBinding.ivClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
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