package com.davidshinto.fitenglish.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SessionDao {

    @Insert
    suspend fun insertSession(session: Session)

    @Delete
    suspend fun deleteSession(session: Session)

//    @Query("SELECT * FROM session_data_table order by date DESC")
//    suspend fun getAllSessions() : LiveData<List<Session>>

    @Query("SELECT * FROM session_data_table ORDER BY session_date DESC")
    suspend fun getAllSessions(): List<Session>
}