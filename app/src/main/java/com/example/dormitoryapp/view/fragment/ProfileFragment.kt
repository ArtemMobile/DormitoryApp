package com.example.dormitoryapp.view.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.iterator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.dormitoryapp.R
import com.example.dormitoryapp.databinding.FragmentProfileBinding
import com.example.dormitoryapp.databinding.ProgressDialogBinding
import com.example.dormitoryapp.model.dto.ProfileModel
import com.example.dormitoryapp.model.dto.Value
import com.example.dormitoryapp.utils.CreateProfileStatus
import com.example.dormitoryapp.utils.PrefsManager
import com.example.dormitoryapp.viewmodel.ProfileViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging


class ProfileFragment : Fragment() {

    private val binding: FragmentProfileBinding by lazy {
        FragmentProfileBinding.inflate(layoutInflater)
    }

    private val viewModel: ProfileViewModel by viewModels()

    private val selectPhotoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                PrefsManager(requireContext()).savePhoto(it.data?.data.toString())
                Glide.with(requireContext())
                    .load(it.data?.data)
                    .into(binding.ivAvatar)
            }
        }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            if (PrefsManager(requireContext()).getProfile().deviceId == "") {
                getFirebaseToken()
            }
            Toast.makeText(requireContext(), "Разрешение получено", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(
                requireContext(),
                "Пожалуйста, дайте разрешение на отправку уведомлений",
                Toast.LENGTH_LONG
            ).show()

        }
    }

    private var deviceId = ""

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
        askNotificationPermission()
        viewModel.getProfileId()
        binding.root.isEnabled = false
    }

    private fun setObservers() {
        val dialogBinding = ProgressDialogBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setCancelable(false)
            .setView(dialogBinding.root)
            .create()
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

        viewModel.isWaiting.observe(viewLifecycleOwner) {
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

        viewModel.profile.observe(viewLifecycleOwner) {
            setData(it)
        }

        viewModel.profileId.observe(viewLifecycleOwner) {
            viewModel.getProfileById(it)
        }
    }

    private fun setData(profile: ProfileModel) {
        with(binding) {
            confidentialityLayout.visibility = View.GONE
            textBlock.text = "Вы можете редактироваь информацию о с себе"
            etName.setText(profile.firstName)
            etRoom.setText(profile.room.toString())
            etInterests.setText(profile.interests)
            etPatronymic.setText(profile.patronymic)
            etSurname.setText(profile.surname)
            etContactInfo.setText(profile.contactInfo)
            etGroupNumber.setText(profile.groupNumber.toString())
            Glide.with(requireContext())
                .load(Uri.parse(PrefsManager(requireContext()).getPhoto()))
                .placeholder(R.drawable.photo_placeholder)
                .into(ivAvatar)
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
                            viewModel.profileId.value!!,
                            deviceId,
                            true
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
                            0,
                            deviceId,
                            true
                        )
                    )
                    viewModel.isLoading.value = true
                }
            }
            ivAvatar.setOnClickListener {
                selectPhotoLauncher.launch(Intent(Intent.ACTION_GET_CONTENT).apply {
                    type = "image/*"
                })
            }
            tvPolicyOfOnfidentiality.setOnClickListener {
                val url = "https://www.4shared.com/web/preview/pdf/7ghfLJdZjq?"
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
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

    private fun getFirebaseToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("Token", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            val token = task.result
            Log.d("Token", token)
            deviceId = token
        })
    }


    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {  // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                Log.d("MainFragment", "askNotificationPermission: sadlfjajlsdf")
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            if (PrefsManager(requireContext()).getProfile().deviceId == "") {
                getFirebaseToken()
            }
        }
    }
}