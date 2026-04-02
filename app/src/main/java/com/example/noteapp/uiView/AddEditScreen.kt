package com.example.noteapp.uiView

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.noteapp.utils.ShareUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    noteId: Int,
    navigateBack: () -> Unit,
    viewModel: NoteViewModel = viewModel(factory = NoteViewModel.Factory)
) {
    val context = LocalContext.current
    val currentNote by viewModel.currentNote.collectAsState()
    val aiResult by viewModel.aiResult.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var currentPhotoPath by remember { mutableStateOf<String?>(null) }
    var showAiDialog by remember { mutableStateOf(false) }

    fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = context.getExternalFilesDir(null)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imageUri = Uri.fromFile(File(currentPhotoPath!!))
        }
    }

    LaunchedEffect(noteId) {
        viewModel.loadNoteById(noteId)
    }

    LaunchedEffect(currentNote) {
        currentNote?.let {
            title = it.title
            content = it.content
            it.imagePath?.let { path ->
                currentPhotoPath = path
                imageUri = Uri.fromFile(File(path))
            }
        }
    }

    // AI Result Dialog
    if (aiResult != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearAiResult() },
            title = { Text("ИИ Предложение") },
            text = { Text(aiResult!!) },
            confirmButton = {
                TextButton(onClick = {
                    content = aiResult!!
                    viewModel.clearAiResult()
                }) {
                    Text("Използвай")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.clearAiResult() }) {
                    Text("Отказ")
                }
            }
        )
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
                    // AI Button
                    IconButton(onClick = { showAiDialog = true }) {
                        if (isAiLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.AutoAwesome, "AI Tools", tint = MaterialTheme.colorScheme.secondary)
                        }
                    }

                    // Camera Button
                    IconButton(onClick = {
                        val photoFile = createImageFile()
                        val photoURI: Uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.provider",
                            photoFile
                        )
                        cameraLauncher.launch(photoURI)
                    }) {
                        Icon(Icons.Default.CameraAlt, "Снимай")
                    }

                    if (title.isNotEmpty() || content.isNotEmpty()) {
                        IconButton(onClick = { ShareUtils.shareNote(context, title, content) }) {
                            Icon(Icons.Default.Share, "Сподели")
                        }
                    }
                    IconButton(onClick = {
                        if (title.isBlank()) {
                            Toast.makeText(context, "Заглавието не може да е празно", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.saveNote(title, content, noteId, currentPhotoPath)
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
        if (showAiDialog) {
            ModalBottomSheet(onDismissRequest = { showAiDialog = false }) {
                Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                    Text("AI Помощник", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            viewModel.summarizeNote(content)
                            showAiDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Обобщи текста")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            viewModel.improveNote(content)
                            showAiDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Подобри стилистично")
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.surface)
        ) {
            imageUri?.let { uri ->
                Box(modifier = Modifier.fillMaxWidth().height(250.dp)) {
                    AsyncImage(
                        model = uri,
                        contentDescription = "Note Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = {
                            imageUri = null
                            currentPhotoPath = null
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(Icons.Default.Delete, "Remove Image", tint = MaterialTheme.colorScheme.error)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

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
                    modifier = Modifier.fillMaxSize().padding(bottom = 100.dp)
                )
            }
        }
    }
}
