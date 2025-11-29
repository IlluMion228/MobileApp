package com.example.noteapp.uiView

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.noteapp.uiView.NoteViewModel // Уверете се, че import-a сочи към правилния пакет на ViewModel
import com.example.noteapp.utils.ShareUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    noteId: Int, // ID-то на бележката (-1 за нова)
    navigateBack: () -> Unit,
    viewModel: NoteViewModel = viewModel(factory = NoteViewModel.Factory)
) {
    val context = LocalContext.current
    val currentNote by viewModel.currentNote.collectAsState()

    // Състояние за текстовите полета
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    // 1. При стартиране на екрана, казваме на ViewModel да зареди данните (ако е редакция)
    LaunchedEffect(noteId) {
        viewModel.loadNoteById(noteId)
    }

    // 2. Когато ViewModel върне данни (от базата), попълваме полетата
    LaunchedEffect(currentNote) {
        currentNote?.let {
            title = it.title
            content = it.content
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (noteId == -1) "Нова бележка" else "Редакция") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        // Използваме AutoMirrored за правилна посока на стрелката при различни езици
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    // --- БОНУС ФУНКЦИОНАЛНОСТ (Оценка 6) ---
                    // Показваме бутон за споделяне само ако има текст
                    if (title.isNotEmpty() || content.isNotEmpty()) {
                        IconButton(onClick = {
                            ShareUtils.shareNote(context, title, content)
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Сподели")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (title.isBlank()) {
                    Toast.makeText(context, "Моля, въведете заглавие", Toast.LENGTH_SHORT).show()
                } else {
                    // Запазваме и се връщаме назад
                    viewModel.saveNote(title, content, noteId)
                    navigateBack()
                }
            }) {
                Icon(Icons.Default.Check, contentDescription = "Запиши")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Поле за заглавие
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Заглавие") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Съдържание") },
                modifier = Modifier
                    .fillMaxSize()
                    .fillMaxHeight(),
                textStyle = LocalTextStyle.current.copy(
                    textAlign = androidx.compose.ui.text.style.TextAlign.Start
                ),
                shape = MaterialTheme.shapes.medium
            )

        }
    }
}
