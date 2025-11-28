package com.example.noteapp

import android.app.Application
import com.example.noteapp.data.NoteDatabase
import com.example.noteapp.data.NoteRepository

class NoteApplication : Application() {

    val database by lazy { NoteDatabase.getDatabase(this) }
    val repository by lazy { NoteRepository(database.noteDao()) }
}
