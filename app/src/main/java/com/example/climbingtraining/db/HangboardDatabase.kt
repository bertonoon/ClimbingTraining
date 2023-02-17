package com.example.climbingtraining.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.climbingtraining.model.SimpleHangboard
import com.example.climbingtraining.ui.viewModels.HangboardViewModel

@Database(entities = [SimpleHangboard::class], version = 1)
abstract class HangboardDatabase: RoomDatabase() {

    abstract fun hangboardDao(): HangboardDao

    companion object{
        @Volatile
        private var INSTANCE: HangboardDatabase? = null

        fun getInstance(context: Context): HangboardDatabase {
            synchronized(this){
                var instance = INSTANCE
                if (instance == null){
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