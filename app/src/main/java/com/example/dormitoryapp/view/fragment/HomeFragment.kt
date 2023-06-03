package com.example.dormitoryapp.view.fragment

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isEmpty
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dormitoryapp.R
import com.example.dormitoryapp.databinding.FilterChipBinding
import com.example.dormitoryapp.databinding.FragmentHomeBinding
import com.example.dormitoryapp.databinding.PostBottomSheetBinding
import com.example.dormitoryapp.model.dto.PostModel
import com.example.dormitoryapp.model.dto.PostTypeModel
import com.example.dormitoryapp.view.adapter.PostAdapter
import com.example.dormitoryapp.viewmodel.PostTypeViewModel
import com.example.dormitoryapp.viewmodel.PostViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomeFragment : Fragment() {

    private val binding: FragmentHomeBinding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }

    private val postTypeViewModel: PostTypeViewModel by viewModels()
    private val viewModel: PostViewModel by viewModels()
    private lateinit var postAdapter: PostAdapter
    private lateinit var typesList: List<PostTypeModel>
    private var bottomSheetDialogBinding: PostBottomSheetBinding? = null
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
        setObservers()
        setUpAdapter()
        initSwipeRefreshLayout()
        applyChips()
        initScroll()
    }

    private fun setObservers() {
        viewModel.posts.observe(viewLifecycleOwner) {
            postAdapter.updatePosts(it)
        }

        viewModel.error.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }

        postTypeViewModel.types.observe(viewLifecycleOwner) { types ->
            if(binding.chipGroup.isEmpty()){
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
        viewModel.isLoading.observe(viewLifecycleOwner){
            binding.root.isRefreshing = it
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
                DateTimeFormatter.ofPattern("dd.MM.yyy hh:mm")
                    .format(LocalDateTime.parse(postModel.publishDate))
            }"
            tvTitle.text = postModel.title
            tvAuthor.text = "${postModel.firstName} ${postModel.surname} "
            tvAuthor.setOnClickListener {
                bottomSheetDialog.dismiss()
                val bundle = Bundle()
                bundle.putInt("idProfile", postModel.idProfile)
                findNavController().navigate(R.id.action_homeFragment_to_userFragment, bundle)
            }

            if (postModel.notificationDate != null) {
                tvNotificationDate.text = "Уведомление придёт: ${
                    DateTimeFormatter.ofPattern("dd.MM.yyy hh:mm")
                        .format(LocalDateTime.parse(postModel.notificationDate as String))
                }"
            } else {
                btnSubscribe.visibility = View.GONE
                tvNotificationDate.visibility = View.GONE
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
                Toast.makeText(requireContext(), "Автор записи...", Toast.LENGTH_SHORT).show()
            }

            btnSubscribe.setOnClickListener {

            }
        }
        bottomSheetDialog.show()
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
            }
        }
    }

    private fun initScroll(){
        with(binding.nestedScrollView){
            setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                var difference = (scrollY - oldScrollY)
                if(difference>0){
                    binding.fab.hide()
                } else{
                    binding.fab.show()
                }
            })
        }
    }

    private fun applyChips(){
        with(binding.chipGroup){
            setOnCheckedStateChangeListener { _, checkedIds ->
                if(checkedIds.isNotEmpty()){
                    val idType = checkedIds[0]
                    viewModel.getPostByType(idType)
                } else{
                    viewModel.isLoading.value = true
                    viewModel.getPosts()
                }
            }
        }
    }
}