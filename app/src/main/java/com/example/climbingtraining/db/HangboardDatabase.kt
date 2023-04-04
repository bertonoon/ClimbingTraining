package com.example.climbingtraining.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.climbingtraining.models.LastHangboard
import com.example.climbingtraining.models.SingleHangboard
import com.example.climbingtraining.models.SingleHangboardHistoryModel


@Database(
    entities = [
        SingleHangboard::class,
        SingleHangboardHistoryModel::class,
        LastHangboard::class
    ],
    version = 5
)
@TypeConverters(Converters::class)
abstract class HangboardDatabase : RoomDatabase() {

    abstract fun hangboardDao(): SavedConfigsDao
    abstract fun historyDao(): HistoryDao
    abstract fun lastHangboardDao(): LastHangboardDao

    companion object {
        @Volatile
        private var INSTANCE: HangboardDatabase? = null

        fun getInstance(context: Context): HangboardDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        HangboardDatabase::class.java,
                        "hangboard_database"
                    ).fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }

    }


}