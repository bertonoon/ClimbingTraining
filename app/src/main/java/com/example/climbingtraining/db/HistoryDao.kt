package com.example.climbingtraining.db

import androidx.room.*
import com.example.climbingtraining.models.SingleHangboardHistoryModel


@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert (singleHistoryEntity: SingleHangboardHistoryModel)

    @Update
    suspend fun update (singleHistoryEntity: SingleHangboardHistoryModel)

    @Delete
    suspend fun delete(singleHistoryEntity: SingleHangboardHistoryModel)

    @Query("SELECT * FROM `singleTrainingHistory` ORDER BY id DESC")
    fun fetchAll():kotlinx.coroutines.flow.Flow<List<SingleHangboardHistoryModel>>

}