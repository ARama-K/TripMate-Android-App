package com.example.travelapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.travelapp.User1.Use
import com.example.travelapp.entity.DiaryEntry
import com.example.travelapp.repository.DiaryRepository
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class DiaryViewModel(application: Application) : AndroidViewModel(application) {
    private val firedb = FirebaseFirestore.getInstance()
    private val dRepository = DiaryRepository(application)
    var allDiary: LiveData<List<DiaryEntry>> = dRepository.allDiary

    fun insert(diaryEntry: DiaryEntry) = viewModelScope.launch {
        val id = dRepository.insert(diaryEntry)
        diaryEntry.id = id
        val email = Use.getUserEmail() ?: return@launch
        firedb.collection("users")
            .document(email)
            .collection("diary_entries")
            .document(id.toString())
            .set(diaryEntry)
    }

    val allId: LiveData<List<Int>>
        get() = dRepository.allId

    fun findDiarybyId(diaryId: Int): DiaryEntry? {
        return dRepository.findDiarybyId(diaryId)
    }

    val rating: List<Int>
        get() = dRepository.rating

    val fee: List<Int>
        get() = dRepository.fee

    val location: List<String>
        get() = dRepository.location

    val date: List<String>
        get() = dRepository.date

    fun getUpdated(diaryId: Int): Boolean {
        return dRepository.getUpdated(diaryId)
    }

    fun SynchronizeGetData() {
        val localId = allId.value
        val localDocId = localId?.map { it.toString() } ?: listOf()
        val email = Use.getUserEmail() ?: return
        val diaryRef = firedb.collection("users").document(email).collection("diary_entries")

        val fetchTask = if (localDocId.isNotEmpty()) {
            diaryRef.whereNotIn(FieldPath.documentId(), localDocId).get()
        } else {
            diaryRef.get()
        }

        fetchTask.addOnSuccessListener { snapshot ->
            for (doc in snapshot) {
                val diary = doc.toObject(DiaryEntry::class.java)
                diary.updated = true
                diary.id = doc.id.toInt()
                viewModelScope.launch {
                    dRepository.insert(diary)
                }
            }
        }
    }
}
