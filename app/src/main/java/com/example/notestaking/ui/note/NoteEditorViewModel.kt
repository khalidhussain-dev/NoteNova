package com.example.notestaking.ui.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notestaking.data.local.entity.NoteEntity
import com.example.notestaking.data.preferences.SessionManager
import com.example.notestaking.data.preferences.SessionState
import com.example.notestaking.data.repository.NoteRepository
import com.example.notestaking.util.NoteLimits
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NoteEditorUiState(
    val noteId: Long = -1L,
    val userId: Long = -1L,
    val title: String = "",
    val content: String = "",
    val isPinned: Boolean = false,
    val isFavorite: Boolean = false,
    val colorCategory: Int = 0,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val lastSavedAt: Long? = null,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null,
    val isLimitReached: Boolean = false,
    val showDeleteDialog: Boolean = false
)

class NoteEditorViewModel(
    private val noteRepository: NoteRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoteEditorUiState())
    val uiState: StateFlow<NoteEditorUiState> = _uiState.asStateFlow()
    private var autoSaveJob: Job? = null

    fun loadNote(noteId: Long) {
        viewModelScope.launch {
            val session = sessionManager.sessionFlow.first()
            val userId = (session as? SessionState.LoggedIn)?.userId ?: return@launch
            if (noteId <= 0) {
                val count = noteRepository.observeNoteCount(userId).first()
                val limitReached = !noteRepository.canCreateNote(count)
                _uiState.value = NoteEditorUiState(
                    noteId = 0,
                    userId = userId,
                    isLoading = false,
                    isLimitReached = limitReached,
                    errorMessage = if (limitReached) {
                        "You've reached the ${NoteLimits.MAX_NOTES_PER_USER} note limit. Delete a note to create a new one."
                    } else null
                )
                return@launch
            }
            val note = noteRepository.getNote(noteId, userId)
            if (note != null) {
                _uiState.value = NoteEditorUiState(
                    noteId = note.id,
                    userId = userId,
                    title = note.title,
                    content = note.content,
                    isPinned = note.isPinned,
                    isFavorite = note.isFavorite,
                    colorCategory = note.colorCategory,
                    isLoading = false
                )
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Note not found") }
            }
        }
    }

    fun onTitleChange(value: String) {
        if (_uiState.value.isLimitReached && _uiState.value.noteId == 0L) return
        _uiState.update { it.copy(title = value) }
        scheduleAutoSave()
    }

    fun onContentChange(value: String) {
        if (_uiState.value.isLimitReached && _uiState.value.noteId == 0L) return
        _uiState.update { it.copy(content = value) }
        scheduleAutoSave()
    }

    fun togglePin() {
        _uiState.update { it.copy(isPinned = !it.isPinned) }
        scheduleAutoSave()
    }

    fun toggleFavorite() {
        _uiState.update { it.copy(isFavorite = !it.isFavorite) }
        scheduleAutoSave()
    }

    fun setColorCategory(category: Int) {
        _uiState.update { it.copy(colorCategory = category) }
        scheduleAutoSave()
    }

    fun requestDelete() {
        if (_uiState.value.noteId > 0) {
            _uiState.update { it.copy(showDeleteDialog = true) }
        }
    }

    fun dismissDeleteDialog() = _uiState.update { it.copy(showDeleteDialog = false) }

    fun confirmDelete(onDeleted: () -> Unit) {
        val state = _uiState.value
        if (state.noteId <= 0) return
        viewModelScope.launch {
            val note = noteRepository.getNote(state.noteId, state.userId) ?: return@launch
            noteRepository.deleteNote(note)
            _uiState.update { it.copy(showDeleteDialog = false) }
            onDeleted()
        }
    }

    fun saveNote(onSaved: () -> Unit = {}) {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.userId <= 0 || state.isLimitReached && state.noteId == 0L) return@launch
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val now = System.currentTimeMillis()
            val existing = if (state.noteId > 0) {
                noteRepository.getNote(state.noteId, state.userId)
            } else null
            val note = NoteEntity(
                id = state.noteId,
                userId = state.userId,
                title = state.title.trim(),
                content = state.content.trim(),
                createdAt = existing?.createdAt ?: now,
                updatedAt = now,
                isPinned = state.isPinned,
                isFavorite = state.isFavorite,
                colorCategory = state.colorCategory
            )
            noteRepository.saveNote(note).fold(
                onSuccess = { savedId ->
                    _uiState.update {
                        it.copy(
                            noteId = savedId,
                            isSaving = false,
                            lastSavedAt = now,
                            saveSuccess = true
                        )
                    }
                    onSaved()
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = e.message,
                            isLimitReached = true
                        )
                    }
                }
            )
        }
    }

    fun consumeSaveSuccess() = _uiState.update { it.copy(saveSuccess = false) }

    private fun scheduleAutoSave() {
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            delay(1200)
            val state = _uiState.value
            if (state.isLimitReached && state.noteId == 0L) return@launch
            if (state.title.isNotBlank() || state.content.isNotBlank()) {
                saveNote()
            }
        }
    }
}
