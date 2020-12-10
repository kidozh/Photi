package com.kidozh.photi.activity

import android.content.Intent
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import androidx.wear.widget.WearableLinearLayoutManager
import com.kidozh.photi.R
import com.kidozh.photi.adapter.ContinentAdapter
import com.kidozh.photi.databinding.ActivitySelectContinentBinding
import com.kidozh.photi.utils.ConstantUtils

class SelectContinentActivity : BaseAmbientActivity(),ContinentAdapter.OnCardClick {
    lateinit var binding: ActivitySelectContinentBinding
    var adapter: ContinentAdapter = ContinentAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectContinentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configureRecyclerview()
        configureIntent()
    }



    fun configureRecyclerview(){
        binding.recyclerview.layoutManager = WearableLinearLayoutManager(this)
        binding.recyclerview.adapter = adapter
        initContinents()
    }

    fun initContinents(){
        val continentList:MutableList<ContinentAdapter.Continent> = ArrayList()

        val continentNameList:List<String> = arrayListOf("Asia",
            "Europe","Africa","America","Indian",
            "Pacific","China","US","Australia","Cananda",
            "Brazil","Atlantic","Antarctica","Arctic","All"
        )
        var cnt = 0
        for(continentName in continentNameList){
            continentList.add(ContinentAdapter.Continent().apply{
                val continentRes = resources.getIdentifier(String.format("timezone_%d",cnt+1),
                    "string",packageName)
                if(continentRes != 0){
                    name=getString(continentRes)
                }
                else{
                    name =getString(R.string.continent_7)
                }
                value = continentNameList.get(cnt)
                star = false

            })

            cnt +=1
        }
        adapter.continentList = continentList

        adapter.notifyDataSetChanged()
    }

    fun configureIntent(){
        val intent = intent
        val continentValue = intent.getStringExtra(ConstantUtils.PASS_CONTINENT_KEY)
        if(continentValue!=null){
            adapter.setStaredContinent(continentValue)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            ConstantUtils.RESULT_CODE_SELECT_TIMEZONE->{
                if (data != null) {
                    intent.putExtra(ConstantUtils.PASS_POSITION_KEY,intent.getSerializableExtra(ConstantUtils.PASS_POSITION_KEY))
                    intent.putExtra(ConstantUtils.PASS_TIMEZONE_ID_KEY,data.getStringExtra(ConstantUtils.PASS_TIMEZONE_ID_KEY))
                }
                setResult(ConstantUtils.RESULT_CODE_SELECT_TIMEZONE,intent)
                finishAfterTransition()
            }
        }
    }

    override fun onContinentClicked(continentValue: String) {
        var intent = Intent(this,SelectTimeZoneActivity::class.java)
        intent.putExtra(ConstantUtils.PASS_CONTINENT_KEY,continentValue)
        intent.putExtra(ConstantUtils.PASS_POSITION_KEY,intent.getSerializableExtra(ConstantUtils.PASS_POSITION_KEY))
        startActivityForResult(intent,ConstantUtils.RESULT_CODE_SELECT_TIMEZONE)
    }
}