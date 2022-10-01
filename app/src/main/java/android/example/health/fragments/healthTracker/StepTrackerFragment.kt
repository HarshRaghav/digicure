package android.example.health.fragments.healthTracker

import android.Manifest
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.content.pm.PackageManager
import android.example.health.BuildConfig
import android.example.health.Constants.GlobalData
import android.example.health.fragments.healthTracker.FitActionRequestCode
import android.example.health.R
import android.example.health.WelcomeScreen
import android.example.health.common.logger.Log
import android.example.health.common.logger.LogWrapper
import android.example.health.common.logger.MessageOnlyLogFilter
import android.example.health.networkUtils.CheckNetworkConnection
import android.example.health.utils.MessagePersistanceManager
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.utils.MPPointF

import com.github.mikephil.charting.components.XAxis

import com.github.mikephil.charting.utils.ViewPortHandler

import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.formatter.ValueFormatter

import com.github.mikephil.charting.data.BarData

import com.github.mikephil.charting.interfaces.datasets.IBarDataSet

import com.github.mikephil.charting.model.GradientColor

import com.github.mikephil.charting.data.BarDataSet

import com.github.mikephil.charting.data.BarEntry

import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.gms.fitness.data.DataSource
import com.google.android.gms.fitness.request.DataReadRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


const val TAG = "StepCounter"

enum class FitActionRequestCode {
    SUBSCRIBE,
    READ_DATA
}

class StepTrackerFragment : Fragment(){
    lateinit var stepsBarchart: BarChart


    private val fitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
        .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
        .build()

    private val runningQOrLater =
        android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q

    var rootView: View? = null
    private lateinit var stepsCount : TextView
    private lateinit var stepsProgressBar : ProgressBar
    private lateinit var stepDayArray: ArrayList<Int>
    lateinit var stepDayXValues: ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.health_tracker_step_counter_fragment, container, false)

    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rootView = view
        stepsCount = rootView!!.findViewById(R.id.steps_count) as TextView
        stepsProgressBar = rootView!!.findViewById(R.id.steps_progressBar) as ProgressBar
        stepsBarchart = rootView!!.findViewById(R.id.barchart_steps) as BarChart
        stepsBarchart.setNoDataText("Data Not Available")
        stepsBarchart.setNoDataTextColor(ContextCompat.getColor(requireActivity(), R.color.purple_500))
        stepsBarchart.setOnChartValueSelectedListener(this)
        stepsBarchart.description.isEnabled = false
        // margin from bottom

        // margin from bottom
        stepsBarchart.extraBottomOffset = 50F
        // Doesnot remove grid but remove greyish background on bar chart
        // Doesnot remove grid but remove greyish background on bar chart
        stepsBarchart.setDrawGridBackground(false)
        // If set to true, all values are drawn above their bars, instead of below their top.
        // If set to true, all values are drawn above their bars, instead of below their top.
        stepsBarchart.setDrawValueAboveBar(true)
        // Grey color shadow to top if 10 is maximum and value of entry is 4 then remaining 6 is grey.
        // Grey color shadow to top if 10 is maximum and value of entry is 4 then remaining 6 is grey.
        stepsBarchart.setDrawBarShadow(false)
        // Sets the minimum scale factor value to which can be zoomed out.
        // Sets the minimum scale factor value to which can be zoomed out.
        stepsBarchart.setScaleMinima(0.2f, 0.2f)
        // If set to true, both x and y axis can be scaled simultaneously with 2 fingers.
        // If set to true, pinch-zooming is enabled. If disabled, x- and y-axis can be zoomed separately.
        // If set to true, both x and y axis can be scaled simultaneously with 2 fingers.
        // If set to true, pinch-zooming is enabled. If disabled, x- and y-axis can be zoomed separately.
        stepsBarchart.setPinchZoom(true)
        stepsBarchart.setXAxisRenderer(
            CustomXAxisRenderer(
                stepsBarchart.viewPortHandler,
                stepsBarchart.xAxis,
                stepsBarchart.getTransformer(YAxis.AxisDependency.LEFT)
            )
        )

        initializeLogging()
        checkPermissionsAndRun(FitActionRequestCode.SUBSCRIBE)
    }

    private fun checkPermissionsAndRun(fitActionRequestCode: FitActionRequestCode) {
        if (permissionApproved()) {
            fitSignIn(fitActionRequestCode)
        } else {
            requestRuntimePermissions(fitActionRequestCode)
        }
    }

    private fun fitSignIn(requestCode: FitActionRequestCode) {
        if (oAuthPermissionsApproved()) {
            performActionForRequestCode(requestCode)
        } else {
            requestCode.let {
                GoogleSignIn.requestPermissions(
                    this,
                    requestCode.ordinal,
                    getGoogleAccount(), fitnessOptions)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (resultCode) {
            AppCompatActivity.RESULT_OK -> {
                val postSignInAction = FitActionRequestCode.values()[requestCode]
                postSignInAction.let {
                    performActionForRequestCode(postSignInAction)
                }
            }
            else -> oAuthErrorMsg(requestCode, resultCode)
        }
    }

    private fun performActionForRequestCode(requestCode: FitActionRequestCode) = when (requestCode) {
        FitActionRequestCode.READ_DATA -> readData()
        FitActionRequestCode.SUBSCRIBE -> subscribe()
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

    private fun oAuthPermissionsApproved() = GoogleSignIn.hasPermissions(getGoogleAccount(), fitnessOptions)

    private fun getGoogleAccount() =
        GoogleSignIn.getAccountForExtension(requireActivity(), fitnessOptions)

    private fun subscribe() {
        // To create a subscription, invoke the Recording API. As soon as the subscription is
        // active, fitness data will start recording.
        Fitness.getRecordingClient(requireActivity(), getGoogleAccount())
            .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i(TAG, "Subscribed!")
                    fitSignIn(FitActionRequestCode.READ_DATA)
                } else {
                    Log.w(TAG, "There was a problem subscribing.", task.exception)
                }
            }
    }

    private fun readData() {
        /*val mHandler = Handler()
        val monitor: Runnable = object : Runnable{
            override fun run() {
            */
            stepDayArray = ArrayList()
            stepDayXValues = ArrayList()
            for (i in 1 until 9) {
                /*val endTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDateTime.now().atZone(ZoneId.systemDefault())
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            val startTime = endTime.minusDays(1)*/
                val endTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (i == 8) {
                        LocalDateTime.now().atZone(ZoneId.systemDefault())
                    } else {
                        LocalDate.now().atStartOfDay(ZoneId.systemDefault())
                    }

                } else {
                    TODO("VERSION.SDK_INT < O")
                }
                val startTime = if (i == 8) {
                    LocalDate.now().atStartOfDay(ZoneId.systemDefault())
                } else {
                    endTime.minusDays(i.toLong())
                }
                if(i != 8){
                    val s = startTime.toString()
                    val s1 = s.split("T")
                    stepDayXValues.add(s1[0])
                }

                val datasource = DataSource.Builder()
                    .setAppPackageName("com.google.android.gms")
                    .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                    .setType(DataSource.TYPE_DERIVED)
                    .setStreamName("estimated_steps")
                    .build()

                val request = DataReadRequest.Builder()
                    .aggregate(datasource)
                    .bucketByTime(1, TimeUnit.DAYS)
                    .setTimeRange(
                        startTime.toEpochSecond(),
                        endTime.toEpochSecond(),
                        TimeUnit.SECONDS
                    )
                    .build()

                Fitness.getHistoryClient(
                    requireActivity(),
                    GoogleSignIn.getAccountForExtension(requireActivity(), fitnessOptions)
                )
                    .readData(request)
                    .addOnSuccessListener { response ->
                                val totalSteps = response.buckets
                                    .flatMap { it.dataSets }
                                    .flatMap { it.dataPoints }
                                    .sumBy { it.getValue(Field.FIELD_STEPS).asInt() }
                                if (i == 8) {
                                    stepsCount.text = totalSteps.toString()
                                    val percentage = totalSteps.toInt() / 100
                                    stepsProgressBar.progress = percentage
                                } else {
                                    stepDayArray.add(totalSteps)
                                }
                                if(stepDayArray.size == 7 ) {
                                    setUpBarChart()
                                }
                                Log.i(TAG, "Total steps: $totalSteps")
                            }
                            //runnable

                        }


                    /*}
            }
        mHandler.postDelayed(monitor, 2000)*/
    }






    private fun initializeLogging() {
        // Wraps Android's native log framework.
        val logWrapper = LogWrapper()
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        Log.setLogNode(logWrapper)
        // Filter strips out everything except the message text.
        val msgFilter = MessageOnlyLogFilter()
        logWrapper.next = msgFilter
        // On screen logging via a customized TextView.
        /*val logView = rootView!!.findViewById<View>(R.id.sample_logview) as LogView
        TextViewCompat.setTextAppearance(logView, R.style.Log)
        logView.setBackgroundColor(Color.WHITE)
        msgFilter.next = logView*/
        Log.i(TAG, "Ready")
    }

    private fun permissionApproved(): Boolean {
        val approved = if (runningQOrLater) {
            PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACTIVITY_RECOGNITION)
        } else {
            true
        }
        return approved
    }

    private fun requestRuntimePermissions(requestCode: FitActionRequestCode) {
        val shouldProvideRationale =
            ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACTIVITY_RECOGNITION)

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        requestCode.let {
            if (shouldProvideRationale) {
                Log.i(TAG, "Displaying permission rationale to provide additional context.")
                Snackbar.make(
                    rootView!!.findViewById(R.id.main_activity_view),
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when {
            grantResults.isEmpty() -> {
                // If user interaction was interrupted, the permission request
                // is cancelled and you receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.")
            }
            grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                // Permission was granted.
                val fitActionRequestCode = FitActionRequestCode.values()[requestCode]
                fitActionRequestCode.let {
                    fitSignIn(fitActionRequestCode)
                }
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
                    rootView!!.findViewById(R.id.main_activity_view),
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

    private fun setUpBarChart() {
        // Class representing the x-axis labels settings.
        val xAxis: XAxis = stepsBarchart.xAxis
        // Sets the position where the XAxis should appear.
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        // Sets the number of label entries for the y-axis
        xAxis.setLabelCount(7, false)
        // Remove vertical lines of grid
        xAxis.setDrawGridLines(false)
        // Set a minimum interval for the axis when zooming in. Can affect interval on x axis.
        xAxis.labelRotationAngle = -45f;
        xAxis.granularity = 0.5f
        xAxis.spaceMin = 1f
        val monthAxisValueFormatter = OrderDeliveryXAxisValueFormatter()
        xAxis.valueFormatter = monthAxisValueFormatter
        val leftAxis: YAxis = stepsBarchart.axisLeft
        // Set label value so that chart has this space on top
        // If 1500 bar chart is small
        leftAxis.spaceTop = 15f
        // if force enabled, the set label count will be forced, meaning that the exact specified count of labels will
        leftAxis.setLabelCount(9, false)
        leftAxis.valueFormatter = monthAxisValueFormatter
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        //leftAxis.setAxisMinimum(0f);
        val rightAxis: YAxis = stepsBarchart.axisRight
        rightAxis.setLabelCount(9, false)
        rightAxis.valueFormatter = monthAxisValueFormatter
        rightAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        rightAxis.spaceTop = 15f
        // Remove horizontal lines of grid if both set false
        leftAxis.setDrawGridLines(false)
        rightAxis.setDrawGridLines(false)
        // Label on right y axis start with 0 now
        rightAxis.axisMinimum = 0f
        val l: Legend = stepsBarchart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)
        // Form of of Blue square next to label of Legend
        l.form = Legend.LegendForm.SQUARE
        // Size of Blue square next to label of Legend
        l.formSize = 9f
        l.textSize = 11f
        // Sets the space between the legend entries on a horizontal axis
        l.xEntrySpace = 4f
        setData()
        stepsBarchart.animateY(1500)
    }

    private fun setData() {
            stepDayArray.sort()
            val value: ArrayList<BarEntry> = ArrayList(7)
            value.add(BarEntry(1f, stepDayArray[0].toFloat()))
            value.add(BarEntry(2f, (stepDayArray[1] - stepDayArray[0]).toFloat()))
            value.add(BarEntry(3f, (stepDayArray[2] - stepDayArray[1]).toFloat()))
            value.add(BarEntry(4f, (stepDayArray[3] - stepDayArray[2]).toFloat()))
            value.add(BarEntry(5f, (stepDayArray[4] - stepDayArray[3]).toFloat()))
            value.add(BarEntry(6f, (stepDayArray[5] - stepDayArray[4]).toFloat()))
            value.add(BarEntry(7f, (stepDayArray[6] - stepDayArray[5]).toFloat()))
            val set1: BarDataSet
            if (stepsBarchart.data != null &&
                stepsBarchart.data.dataSetCount > 0
            ) {
                set1 = stepsBarchart.data.getDataSetByIndex(0) as BarDataSet
                set1.values = value
                stepsBarchart.data.notifyDataChanged()
                stepsBarchart.notifyDataSetChanged()
            } else {
                set1 = BarDataSet(value, "Activity of past 7 days")
                val startColor1 =
                    ContextCompat.getColor(requireActivity(), android.R.color.holo_orange_light)
                val startColor2 =
                    ContextCompat.getColor(requireActivity(), android.R.color.holo_blue_light)
                val startColor3 =
                    ContextCompat.getColor(requireActivity(), android.R.color.holo_orange_light)
                val startColor4 =
                    ContextCompat.getColor(requireActivity(), android.R.color.holo_green_light)
                val startColor5 =
                    ContextCompat.getColor(requireActivity(), android.R.color.holo_red_light)
                val startColor6 =
                    ContextCompat.getColor(requireActivity(), android.R.color.holo_blue_light)
                val startColor7 =
                    ContextCompat.getColor(requireActivity(), android.R.color.holo_green_light)
                val endColor1 =
                    ContextCompat.getColor(requireActivity(), android.R.color.holo_blue_dark)
                val endColor2 =
                    ContextCompat.getColor(requireActivity(), android.R.color.holo_purple)
                val endColor3 =
                    ContextCompat.getColor(requireActivity(), android.R.color.holo_green_dark)
                val endColor4 =
                    ContextCompat.getColor(requireActivity(), android.R.color.holo_red_dark)
                val endColor5 =
                    ContextCompat.getColor(requireActivity(), android.R.color.holo_orange_dark)
                val endColor6 =
                    ContextCompat.getColor(requireActivity(), android.R.color.holo_purple)
                val endColor7 =
                    ContextCompat.getColor(requireActivity(), android.R.color.holo_red_dark)
                val gradientFills: MutableList<GradientColor> = ArrayList()
                gradientFills.add(GradientColor(startColor1, endColor1))
                gradientFills.add(GradientColor(startColor2, endColor2))
                gradientFills.add(GradientColor(startColor3, endColor3))
                gradientFills.add(GradientColor(startColor4, endColor4))
                gradientFills.add(GradientColor(startColor5, endColor5))
                gradientFills.add(GradientColor(startColor6, endColor6))
                gradientFills.add(GradientColor(startColor7, endColor7))
                set1.gradientColors = gradientFills
                val dataSets: ArrayList<IBarDataSet> = ArrayList()
                dataSets.add(set1)
                val data = BarData(dataSets)
                data.setValueTextSize(10f)
                data.barWidth = 0.9f
                stepsBarchart.data = data
            }
    }

    inner class OrderDeliveryXAxisValueFormatter : ValueFormatter() {
        // Deciding the x axis and y axis values labels not entry values
        override fun getFormattedValue(value: Float): String {
            return if(value.toInt() in 1..7)
                stepDayXValues[value.toInt() - 1]
            else
                value.toString()
        }
    }

    internal class OrderDeliveryYLeftAndRightAxisValueFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return "" + value
        }
    }

    class CustomXAxisRenderer(
        viewPortHandler: ViewPortHandler?,
        xAxis: XAxis?,
        trans: Transformer?
    ) :
        XAxisRenderer(viewPortHandler, xAxis, trans) {
        override fun drawLabel(
            c: Canvas?,
            formattedLabel: String,
            x: Float,
            y: Float,
            anchor: MPPointF?,
            angleDegrees: Float
        ) {
            val lines = formattedLabel.split(" ".toRegex()).toTypedArray()
            for (i in lines.indices) {
                val vOffset = i * mAxisLabelPaint.textSize
                Utils.drawXAxisValue(
                    c,
                    lines[i],
                    x,
                    y + vOffset,
                    mAxisLabelPaint,
                    anchor,
                    angleDegrees
                )
            }
        }
    }
}

private fun BarChart.setOnChartValueSelectedListener(stepTrackerFragment: StepTrackerFragment) {

}


