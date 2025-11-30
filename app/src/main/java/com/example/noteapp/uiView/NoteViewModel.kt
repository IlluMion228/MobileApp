package com.example.noteapp.uiView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.noteapp.NoteApplication
import com.example.noteapp.data.Note
import com.example.noteapp.data.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine
class NoteViewModel(private val repository: NoteRepository) : ViewModel() {
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText
    val notes: StateFlow<List<Note>> = repository.allNotes
        .combine(_searchText) { notes, text ->
            if (text.isBlank()) {
                notes
            } else {
                notes.filter {
                    it.title.contains(text, ignoreCase = true) ||
                            it.content.contains(text, ignoreCase = true)
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentNote = MutableStateFlow<Note?>(null)
    val currentNote: StateFlow<Note?> = _currentNote

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }
    fun loadNoteById(id: Int) {
        viewModelScope.launch {
            if (id != -1) {
                _currentNote.value = repository.getNote(id)
            } else {
                _currentNote.value = null
            }
        }
    }

    fun saveNote(title: String, content: String, id: Int = 0, imagePath: String? = null) {
        viewModelScope.launch {
            val noteId = if (id == -1) 0 else id
            repository.insertNote(Note(
                id = noteId,
                title = title,
                content = content,
                imagePath = imagePath ))
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }
    fun toggleNoteDone(note: Note) {
        viewModelScope.launch {
            repository.insertNote(note.copy(isDone = !note.isDone))
        }
    }
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as NoteApplication)
                NoteViewModel(application.repository)
            }
        }
    }
}
