package android.example.health.fragments.healthTracker

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.example.health.BuildConfig
import android.example.health.Constants.GlobalData
import android.example.health.R
import android.example.health.common.logger.Log.clear
import android.example.health.fragments.medicineBox.MyMedicinesFragment
import android.example.health.utils.LocalPersistenceManager
import android.example.health.utils.UtilsFragmentTransaction
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessActivities
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.*
import com.google.android.gms.fitness.request.SessionInsertRequest
import com.google.android.gms.fitness.request.SessionReadRequest
import com.google.android.gms.fitness.result.SessionReadResponse
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.math.E


enum class FitActionRequestCodes {
    INSERT_SLEEP_SESSIONS,
    READ_SLEEP_SESSIONS
}

const val TAGS = "SleepKotlin"

val SLEEP_STAGES = arrayOf(
    "Unused",
    "Awake (during sleep)",
    "Sleep",
    "Out-of-bed",
    "Light sleep",
    "Deep sleep",
    "REM sleep"
)

const val PERIOD_START_DATE_TIME = "2020-08-10T12:00:00Z"
const val PERIOD_END_DATE_TIME = "2020-08-17T12:00:00Z"

class SleepTrackerFragment : Fragment() {

    lateinit var stepsLinechart: LineChart
    private lateinit var todaySleep : TextView
    private lateinit var addSleep : FloatingActionButton
    private val fitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_WRITE)
        .addDataType(DataType.TYPE_SLEEP_SEGMENT, FitnessOptions.ACCESS_WRITE)
        .build()

    private val runningQOrLater =
        android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q
    // Defines the start and end of the period of interest in this example.
    private val periodStartMillis = millisFromRfc339DateString(PERIOD_START_DATE_TIME)
    private val periodEndMillis = millisFromRfc339DateString(PERIOD_END_DATE_TIME)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.health_tracker_sleep_tracker_fragment, container, false)

    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        todaySleep = requireView().findViewById(R.id.today_sleep) as TextView
        addSleep = requireView().findViewById(R.id.add_sleep) as FloatingActionButton
        stepsLinechart = requireView().findViewById(R.id.lineChart_sleep) as LineChart
        todaySleep.text = "Today's duration of sleep is " + LocalPersistenceManager.getLocalPersistenceManager().todaySleepData.toString() + " hours"
        stepsLinechart.setNoDataText("Data Not Available")
        stepsLinechart.setNoDataTextColor(ContextCompat.getColor(requireActivity(), R.color.purple_500))
        stepsLinechart.description.isEnabled = false
        stepsLinechart.extraBottomOffset = 50F
        stepsLinechart.setScaleMinima(0.2f, 0.2f)
        stepsLinechart.setPinchZoom(true)
        stepsLinechart.setScaleEnabled(false)
        stepsLinechart.isDragEnabled = false
        val values : ArrayList<Entry>  = ArrayList()
        values.add(Entry(0f, 9f))
        values.add(Entry(1f, 2f))
        values.add(Entry(2f, 6f))
        values.add(Entry(3f, 4f))
        values.add(Entry(4f, 7f))
        values.add(Entry(5f, 10f))
        values.add(Entry(6f, 5f))
        val set1 : LineDataSet = LineDataSet(values, "Data Set 1")
        set1.fillAlpha = 110
        val dataSets : ArrayList<ILineDataSet>  = ArrayList()
        dataSets.add(set1)
        val data : LineData = LineData(dataSets)
        stepsLinechart.data = data
        addSleep.setOnClickListener {
            UtilsFragmentTransaction.addFragmentTransaction(
                R.id.fragment_container,
                activity, AddSleepDataFragment(),
                AddSleepDataFragment::class.java.simpleName, null
            )
        }
        if(LocalPersistenceManager.getLocalPersistenceManager().startSleepData != "") {
            checkPermissionsAndRun(FitActionRequestCodes.INSERT_SLEEP_SESSIONS)
        }

    }

    private fun createSleepDataSets(): List<DataSet> {
        val dataSource = getSleepDataSource()
        var startDate: String= ""
        if(LocalPersistenceManager.getLocalPersistenceManager().startSleepData != ""){
            startDate = LocalPersistenceManager.getLocalPersistenceManager().startSleepData
            LocalPersistenceManager.getLocalPersistenceManager().startSleepData = ""
        }
        var time: Long = 0L
        if(LocalPersistenceManager.getLocalPersistenceManager().sleepData != 0L){
            time = LocalPersistenceManager.getLocalPersistenceManager().sleepData
            LocalPersistenceManager.getLocalPersistenceManager().sleepData = 0
        }
        if(startDate != "" && time != 0L){
            return listOf(
                dataSource.createDataSet(startDate,
                    Pair(SleepStages.SLEEP_DEEP, TimeUnit.MILLISECONDS.toMinutes(time))
                )
            )
        }
        return emptyList()

    }

    private fun insertSleepSessions() {
        val requests = createSleepSessionRequests()
        val client = Fitness.getSessionsClient(requireActivity(), getGoogleAccount())
        for (request in requests) {
            client.insertSession(request)
                .addOnSuccessListener {
                    val (start, end) = getSessionStartAndEnd(request.session)
                    Log.i(TAGS, "Added sleep: $start - $end")
                }
                .addOnFailureListener {
                    Log.e(TAGS, "Failed to insert session for ", it)
                    it.printStackTrace()
                }
        }
    }

    private fun createSleepSessionRequests(): List<SessionInsertRequest> {
        val dataSets = createSleepDataSets()

        return dataSets.map {
            val session = createSleepSession(it)
            val request = SessionInsertRequest.Builder()
            request.addDataSet(it)
            request.setSession(session)
            request.build()
        }
    }

    private fun getSleepDataSource(): DataSource {
        return DataSource.Builder()
            .setDataType(DataType.TYPE_SLEEP_SEGMENT)
            .setType(DataSource.TYPE_RAW)
            .setStreamName("MySleepSource")
            .setAppPackageName(context as Context)
            .build()
    }

    private fun readSleepSessions() {
        val client = Fitness.getSessionsClient(requireActivity(), getGoogleAccount())

        val sessionReadRequest = SessionReadRequest.Builder()
            .read(DataType.TYPE_SLEEP_SEGMENT)
            // By default, only activity sessions are included, not sleep sessions. Specifying
            // includeSleepSessions also sets the behaviour to *exclude* activity sessions.
            .includeSleepSessions()
            .readSessionsFromAllApps()
            .setTimeInterval(periodStartMillis, periodEndMillis, TimeUnit.MILLISECONDS)
            .build()

        client.readSession(sessionReadRequest)
            .addOnSuccessListener {
                dumpSleepSessions(it)
            }
            .addOnFailureListener { Log.e(TAGS, "Unable to read sleep sessions", it) }
    }

    private fun dumpSleepSessions(response: SessionReadResponse) {
        //Log.clear()

        for (session in response.sessions) {
            dumpSleepSession(session, response.getDataSet(session))
        }
    }

    private fun dumpSleepSession(session: Session, dataSets: List<DataSet>) {
        dumpSleepSessionMetadata(session)
        dumpSleepDataSets(dataSets)
    }

    private fun dumpSleepSessionMetadata(session: Session) {
        val (startDateTime, endDateTime) = getSessionStartAndEnd(session)
        val totalSleepForNight = calculateSessionDuration(session)
        Log.i(TAGS, "$startDateTime to $endDateTime ($totalSleepForNight mins)")
    }

    private fun dumpSleepDataSets(dataSets: List<DataSet>) {
        for (dataSet in dataSets) {
            for (dataPoint in dataSet.dataPoints) {
                val sleepStageOrdinal = dataPoint.getValue(Field.FIELD_SLEEP_SEGMENT_TYPE).asInt()
                val sleepStage = SLEEP_STAGES[sleepStageOrdinal]

                val durationMillis = dataPoint.getEndTime(TimeUnit.MILLISECONDS) - dataPoint.getStartTime(TimeUnit.MILLISECONDS)
                val duration = TimeUnit.MILLISECONDS.toMinutes(durationMillis)
                Log.i(TAGS, "\t$sleepStage: $duration (mins)")
            }
        }
    }

    private fun calculateSessionDuration(session: Session): Long {
        val total = session.getEndTime(TimeUnit.MILLISECONDS) - session.getStartTime(TimeUnit.MILLISECONDS)
        return TimeUnit.MILLISECONDS.toMinutes(total)
    }

    private fun getSessionStartAndEnd(session: Session): Pair<String, String> {
        val dateFormat = DateFormat.getDateTimeInstance()
        val startDateTime = dateFormat.format(session.getStartTime(TimeUnit.MILLISECONDS))
        val endDateTime = dateFormat.format(session.getEndTime(TimeUnit.MILLISECONDS))
        return startDateTime to endDateTime
    }

    private fun createSleepSession(dataSet: DataSet): Session {
        return Session.Builder()
            .setActivity(FitnessActivities.SLEEP)
            .setStartTime(dataSet.dataPoints.first().getStartTime(TimeUnit.NANOSECONDS), TimeUnit.NANOSECONDS)
            .setEndTime(dataSet.dataPoints.last().getEndTime(TimeUnit.NANOSECONDS), TimeUnit.NANOSECONDS)
            .setName("Sleep")
            .build()
    }

    private fun millisFromRfc339DateString(dateString: String) =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            .parse(dateString).time

    private fun checkPermissionsAndRun(fitActionRequestCode: FitActionRequestCodes) {
        if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                permissionApproved()
            } else {
                TODO("VERSION.SDK_INT < Q")
            }
        ) {
            fitSignIn(fitActionRequestCode)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestRuntimePermissions(fitActionRequestCode)
            }
        }
    }

    private fun fitSignIn(requestCode: FitActionRequestCodes) {
        if (oAuthPermissionsApproved()) {
            performActionForRequestCode(requestCode)
        } else {
            requestCode.let {
                GoogleSignIn.requestPermissions(
                    this,
                    it.ordinal,
                    getGoogleAccount(), fitnessOptions)
            }
        }
    }

    private fun oAuthPermissionsApproved() = GoogleSignIn.hasPermissions(getGoogleAccount(), fitnessOptions)

    /**
     * Gets a Google account for use in creating the Fitness client. This is achieved by either
     * using the last signed-in account, or if necessary, prompting the user to sign in.
     * `getAccountForExtension` is recommended over `getLastSignedInAccount` as the latter can
     * return `null` if there has been no sign in before.
     */
    private fun getGoogleAccount() = GoogleSignIn.getAccountForExtension(requireActivity(), fitnessOptions)

    /**
     * Handles the callback from the OAuth sign in flow, executing the post sign in function
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (resultCode) {
            AppCompatActivity.RESULT_OK -> {
                val postSignInAction = FitActionRequestCodes.values()[requestCode]
                performActionForRequestCode(postSignInAction)
            }
            else -> oAuthErrorMsg(requestCode, resultCode)
        }
    }

    /**
     * Runs the desired method, based on the specified request code. The request code is typically
     * passed to the Fit sign-in flow, and returned with the success callback. This allows the
     * caller to specify which method, post-sign-in, should be called.
     *
     * @param requestCode The code corresponding to the action to perform.
     */
    private fun performActionForRequestCode(requestCode: FitActionRequestCodes) = when (requestCode) {
        FitActionRequestCodes.INSERT_SLEEP_SESSIONS -> insertSleepSessions()
        FitActionRequestCodes.READ_SLEEP_SESSIONS -> readSleepSessions()
    }

    private fun oAuthErrorMsg(requestCode: Int, resultCode: Int) {
        val message = """
            There was an error signing into Fit. Check the troubleshooting section of the README
            for potential issues.
            Request code was: $requestCode
            Result code was: $resultCode
        """.trimIndent()
        Log.e(TAG, message)
    }
    // [END auth_oncreate_setup]

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun permissionApproved(): Boolean {
        return if (runningQOrLater) {
            PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACTIVITY_RECOGNITION)
        } else {
            true
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestRuntimePermissions(requestCode: FitActionRequestCodes) {
        val shouldProvideRationale =
            ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACTIVITY_RECOGNITION)

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        requestCode.let {
            if (shouldProvideRationale) {
                Log.i(TAG, "Displaying permission rationale to provide additional context.")
                Snackbar.make(
                    requireActivity().findViewById(R.id.main_activity_view),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok) {
                        // Request permission
                        ActivityCompat.requestPermissions(requireActivity(),
                            arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                            requestCode.ordinal)
                    }
                    .show()
            } else {
                Log.i(TAG, "Requesting permission")
                // Request permission. It's possible this can be auto answered if device policy
                // sets the permission in a given state or the user denied the permission
                // previously and checked "Never ask again".
                ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                    requestCode.ordinal)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when {
            grantResults.isEmpty() -> {
                // If user interaction was interrupted, the permission request
                // is cancelled and you receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.")
            }
            grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                // Permission was granted.
                val fitActionRequestCode = FitActionRequestCodes.values()[requestCode]
                fitSignIn(fitActionRequestCode)
            }
            else -> {
                // Permission denied.

                // In this Activity we've chosen to notify the user that they
                // have rejected a core permission for the app since it makes the Activity useless.
                // We're communicating this message in a Snackbar since this is a sample app, but
                // core permissions would typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.

                Snackbar.make(
                    requireActivity().findViewById(R.id.main_activity_view),
                    R.string.permission_denied_explanation,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.settings) {
                        // Build intent that displays the App settings screen.
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts("package",
                            BuildConfig.APPLICATION_ID, null)
                        intent.data = uri
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }
                    .show()
            }
        }
    }

    /** Outputs both to in-app targets and logcat.  */



/**
 * Creates a {@code DataSet} from a start date/time and sleep periods (fine-grained sleep segments).
 *
 * @param startDateTime The start of the sleep session in UTC
 * @param sleepPeriods One or more sleep periods, defined as a Pair of {@code SleepStages}
 *     string constant and duration in minutes.
 * @return The created DataSet.
 */
fun DataSource.createDataSet(startDateTime: String, vararg sleepPeriods: Pair<Int, Long>): DataSet {
    val builder: DataSet.Builder = DataSet.builder(this)
    var cursorMilliseconds = startDateTime.toLong()

    for (sleepPeriod in sleepPeriods) {
        val duration = TimeUnit.MINUTES.toMillis(sleepPeriod.second)
        val dataPoint = DataPoint.builder(this)
            .setField(Field.FIELD_SLEEP_SEGMENT_TYPE, sleepPeriod.first)
            .setTimeInterval(cursorMilliseconds, cursorMilliseconds + duration, TimeUnit.MILLISECONDS)
            .build()
        builder.add(dataPoint)
        cursorMilliseconds += duration
    }
    return builder.build()
}



}