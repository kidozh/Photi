package com.kidozh.photi.utils

import android.content.Context
import com.kidozh.photi.R
import java.text.DateFormat
import java.util.*


class LocalizationUtils {
    companion object{
        fun dateToCalendar(date: Date): Calendar {
            val calendar = Calendar.getInstance()
            calendar.time = date
            return calendar
        }

        fun getLocalePastTimeString(mContext: Context, date: Date): String? {
            val now = Date()
            val nowCalendar: Calendar = Calendar.getInstance()
            val dateCalendar: Calendar = dateToCalendar(date)
            val nowDay: Int = nowCalendar.get(Calendar.DAY_OF_YEAR)
            val dateDay: Int = dateCalendar.get(Calendar.DAY_OF_YEAR)
            val nowYear: Int = nowCalendar.get(Calendar.YEAR)
            val dateYear: Int = dateCalendar.get(Calendar.YEAR)
            val timeFormat: DateFormat =
                DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault())
            val timeString: String = timeFormat.format(date)
            var timeMillsecondsInterval: Long = now.getTime() - date.getTime()
            // handle ago
            return if (timeMillsecondsInterval >= 0) {
                if (timeMillsecondsInterval < 1000 * 60) {
                    mContext.getString(R.string.date_just_now)
                } else if (timeMillsecondsInterval < 1000 * 60 * 60) {
                    mContext.getString(R.string.date_minutes_before,timeMillsecondsInterval.toInt() / (60 * 1000))
                } else if (timeMillsecondsInterval < 1000 * 60 * 60 * 24) {
                    mContext.getString(R.string.date_hours_before,timeMillsecondsInterval.toInt() / (60 * 60 * 1000))
                } else if (nowDay == dateDay) {
                    mContext.getString(R.string.date_today_time, timeString)
                } else if (nowDay - dateDay > 0 && nowYear == dateYear) {
                    val intervalDay = nowDay - dateDay
                    if (intervalDay == 1) {
                        mContext.getString(R.string.date_yesterday_time,timeString)

                    } else if (intervalDay < 10) {
                        mContext.getString(R.string.date_days_ago, intervalDay, timeString)

                    } else {
                        val df: DateFormat = DateFormat.getDateTimeInstance(
                            DateFormat.SHORT,
                            DateFormat.MEDIUM,
                            Locale.getDefault()
                        )
                        df.format(date)
                    }
                } else {
                    val df: DateFormat = DateFormat.getDateTimeInstance(
                        DateFormat.SHORT,
                        DateFormat.MEDIUM,
                        Locale.getDefault()
                    )
                    df.format(date)
                }
            } else {
                // future
                // reverse it
                timeMillsecondsInterval = -timeMillsecondsInterval
                if (timeMillsecondsInterval < 1000 * 60) {
                    mContext.getString(R.string.date_right_away)
                } else if (timeMillsecondsInterval < 1000 * 60 * 60) {
                    mContext.getString(
                        R.string.date_in_minutes,
                        timeMillsecondsInterval.toInt() / (60 * 1000)
                    )
                } else if (timeMillsecondsInterval < 1000 * 60 * 60 * 24) {
                    mContext.getString(
                        R.string.date_in_hours,
                        timeMillsecondsInterval.toInt() / (60 * 60 * 1000)
                    )
                } else if (nowDay == dateDay) {
                    mContext.getString(R.string.date_today_time,timeString)
                } else if (dateDay - nowDay > 0 && nowYear == dateYear) {
                    val intervalDay = dateDay - nowDay
                    if (intervalDay == 1) {
                        mContext.getString(R.string.date_tomorrow_time, timeString)
                    } else if (intervalDay < 10) {
                        mContext.getString(R.string.date_in_days, intervalDay, timeString)
                    } else {
                        val df: DateFormat = DateFormat.getDateTimeInstance(
                            DateFormat.SHORT,
                            DateFormat.MEDIUM,
                            Locale.getDefault()
                        )
                        df.format(date)
                    }
                } else {
                    val df: DateFormat = DateFormat.getDateTimeInstance(
                        DateFormat.SHORT,
                        DateFormat.MEDIUM,
                        Locale.getDefault()
                    )
                    df.format(date)
                }
            }
        }
    }
}