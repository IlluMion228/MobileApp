package com.example.noteapp.data

import kotlinx.coroutines.flow.Flow

open class NoteRepository(private val noteDao: NoteDao) {

    open val allNotes: Flow<List<Note>> = noteDao.getAllNotes()

    open suspend fun getNote(id: Int): Note? {
        return noteDao.getNoteById(id)
    }

    open suspend fun insertNote(note: Note) {
        noteDao.insertNote(note)
    }

    open suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note)
    }
}
