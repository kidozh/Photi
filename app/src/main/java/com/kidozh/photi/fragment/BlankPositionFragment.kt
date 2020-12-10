package com.kidozh.photi.fragment

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kidozh.photi.R
import com.kidozh.photi.activity.NewPositionActivity
import com.kidozh.photi.database.ObservationPositionDatabase
import com.kidozh.photi.databinding.FragmentBlankLocationBinding
import com.kidozh.photi.databinding.FragmentLocationTimeBinding
import com.kidozh.photi.entity.ObservationPosition
import com.kidozh.photi.utils.ConstantUtils
import com.kidozh.photi.vmodel.LocationTimeViewModel
import java.lang.Exception
import java.text.DateFormat
import java.util.*
import java.util.jar.Manifest

class BlankPositionFragment : Fragment() {
    lateinit var binding: FragmentBlankLocationBinding
    lateinit var fusedLocationClient: FusedLocationProviderClient
    val TAG = BlankPositionFragment::class.simpleName

    companion object{
        fun newInstance(): BlankPositionFragment {
            val fragment:BlankPositionFragment = BlankPositionFragment()
            val args = Bundle()
            return fragment
        }
    }

    init {

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBlankLocationBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureAddLocation()
        configureManuallyAddLocation()
    }

    fun configureManuallyAddLocation(){
        binding.addLocation.setOnClickListener{v ->
            val intent = Intent(activity,NewPositionActivity::class.java)
            startActivity(intent)
        }
    }

    fun configureAddLocation(){

        binding.useCurrentLocation.setOnClickListener { v ->
            // check for permission

            when{
                context?.let { ContextCompat.checkSelfPermission(it,android.Manifest.permission.ACCESS_FINE_LOCATION) }
                        == PackageManager.PERMISSION_GRANTED ->{
                            // get location
                            getLastknownLocationAndAddToDatabase()
                        }
                shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION,) ->{
                    Log.d(TAG,"using a dialog to announce request")
                    val builder = AlertDialog.Builder(context)
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            200 -> {
                if((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                    // permission is granted && continue to work
                    getLastknownLocationAndAddToDatabase()
                } else{
                    Toast.makeText(context,R.string.request_location_permission_failed,Toast.LENGTH_LONG).show()
                }
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun getLastknownLocationAndAddToDatabase(){
        if (context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED && context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED
        ) {
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
            .addOnSuccessListener { location:Location?->
                // get last known name
                Log.d(TAG,"Current location "+location);

                if(location == null){
                    Toast.makeText(context,R.string.request_location_null,Toast.LENGTH_SHORT).show()
                }
                else{
                    var savedObservationPosition : ObservationPosition? = null
                    try{
                        var geocoder = Geocoder(context, Locale.getDefault())
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
                    catch (e:Exception){
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
                            var db = ObservationPositionDatabase.getInstance(requireContext())
                            var dao = db.observationDao()
                            dao.insert(savedObservationPosition)
                        }
                        thread.start()
                    }

                }
            }
    }


}