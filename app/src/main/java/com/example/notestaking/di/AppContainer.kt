package com.example.notestaking.di

import android.content.Context
import androidx.room.Room
import com.example.notestaking.data.local.NoteNovaDatabase
import com.example.notestaking.data.preferences.SessionManager
import com.example.notestaking.data.repository.AuthRepository
import com.example.notestaking.data.repository.NoteRepository

class AppContainer(context: Context) {

    private val database: NoteNovaDatabase = Room.databaseBuilder(
        context.applicationContext,
        NoteNovaDatabase::class.java,
        "note_nova_db"
    ).build()

    val sessionManager = SessionManager(context.applicationContext)
    val authRepository = AuthRepository(database.userDao(), sessionManager)
    val noteRepository = NoteRepository(database.noteDao())
}
