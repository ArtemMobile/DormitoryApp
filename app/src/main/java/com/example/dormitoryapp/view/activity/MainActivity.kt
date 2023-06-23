package com.example.dormitoryapp.view.activity

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.dormitoryapp.MessagingService
import com.example.dormitoryapp.R
import com.example.dormitoryapp.databinding.ActivityMainBinding
import com.example.dormitoryapp.databinding.AgendaLayoutBinding
import com.example.dormitoryapp.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val viewModel: PostViewModel by viewModels()

    private lateinit var pushBroadcastReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        showNewsDialog()
        initReceiver()
        // setObservers()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, des, _ ->
            when (des.id) {
                R.id.userFragment -> hideBottomNav()
                R.id.postFragment -> hideBottomNav()
                R.id.homeFragment -> {
                    viewModel.clearPostByIdStatus()
                }
                else -> showBottomNav()
            }
        }
    }

    private fun initReceiver() {
        pushBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val extras = intent?.extras
                val title = extras?.getString("title")
                val body = extras?.getString("body")
                val idUser = extras?.getString("notifier")

                if (body?.contains("опубликовал") == true) {
                    val idPost = extras?.getString("post")
                    val dialog = AlertDialog.Builder(this@MainActivity)
                        .setTitle(title)
                        .setMessage(body)
                        .setCancelable(true)
                        .setPositiveButton("Ок") { _, _ ->
                            val bundle = Bundle()
                            idUser?.let { bundle.putInt("idPost", it.toInt()) }
                            findNavController(R.id.nav_host_fragment).navigate(
                                R.id.postFragment,
                                bundle
                            )
                        }
                        .create()
                    dialog.show()
                    val posBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    posBtn.setTextColor(
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                this@MainActivity,
                                R.color.accent
                            )
                        )
                    )
                    posBtn.isAllCaps = false
                    posBtn.letterSpacing = 0F

                } else {
                    val dialog = AlertDialog.Builder(this@MainActivity)
                        .setTitle(title)
                        .setMessage(body)
                        .setCancelable(true)
                        .setPositiveButton("Посмотреть пользователя") { _, _ ->
                            val bundle = Bundle()
                            idUser?.let { bundle.putInt("idProfile", it.toInt()) }
                            findNavController(R.id.nav_host_fragment).navigate(
                                R.id.userFragment,
                                bundle
                            )}
                        .create()

                    dialog.show()
                    val posBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    posBtn.setTextColor(
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                applicationContext,
                                R.color.accent
                            )
                        )
                    )
                    posBtn.isAllCaps = false
                    posBtn.letterSpacing = 0F
                }
            }
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction(MessagingService.INTENT_FILTER)

        registerReceiver(pushBroadcastReceiver, intentFilter)
    }

//    private fun setObservers() {
//        viewModel.postById.observe(this) { post ->
//            viewModel.postByIdStatus.observe(this) {
//                when (it) {
//                    CreatePostStatus.SUCCESS -> {
//                        val bundle = Bundle()
//                        post?.let {
//                            bundle.putSerializable("postToWatch", it)
//                            findNavController(R.id.nav_host_fragment).navigate(
//                                R.id.homeFragment,
//                                bundle
//                            )
//                        }
//
//
//                        Handler(Looper.getMainLooper()).postDelayed({
//                            viewModel.postById.value = null
//                        },1000)
//                    }
//                    CreatePostStatus.FAILURE -> {
//
//                    }
//                    CreatePostStatus.NOTHING -> {
//
//                    }
//                }
//            }
//        }
//    }


    private fun showNewsDialog() {
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
        binding.bottomNavigationView.animate().translationY(0F).duration = 300
    }
}