package com.kidozh.photi.entity

import android.content.Context
import android.location.Location
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kidozh.photi.R
import java.io.Serializable

@Entity
class ObservationPosition: Serializable{
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var name: String = ""
    var longitude: Double = 0.0
    var latitude : Double = 0.0
    var timeZone: String? = null

    constructor(name:String,longitude:Double,latitude : Double, timeZone:String?){
        this.name = name
        this.longitude = longitude
        this.latitude = latitude
        this.timeZone = timeZone
    }

    fun getCoordinatorDisplayString(context: Context):String{
        var coordinate = ""
        if(latitude > 0){
            coordinate += context.getString(R.string.north_latitude, latitude)
        }
        else{
            coordinate += context.getString(R.string.south_latitude, -latitude)
        }

        if(longitude > 0){
            coordinate += context.getString(R.string.east_longitude, longitude)
        }
        else{
            coordinate += context.getString(R.string.west_longitude, -longitude)
        }

        return coordinate
    }
}