package com.example.dormitoryapp.view.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isEmpty
import androidx.core.view.iterator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.dormitoryapp.R
import com.example.dormitoryapp.databinding.FilterChipBinding
import com.example.dormitoryapp.databinding.FragmentCreatePostBinding
import com.example.dormitoryapp.model.dto.CreatePostModel
import com.example.dormitoryapp.model.dto.PostModel
import com.example.dormitoryapp.model.dto.PostTypeModel
import com.example.dormitoryapp.utils.CreatePostStatus
import com.example.dormitoryapp.utils.PrefsManager
import com.example.dormitoryapp.viewmodel.PostViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@SuppressLint("SetTextI18n", "SimpleDateFormat")
class CreatePostFragment : Fragment() {

    private val binding: FragmentCreatePostBinding by lazy {
        FragmentCreatePostBinding.inflate(layoutInflater)
    }

    private val viewModel: PostViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    private val expireCalendar = GregorianCalendar.getInstance()
    private val notificationCalendar = GregorianCalendar.getInstance()
    private var post: PostModel? = null

    private val expireDateSetListener: DatePickerDialog.OnDateSetListener by lazy {
        DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            expireCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            expireCalendar.set(Calendar.MONTH, month)
            expireCalendar.set(Calendar.YEAR, year)
            TimePickerDialog(
                requireContext(),
                R.style.ColorPickerTheme,
                { _, hourOfDay, minute ->
                    val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm")
                    expireCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    expireCalendar.set(Calendar.MINUTE, minute)
                    binding.etExpireDate.text = sdf.format(expireCalendar.time)
                },
                expireCalendar.get(Calendar.HOUR_OF_DAY),
                expireCalendar.get(Calendar.MINUTE),
                true
            ).apply {
                setTitle("Выберите время")
                show()
                getButton(DatePickerDialog.BUTTON_NEGATIVE).apply {
                    text = "Отмена"
                    isAllCaps = false
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.accent))
                }
                getButton(DatePickerDialog.BUTTON_POSITIVE).apply {
                    text = "Выбрать"
                    isAllCaps = false
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.accent))
                }
            }
        }
    }

    private val notificationDateSetListener: DatePickerDialog.OnDateSetListener by lazy {
        DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            notificationCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            notificationCalendar.set(Calendar.MONTH, month)
            notificationCalendar.set(Calendar.YEAR, year)
            TimePickerDialog(
                requireContext(),
                R.style.ColorPickerTheme,
                { _, hourOfDay, minute ->
                    val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm")
                    notificationCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    notificationCalendar.set(Calendar.MINUTE, minute)
                    binding.etNotificationDate.text = sdf.format(notificationCalendar.time)
                },
                notificationCalendar.get(Calendar.HOUR_OF_DAY),
                notificationCalendar.get(Calendar.MINUTE),
                true
            ).apply {
                setTitle("Выберите время уведомления")
                show()
                getButton(DatePickerDialog.BUTTON_NEGATIVE).apply {
                    text = "Отмена"
                    isAllCaps = false
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.accent))
                }
                getButton(DatePickerDialog.BUTTON_POSITIVE).apply {
                    text = "Выбрать"
                    isAllCaps = false
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.accent))
                }
            }.show()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyClicks()
        setObservers()
        applyFields()
        applySpinner()
        post = arguments?.getSerializable("post") as? PostModel
        if (post != null) {
            setData(post!!)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setData(postModel: PostModel) {
        with(binding) {
            tvCreatePost.text = "Редактирование записи"
            btnCreatePost.text = "Редактировать"
            etTitle.setText(postModel.title)
            etDescription.setText(postModel.description)
            val expireDate = LocalDateTime.parse(postModel.expireDate)
            postModel.notificationDate?.let {
                val notificationDate = LocalDateTime.parse(it)
                notificationDate?.let {
                    etNotificationDate.setText(
                        DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").format(it)
                    )
                }
            }
            etExpireDate.setText(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").format(expireDate))
            cbPayable.isChecked = postModel.isPayable
            etTitle.setText(postModel.title)
            chipGroup.check(postModel.idType)
            btnDeletePost.visibility = View.VISIBLE
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun showClearTextDialog(view: View) {
        val dialog = AlertDialog.Builder(requireContext(), R.style.ColorPickerTheme)
            .setTitle("Очистка даты")
            .setMessage("Вы точно хотите очистить дату?")
            .setPositiveButton("Да") { _, _ -> (view as TextView).text = "" }
            .setNegativeButton("Нет") { _, _ -> }
            .create()
        dialog.show()

        val posBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        posBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.accent))
        posBtn.isAllCaps = false

        val negBtn = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        negBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.accent))
        negBtn.isAllCaps = false


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun applyClicks() {
        with(binding) {
            etExpireDate.setOnLongClickListener {
                showClearTextDialog(it)
                true
            }

            etNotificationDate.setOnLongClickListener {
                showClearTextDialog(it)
                true
            }

            etExpireDate.setOnClickListener {
                showExpireDatePicker()
            }

            etNotificationDate.setOnClickListener {
                showNotificationTimePicker()
            }

            btnCreatePost.setOnClickListener {
                val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

                val expireDate =
                    LocalDateTime.parse(binding.etExpireDate.text.toString(), formatter)
                var notificationDate : LocalDateTime? = null
                if(binding.etNotificationDate.text.toString().isNotEmpty() || binding.etNotificationDate.text.toString().isNotBlank()){
                    notificationDate =
                        LocalDateTime.parse(binding.etNotificationDate.text.toString(), formatter)
                }
                if (datesAreValid()) {
                    if (post != null) {
                        viewModel.updatePost(
                            CreatePostModel(
                                etTitle.text.toString(),
                                etDescription.text.toString(),
                                binding.chipGroup.checkedChipId,
                                PrefsManager(requireContext()).getProfile().id,
                                notificationDate?.toString() ?: LocalDate.now().toString(),
                                cbPayable.isChecked,
                                expireDate.toString()
                            ),
                            post!!.id
                        )
                    } else {
                        viewModel.createPost(
                            CreatePostModel(
                                etTitle.text.toString(),
                                etDescription.text.toString(),
                                binding.chipGroup.checkedChipId,
                                PrefsManager(requireContext()).getProfile().id,
                                notificationDate?.toString() ?: LocalDate.now().toString(),
                                cbPayable.isChecked,
                                expireDate.toString()
                            )
                        )
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Проверьте правильность указанны дат",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }
    }

    private fun setObservers() {
        viewModel.createPostResponse.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it.value.message, Toast.LENGTH_SHORT).show()
        }

        viewModel.createPostStatus.observe(viewLifecycleOwner) {
            when (it) {
                CreatePostStatus.SUCCESS -> {
                    findNavController().popBackStack()
                    viewModel.clearCreatePostStatus()
                }
                CreatePostStatus.FAILURE -> {

                }
                CreatePostStatus.NOTHING -> {

                }
            }
        }

        viewModel.updatePostResponse.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it.value.message, Toast.LENGTH_SHORT).show()
        }

        viewModel.updatePostStatus.observe(viewLifecycleOwner) {
            when (it) {
                CreatePostStatus.SUCCESS -> {
                    findNavController().popBackStack()
                    viewModel.clearUpdatePostStatus()
                }
                CreatePostStatus.FAILURE -> {

                }
                CreatePostStatus.NOTHING -> {

                }
            }
        }
    }

    private fun showExpireDatePicker() {
        val dialog = DatePickerDialog(
            requireContext(),
            R.style.ColorPickerTheme,
            expireDateSetListener,
            expireCalendar.get(Calendar.YEAR),
            expireCalendar.get(Calendar.MONTH),
            expireCalendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            setTitle("Выберите дату потери актуальности")
            datePicker.minDate = System.currentTimeMillis()
        }
        dialog.show()
        dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).apply {
            text = "Отмена"
            isAllCaps = false
            setTextColor(ContextCompat.getColor(requireContext(), R.color.accent))
        }
        dialog.getButton(DatePickerDialog.BUTTON_POSITIVE).apply {
            text = "Выбрать"
            isAllCaps = false
            setTextColor(ContextCompat.getColor(requireContext(), R.color.accent))
        }
    }

    private fun showNotificationTimePicker() {
        val dialog = DatePickerDialog(
            requireContext(),
            R.style.ColorPickerTheme,
            notificationDateSetListener,
            notificationCalendar.get(Calendar.YEAR),
            notificationCalendar.get(Calendar.MONTH),
            notificationCalendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            setTitle("Выберите дату уведомления")
            datePicker.minDate = System.currentTimeMillis()
        }
        dialog.show()
        dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).apply {
            text = "Отмена"
            isAllCaps = false
            setTextColor(ContextCompat.getColor(requireContext(), R.color.accent))
        }
        dialog.getButton(DatePickerDialog.BUTTON_POSITIVE).apply {
            text = "Выбрать"
            isAllCaps = false
            setTextColor(ContextCompat.getColor(requireContext(), R.color.accent))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun datesAreValid(): Boolean {
        if (binding.etNotificationDate.text.toString().isNotEmpty()) {
            val notificationDate = LocalDateTime.parse(binding.etNotificationDate.text.toString(),
                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
            val expireDate = LocalDateTime.parse(binding.etExpireDate.text.toString(),
                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
            Log.d("DATE", notificationCalendar.time.toString())
            return  (expireDate.isAfter(notificationDate) || expireDate.isEqual(notificationDate))
                    && binding.etNotificationDate.text.toString()
                .isNotEmpty() && binding.etExpireDate.text.toString().isNotEmpty()
        } else {
            val expireDate = LocalDateTime.parse(binding.etExpireDate.text.toString(),
                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
            return expireDate.isAfter(LocalDateTime.now())
                    && binding.etExpireDate.text.toString().isNotEmpty()
        }
    }

    private fun applyFields() {
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
            val fieldsNoEmpty = etTitle.text.toString()
                .isNotEmpty() && etDescription.text.toString()
                .isNotEmpty() && etExpireDate.text.toString()
                .isNotEmpty() && chipGroup.checkedChipIds.size != 0

            if (fieldsNoEmpty) {
                btnCreatePost.isEnabled = true
                btnCreatePost.setBackgroundColor(
                    resources.getColor(
                        R.color.accent,
                        null
                    )
                )
            } else {
                btnCreatePost.isEnabled = false
                btnCreatePost.setBackgroundColor(
                    resources.getColor(
                        R.color.inactive_button,
                        null
                    )
                )
            }
        }
    }

    private fun applySpinner() {
        val types = Gson().fromJson(
            arguments?.getString("types", "[ ]"),
            object : TypeToken<List<PostTypeModel>>() {}.type
        ) as? List<PostTypeModel>
        if (binding.chipGroup.isEmpty() && types?.size != 0) {
            types?.forEachIndexed { _, category ->
                val chip =
                    FilterChipBinding.inflate(layoutInflater).rootChip.apply {
                        text = category.name
                    }
                binding.chipGroup.addView(chip.apply {
                    id = category.id
                })
            }
            binding.chipGroup.check(binding.chipGroup.getChildAt(0).id)
        }
    }
}