package com.kidozh.photi.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import com.kidozh.photi.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

open class UserPreferenceUtils {
    companion object{
        val TAG = UserPreferenceUtils::class.simpleName
        fun displayLocalTime(
            context: Context,
        ): Boolean {
            val preferenceName = context.getString(R.string.preference_key_use_local_time)
            val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            return prefs.getBoolean(preferenceName, true)
        }

        fun getTimeDisplay(context: Context, calendar : Calendar, timeZone: String?): String{
            val df: DateFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.getDefault())
            val date = calendar.time
            if(timeZone!=null && displayLocalTime(context)){
                // return local time
                df.timeZone = TimeZone.getTimeZone(timeZone)
                return df.format(date)
            }
            return df.format(date)
        }

        fun getShortTimeDisplay(context: Context, calendar : Calendar, timeZone: String?): String{
            val df: DateFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault())
            val date = calendar.time
            if(timeZone!=null && displayLocalTime(context)){
                // return local time
                df.timeZone = TimeZone.getTimeZone(timeZone)
                Log.d(TAG,"Local Sun time "+calendar+" timezone "+timeZone)
                return df.format(date)
            }
            return df.format(date)
        }

        fun getShortTimeIntervalDisplay(context: Context, calendar : Calendar, timeZone: String?): String{
            val df = SimpleDateFormat("HH:mm", Locale.getDefault())
            //val df: DateFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault())
            if(timeZone!=null && displayLocalTime(context)){
                // return local time
                df.timeZone = TimeZone.getTimeZone(timeZone)

                return df.format(calendar.time)
            }
            val date = calendar.time
            return df.format(date)
        }
    }

}