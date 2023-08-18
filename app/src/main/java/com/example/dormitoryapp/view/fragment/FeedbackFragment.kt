package com.example.dormitoryapp.view.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.dormitoryapp.databinding.FragmentFeedbackBinding
import com.example.dormitoryapp.model.dto.FeedbackModel
import com.example.dormitoryapp.utils.FeedbackStatus
import com.example.dormitoryapp.viewmodel.FeedbackViewModel

class FeedbackFragment : Fragment() {

    private val binding: FragmentFeedbackBinding by lazy {
        FragmentFeedbackBinding.inflate(layoutInflater)
    }

    private val viewModel: FeedbackViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
        applyClicks()
        binding.textView5.setOnLongClickListener {
            with(binding) {
                etAuthor.setText("Анонимный студент")
                etComment.setText("Спасибо большое за ваш сервис. Он очень помог мне в преодолении соцальной неловкости")
            }
            true
        }

        binding.textView5.setOnClickListener {
            with(binding) {
                etAuthor.setText("Анонимный студент")
                etComment.setText("Спасибо большое за ваш сервис. Он очень помог мне в преодолении соцальной неловкости")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.clearStatus()
    }


    private fun setObservers() {

        val dialog = ProgressDialog(requireContext())
        dialog.setCancelable(false)
        dialog.setMessage("Ждём-ждём")
        viewModel.status.observe(viewLifecycleOwner) {
            when (it) {
                FeedbackStatus.SUCCESS -> {
                    with(binding) {
                        etAuthor.setText("")
                        etComment.setText("")
                        etComment.clearFocus()
                        etAuthor.clearFocus()
                    }
                    Toast.makeText(
                        requireContext(),
                        viewModel.feedbackResponse.value?.value?.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                FeedbackStatus.FAIL -> {

                }

                FeedbackStatus.NOTHING -> {

                }
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

    private fun applyClicks() {
        with(binding) {
            btnSendFeedback.setOnClickListener {
                viewModel.sendFeedBack(
                    FeedbackModel(
                        etAuthor.text.toString(),
                        etComment.text.toString() + " Оценка: ${ratingBar.rating.toInt()}"
                    )
                )
            }
        }
    }
}