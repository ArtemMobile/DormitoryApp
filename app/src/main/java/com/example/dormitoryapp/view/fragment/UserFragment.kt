package com.example.dormitoryapp.view.fragment

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.dormitoryapp.databinding.FragmentUserBinding
import com.example.dormitoryapp.model.dto.ProfileModel
import com.example.dormitoryapp.viewmodel.ProfileViewModel

class UserFragment : Fragment() {

    private val profileViewModel: ProfileViewModel by viewModels()

    private val binding: FragmentUserBinding by lazy{
        FragmentUserBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileViewModel.getProfileById(requireArguments().getInt("idProfile"))
        val user = arguments?.getSerializable("profile") as? ProfileModel
        user?.let {
            setData(it)
        }
        setObServers()
        applyClicks()
    }

    private fun setObServers() {
        profileViewModel.profileById.observe(viewLifecycleOwner) {
            it?.let {
                setData(it)
            }
        }
    }

    private fun applyClicks() {
        with(binding) {
            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setData(profileModel: ProfileModel) {
        with(binding) {
            etFullName.text =
                "${profileModel.firstName} ${profileModel.surname} ${profileModel.patronymic}"
            etInfo.text = "Проживает в ${profileModel.room} комнате"
            etGroupNumber.text = "Обучается в группе ${profileModel.groupNumber}"
            etContactInfo.text = "Контактная иноформация:\n${profileModel.contactInfo}"
            etInterests.text = "Интересы:\n${profileModel.interests}"
        }
    }
}