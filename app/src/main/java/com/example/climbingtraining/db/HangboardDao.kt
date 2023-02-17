package com.example.climbingtraining.db

import androidx.room.*
import com.example.climbingtraining.model.SimpleHangboard

@Dao
interface HangboardDao {
    @Insert
    suspend fun insert (simpleHangboardEntity: SimpleHangboard)

    @Update
    suspend fun update (simpleHangboardEntity: SimpleHangboard)

    @Delete
    suspend fun delete(simpleHangboardEntity: SimpleHangboard)

    @Query("SELECT * FROM `savedSimpleConfigs` ORDER BY id DESC")
    fun fetchAll():kotlinx.coroutines.flow.Flow<List<SimpleHangboard>>

}