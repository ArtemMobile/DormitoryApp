package com.example.dormitoryapp.view.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.iterator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.dormitoryapp.R
import com.example.dormitoryapp.databinding.FragmentProfileBinding
import com.example.dormitoryapp.model.dto.ProfileModel
import com.example.dormitoryapp.model.dto.Value
import com.example.dormitoryapp.utils.CreateProfileStatus
import com.example.dormitoryapp.utils.PrefsManager
import com.example.dormitoryapp.viewmodel.ProfileViewModel

class ProfileFragment : Fragment() {

    private val binding: FragmentProfileBinding by lazy {
        FragmentProfileBinding.inflate(layoutInflater)
    }

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.clearStatus()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyEditors()
        applyClicks()
        setObservers()
        viewModel.getProfileId()
    }

    private fun setObservers() {
        val dialog = ProgressDialog(requireContext())
        dialog.setCancelable(false)
        dialog.setMessage("Ждём-ждём")
        viewModel.createProfileStatus.observe(viewLifecycleOwner) {
            when (it) {
                CreateProfileStatus.SUCCESS -> {
                    Toast.makeText(requireContext(), "Профиль создан", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    findNavController().navigate(R.id.action_profileFragment_to_mainActivity)
                    requireActivity().finish()
                }
                CreateProfileStatus.FAIL -> {
                    val message = (viewModel.createProfileResponse.value?.value) as Value
                    Toast.makeText(requireContext(), message.message, Toast.LENGTH_SHORT).show()
                }
                CreateProfileStatus.NOTHING -> {}
            }
        }

        viewModel.updateProfileStatus.observe(viewLifecycleOwner) {
            when (it) {
                CreateProfileStatus.SUCCESS -> {
                    Toast.makeText(requireContext(), "Профиль обновлён", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                CreateProfileStatus.FAIL -> {
                    val message = (viewModel.updateProfileResponse.value?.value) as Value
                    Toast.makeText(requireContext(), message.message, Toast.LENGTH_SHORT).show()
                }
                CreateProfileStatus.NOTHING -> {}
            }
        }


        //nahera?
        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                dialog.show()
            } else {
                dialog.dismiss()
            }
        }

        viewModel.profile.observe(viewLifecycleOwner) {
            setData(it)
        }

        viewModel.profileId.observe(viewLifecycleOwner){
            viewModel.getProfileById(it)
        }
    }

    private fun setData(profile: ProfileModel) {
        with(binding) {
            etName.setText(profile.firstName)
            etRoom.setText(profile.room.toString())
            etInterests.setText(profile.interests)
            etPatronymic.setText(profile.patronymic)
            etSurname.setText(profile.surname)
            etContactInfo.setText(profile.contactInfo)
            etGroupNumber.setText(profile.groupNumber.toString())
        }
    }

    private fun applyClicks() {
        with(binding) {
            btnSaveProfile.setOnClickListener {
                if (viewModel.profile.value?.id != 0) {
                    viewModel.updateProfile(
                        ProfileModel(
                            "",
                            etContactInfo.text.toString(),
                            etGroupNumber.text.toString().toInt(),
                            etInterests.text.toString(),
                            etName.text.toString(),
                            etPatronymic.text.toString(),
                            etRoom.text.toString().toInt(),
                            etSurname.text.toString(),
                            PrefsManager(requireContext()).getEmail(),
                            viewModel.profileId.value!!
                        ), viewModel.profileId.value!!
                    )
                } else {
                    viewModel.createProfile(
                        ProfileModel(
                            "",
                            etContactInfo.text.toString(),
                            etGroupNumber.text.toString().toInt(),
                            etInterests.text.toString(),
                            etName.text.toString(),
                            etPatronymic.text.toString(),
                            etRoom.text.toString().toInt(),
                            etSurname.text.toString(),
                            PrefsManager(requireContext()).getEmail(),
                            0
                        )
                    )
                    viewModel.isLoading.value = true
                }
            }
        }
    }

    private fun applyEditors() {
        with(binding) {
            mainContainer.iterator().forEach { view ->
                if (view is TextView) {
                    view.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int,
                        ) {
                        }

                        override fun onTextChanged(
                            s: CharSequence?, start: Int, before: Int, count: Int,
                        ) {
                            if (s!!.isNotBlank()) {
                                view.setBackgroundResource(R.drawable.default_editor_filled)
                            } else {
                                view.setBackgroundResource(R.drawable.default_editor_empty)
                            }
                            applyEditButton()
                        }

                        override fun afterTextChanged(s: Editable?) {}
                    })
                }
            }
        }
    }

    private fun applyEditButton() {
        with(binding) {
            val fieldsNoEmpty = etSurname.text.toString()
                .isNotEmpty() && etContactInfo.text.toString()
                .isNotEmpty() && etRoom.text.toString()
                .isNotEmpty() && etInterests.text.toString()
                .isNotEmpty() && etGroupNumber.text.toString().isNotEmpty()

            if (fieldsNoEmpty) {
                btnSaveProfile.isEnabled = true
                btnSaveProfile.setBackgroundColor(
                    resources.getColor(
                        R.color.accent,
                        null
                    )
                )
            } else {
                btnSaveProfile.isEnabled = false
                btnSaveProfile.setBackgroundColor(
                    resources.getColor(
                        R.color.inactive_button,
                        null
                    )
                )
            }
        }
    }
}