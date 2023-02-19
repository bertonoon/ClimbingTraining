package com.example.climbingtraining.db

import androidx.room.*
import com.example.climbingtraining.model.SingleHangboard

@Dao
interface SavedConfigsDao {
    @Insert
    suspend fun insert (singleHangboardEntity: SingleHangboard)

    @Update
    suspend fun update (singleHangboardEntity: SingleHangboard)

    @Delete
    suspend fun delete(singleHangboardEntity: SingleHangboard)

    @Query("SELECT * FROM `savedSingleConfigs` ORDER BY id DESC")
    fun fetchAll():kotlinx.coroutines.flow.Flow<List<SingleHangboard>>

}