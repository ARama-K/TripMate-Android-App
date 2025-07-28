package com.example.travelapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.travelapp.User1.Use
import com.example.travelapp.dao.DiaryDao
import com.example.travelapp.entity.DiaryEntry
import java.util.concurrent.Executors

@Database(entities = [DiaryEntry::class], version = 2, exportSchema = false)
abstract class DiaryDatabase : RoomDatabase() {
    abstract fun diaryDao(): DiaryDao

    companion object {
        @Volatile
        private var INSTANCE: DiaryDatabase? = null

        val databaseWriteExecutor = Executors.newFixedThreadPool(4)

        fun getInstance(context: Context): DiaryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DiaryDatabase::class.java,
                    Use.getUserEmail() + "DiaryDatabase" // Corrected to use the right method
                )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
