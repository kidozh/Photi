package com.kidozh.photi.activity

import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.text.TextUtils
import android.util.Log
import androidx.wear.widget.WearableLinearLayoutManager
import com.kidozh.photi.R
import com.kidozh.photi.adapter.ContinentAdapter
import com.kidozh.photi.adapter.TimeZoneAdapter
import com.kidozh.photi.databinding.ActivitySelectContinentBinding
import com.kidozh.photi.databinding.ActivitySelectTimeZoneBinding
import com.kidozh.photi.utils.ConstantUtils
import com.kidozh.photi.utils.ConstantUtils.Companion.PASS_TIMEZONE_ID_KEY
import com.kidozh.photi.utils.ConstantUtils.Companion.RESULT_CODE_SELECT_TIMEZONE
import java.util.*
import kotlin.collections.ArrayList

class SelectTimeZoneActivity : BaseAmbientActivity(), TimeZoneAdapter.OnTimeZoneClick {
    lateinit var binding: ActivitySelectTimeZoneBinding
    var adapter = TimeZoneAdapter()
    val TAG = SelectContinentActivity::class.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectTimeZoneBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configureRecyclerview()
        configureIntent()
    }

    fun configureRecyclerview(){
        binding.recyclerview.layoutManager = WearableLinearLayoutManager(this)
        binding.recyclerview.adapter = adapter

    }

    fun configureIntent(){
        val intent = intent
        val continentValue = intent.getStringExtra(ConstantUtils.PASS_CONTINENT_KEY)
        val allAvailableTimeZoneIdList = TimeZone.getAvailableIDs()
        Log.d(TAG,"timezone id "+continentValue)
        var timezoneList : MutableList<TimeZone> = ArrayList()
        if(continentValue!=null && !TextUtils.isEmpty(continentValue) && !continentValue.equals("All")){
            if(continentValue.equals("China")){
                val chinaZoneId = arrayListOf<String>("Asia/Shanghai",
                    "Asia/Chongqing","Asia/Harbin","Asia/Chungking","Asia/Urumqi","Asia/Hong_Kong","Asia/Macau","Asia/Macao","Asia/Taipei")
                for(timeZoneId in chinaZoneId){
                    val timeZoneEntity = TimeZone.getTimeZone(timeZoneId)
                    val timeZoneString = timeZoneEntity.toZoneId().toString()
                    timezoneList.add(TimeZone.getTimeZone(timeZoneId))
                }
            }
            else{
                for(timeZoneId in allAvailableTimeZoneIdList){
                    val timeZoneEntity = TimeZone.getTimeZone(timeZoneId)
                    val timeZoneString = timeZoneEntity.toZoneId().toString()
                    if(timeZoneString.startsWith(continentValue)){
                        timezoneList.add(TimeZone.getTimeZone(timeZoneId))
                    }
                }
            }


        }
        else{
            // no filter
            for(timeZoneId in allAvailableTimeZoneIdList){
                timezoneList.add(TimeZone.getTimeZone(timeZoneId))
            }
        }
        adapter.timezoneList = timezoneList
        adapter.notifyDataSetChanged()


    }

    override fun onTimeZoneClicked(timeZone: TimeZone) {
        intent.putExtra(PASS_TIMEZONE_ID_KEY,timeZone.toZoneId().toString())
        intent.putExtra(ConstantUtils.PASS_POSITION_KEY,intent.getSerializableExtra(ConstantUtils.PASS_POSITION_KEY))
        setResult(RESULT_CODE_SELECT_TIMEZONE,intent)
        finishAfterTransition()
    }
}