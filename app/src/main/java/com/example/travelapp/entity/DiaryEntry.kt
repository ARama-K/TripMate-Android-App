package com.example.travelapp.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary_entries")
class DiaryEntry {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null

    @ColumnInfo(name = "user_email")
    var userEmail: String = "" // ✅ NEW FIELD

    @ColumnInfo(name = "title")
    var title: String? = null

    @ColumnInfo(name = "date")
    var date: String? = null

    @ColumnInfo(name = "description")
    var description: String? = null

    @ColumnInfo(name = "weather")
    var weather: String? = null

    @ColumnInfo(name = "location")
    var location: String? = null

    @ColumnInfo(name = "fee")
    var fee: Int = 0

    @ColumnInfo(name = "rating")
    var rating: Int = 0

    @ColumnInfo(name = "updated")
    var updated: Boolean = false

    constructor(
        userEmail: String, // ✅ Add this to constructor
        title: String?,
        date: String?,
        description: String?,
        weather: String?,
        location: String?,
        fee: Int,
        rating: Int
    ) {
        this.userEmail = userEmail
        this.title = title
        this.date = date
        this.description = description
        this.weather = weather
        this.location = location
        this.fee = fee
        this.rating = rating
        this.updated = false
    }
}
