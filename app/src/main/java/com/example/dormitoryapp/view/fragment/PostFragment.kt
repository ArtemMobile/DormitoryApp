package com.example.dormitoryapp.view.fragment

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.dormitoryapp.R
import com.example.dormitoryapp.databinding.FragmentPostBinding
import com.example.dormitoryapp.databinding.PostBottomSheetBinding
import com.example.dormitoryapp.model.dto.PostModel
import com.example.dormitoryapp.model.dto.PostSubscriptionModel
import com.example.dormitoryapp.utils.PrefsManager
import com.example.dormitoryapp.viewmodel.NotificationViewModel
import com.example.dormitoryapp.viewmodel.PostSubscriptionViewModel
import com.example.dormitoryapp.viewmodel.PostViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class PostFragment : Fragment() {

    private val binding: FragmentPostBinding by lazy {
        FragmentPostBinding.inflate(layoutInflater)
    }

    private val postSubscriptionViewModel: PostSubscriptionViewModel by viewModels()
    private var postSubscriptionsOfUser: List<PostSubscriptionModel>? = null
    private val notificationViewModel: NotificationViewModel by viewModels()
    private val postViewModel: PostViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var post = arguments?.getInt("idPost")
        post?.let {  postViewModel.getPostById(it) }
        setObservers()
        postSubscriptionViewModel.getProfileSubscriptions()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setObservers() {
        postSubscriptionViewModel.postSubscriptionOfUser.observe(viewLifecycleOwner) {
            postSubscriptionsOfUser = it
        }

        postViewModel.postById.observe(viewLifecycleOwner){
            showBottomSheetDialog(it)
        }

    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun showBottomSheetDialog(postModel: PostModel) {
        with(binding!!) {
            tvTitle.text = postModel.title
            tvDescription.text = postModel.description
            tvType.text = postModel.name
            tvPublishDate.text = "Опубликовано: ${
                java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyy HH:mm")
                    .format(java.time.LocalDateTime.parse(postModel.publishDate))
            }"

            tvExpireDate.text = "Потеряет актуальность: ${
                java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyy HH:mm")
                    .format(java.time.LocalDateTime.parse(postModel.expireDate))
            }"
            tvTitle.text = postModel.title
            tvAuthor.text = "${postModel.firstName} ${postModel.surname} "
            tvAuthor.setOnClickListener {
                if (postModel.idProfile == PrefsManager(requireContext()).getProfile().id) {
                    findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
                } else {
                    val bundle = Bundle()
                    bundle.putInt("idProfile", postModel.idProfile)
                    findNavController().navigate(R.id.action_postFragment_to_userFragment, bundle)
                }
            }

            if (postModel.idProfile == PrefsManager(
                    requireContext()
                ).getProfile().id
            ) {
                btnNotify.visibility = android.view.View.GONE
                btnSubscribe.visibility = android.view.View.GONE
                btnEdit.visibility = android.view.View.VISIBLE
                tvNotificationDate.visibility = android.view.View.GONE
            } else {
                btnNotify.visibility = android.view.View.VISIBLE
                btnSubscribe.visibility = android.view.View.VISIBLE
                btnEdit.visibility = android.view.View.GONE
                if (postModel.notificationDate != null) {
                    tvNotificationDate.text = "Уведомление придёт: ${
                        java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyy HH:mm")
                            .format(java.time.LocalDateTime.parse(postModel.notificationDate))
                    }"
                    btnNotify.visibility = android.view.View.GONE

                } else {
                    btnSubscribe.visibility = android.view.View.GONE
                    tvNotificationDate.visibility = android.view.View.GONE
                    btnNotify.visibility = android.view.View.VISIBLE
                }
            }

            if (postModel.isPayable) {
                cardViewPayable.visibility = android.view.View.VISIBLE
            } else {
                cardViewPayable.visibility = android.view.View.GONE
            }

            ivClose.setOnClickListener {
                findNavController().popBackStack()
            }

            cardPay.setOnClickListener {
                android.widget.Toast.makeText(
                    requireContext(),
                    "Автор предлаагет материальное вознаграждение за помощь",
                    android.widget.Toast.LENGTH_LONG
                ).show()
            }

            btnSubscribe.apply {
                if (postSubscriptionsOfUser?.let { it.contains((postSubscriptionsOfUser?.firstOrNull { it.idPost == postModel.id })) } == true) {
                    text = "Отписаться от уведомления"
                    setOnClickListener {
                        postSubscriptionViewModel.deletePostSubscription(postModel.id)
                        findNavController().popBackStack()
                    }

                } else {
                    text = "Подписаться на уведомление"
                    setOnClickListener {
                        postSubscriptionViewModel.addPostSubscription(postModel.id)
                        findNavController().popBackStack()
                    }
                }
            }

            btnNotify.setOnClickListener {
                notificationViewModel.sendNotifications(postModel.idProfile, postModel.id)
            }
        }
    }

}