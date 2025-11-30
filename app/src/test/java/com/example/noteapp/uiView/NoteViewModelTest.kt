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
    private lateinit var fakeRepository: FakeNoteRepository

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        fakeRepository = FakeNoteRepository()

        viewModel = NoteViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `saveNote adds note to repository`() = runTest(testDispatcher) {

        val title = "Test Title"
        val content = "Test Content"

        viewModel.saveNote(title, content, -1)

        testDispatcher.scheduler.advanceUntilIdle()

        val savedNotes = fakeRepository.getNotesList()

        assertEquals("Бележката трябва да е записана", 1, savedNotes.size)
        assertEquals(title, savedNotes[0].title)
        assertEquals(content, savedNotes[0].content)
    }

}
