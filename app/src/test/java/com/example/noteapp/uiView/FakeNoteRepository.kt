package com.example.noteapp.uiView

import com.example.noteapp.data.Note
import com.example.noteapp.data.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeNoteRepository : NoteRepository(
    noteDao = org.mockito.Mockito.mock(com.example.noteapp.data.NoteDao::class.java)
) {

    private val notes = MutableStateFlow<List<Note>>(emptyList())

    override val allNotes: Flow<List<Note>> = notes

    override suspend fun getNote(id: Int): Note? {
        return notes.value.find { it.id == id }
    }

    override suspend fun insertNote(note: Note) {
        val currentList = notes.value.toMutableList()

        currentList.removeAll { it.id == note.id }

        currentList.add(note)

        notes.value = currentList
    }

    override suspend fun deleteNote(note: Note) {
        val currentList = notes.value.toMutableList()
        currentList.remove(note)
        notes.value = currentList
    }

    fun getNotesList(): List<Note> {
        return notes.value
    }
}
