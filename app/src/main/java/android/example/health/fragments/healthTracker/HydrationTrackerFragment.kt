package android.example.health.fragments.healthTracker

import android.example.health.R
import android.example.health.common.logger.Log
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataPoint
import com.google.android.gms.fitness.data.DataSource
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field.FIELD_VOLUME
import com.google.android.gms.fitness.request.DataReadRequest
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

class HydrationTrackerFragment : Fragment() {

    var rootView: View? = null
    private lateinit var manualTrack : EditText
    private lateinit var saveButton : Button
    private lateinit var readButton : Button
    private val fitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.TYPE_HYDRATION)
        .build()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.health_tracker_hydration_tracker_fragment, container, false)

    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rootView = view
        manualTrack = rootView!!.findViewById(R.id.manual_track) as EditText
        saveButton = rootView!!.findViewById(R.id.save_button) as Button
        readButton = rootView!!.findViewById(R.id.read_button) as Button

        val hydrationSource = DataSource.Builder()
            .setDataType(DataType.TYPE_HYDRATION)
            .setType(DataSource.TYPE_DERIVED)
            .setStreamName("hydrationSource")
            .build()

        val currentTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now().atZone(ZoneId.systemDefault())
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        saveButton.setOnClickListener (View.OnClickListener {
            var value = manualTrack.text.toString()
            val hydration = DataPoint.builder(hydrationSource)
                .setTimestamp(currentTime.toEpochSecond(), TimeUnit.MILLISECONDS)
                .setField(FIELD_VOLUME, value.toFloat()/1000)
                .build()
        })

        readButton.setOnClickListener (View.OnClickListener {
            val endTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDateTime.now().atZone(ZoneId.systemDefault())

            } else {
                TODO("VERSION.SDK_INT < O")
            }
            val startTime = endTime.minusDays(1)

            val readRequest = DataReadRequest.Builder()
                .aggregate(DataType.AGGREGATE_HYDRATION)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime.toEpochSecond(), endTime.toEpochSecond(), TimeUnit.MILLISECONDS)
                .build()

            Fitness.getHistoryClient(
                requireActivity(),
                GoogleSignIn.getAccountForExtension(requireActivity(), fitnessOptions)
            )
                .readData(readRequest)
                .addOnSuccessListener { response ->
                    Log.v("Ishita", response.toString())
                }
        })






    }


}