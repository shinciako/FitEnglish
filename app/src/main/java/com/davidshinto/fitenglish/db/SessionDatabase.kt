package com.davidshinto.fitenglish.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

abstract class SessionDatabase : RoomDatabase() {
    abstract fun sessionDao() : SessionDao

    companion object{
        @Volatile
        private var INSTANCE : SessionDatabase? = null

        fun getInstance(context: Context):SessionDatabase{
            return INSTANCE?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SessionDatabase::class.java,
                    "session_data_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}