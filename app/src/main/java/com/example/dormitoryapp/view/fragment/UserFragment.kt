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
import com.example.dormitoryapp.viewmodel.ProfileSubscriptionViewModel
import com.example.dormitoryapp.viewmodel.ProfileViewModel

class UserFragment : Fragment() {

    private val profileViewModel: ProfileViewModel by viewModels()
    private val profileSubscriptionViewModel: ProfileSubscriptionViewModel by viewModels()

    private val binding: FragmentUserBinding by lazy{
        FragmentUserBinding.inflate(layoutInflater)
    }

    private var user : ProfileModel? = null

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
        user = arguments?.getSerializable("profile") as? ProfileModel
        user?.let {
            setData(it)
        }
        setObServers()
        applyClicks()
        profileSubscriptionViewModel.getProfileSubscriptions()
    }

    private fun setObServers() {
        profileSubscriptionViewModel.profileSubscriptionOfUser.observe(viewLifecycleOwner){
            var profileSubscriptionsOfUser = it
            binding.btnFollow.apply {
                if(profileSubscriptionsOfUser.contains(profileSubscriptionsOfUser.firstOrNull { u -> u.idProfile == user?.id })){
                    text = "Отписаться от уведомлений"
                    setOnClickListener {
                        user?.id?.let { it1 ->
                            profileSubscriptionViewModel.deleteProfileSubscription(
                                it1
                            )
                        }
                    }
                } else{
                    text = "Подписаться на уведомления"
                    setOnClickListener {
                        user?.id?.let { it1 ->
                            profileSubscriptionViewModel.addProfileSubscription(
                                it1
                            )
                        }
                    }
                }
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