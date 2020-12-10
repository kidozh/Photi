package com.kidozh.photi.adapter

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kidozh.photi.R
import com.kidozh.photi.activity.SelectTimeZoneActivity
import com.kidozh.photi.databinding.ItemContinentBinding
import com.kidozh.photi.databinding.ItemObservationPositionBinding
import com.kidozh.photi.entity.ObservationPosition
import com.kidozh.photi.utils.ConstantUtils
import java.util.*
import kotlin.collections.ArrayList

class ContinentAdapter:
    RecyclerView.Adapter<ContinentAdapter.ContinentViewHolder>() {
    val TAG = ContinentAdapter::class.simpleName
    var continentList: MutableList<Continent> = ArrayList()
    var listener: OnCardClick? = null

    lateinit var context: Context
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContinentViewHolder {
        context = parent.context

        val layoutInflator = LayoutInflater.from(parent.context)
        val binding = ItemContinentBinding.inflate(layoutInflator,parent,false)
        if(context is OnCardClick){
            listener = context as OnCardClick
        }
        return ContinentViewHolder(binding,binding.root)
    }

    public fun setStaredContinent(value:String){
        var cnt = 0
        for(continent in continentList){
            if(continent.value.equals(value)){
                continent.star = true
                notifyItemChanged(cnt)
                break
            }
            cnt += 1
        }
        notifyDataSetChanged()
    }



    override fun onBindViewHolder(holder: ContinentViewHolder, position: Int) {
        var continent = continentList.get(position)
        if(continent.star){
            holder.binding.starLabel.visibility = View.VISIBLE
        }
        else{
            holder.binding.starLabel.visibility = View.GONE
        }

        holder.binding.name.setText(continent.name)
        when(continent.value){
            "Asia"-> holder.binding.icon.setImageDrawable(context.getDrawable(R.drawable.ic_asia))
            "Africa"-> holder.binding.icon.setImageDrawable(context.getDrawable(R.drawable.ic_africa_24))
            "Europe"-> holder.binding.icon.setImageDrawable(context.getDrawable(R.drawable.ic_europe_24))
            "America"-> holder.binding.icon.setImageDrawable(context.getDrawable(R.drawable.ic_america_24))
            "Indian"-> holder.binding.icon.setImageDrawable(context.getDrawable(R.drawable.ic_outline_place_24))
            "Pacific"-> holder.binding.icon.setImageDrawable(context.getDrawable(R.drawable.ic_pacific_24))
            "US"-> holder.binding.icon.setImageDrawable(context.getDrawable(R.drawable.ic_usa_ball_24))
            "China"-> holder.binding.icon.setImageDrawable(context.getDrawable(R.drawable.ic_china_ball_24))
            "Australia"-> holder.binding.icon.setImageDrawable(context.getDrawable(R.drawable.ic_australia_ball))
            "Canada"-> holder.binding.icon.setImageDrawable(context.getDrawable(R.drawable.ic_canada_ball_24))
            "Brazil"-> holder.binding.icon.setImageDrawable(context.getDrawable(R.drawable.ic_brazil_ball_24))
            //"China"-> holder.binding.icon.setImageDrawable(context.getDrawable(R.drawable.ic_china_24))
            "All"-> holder.binding.icon.setImageDrawable(context.getDrawable(R.drawable.ic_round_stars_24))
            else -> holder.binding.icon.setImageDrawable(context.getDrawable(R.drawable.ic_outline_place_24))
        }
        if(listener !=null){
            holder.binding.card.setOnClickListener{l->
                listener!!.onContinentClicked(continent.value)
            }
        }

    }

    override fun getItemCount(): Int {
        return continentList.size
    }

    class ContinentViewHolder(binding: ItemContinentBinding, itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        public val binding: ItemContinentBinding
        init {
            this.binding = binding
        }
    }

    class Continent{
        var star = false
        var name:String = ""
        var value:String = ""
    }

    interface OnCardClick{
        fun onContinentClicked(continentValue:String)
    }
}