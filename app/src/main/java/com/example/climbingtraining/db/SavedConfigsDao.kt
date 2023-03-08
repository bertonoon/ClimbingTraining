package com.example.climbingtraining.db

import androidx.room.*
import com.example.climbingtraining.models.SingleHangboard

@Dao
interface SavedConfigsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(singleHangboardEntity: SingleHangboard)

    @Update
    suspend fun update(singleHangboardEntity: SingleHangboard)

    @Delete
    suspend fun delete(singleHangboardEntity: SingleHangboard)

    @Query("SELECT * FROM `savedSingleConfigs` ORDER BY name")
    fun fetchAll(): kotlinx.coroutines.flow.Flow<List<SingleHangboard>>

}