package com.example.travelapp.fragment

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.travelapp.R
import com.example.travelapp.SelectMapActivity
import com.example.travelapp.User1.Use
import com.example.travelapp.databinding.AddTravelDiaryFragmentBinding
import com.example.travelapp.entity.DiaryEntry
import com.example.travelapp.viewmodel.DiaryViewModel
import java.text.SimpleDateFormat
import java.util.*

class AddTravelDiaryFragment : Fragment() {
    var titleEditText: EditText? = null
    var descriptionEditText: EditText? = null
    var locationEditText: EditText? = null
    var expenseEditText: EditText? = null
    var datePicker: DatePicker? = null

    var satisfactionSeekBar: SeekBar? = null
    private var satisfactionScoreTextView: TextView? = null
    var weatherRadioGroup: RadioGroup? = null
    private var binding: AddTravelDiaryFragmentBinding? = null
    var diaryViewModel: DiaryViewModel? = null

    // Test hooks
    var isSaveButtonClicked: Boolean = false
    var isMapButtonClicked: Boolean = false
    var isClearButtonClicked: Boolean = false
    var isDatePickerUsed: Boolean = false // Added flag for date picker usage
    var isWeatherRadioButtonChecked: Boolean = false // Added flag for weather radio button check

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AddTravelDiaryFragmentBinding.inflate(inflater, container, false)
        val view: View = binding!!.root

        titleEditText = binding!!.titleEdittext
        descriptionEditText = binding!!.descriptionEdittext
        locationEditText = binding!!.locationEdittext
        expenseEditText = binding!!.expenseEdittext
        datePicker = binding!!.datePicker
        satisfactionSeekBar = binding!!.satisfactionSlider
        satisfactionScoreTextView = binding!!.satisfactionScoreTextview
        weatherRadioGroup = binding!!.weatherRadiogroup
        satisfactionSeekBar!!.max = 10
        satisfactionSeekBar!!.progress = 5
        satisfactionScoreTextView!!.text = "5"
        satisfactionSeekBar!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                satisfactionScoreTextView!!.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        diaryViewModel = ViewModelProvider(requireActivity()).get(DiaryViewModel::class.java)

        val saveButton: Button = binding!!.saveButton
        saveButton.setOnClickListener {
            onSaveButtonClick()
            isSaveButtonClicked = true
        }

        binding!!.mapbutton.setOnClickListener {
            val location = locationEditText!!.text.toString()
            if (location == "") {
                Toast.makeText(context, "Type any location", Toast.LENGTH_SHORT).show()
            } else {
                val preferences =
                    requireContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
                val editor = preferences.edit()
                editor.putString("location", location)
                editor.apply()
                val intent = Intent(context, SelectMapActivity::class.java)
                startActivity(intent)
                isMapButtonClicked = true
            }
        }

        binding!!.clearButton.setOnClickListener {
            titleEditText!!.setText("")
            descriptionEditText!!.setText("")
            locationEditText!!.setText("")
            expenseEditText!!.setText("")
            satisfactionSeekBar!!.progress = 5
            satisfactionScoreTextView!!.text = "5"
            val sunnyRadioButton: RadioButton = binding!!.sunnyRadiobutton
            weatherRadioGroup!!.check(sunnyRadioButton.id)
            isClearButtonClicked = true
        }

        diaryViewModel!!.allDiary.observe(viewLifecycleOwner, Observer { customers ->
            var allCustomers = ""
            for (temp in customers) {
                val customerDetails = "${temp.title} ${temp.date} ${temp.weather} ${temp.location}"
                allCustomers = allCustomers +
                        System.getProperty("line.separator") + customerDetails
            }
        })

        // Add a listener for the date picker
        datePicker!!.setOnDateChangedListener { _, _, _, _ ->
            isDatePickerUsed = true
        }

        // Add a listener for the radio buttons to track weather selection
        weatherRadioGroup!!.setOnCheckedChangeListener { _, _ ->
            isWeatherRadioButtonChecked = true
        }

        return view
    }

    fun onSaveButtonClick() {
        if (TextUtils.isEmpty(titleEditText!!.text)) {
            titleEditText!!.error = "Title is required."
            toastMsg("Title is required")
            return
        }

        if (TextUtils.isEmpty(expenseEditText!!.text)) {
            expenseEditText!!.error = "Expense is required."
            toastMsg("Expense is required.")
            return
        }

        if (TextUtils.isEmpty(descriptionEditText!!.text)) {
            descriptionEditText!!.error = "Description is required."
            toastMsg("Description is required.")
            return
        }

        if (TextUtils.isEmpty(locationEditText!!.text)) {
            locationEditText!!.error = "Location is required."
            toastMsg("Location is required.")
            return
        }

        if (weatherRadioGroup!!.checkedRadioButtonId == -1) {
            Toast.makeText(context, "Please select weather.", Toast.LENGTH_SHORT).show()
            return
        }

        val title = titleEditText!!.text.toString().trim()
        val description = descriptionEditText!!.text.toString().trim()
        val location = locationEditText!!.text.toString().trim()
        val expense = expenseEditText!!.text.toString().toInt()
        val satisfaction = satisfactionSeekBar!!.progress
        val weatherId = weatherRadioGroup!!.checkedRadioButtonId
        val weatherRadioButton = view?.findViewById<RadioButton>(weatherId)
        val weather = weatherRadioButton?.text.toString()
        val day = datePicker!!.dayOfMonth
        val month = datePicker!!.month
        val year = datePicker!!.year
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        val date = calendar.time
        val sdf = SimpleDateFormat("MM/dd/yyyy")
        val dateString = sdf.format(date)

        val imageUrl = "https://source.unsplash.com/600x400/?$location,travel" // or any logic you prefer

        val newDiary = DiaryEntry(
            userEmail = Use.getUserEmail() ?: "",
            title = title,
            date = dateString,
            description = description,
            weather = weather,
            location = location,
            fee = expense,
            rating = satisfaction,
            imageUrl = imageUrl
        )

        diaryViewModel!!.insert(newDiary)
        toastMsg("Add successful")

        titleEditText!!.setText("")
        descriptionEditText!!.setText("")
        locationEditText!!.setText("")
        expenseEditText!!.setText("")
        satisfactionSeekBar!!.progress = 5
        satisfactionScoreTextView!!.text = "5"
        val sunnyRadioButton: RadioButton = binding!!.sunnyRadiobutton
        weatherRadioGroup!!.check(sunnyRadioButton.id)

    }

    private fun toastMsg(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
