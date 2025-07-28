package com.example.travelapp.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary_entries")
class DiaryEntry {

    @PrimaryKey(autoGenerate = true)
    var id: Int? = null

    @ColumnInfo(name = "user_email")
    var userEmail: String = ""

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

    @ColumnInfo(name = "image_url")
    var imageUrl: String? = null


    // ✅ Required no-arg constructor for Firestore
    constructor()

    constructor(
        userEmail: String,
        title: String?,
        date: String?,
        description: String?,
        weather: String?,
        location: String?,
        fee: Int,
        rating: Int,
        imageUrl: String? // <-- add this

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
        this.imageUrl = imageUrl // Add this

    }
}
