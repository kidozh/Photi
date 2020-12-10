package com.kidozh.photi.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kidozh.photi.databinding.ItemTimeZoneBinding
import com.kidozh.photi.entity.ObservationPosition
import java.util.*
import kotlin.collections.ArrayList

class TimeZoneAdapter:
    RecyclerView.Adapter<TimeZoneAdapter.TimeZoneViewHolder>() {
    val TAG = TimeZoneAdapter::class.simpleName
    var timezoneList: MutableList<TimeZone> = ArrayList()
    var listener: OnTimeZoneClick? = null

    lateinit var context: Context
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TimeZoneViewHolder {
        context = parent.context
        if(context is OnTimeZoneClick){
            listener = context as OnTimeZoneClick
        }
        val layoutInflator = LayoutInflater.from(parent.context)
        val binding = ItemTimeZoneBinding.inflate(layoutInflator,parent,false)
        return TimeZoneViewHolder(binding,binding.root)
    }

    override fun onBindViewHolder(holder: TimeZoneViewHolder, position: Int) {
        val timeZone = timezoneList.get(position)
        holder.binding.zoneId.setText(timeZone.toZoneId().toString())
        holder.binding.displayName.setText(timeZone.displayName)
        holder.binding.timeOffset.setText(timeZone.getOffset(Date().time).toString())

        holder.binding.card.setOnClickListener{ v ->
            listener?.onTimeZoneClicked(timeZone)
        }


    }

    override fun getItemCount(): Int {
        return timezoneList.size
    }

    class TimeZoneViewHolder(binding: ItemTimeZoneBinding, itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        public val binding: ItemTimeZoneBinding
        init {
            this.binding = binding
        }
    }

    interface OnTimeZoneClick{
        fun onTimeZoneClicked(timeZone: TimeZone)
    }
}