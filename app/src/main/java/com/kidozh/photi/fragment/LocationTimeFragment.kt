package com.kidozh.photi.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kidozh.photi.R
import com.kidozh.photi.databinding.FragmentLocationTimeBinding
import com.kidozh.photi.databinding.ViewMoonPhaseDisplayBinding
import com.kidozh.photi.databinding.ViewSpecialTimeDisplayBinding
import com.kidozh.photi.entity.ObservationPosition
import com.kidozh.photi.utils.ConstantUtils
import com.kidozh.photi.utils.MoonPhaseUtils
import com.kidozh.photi.utils.SunriseSunsetUtils
import com.kidozh.photi.utils.UserPreferenceUtils
import com.kidozh.photi.vmodel.LocationTimeViewModel
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.concurrent.fixedRateTimer

class LocationTimeFragment : Fragment() {
    val TAG = LocationTimeFragment::class.simpleName
    lateinit var binding : FragmentLocationTimeBinding
    lateinit var goldenBinding: ViewSpecialTimeDisplayBinding
    lateinit var blueBinding: ViewSpecialTimeDisplayBinding
    lateinit var moonBinding: ViewMoonPhaseDisplayBinding
    var pos : Int = 0
    lateinit var viewModel: LocationTimeViewModel
    lateinit var timer:Timer
    init {

    }

    companion object{
        fun newInstance(id:Int): LocationTimeFragment {
            val fragment:LocationTimeFragment = LocationTimeFragment()
            val args = Bundle()
            args.putInt(ConstantUtils.PASS_LOCATION_ID_KEY,id)

            fragment.arguments = args
            return fragment
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(LocationTimeViewModel::class.java)
        if(arguments!=null){
            pos = requireArguments().getInt(ConstantUtils.PASS_LOCATION_ID_KEY)
            //viewModel.setObservationId(pos)
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLocationTimeBinding.inflate(inflater)
        goldenBinding = binding.goldenTimeView
        blueBinding = binding.blueTimeView
        moonBinding = binding.moonTimeView
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // bind viewmodel
        viewModel.setObservationId(pos)
        bindViewModel()
    }

    fun bindViewModel(){
        viewModel.observationPosition.observe(viewLifecycleOwner, Observer { observation ->
            if(observation ==null){
                return@Observer;
            }
            binding.locationName.setText(observation.name)
            binding.locationCoordinate.setText(context?.let {
                observation.getCoordinatorDisplayString(
                    it
                )
            })

            // get upmost data
            timer = fixedRateTimer("TIMER",false,0,1000){
                binding.locationTime.post {
                    binding.locationTime.setText(context?.let {
                        UserPreferenceUtils.getTimeDisplay(
                            it,
                            Calendar.getInstance(),observation.timeZone)
                    })
                }
            }
            // timezone offset
            val observationPositionTimeZone = TimeZone.getTimeZone(observation.timeZone)
            val currentTimeZone = TimeZone.getDefault()


            val now = Calendar.getInstance()
            val context = requireContext()
            val sunriseSunsetTime = SunriseSunsetUtils.getSunriseSunset(now,observation.latitude,observation.longitude)
            if(sunriseSunsetTime != null){
                val sunriseTime = sunriseSunsetTime[0]
                val sunsetTime = sunriseSunsetTime[1]
                binding.sunriseTime.setText(UserPreferenceUtils.getShortTimeDisplay(context,sunriseTime,observation.timeZone))
                binding.sunsetTime.setText(UserPreferenceUtils.getShortTimeDisplay(context,sunsetTime,observation.timeZone))
                val daylightTimeInSeconds = (sunsetTime.timeInMillis - sunriseTime.timeInMillis) / (1000)
                val daylightHours: Int = (daylightTimeInSeconds / (60*60) ).toInt()
                val daylightMinutes : Int = (daylightTimeInSeconds / (60) % 60 ).toInt()
                if(daylightHours > 23){
                    binding.daytime.setText(getString(R.string.polar_day))
                }
                else if(daylightHours>1){
                    binding.daytime.setText(getString(R.string.time_hours,daylightHours,daylightMinutes))
                }
                else if(daylightTimeInSeconds > 0){
                    binding.daytime.setText(getString(R.string.time_minutes,daylightMinutes))
                }
                else{
                    binding.daytime.setText(getString(R.string.polar_night))
                }
            }
            else{
                binding.sunriseTime.visibility = View.GONE
                binding.sunsetTime.visibility = View.GONE
                binding.daytime.setText(getString(R.string.polar_time))
            }

            val goldenTime = SunriseSunsetUtils.getGoldenHours(now,observation.latitude,observation.longitude)
            val blueTime = SunriseSunsetUtils.getBlueHours(now,observation.latitude,observation.longitude)

            if(goldenTime !=null){
                goldenBinding.timeStartMonrning.setText(UserPreferenceUtils.getShortTimeIntervalDisplay(context,goldenTime.get(0),observation.timeZone))
                goldenBinding.timeEndMonrning.setText(UserPreferenceUtils.getShortTimeIntervalDisplay(context,goldenTime.get(1),observation.timeZone))
                goldenBinding.timeStartEvening.setText(UserPreferenceUtils.getShortTimeIntervalDisplay(context,goldenTime.get(2),observation.timeZone))
                goldenBinding.timeEndEvening.setText(UserPreferenceUtils.getShortTimeIntervalDisplay(context,goldenTime.get(3),observation.timeZone))
                goldenBinding.timeLabel.setText(R.string.golden_time)
                goldenBinding.displayView.setBackgroundColor(getColor(context,R.color.MaterialColorOrange))

            }
            else{
                binding.goldenTimeView.root.visibility = View.GONE
            }

            if(blueTime !=null){
                blueBinding.timeStartMonrning.setText(UserPreferenceUtils.getShortTimeIntervalDisplay(context,blueTime.get(0),observation.timeZone))
                blueBinding.timeEndMonrning.setText(UserPreferenceUtils.getShortTimeIntervalDisplay(context,blueTime.get(1),observation.timeZone))
                blueBinding.timeStartEvening.setText(UserPreferenceUtils.getShortTimeIntervalDisplay(context,blueTime.get(2),observation.timeZone))
                blueBinding.timeEndEvening.setText(UserPreferenceUtils.getShortTimeIntervalDisplay(context,blueTime.get(3),observation.timeZone))
                blueBinding.timeLabel.setText(R.string.blue_time)
                blueBinding.displayView.setBackgroundColor(getColor(context,R.color.MaterialColorIndigo))
            }
            else{
                binding.blueTimeView.root.visibility = View.GONE
            }
            // moon
            moonBinding.moonView.setCalendar(now)
            MoonPhaseUtils(now).phase


        })
    }
}