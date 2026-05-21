package com.example.notestaking

import android.app.Application
import com.example.notestaking.di.AppContainer

class NoteNovaApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
