package com.example.dormitoryapp.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dormitoryapp.R
import com.example.dormitoryapp.databinding.FragmentUsersBinding
import com.example.dormitoryapp.model.dto.ProfileModel
import com.example.dormitoryapp.view.adapter.UserAdapter
import com.example.dormitoryapp.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

class UsersFragment : Fragment() {

    private val binding: FragmentUsersBinding by lazy {
        FragmentUsersBinding.inflate(layoutInflater)
    }
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var userAdapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        viewModel.getAllUsers()
        setObservers()
        initSwipeRefreshLayout()
    }

    private fun setObservers() {
        viewModel.users.observe(viewLifecycleOwner) {
            it?.let {
                userAdapter.updateList(it)
            }
        }
        viewModel.isLoading.observe(viewLifecycleOwner){
            binding.root.isRefreshing = it
        }
    }

    private fun navigateToUserPage(user: ProfileModel){
        val bundle = Bundle()
        bundle.putSerializable("profile", user)
        findNavController().navigate(R.id.action_usersFragment_to_userFragment, bundle)
    }

    private fun setAdapter() {
        userAdapter = UserAdapter(requireContext(), listOf(), onClick = {
            navigateToUserPage(it)
        })
        with(binding.rvUsers) {
            adapter = userAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun initSwipeRefreshLayout() {
        binding.root.setOnRefreshListener {
            lifecycleScope.launch {
                viewModel.getAllUsers()
            }
        }
    }
}