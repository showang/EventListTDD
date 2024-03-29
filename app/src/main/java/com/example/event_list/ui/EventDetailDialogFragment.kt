package com.example.event_list.ui

import android.annotation.TargetApi
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.example.event_list.R
import com.example.event_list.model.Category
import com.example.event_list.model.EventItem
import com.example.event_list.presenter.EventPresenter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import github.showang.kat.assign
import github.showang.kat.extra
import kotlinx.android.synthetic.main.fragment_event_detail.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*

class EventDetailDialogFragment : BottomSheetDialogFragment() {

    companion object {
        private const val INPUT_STRING_EDIT_ID = "01"

        fun createInstance(id: String? = null) = EventDetailDialogFragment().apply {
            arguments = Bundle().apply {
                putString(INPUT_STRING_EDIT_ID, id ?: "")
            }
        }
    }

    private val editItemId: String by extra(INPUT_STRING_EDIT_ID, "")

    private var editingItem: EventItem? = null
    private var currentStartDate: Long = System.currentTimeMillis()
    private var currentEndDate: Long = System.currentTimeMillis()
    private lateinit var currentCategory: Category

    private val presenter: EventPresenter by inject()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return context?.let { BottomSheetDialog(it) } ?: super.onCreateDialog(savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentCategory = presenter.categories.first()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_event_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CoroutineScope(Main).launch {
            if (editItemId.isEmpty()) {
                categoryEditText.setText(currentCategory.name)
                confirmButton.setOnClickListener(onCreateEventClick)
            } else {
                withContext(IO) { editingItem = presenter.eventList.findLast { event -> event.id == editItemId } }
                editingItem?.let {
                    categoryEditText.setText(it.category.name)
                    titleEditText.setText(it.title)
                    descEditText.setText(it.desc)
                    currentStartDate = it.startDate.time
                    currentEndDate = it.endDate.time
                    confirmButton.apply {
                        text = getString(R.string.button_updateEvent)
                        setOnClickListener(onUpdateEventClick)
                    }
                } ?: run {
                    Toast.makeText(context, "Event not found", Toast.LENGTH_LONG).show()
                    dismiss()
                }
            }
            categoryEditText.setOnClickListener { view ->
                AlertDialog.Builder(view.context)
                    .setItems(presenter.categories.map { it.name }.let {
                        it.toMutableList().apply { add("(Create New Category)") }
                    }.toTypedArray()) { _, which ->
                        if (which < presenter.categories.size) {
                            currentCategory = presenter.categories[which]
                            categoryEditText.setText(currentCategory.name)
                        } else {
                            showAddNewCategoryDialog()
                        }
                    }.create().show()
            }
            initDateLayouts()
        }
    }

    private fun showAddNewCategoryDialog() {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_category, view as ViewGroup, false)
        val editText: EditText = dialogView.findViewById(R.id.newCategoryNameEditText)
        val dialog = AlertDialog.Builder(requireContext()).setView(dialogView)
            .create()
        dialogView.findViewById<Button>(R.id.createCategoryButton).setOnClickListener {
            if (editText.text.isEmpty()) {
                editText.error = "Name can not be empty."
            } else {
                Category(editText.text.toString()).apply(presenter::addCategory)
                    .assign(::currentCategory)
                dialog.dismiss()
                categoryEditText.setText(currentCategory.name)
            }
        }
        dialog.show()
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun initDateLayouts() {
        updateDateLayouts()
        startDateText.apply {
            setOnClickListener {
                DatePickerDialog(it.context).apply {
                    setOnDateSetListener { _, year, month, dayOfMonth ->
                        Calendar.getInstance().apply {
                            set(year, month, dayOfMonth)
                        }.time.time.apply {
                            if (currentEndDate < this) {
                                Toast.makeText(
                                    requireContext(),
                                    R.string.error_toast_end_date_must_after_start,
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                currentStartDate = this
                            }
                        }
                        updateDateLayouts()
                    }
                }.show()
            }
        }
        endDateText.apply {
            setOnClickListener {
                DatePickerDialog(it.context).apply {
                    setOnDateSetListener { _, year, month, dayOfMonth ->
                        Calendar.getInstance().apply {
                            set(year, month, dayOfMonth)
                        }.time.time.apply {
                            if (this < currentStartDate) {
                                Toast.makeText(
                                    requireContext(),
                                    R.string.error_toast_end_date_must_after_start,
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                currentEndDate = this
                            }
                        }
                        updateDateLayouts()
                    }
                }.show()
            }
        }
    }

    private fun checkInputLength(): Boolean {
        var isPass = true
        val titleLength = titleEditText.text.length
        titleEditText.error = when {
            titleLength > 50 -> getString(R.string.error_text_overflow, "title", 50)
            titleLength == 0 -> getString(R.string.error_text_empty, "title")
            else -> null
        }?.also { isPass = false }

        val descLength = descEditText.text.length
        descEditText.error = when {
            descLength > 200 -> getString(R.string.error_text_overflow, "description", 200)
            descLength == 0 -> getString(R.string.error_text_empty, "description")
            else -> null
        }?.also { isPass = false }

        if (currentEndDate < currentStartDate) {
            isPass = false
            Toast.makeText(requireContext(), R.string.error_toast_end_date_must_after_start, Toast.LENGTH_LONG).show()
        }
        return isPass
    }

    private fun updateDateLayouts() {
        startDateText.text = getFormattedDateString(R.string.label_startDate, currentStartDate)
        endDateText.text = getFormattedDateString(R.string.label_endDate, currentEndDate)
    }

    private fun getFormattedDateString(@StringRes id: Int, time: Long) =
        getString(id, SimpleDateFormat.getDateInstance().format(Date(time)))

    private val onCreateEventClick = { _: View ->
        if (checkInputLength()) {
            presenter.append(
                EventItem(
                    System.currentTimeMillis().toString(),
                    titleEditText.text.toString(),
                    descEditText.text.toString(),
                    Date(currentStartDate),
                    Date(currentEndDate),
                    currentCategory
                )
            )
            dismiss()
        }
    }

    private val onUpdateEventClick = { _: View ->
        if (checkInputLength()) {
            editingItem?.apply {
                title = titleEditText.text.toString()
                desc = descEditText.text.toString()
                startDate = Date(currentStartDate)
                endDate = Date(currentEndDate)
                category = currentCategory
            }?.let(presenter::update)
            dismiss()
        }
    }

}