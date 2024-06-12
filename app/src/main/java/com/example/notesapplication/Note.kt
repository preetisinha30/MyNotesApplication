package com.example.notesapplication

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName="Notes")
data class Note (
    @PrimaryKey
    @ColumnInfo(name="dateAdded")
    val dateAdded:Date,
    @ColumnInfo(name="noteText")
    var noteText:String
)


