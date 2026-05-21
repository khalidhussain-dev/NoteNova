package com.example.notestaking.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.notestaking.ui.auth.LoginViewModel
import com.example.notestaking.ui.auth.RegisterViewModel
import com.example.notestaking.ui.home.HomeViewModel
import com.example.notestaking.ui.note.NoteEditorViewModel
import com.example.notestaking.ui.splash.SplashViewModel

class ViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SplashViewModel::class.java) ->
                SplashViewModel(container.sessionManager) as T
            modelClass.isAssignableFrom(LoginViewModel::class.java) ->
                LoginViewModel(container.authRepository) as T
            modelClass.isAssignableFrom(RegisterViewModel::class.java) ->
                RegisterViewModel(container.authRepository, container.sessionManager) as T
            modelClass.isAssignableFrom(HomeViewModel::class.java) ->
                HomeViewModel(
                    container.noteRepository,
                    container.authRepository,
                    container.sessionManager
                ) as T
            modelClass.isAssignableFrom(NoteEditorViewModel::class.java) ->
                NoteEditorViewModel(container.noteRepository, container.sessionManager) as T
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}
