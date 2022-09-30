package android.example.health.fragments.healthTracker

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.example.health.R
import android.example.health.utils.LocalPersistenceManager
import android.example.health.utils.UtilsFragmentTransaction
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit


class AddSleepDataFragment : Fragment() {

    private lateinit var sleepStartDay : TextView
    private lateinit var sleepStartTime : TextView
    private lateinit var sleepStopTime : TextView
    private lateinit var sleepStopDay: TextView
    private lateinit var sleepSave: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.add_sleep_data_fragment, container, false)

    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sleepStartDay = requireView().findViewById(R.id.sleep_start_day) as TextView
        sleepStartTime = requireView().findViewById(R.id.sleep_start_time) as TextView
        sleepStopTime = requireView().findViewById(R.id.sleep_stop_time) as TextView
        sleepStopDay = requireView().findViewById(R.id.sleep_stop_day) as TextView
        sleepSave = requireView().findViewById(R.id.sleep_save) as Button
        sleepStopDay.setOnClickListener {
            val mcurrentDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            val year = mcurrentDate[Calendar.YEAR]
            val month = mcurrentDate[Calendar.MONTH]
            val day = mcurrentDate[Calendar.DAY_OF_MONTH]
            val mDatePicker = DatePickerDialog(
                requireContext(),
                { datepicker, selectedYear, selectedMonth, selectedDay -> sleepStopDay.text =
                    selectedYear.toString() + "-" + (selectedMonth + 1) + "-" + selectedDay },
                year,
                month,
                day
            )
            mDatePicker.setTitle("Select date")
            mDatePicker.show()
            val minDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            minDate[Calendar.DAY_OF_MONTH] = day
            minDate[Calendar.MONTH] = month
            minDate[Calendar.YEAR] = year
            mDatePicker.datePicker.maxDate = minDate.timeInMillis
            mDatePicker.datePicker.minDate = minDate.timeInMillis
        }

        sleepStartDay.setOnClickListener {
            val mcurrentDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            val year = mcurrentDate[Calendar.YEAR]
            val month = mcurrentDate[Calendar.MONTH]
            val day = mcurrentDate[Calendar.DAY_OF_MONTH]
            val mDatePicker = DatePickerDialog(
                requireContext(),
                { datepicker, selectedYear, selectedMonth, selectedDay -> sleepStartDay.text =
                    selectedYear.toString() + "-" + (selectedMonth + 1) + "-" + selectedDay },
                year,
                month,
                day
            )
            mDatePicker.setTitle("Select date")
            mDatePicker.show()
            val maxDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            maxDate[Calendar.DAY_OF_MONTH] = day
            maxDate[Calendar.MONTH] = month
            maxDate[Calendar.YEAR] = year
            val c = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            c.add(Calendar.DAY_OF_MONTH, -1)
            mDatePicker.datePicker.maxDate = maxDate.timeInMillis
            mDatePicker.datePicker.minDate = c.timeInMillis
        }

        sleepStopTime.setOnClickListener {
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
            val minute = mcurrentTime[Calendar.MINUTE]
            val mTimePicker = TimePickerDialog(context,
                { _, selectedHour, selectedMinute -> sleepStopTime.text = "$selectedHour:$selectedMinute" },
                hour,
                minute,
                true
            ) //Yes 24 hour time

            mTimePicker.setTitle("Select Time")
            mTimePicker.show()
        }

        sleepStartTime.setOnClickListener {
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
            val minute = mcurrentTime[Calendar.MINUTE]
            val mTimePicker = TimePickerDialog(context,
                { _, selectedHour, selectedMinute -> sleepStartTime.text = "$selectedHour:$selectedMinute" },
                hour,
                minute,
                true
            ) //Yes 24 hour time

            mTimePicker.setTitle("Select Time")
            mTimePicker.show()
        }

        sleepSave.setOnClickListener {

            val startSleepData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dateAndTimeToMillis(sleepStartDay.text.toString() + " " + sleepStartTime.text.toString())
            } else {
                TODO("VERSION.SDK_INT < O")
            }

            val stopSleepData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dateAndTimeToMillis(sleepStopDay.text.toString() + " " + sleepStopTime.text.toString())
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            if(startSleepData >= stopSleepData){
                Toast.makeText(context, "Invalid sleep data", Toast.LENGTH_SHORT).show()
            }
            else{

                LocalPersistenceManager.getLocalPersistenceManager().startSleepData = startSleepData.toString()
                LocalPersistenceManager.getLocalPersistenceManager().sleepData = stopSleepData - startSleepData

                LocalPersistenceManager.getLocalPersistenceManager().todaySleepData += TimeUnit.MILLISECONDS.toHours(stopSleepData - startSleepData)
                UtilsFragmentTransaction.addFragmentTransaction(
                    R.id.fragment_container,
                    activity, HealthFragment(),
                    HealthFragment::class.java.simpleName, null
                )

            }
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun dateAndTimeToMillis(date: String): Long{
        val formatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-M-dd HH:mm", Locale.ENGLISH)
        val localDate: LocalDateTime = LocalDateTime.parse(date, formatter)
        return localDate.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()

    }

}