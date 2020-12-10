package com.kidozh.photi.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kidozh.photi.dao.ObservationPositionDao
import com.kidozh.photi.entity.ObservationPosition
import com.kidozh.photi.utils.ConstantUtils

@Database(entities = arrayOf(ObservationPosition::class), version = 3)
abstract class ObservationPositionDatabase : RoomDatabase() {
    abstract fun observationDao() : ObservationPositionDao


    companion object{
        @Volatile
        private var instance: ObservationPositionDatabase? = null
        fun getInstance(context: Context): ObservationPositionDatabase{
            return instance?: synchronized(this){
                val INSTANCE = getDatabase(context)
                instance = INSTANCE
                INSTANCE
            }

        }

        private fun getDatabase(context: Context): ObservationPositionDatabase{
            return Room.databaseBuilder(context,
                ObservationPositionDatabase::class.java,
                ConstantUtils.databaseName)
                .fallbackToDestructiveMigration()
                .build()
        }
    }



}