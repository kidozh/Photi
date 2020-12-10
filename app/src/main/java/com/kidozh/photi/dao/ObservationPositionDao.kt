package com.kidozh.photi.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kidozh.photi.entity.ObservationPosition

@Dao
interface ObservationPositionDao {

    @Query("SELECT * FROM ObservationPosition")
    fun getAllObservationPositionList(): List<ObservationPosition>

    @Query("SELECT * FROM ObservationPosition")
    fun getAllObservationPositionLiveDataList(): LiveData<List<ObservationPosition>>

    @Query("SELECT * FROM ObservationPosition WHERE id=:id")
    fun getObservationPositionLiveDataById(id: Int): LiveData<ObservationPosition>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(observationPosition: ObservationPosition)

    @Delete
    fun delete(observationPosition: ObservationPosition)
}