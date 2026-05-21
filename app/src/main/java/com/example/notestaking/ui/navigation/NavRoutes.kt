package com.example.notestaking.ui.navigation

object NavRoutes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val NOTE_EDITOR = "note_editor/{noteId}"
    const val NOTE_EDITOR_NEW = "note_editor/-1"

    fun noteEditor(noteId: Long) = "note_editor/$noteId"
}
