package com.example.travelapp.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.travelapp.entity.DiaryEntry

@Dao    
interface DiaryDao {
    @get:Query("SELECT * FROM diary_entries")
    val all: LiveData<List<DiaryEntry>>

    @Query("SELECT * FROM diary_entries WHERE id = :diaryid")
    fun findByID(diaryid: Int): DiaryEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(diary: DiaryEntry): Long

    @Delete
    fun delete(diary: DiaryEntry)

    @Update
    fun updateDiary(diary: DiaryEntry)

    @Query("DELETE FROM diary_entries")
    fun deleteAll()

    @get:Query("SELECT fee FROM diary_entries")
    val fee: List<Int>

    @get:Query("SELECT rating FROM diary_entries")
    val rating: List<Int>

    @get:Query("SELECT location FROM diary_entries")
    val location: List<String>

    @get:Query("SELECT id FROM diary_entries ORDER BY id DESC")
    val id: LiveData<List<Int>>

    @get:Query("SELECT date FROM diary_entries")
    val date: List<String>

    @Query("UPDATE diary_entries SET updated = :updated WHERE id = :diaryId")
    fun setUpdated(diaryId: Int, updated: Boolean)

    @Query("SELECT updated FROM diary_entries WHERE id = :diaryId")
    fun getUpdated(diaryId: Int): Boolean

    @Query("SELECT * FROM diary_entries WHERE user_email = :email")
    fun getAllDiariesForUser(email: String): LiveData<List<DiaryEntry>>

    @Query("SELECT * FROM diary_entries WHERE id = :diaryId AND user_email = :email")
    fun findDiaryByIdForUser(diaryId: Int, email: String): DiaryEntry?

}

