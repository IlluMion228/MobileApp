package com.example.noteapp.utils

import android.content.Context
import android.content.Intent

object ShareUtils {    fun shareNote(context: Context, title: String, content: String) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "$title\n\n$content")
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, "Сподели бележката чрез:")

    context.startActivity(shareIntent)
}
}
