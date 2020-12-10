package com.kidozh.photi.vmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.kidozh.photi.dao.ObservationPositionDao
import com.kidozh.photi.database.ObservationPositionDatabase
import com.kidozh.photi.entity.ObservationPosition
import com.kidozh.photi.utils.ConstantUtils

class LocationTimeViewModel(application: Application) : AndroidViewModel(application) {
    lateinit var observationPosition : LiveData<ObservationPosition>
    val db = ObservationPositionDatabase.getInstance(application)
    lateinit var dao: ObservationPositionDao
    init {
        dao = db.observationDao()
        observationPosition = dao.getObservationPositionLiveDataById(0)
    }

    public fun setObservationId(initPos: Int){
        observationPosition = dao.getObservationPositionLiveDataById(initPos)
    }
}