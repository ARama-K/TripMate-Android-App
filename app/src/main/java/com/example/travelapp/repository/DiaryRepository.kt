package com.example.travelapp.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.travelapp.dao.DiaryDao
import com.example.travelapp.database.DiaryDatabase
import com.example.travelapp.entity.DiaryEntry

class DiaryRepository(application: Application) {
    private val diaryDao: DiaryDao = DiaryDatabase.getInstance(application)!!.diaryDao()
    val allDiary: LiveData<List<DiaryEntry>> = diaryDao.all

    suspend fun insert(diaryEntry: DiaryEntry): Int {
        val id = diaryDao.insert(diaryEntry).toInt()
        diaryEntry.id = id
        return id
    }


    val allId: LiveData<List<Int>>
        get() = diaryDao.id

    fun findDiarybyId(diaryId: Int): DiaryEntry? {
        return diaryDao.findByID(diaryId)
    }

    val rating: List<Int>
        get() = diaryDao.rating

    val fee: List<Int>
        get() = diaryDao.fee

    val location: List<String>
        get() = diaryDao.location

    val date: List<String>
        get() = diaryDao.date

    fun setUpdate(diaryId: Int, updated: Boolean) {
        DiaryDatabase.databaseWriteExecutor.execute { diaryDao.setUpdated(diaryId, updated) }
    }

    fun getUpdated(diaryId: Int): Boolean {
        return diaryDao.getUpdated(diaryId)
    }

    fun getAllDiariesForUser(email: String): LiveData<List<DiaryEntry>> {
        return diaryDao.getAllDiariesForUser(email)
    }

    fun findDiaryByIdForUser(diaryId: Int, email: String): DiaryEntry? {
        return diaryDao.findDiaryByIdForUser(diaryId, email)
    }

}
