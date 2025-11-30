package com.example.noteapp.uiView

import com.example.noteapp.uiView.FakeNoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NoteViewModelTest {

    private lateinit var viewModel: NoteViewModel
    private lateinit var fakeRepository: FakeNoteRepository // Използваме нашия Fake

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // 1. Създаваме фалшивото репо
        fakeRepository = FakeNoteRepository()

        // 2. Подаваме го на ViewModel-а
        viewModel = NoteViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `saveNote adds note to repository`() = runTest(testDispatcher) {
        // Arrange
        val title = "Test Title"
        val content = "Test Content"

        // Act
        viewModel.saveNote(title, content, -1)

        // Изчакваме корутините
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert - Проверяваме директно във Fake Repository-то
        val savedNotes = fakeRepository.getNotesList() // Използваме новия метод

        assertEquals("Бележката трябва да е записана", 1, savedNotes.size)
        assertEquals(title, savedNotes[0].title)
        assertEquals(content, savedNotes[0].content)
    }

}
