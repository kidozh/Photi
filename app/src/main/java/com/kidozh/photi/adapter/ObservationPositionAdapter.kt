package com.kidozh.photi.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kidozh.photi.databinding.ItemObservationPositionBinding
import com.kidozh.photi.entity.ObservationPosition
import java.util.*
import kotlin.collections.ArrayList

class ObservationPositionAdapter:
    RecyclerView.Adapter<ObservationPositionAdapter.ObservationPositionViewHolder>() {
    val TAG = ObservationPositionAdapter::class.simpleName
    var observationPositionList: List<ObservationPosition> = ArrayList()
    var mListener: OnCardClicked? = null
    fun setObservationPositions(observationPositionList: List<ObservationPosition>){
        this.observationPositionList = observationPositionList
        notifyDataSetChanged()
    }

    lateinit var context: Context
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ObservationPositionViewHolder {
        context = parent.context
        if(context is OnCardClicked){
            mListener = context as OnCardClicked
        }
        val layoutInflator = LayoutInflater.from(parent.context)
        val binding = ItemObservationPositionBinding.inflate(layoutInflator,parent,false)
        return ObservationPositionViewHolder(binding,binding.root)
    }

    override fun onBindViewHolder(holder: ObservationPositionViewHolder, position: Int) {
        val location = observationPositionList.get(position)
        holder.binding.name.setText(location.name)
        val timeZone : TimeZone = TimeZone.getTimeZone(location.timeZone)

        holder.binding.timezone.setText(timeZone.displayName)
        holder.binding.coordinator.setText(location.getCoordinatorDisplayString(context))
        holder.binding.card.setOnClickListener{ v ->
            Log.d(TAG,"Click it "+position)
            mListener?.onClicked(position,location)
        }
        holder.binding.card.setOnLongClickListener { it ->
            mListener?.onLongClicked(position,location)
            true
        }

    }

    override fun getItemCount(): Int {
        return observationPositionList.size
    }

    class ObservationPositionViewHolder(binding: ItemObservationPositionBinding, itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        public val binding: ItemObservationPositionBinding
        init {
            this.binding = binding
        }
    }

    interface OnCardClicked{
        public fun onClicked(pos: Int, observationPosition: ObservationPosition)
        public fun onLongClicked(pos: Int, observationPosition: ObservationPosition)
    }
}