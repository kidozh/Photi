package com.kidozh.photi.activity

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.wear.activity.ConfirmationActivity
import androidx.wear.widget.WearableLinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kidozh.photi.R
import com.kidozh.photi.adapter.ObservationPositionAdapter
import com.kidozh.photi.database.ObservationPositionDatabase
import com.kidozh.photi.databinding.ActivityNewPositionBinding
import com.kidozh.photi.entity.ObservationPosition
import com.kidozh.photi.utils.ConstantUtils
import com.kidozh.photi.utils.ConstantUtils.Companion.RCV_MAP_INTENT_CODE
import com.kidozh.photi.utils.ConstantUtils.Companion.REQUEST_CODE_SELECT_CONTINENT
import com.kidozh.photi.utils.ConstantUtils.Companion.RESULT_CODE_SELECT_TIMEZONE
import java.lang.Exception
import java.sql.Time
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList

class NewPositionActivity : BaseAmbientActivity(), ObservationPositionAdapter.OnCardClicked{
    private val TAG = NewPositionActivity::class.simpleName
    private lateinit var binding: ActivityNewPositionBinding
    private var adapter: ObservationPositionAdapter = ObservationPositionAdapter()
    lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewPositionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        configureRecyclerview()
        configureViewOnMapBtn()
        initSuggestion()
        configureMyLocationBtn()
        Log.d(TAG,"Get suggestion "+adapter.itemCount+" rv "+binding.recyclerview.height)
        getLastKnownLocationIfPossible()

    }



    fun configureRecyclerview(){
        binding.recyclerview.layoutManager = LinearLayoutManager(this)
        binding.recyclerview.adapter = adapter

    }

    fun initSuggestion(){
        val exampleCities = arrayListOf<String>("beijing","shanghai","guangzhou",
            "wuhan","chengdu","xian","london","berlin","paris","manchester","aachen","nyc","sydney")
        val latitudes = arrayListOf<Double>(39.9,34.5,23.07,30.0,30.67,34.26,51.62,52.30,48.51,53.29,50.47,40.42,-33.51)
        val longitudes = arrayListOf<Double>(116.39,121.4,113.15,114.0,104.06,108.95,0.1,13.25,2.21,-2.140,6.05,-74.0,-151.12)
        val timeZoneList = arrayListOf<String>("Asia/Shanghai","Asia/Shanghai","Asia/Shanghai",
            "Asia/Shanghai","Asia/Shanghai","Asia/Shanghai","Europe/London","Europe/Berlin",
            "Europe/Paris","Europe/London","Europe/Berlin","US/Eastern","Australia/Sydney")
        var cnt = 0
        var suggestionCities : MutableList<ObservationPosition> = ArrayList()
        for(cityName in exampleCities){
            val i18nCityRes = resources.getIdentifier(cityName,"string",packageName)
            var i18nCityName = cityName
            if(i18nCityRes != 0){
                i18nCityName = getString(i18nCityRes)
            }

            suggestionCities.add(ObservationPosition(
                i18nCityName
                ,longitudes.get(cnt)
                ,latitudes.get(cnt),
                timeZoneList.get(cnt)))
            cnt +=1
        }
        adapter.setObservationPositions(suggestionCities)
    }

    fun configureMyLocationBtn(){
        binding.useMyLocation.setOnClickListener{ l ->
            when{
                ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED ->{
                    // get location
                    getLastknownLocationAndAddToDatabase()
                }
                shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION,) ->{
                    Log.d(TAG,"using a dialog to announce request")
                    val builder = AlertDialog.Builder(this)
                            .setMessage(R.string.request_location_permission)
                            .setPositiveButton(android.R.string.ok) { dialog, which ->
                                requestPermissions(
                                        arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),
                                        200
                                )
                            }
                            .setNegativeButton(android.R.string.cancel) {dialog, which ->
                                dialog.cancel()
                            }
                    builder.show()
                }
                else ->{
                    requestPermissions(
                            arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),
                            200
                    )
                }
            }
        }
    }



    fun getLastKnownLocationIfPossible(){
        if ( ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener{ location ->
                if(location != null){
                    binding.useMyLocation.visibility = View.VISIBLE
                    try{
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val address = geocoder.getFromLocation(location.latitude,location.longitude,1)
                        if(address !=null){
                            Log.d(TAG,"Get address "+address)
                            binding.useMyLocation.setText(address.get(0).getAddressLine(0))
                        }

                    }
                    catch (e: Exception){
                        binding.useMyLocation.visibility = View.GONE
                    }
                }
                else{
                    binding.useMyLocation.visibility = View.GONE
                }
            }
    }

    fun getLastknownLocationAndAddToDatabase(){
        if ( ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location?->
                    // get last known name
                    Log.d(TAG,"Current location "+location);

                    if(location == null){
                        Toast.makeText(this,R.string.request_location_null, Toast.LENGTH_SHORT).show()
                    }
                    else{
                        var savedObservationPosition : ObservationPosition? = null
                        try{
                            var geocoder = Geocoder(this, Locale.getDefault())
                            var address = geocoder.getFromLocation(location.latitude,location.longitude,1)
                            if(address !=null){
                                Log.d(TAG,"Current address "+address);
                                val timeZone = TimeZone.getDefault()
                                Log.d(TAG,"GET display name "+timeZone.toZoneId().toString())
                                savedObservationPosition = ObservationPosition(
                                        address.get(0).getAddressLine(0),
                                        location.longitude,
                                        location.latitude,
                                        timeZone.toZoneId().toString()
                                )
                            }
                            else{
                                val date = Date()
                                val df: DateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault())
                                val timeZone = TimeZone.getDefault()
                                Log.d(TAG,"GET display name "+timeZone.toZoneId().toString())
                                savedObservationPosition = ObservationPosition(
                                        getString(R.string.my_location,df.format(date)),
                                        location.longitude,
                                        location.latitude,
                                        timeZone.toZoneId().toString()
                                )
                            }
                        }
                        catch (e: Exception){
                            val date = Date()
                            val df: DateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault())
                            val timeZone = TimeZone.getDefault()

                            savedObservationPosition = ObservationPosition(
                                    getString(R.string.my_location,df.format(date)),
                                    location.longitude,
                                    location.latitude,
                                    timeZone.displayName
                            )
                        }
                        if(savedObservationPosition !=null){
                            val thread = Thread{
                                var db = ObservationPositionDatabase.getInstance(this)
                                var dao = db.observationDao()
                                dao.insert(savedObservationPosition)
                                runOnUiThread {
                                    // passing to
                                    val intent = Intent(this, ConfirmationActivity::class.java).apply {
                                        putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.SUCCESS_ANIMATION)
                                        putExtra(ConfirmationActivity.EXTRA_MESSAGE, getString(R.string.save_location_successful,savedObservationPosition.name))
                                    }
                                    startActivity(intent)
                                    //finishAfterTransition()
                                }
                            }
                            thread.start()
                        }

                    }
                }
    }

    override fun onClicked(pos: Int, observationPosition: ObservationPosition) {
        // add to database
        val thread = Thread{
            var db = ObservationPositionDatabase.getInstance(this)
            var dao = db.observationDao()
            dao.insert(observationPosition)
        }
        thread.start()
        finishAfterTransition()
    }

    override fun onLongClicked(pos: Int, observationPosition: ObservationPosition) {
        TODO("Not yet implemented")
    }



    fun configureViewOnMapBtn(){
        binding.viewOnMap.setOnClickListener{ l->
            val intent = Intent(this,SelectInMapActivity::class.java)
            startActivityForResult(intent,RCV_MAP_INTENT_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            RCV_MAP_INTENT_CODE ->{
                // need further selection
                if(data!=null){
                    // select continent
                    val continent = data.getStringExtra(ConstantUtils.PASS_CONTINENT_KEY)
                    val observationPosition = data.getSerializableExtra(ConstantUtils.PASS_POSITION_KEY)
                    val intent = Intent(this,SelectContinentActivity::class.java)
                    intent.putExtra(ConstantUtils.PASS_CONTINENT_KEY,continent)
                    intent.putExtra(ConstantUtils.PASS_POSITION_KEY,observationPosition)
                    startActivityForResult(intent, REQUEST_CODE_SELECT_CONTINENT)
                    Toast.makeText(this,getString(R.string.selecting_continent),Toast.LENGTH_LONG).show()
                }
                else{
                    val intent = Intent(this, ConfirmationActivity::class.java).apply {
                        putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.FAILURE_ANIMATION)
                        putExtra(ConfirmationActivity.EXTRA_MESSAGE, getString(R.string.recv_no_observation_position))
                    }
                    startActivity(intent)
                }
            }

        }
        when(resultCode){
            RESULT_CODE_SELECT_TIMEZONE->{

                val timeZoneString = data?.getStringExtra(ConstantUtils.PASS_TIMEZONE_ID_KEY)
                var observationPosition = data?.getSerializableExtra(ConstantUtils.PASS_POSITION_KEY) as ObservationPosition
                if(timeZoneString != null){
                    observationPosition.timeZone = timeZoneString
                }
                else{
                    observationPosition.timeZone = TimeZone.getDefault().toZoneId().toString()
                }
                val thread = Thread{
                    var db = ObservationPositionDatabase.getInstance(this)
                    var dao = db.observationDao()
                    dao.insert(observationPosition)
                    runOnUiThread {
                        // passing to
                        val intent = Intent(this, ConfirmationActivity::class.java).apply {
                            putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.SUCCESS_ANIMATION)
                            putExtra(ConfirmationActivity.EXTRA_MESSAGE, getString(R.string.save_location_successful,observationPosition.name))
                        }
                        startActivity(intent)
                        //finishAfterTransition()
                    }
                }
                thread.start()
                finishAfterTransition()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}