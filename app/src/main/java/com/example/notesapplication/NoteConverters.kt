package com.example.notesapplication

import androidx.room.TypeConverter
import java.util.Date

class NoteConverters {
    @TypeConverter
    fun timestampToDate(value:Long?):Date?{
        return value?.let{Date(it)}
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?):Long?{
        return date?.time
    }
}