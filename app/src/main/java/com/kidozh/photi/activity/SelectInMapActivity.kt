package com.kidozh.photi.activity

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.kidozh.photi.R
import com.kidozh.photi.adapter.ObservationPositionAdapter
import com.kidozh.photi.entity.ObservationPosition
import com.kidozh.photi.utils.ConstantUtils
import com.kidozh.photi.utils.GeoUtils
import java.lang.Exception
import java.text.DateFormat
import java.util.*


class SelectInMapActivity : AppCompatActivity(), OnMapReadyCallback {
    private val TAG = SelectInMapActivity::class.simpleName
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_in_map)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val wuhanLocation = LatLng(30.59, 114.30)
        mMap.addMarker(
            MarkerOptions().position(wuhanLocation)
                .title(getString(R.string.google_map_init_marker_wuhan))
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLng(wuhanLocation))
        mMap.setOnMapClickListener { latlng->
            Log.d(TAG,"latlng is "+latlng+" "+latlng.describeContents())
            val latitude = latlng.latitude
            val longitude = latlng.longitude
            try{
                val geocoder = Geocoder(this, Locale.getDefault())
                val address = geocoder.getFromLocation(latitude,longitude,1)
                if(address !=null){
                    Log.d(TAG,"Get address "+address)
                    val countryCode = address.get(0).countryCode
                    val continent = GeoUtils.getContinentByCountryCode(countryCode)
                    val intent = Intent()
                    intent.putExtra(ConstantUtils.PASS_POSITION_KEY,ObservationPosition(
                        address.get(0).getAddressLine(0),
                        longitude,
                        latitude,
                        null
                    ))
                    intent.putExtra(ConstantUtils.PASS_CONTINENT_KEY,continent)
                    setResult(ConstantUtils.RESULT_CODE_SELECT_MAP,intent)
                    finishAfterTransition()
                    return@setOnMapClickListener
                }
            }
            catch (e: Exception){
                e.printStackTrace()
            }
            val date = Date()
            val df: DateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault())
            intent.putExtra(ConstantUtils.PASS_POSITION_KEY,ObservationPosition(
                getString(R.string.my_location,df.format(date)),
                longitude,
                latitude,
                null
            ))
            setResult(ConstantUtils.RESULT_CODE_SELECT_MAP,intent)
            finishAfterTransition()
        }
    }

}