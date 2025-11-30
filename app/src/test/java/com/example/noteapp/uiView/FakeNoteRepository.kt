package com.example.noteapp.uiView

// 1. Поправени импорти
import com.example.noteapp.data.Note
import com.example.noteapp.data.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

// 2. Класът FakeNoteRepository
class FakeNoteRepository : NoteRepository(
    // Използваме mock за DAO, само за да удовлетворим конструктора на родителя.
    // Методите на това DAO никога няма да бъдат извикани, защото пренаписваме всичко долу.
    noteDao = org.mockito.Mockito.mock(com.example.noteapp.data.NoteDao::class.java)
) {

    // Нашата "база данни" в паметта (списък с бележки)
    private val notes = MutableStateFlow<List<Note>>(emptyList())

    // Пренаписваме (override) полето allNotes, за да връща нашия локален списък
    override val allNotes: Flow<List<Note>> = notes

    // Пренаписваме getNote
    override suspend fun getNote(id: Int): Note? {
        return notes.value.find { it.id == id }
    }

    // Пренаписваме insertNote (симулираме запис в база)
    override suspend fun insertNote(note: Note) {
        val currentList = notes.value.toMutableList()

        // Ако редактираме съществуваща бележка, махаме старата версия
        currentList.removeAll { it.id == note.id }

        // Добавяме новата/обновената бележка
        currentList.add(note)

        // Обновяваме Flow-а, което ще уведоми ViewModel-а
        notes.value = currentList
    }

    // Пренаписваме deleteNote
    override suspend fun deleteNote(note: Note) {
        val currentList = notes.value.toMutableList()
        currentList.remove(note)
        notes.value = currentList
    }

    fun getNotesList(): List<Note> {
        return notes.value
    }
}
