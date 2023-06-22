package com.example.dormitoryapp.view.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isEmpty
import androidx.core.widget.NestedScrollView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dormitoryapp.R
import com.example.dormitoryapp.databinding.FilterChipBinding
import com.example.dormitoryapp.databinding.FragmentHomeBinding
import com.example.dormitoryapp.databinding.NewsBottomSheetBinding
import com.example.dormitoryapp.databinding.PostBottomSheetBinding
import com.example.dormitoryapp.model.dto.NewsModel
import com.example.dormitoryapp.model.dto.PostModel
import com.example.dormitoryapp.model.dto.PostSubscriptionModel
import com.example.dormitoryapp.model.dto.PostTypeModel
import com.example.dormitoryapp.utils.PrefsManager
import com.example.dormitoryapp.view.adapter.NewsAdapter
import com.example.dormitoryapp.view.adapter.PostAdapter
import com.example.dormitoryapp.viewmodel.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.internal.notify
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomeFragment : Fragment() {

    private val binding: FragmentHomeBinding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }

    private lateinit var pushBroadcastReceiver: BroadcastReceiver

    private val postTypeViewModel: PostTypeViewModel by viewModels()
    private val viewModel: PostViewModel by viewModels()
    private val postSubscriptionViewModel: PostSubscriptionViewModel by viewModels()
    private val newsViewModel: NewsViewModel by viewModels()
    private val notificationViewModel: NotificationViewModel by viewModels()
    private lateinit var postAdapter: PostAdapter
    private lateinit var newsAdapter: NewsAdapter
    private var typesList: List<PostTypeModel>? = null
    private var bottomSheetDialogBinding: PostBottomSheetBinding? = null
    private var postSubscriptionsOfUser: List<PostSubscriptionModel>? = null
    private var posts: List<PostModel>? = null
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
        viewModel.getPosts()
        postTypeViewModel.getPostTypes()
        postSubscriptionViewModel.getProfileSubscriptions()
        newsViewModel.getNews()
        setObservers()
        setUpAdapter()
        initSwipeRefreshLayout()
        initScroll()
        applyClicks()
        initNewsRecycler()

        var post = arguments?.getSerializable("postToWatch") as? PostModel
        post?.let { showBottomSheetDialog(it) }
        viewModel.clearPostByIdStatus()

        binding.etSearch.doOnTextChanged { text, start, before, count ->
            if (text!!.isNotBlank()) {
                posts = posts?.filter {
                    it.name.lowercase().contains(
                        binding.etSearch.text.toString().lowercase()
                    ) || it.description.lowercase()
                        .contains(binding.etSearch.text.toString().lowercase())
                }
                posts?.let { postAdapter.updatePosts(it) }
            } else {
                viewModel.getPosts()
            }
        }

        with(binding.chipGroup) {
            setOnCheckedStateChangeListener { _, checkedIds ->
                filterList()
            }
        }

        binding.cbPayable.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                posts = posts?.filter { it.isPayable == binding.cbPayable.isChecked }
                posts?.let {
                    postAdapter.updatePosts(it)
                }
            } else {
                viewModel.getPosts()
            }
        }


    }


    override fun onDestroy() {
        super.onDestroy()
        //requireContext().unregisterReceiver(pushBroadcastReceiver)
    }

    private fun applyClicks() {
        binding.fab.setOnClickListener {
            val bundle = Bundle()
            if (typesList != null) {
                bundle.putString("types", Gson().toJson(typesList))
            } else {
                bundle.putString("types", null)
            }

            findNavController().navigate(R.id.action_homeFragment_to_createPostFragment, bundle)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setObservers() {
        val dialog = ProgressDialog(requireContext())
        dialog.setCancelable(false)
        dialog.setMessage("Ждём-ждём")

        viewModel.posts.observe(viewLifecycleOwner) {
            posts = it
            postAdapter.updatePosts(it)
        }

        viewModel.error.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }

        postTypeViewModel.types.observe(viewLifecycleOwner) { types ->
            typesList = types
            if (binding.chipGroup.isEmpty()) {
                types.forEachIndexed { index, category ->
                    val chip =
                        FilterChipBinding.inflate(layoutInflater).rootChip.apply {
                            text = category.name
                        }
                    binding.chipGroup.addView(chip.apply {
                        id = category.id
                    })
                }
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.root.apply {
                isRefreshing = it
                setColorSchemeResources(R.color.white)
                setProgressBackgroundColorSchemeColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.accent
                    )
                )
            }
        }


        postSubscriptionViewModel.postSubscriptionOfUser.observe(viewLifecycleOwner) {
            postSubscriptionsOfUser = it
        }

        newsViewModel.news.observe(viewLifecycleOwner) {
            newsAdapter.updateList(it)
        }

        notificationViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                dialog.show()
            } else {
                dialog.dismiss()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun showBottomSheetDialog(postModel: PostModel) {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheet)
        bottomSheetDialogBinding = PostBottomSheetBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(bottomSheetDialogBinding!!.root)
        with(bottomSheetDialogBinding!!) {
            tvTitle.text = postModel.title
            tvDescription.text = postModel.description
            tvType.text = postModel.name
            tvPublishDate.text = "Опубликовано: ${
                DateTimeFormatter.ofPattern("dd.MM.yyy HH:mm")
                    .format(LocalDateTime.parse(postModel.publishDate))
            }"

            tvExpireDate.text = "Потеряет актуальность: ${
                DateTimeFormatter.ofPattern("dd.MM.yyy HH:mm")
                    .format(LocalDateTime.parse(postModel.expireDate))
            }"
            tvTitle.text = postModel.title
            tvAuthor.text = "${postModel.firstName} ${postModel.surname} "
            tvAuthor.setOnClickListener {
                bottomSheetDialog.dismiss()
                if (postModel.idProfile == PrefsManager(requireContext()).getProfile().id) {
                    findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
                } else {
                    val bundle = Bundle()
                    bundle.putInt("idProfile", postModel.idProfile)
                    findNavController().navigate(R.id.action_homeFragment_to_userFragment, bundle)
                }
            }

            if (postModel.idProfile == PrefsManager(
                    requireContext()
                ).getProfile().id
            ) {
                btnNotify.visibility = View.GONE
                btnSubscribe.visibility = View.GONE
                btnEdit.visibility = View.VISIBLE
                tvNotificationDate.visibility = View.GONE
            } else {
                btnNotify.visibility = View.VISIBLE
                btnSubscribe.visibility = View.VISIBLE
                btnEdit.visibility = View.GONE
                if (postModel.notificationDate != null) {
                    tvNotificationDate.text = "Уведомление придёт: ${
                        DateTimeFormatter.ofPattern("dd.MM.yyy HH:mm")
                            .format(LocalDateTime.parse(postModel.notificationDate))
                    }"
                    btnNotify.visibility = View.GONE

                } else {
                    btnSubscribe.visibility = View.GONE
                    tvNotificationDate.visibility = View.GONE
                    btnNotify.visibility = View.VISIBLE
                }
            }

            if (postModel.isPayable) {
                cardViewPayable.visibility = View.VISIBLE
            } else {
                cardViewPayable.visibility = View.GONE
            }

            ivClose.setOnClickListener {
                bottomSheetDialog.dismiss()
            }

            cardPay.setOnClickListener {
                Toast.makeText(
                    requireContext(),
                    "Автор предлаагет материальное вознаграждение за помощь",
                    Toast.LENGTH_LONG
                ).show()
            }

            btnSubscribe.apply {
                if (postSubscriptionsOfUser?.let { it.contains((postSubscriptionsOfUser?.firstOrNull { it.idPost == postModel.id })) } == true) {
                    text = "Отписаться от уведомления"
                    setOnClickListener {
                        postSubscriptionViewModel.deletePostSubscription(postModel.id)
                        bottomSheetDialog.dismiss()
                    }

                } else {
                    text = "Подписаться на уведомление"
                    setOnClickListener {
                        postSubscriptionViewModel.addPostSubscription(postModel.id)
                        bottomSheetDialog.dismiss()
                    }
                }
            }

            btnNotify.setOnClickListener {
                notificationViewModel.sendNotifications(postModel.idProfile, postModel.id)
            }

            btnEdit.setOnClickListener {
                val bundle = Bundle()
                bundle.putSerializable("post", postModel)
                if (typesList != null) {
                    bundle.putString("types", Gson().toJson(typesList))
                } else {
                    bundle.putString("types", null)
                }
                bottomSheetDialog.dismiss()
                findNavController().navigate(R.id.action_homeFragment_to_createPostFragment, bundle)
            }
        }
        bottomSheetDialog.show()
        viewModel.postById.value = null
    }

    private fun showNewsDialog(newsModel: NewsModel) {
        val newsDialogBinding = NewsBottomSheetBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(newsDialogBinding.root)
            .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        newsDialogBinding.tvTitle.text = newsModel.title
        newsDialogBinding.tvContent.text = newsModel.content
        newsDialogBinding.ivClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpAdapter() {
        postAdapter =
            PostAdapter(requireContext(), mutableListOf(), onClick = { showBottomSheetDialog(it) })
        with(binding.rvPosts) {
            adapter = postAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun initSwipeRefreshLayout() {
        binding.root.setOnRefreshListener {
            lifecycleScope.launch {
                viewModel.getPosts()
                binding.etSearch.setText("")
                binding.etSearch.clearFocus()
                binding.chipGroup.clearCheck()
            }
        }
    }

    private fun initScroll() {
        with(binding.nestedScrollView) {
            setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                var difference = (scrollY - oldScrollY)
                if (difference > 0) {
                    binding.fab.hide()
                } else {
                    binding.fab.show()
                }
            })
        }
        for (thread in list) {
            thread.notify()
        }
    }

    var list = ArrayList<Thread>()


    private fun filterList() {
        val type = binding.chipGroup.checkedChipId
        if (type != -1) {
            posts = posts?.filter { it.idType == type }
        } else {
            viewModel.getPosts()
        }
        posts?.let { postAdapter.updatePosts(it) }

    }

    private fun initNewsRecycler() {
        newsAdapter = NewsAdapter(requireContext(), listOf(), onClick = {
            showNewsDialog(it)
        })
        with(binding.rvNews) {
            adapter = newsAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }
}