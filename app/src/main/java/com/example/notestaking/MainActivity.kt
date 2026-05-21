package com.example.notestaking

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.notestaking.di.ViewModelFactory
import com.example.notestaking.ui.navigation.NoteNovaNavGraph
import com.example.notestaking.ui.theme.NoteNovaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as NoteNovaApplication
        val factory = ViewModelFactory(app.container)
        setContent {
            NoteNovaTheme {
                NoteNovaNavGraph(factory = factory)
            }
        }
    }
}
