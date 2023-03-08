package com.example.climbingtraining.db

import androidx.room.*
import com.example.climbingtraining.models.LastHangboard


@Dao
interface LastHangboardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(lastHangboard: LastHangboard)

    @Update
    suspend fun update(lastHangboard: LastHangboard)

    @Query("SELECT * FROM `lastHangboard`")
    fun fetchAll(): LastHangboard

}