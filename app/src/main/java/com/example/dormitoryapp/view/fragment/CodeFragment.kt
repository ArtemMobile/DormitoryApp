package com.example.dormitoryapp.view.fragment

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.dormitoryapp.R
import com.example.dormitoryapp.databinding.FragmentCodeBinding
import com.example.dormitoryapp.utils.GenericKeyEvent
import com.example.dormitoryapp.utils.GenericTextWatcher


class CodeFragment : Fragment() {


    private val binding: FragmentCodeBinding by lazy{
        FragmentCodeBinding.inflate(layoutInflater)
    }

    private val timer = object : CountDownTimer(60000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            binding.tvCountDown.text =
                "Отправить код повторно можно\nбудет через ${millisUntilFinished / 1000} секунд"
        }

        override fun onFinish() {
            this.cancel()
            Toast.makeText(requireContext(), "Код отправлен повторно", Toast.LENGTH_SHORT).show()
            this.start()
//            viewModel.sendCode(viewModel.email.value ?: "")
        }
    }


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
        setUpEditors()
        timer.start()
    }

    private fun applyClicks(){
        with(binding){
            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun setUpEditors() {
        with(binding) {
            etCode1.addTextChangedListener(GenericTextWatcher(etCode1, etCode2))
            etCode2.addTextChangedListener(GenericTextWatcher(etCode2, etCode3))
            etCode3.addTextChangedListener(GenericTextWatcher(etCode3, etCode4))
            etCode4.addTextChangedListener(GenericTextWatcher(
                etCode4,
                null,
                onLastEditTextFilled = {
                    etCode4.clearFocus()
                    sendRequest()
                    val imm =
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    view?.let {
                        imm.hideSoftInputFromWindow(it.windowToken, 0)
                    }
                    timer.cancel()
                    findNavController().navigate(R.id.action_codeFragment_to_passwordFragment)
                }
            ))

            etCode1.setOnKeyListener(GenericKeyEvent(etCode1, null))
            etCode2.setOnKeyListener(GenericKeyEvent(etCode2, etCode1))
            etCode3.setOnKeyListener(GenericKeyEvent(etCode3, etCode2))
            etCode4.setOnKeyListener(GenericKeyEvent(etCode4, etCode3))
        }
    }

    private fun sendRequest() {
        with(binding) {
            val password = "${etCode1.text}${etCode2.text}${etCode3.text}${etCode4.text}"
//            viewModel.signIn(viewModel.email.value ?: "", password)
        }
    }

}