package com.kidozh.photi.vmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.kidozh.photi.dao.ObservationPositionDao
import com.kidozh.photi.database.ObservationPositionDatabase
import com.kidozh.photi.entity.ObservationPosition
import com.kidozh.photi.utils.ConstantUtils

class ManageViewModel(application: Application) : AndroidViewModel(application) {
    lateinit var observationPositions : LiveData<List<ObservationPosition>>
    val db = ObservationPositionDatabase.getInstance(application)
    lateinit var dao: ObservationPositionDao
    init {
        dao = db.observationDao()
        observationPositions = dao.getAllObservationPositionLiveDataList()
    }
}