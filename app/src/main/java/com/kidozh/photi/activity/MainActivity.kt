package com.kidozh.photi.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.wear.widget.drawer.WearableNavigationDrawerView
import com.kidozh.photi.R
import com.kidozh.photi.databinding.ActivityMainBinding
import com.kidozh.photi.entity.ObservationPosition
import com.kidozh.photi.fragment.BlankPositionFragment
import com.kidozh.photi.fragment.LocationTimeFragment
import com.kidozh.photi.vmodel.MainViewModel

class MainActivity : BaseAmbientActivity(),
    MenuItem.OnMenuItemClickListener,
    WearableNavigationDrawerView.OnItemSelectedListener  {
    val TAG = MainActivity::class.simpleName
    lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MainViewModel
    var pagerAdapter: ObservationPositionTimePagerAdapter = ObservationPositionTimePagerAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        configureNavigationDrawer()
        bindViewModel()
        initViewPager()
    }

    fun configureNavigationDrawer(){
        binding.topNavigationDrawer.controller.peekDrawer()
        binding.topNavigationDrawer.addOnItemSelectedListener(this)
        binding.bottomNavigationDrawer.controller.peekDrawer()
        binding.bottomNavigationDrawer.setOnMenuItemClickListener(this)
    }



    fun bindViewModel(){
        viewModel.observationPositions.observe(this,Observer { positions ->
            pagerAdapter.setPositions(positions)
            binding.topNavigationDrawer.setAdapter(MainNavigationAdapter(this,positions))

        })

        viewModel.selectedId.observe(this, Observer { id->
            var positions = viewModel.observationPositions.value
            if (positions != null) {
                var cnt = 0
                for(position in positions){
                    if(position.id == id){
                        binding.viewPager.setCurrentItem(cnt,true)
                    }
                    cnt += 1
                }
            }
        })
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {

        if (item != null) {
            Log.d(TAG, "You press item "+item.itemId)
            when(item.itemId){
                R.id.menu_settings -> {
                    intent = Intent(this,SettingsActivity::class.java)
                    startActivity(intent)
                    return true
                }
                R.id.menu_add_location -> {
                    intent = Intent(this,NewPositionActivity::class.java)
                    startActivity(intent)
                    return true
                }
                R.id.menu_manage_location -> {
                    intent = Intent(this,ManagePositionActivity::class.java)
                    startActivity(intent)
                    return true
                }
            }
        }
        return false
    }

    class MainNavigationAdapter(context: Context,observationPositionList: List<ObservationPosition>): WearableNavigationDrawerView.WearableNavigationDrawerAdapter() {
        lateinit var observationPositions: List<ObservationPosition>
        lateinit var context: Context
        init {
            this.observationPositions = observationPositionList
            this.context = context
        }

        override fun getItemText(pos: Int): CharSequence {
            return observationPositions.get(pos).name
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        override fun getItemDrawable(pos: Int): Drawable? {
            return context.getDrawable(R.drawable.ic_outline_place_24)
        }

        override fun getCount(): Int {
            return observationPositions.size
        }

    }

    override fun onItemSelected(pos: Int) {
        val list = viewModel.observationPositions.value
        if (list != null) {
            viewModel.selectedId.postValue(list.get(pos).id)
        }
        Log.d(TAG,"Selecting item "+pos)
    }

    fun initViewPager(){
        binding.viewPager.adapter = pagerAdapter
    }

    class ObservationPositionTimePagerAdapter(activity:MainActivity): FragmentStateAdapter(activity){
        var positionList : List<ObservationPosition> = ArrayList();

        fun setPositions(observationPositionList: List<ObservationPosition>){
            positionList = observationPositionList
            notifyDataSetChanged()
        }


        override fun getItemCount(): Int {
            return positionList.size
        }

        override fun createFragment(position: Int): Fragment {
            if(itemCount == 0){
                return BlankPositionFragment.newInstance()
            }
            else{
                return LocationTimeFragment.newInstance(positionList.get(position).id)
            }

        }

    }
}