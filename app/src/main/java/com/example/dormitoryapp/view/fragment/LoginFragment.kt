package com.example.dormitoryapp.view.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.dormitoryapp.R
import com.example.dormitoryapp.databinding.FragmentLoginBinding
import com.example.dormitoryapp.model.dto.LoginModel
import com.example.dormitoryapp.utils.SendCodeStatus
import com.example.dormitoryapp.utils.Utils
import com.example.dormitoryapp.viewmodel.LoginViewModel

class LoginFragment : Fragment() {

    private val binding: FragmentLoginBinding by lazy {
        FragmentLoginBinding.inflate(layoutInflater)
    }

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyClicks()
        applyButtonNext()
        setObservers()
    }

    private fun applyClicks() {
        binding.btnNext.setOnClickListener {
            viewModel.sendCode(LoginModel(binding.etTelegramNick.text.toString()))
            viewModel.isLoading.value = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.clearSendCodeStatus()
    }

    private fun setObservers() {
        val dialog = ProgressDialog(requireContext())
        dialog.setCancelable(false)
        dialog.setMessage("Ждём-ждём")
        viewModel.sendCodeStatus.observe(viewLifecycleOwner) {
            val message = viewModel.responseMessage.value?.value?.message
            when (it) {
                SendCodeStatus.SUCCESS -> {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    findNavController().navigate(R.id.action_loginFragment_to_codeFragment)
                    viewModel.clearSendCodeStatus()
                }
                SendCodeStatus.FAIL -> {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
                SendCodeStatus.NOTHING -> {}
            }
        }
        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                dialog.show()
            } else {
                dialog.dismiss()
            }
        }
    }

    private fun applyButtonNext() {
        binding.etTelegramNick.doOnTextChanged { text, _, _, _ ->
            if (Utils.isEmailValid(text!!.toString())) {
                binding.btnNext.apply {
                    setBackgroundColor(resources.getColor(R.color.accent, null))
                    isEnabled = true
                }
            } else {
                binding.btnNext.apply {
                    setBackgroundColor(resources.getColor(R.color.inactive_button, null))
                    isEnabled = false
                }
            }
        }
    }
}