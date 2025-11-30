package com.example.noteapp.uiView

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.noteapp.utils.ShareUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    noteId: Int,
    navigateBack: () -> Unit,
    viewModel: NoteViewModel = viewModel(factory = NoteViewModel.Factory)
) {
    val context = LocalContext.current
    val currentNote by viewModel.currentNote.collectAsState()

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    LaunchedEffect(noteId) {
        viewModel.loadNoteById(noteId)
    }

    LaunchedEffect(currentNote) {
        currentNote?.let {
            title = it.title
            content = it.content
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад")
                    }
                },
                actions = {
                    if (title.isNotEmpty() || content.isNotEmpty()) {
                        IconButton(onClick = {
                            ShareUtils.shareNote(context, title, content)
                        }) {
                            Icon(Icons.Default.Share, "Сподели")
                        }
                    }
                    // Бутон "Запиши" горе в лентата (по-удобно)
                    IconButton(onClick = {
                        if (title.isBlank()) {
                            Toast.makeText(context, "Заглавието не може да е празно", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.saveNote(title, content, noteId)
                            navigateBack()
                        }
                    }) {
                        Icon(Icons.Default.Check, "Запиши", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .background(MaterialTheme.colorScheme.surface) // Бял/Тъмен фон
        ) {
            Box(modifier = Modifier.padding(vertical = 10.dp)) {
                if (title.isEmpty()) {
                    Text(
                        text = "Заглавие",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    )
                }
                BasicTextField(
                    value = title,
                    onValueChange = { title = it },
                    textStyle = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(modifier = Modifier.fillMaxSize()) {
                if (content.isEmpty()) {
                    Text(
                        text = "Започнете да пишете тук...",
                        style = TextStyle(
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    )
                }
                BasicTextField(
                    value = content,
                    onValueChange = { content = it },
                    textStyle = TextStyle(
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 24.sp
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
