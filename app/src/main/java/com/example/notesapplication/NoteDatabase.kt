package com.example.notesapplication

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Note::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(NoteConverters::class)
abstract class NoteDatabase:RoomDatabase() {
    abstract fun noteDao():NoteDao

    companion object{

        private var INSTANCE:NoteDatabase?=null

        fun getDatabase(context: Context):NoteDatabase {
            if(INSTANCE==null)
            {
                synchronized(this) {
                    INSTANCE = buildDatabase(context)
                }
            }
            return INSTANCE!!
        }

        private fun buildDatabase(context: Context):NoteDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                NoteDatabase::class.java,
                "notes_database"
            ).build()
        }
    }
}