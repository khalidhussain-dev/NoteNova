package com.example.notestaking.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notestaking.data.local.entity.NoteEntity
import com.example.notestaking.data.preferences.SessionManager
import com.example.notestaking.data.preferences.SessionState
import com.example.notestaking.data.repository.AuthRepository
import com.example.notestaking.data.repository.NoteFilter
import com.example.notestaking.data.repository.NoteRepository
import com.example.notestaking.data.repository.NoteSortOrder
import com.example.notestaking.util.NoteLimits
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val userName: String = "",
    val userEmail: String = "",
    val notes: List<NoteEntity> = emptyList(),
    val noteCount: Int = 0,
    val maxNotes: Int = NoteLimits.MAX_NOTES_PER_USER,
    val searchQuery: String = "",
    val filter: NoteFilter = NoteFilter.ALL,
    val sortOrder: NoteSortOrder = NoteSortOrder.UPDATED_DESC,
    val isGridView: Boolean = true,
    val isLoading: Boolean = true,
    val showDeleteDialog: Boolean = false,
    val noteToDelete: NoteEntity? = null,
    val pendingUndoNote: NoteEntity? = null,
    val showUndoSnackbar: Boolean = false,
    val showLimitSnackbar: Boolean = false,
    val logoutRequested: Boolean = false
) {
    val remainingNotes: Int get() = (maxNotes - noteCount).coerceAtLeast(0)
    val isLimitReached: Boolean get() = noteCount >= maxNotes
    val isNearLimit: Boolean get() = noteCount >= NoteLimits.NEAR_LIMIT_THRESHOLD && !isLimitReached
}

class HomeViewModel(
    private val noteRepository: NoteRepository,
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val filter = MutableStateFlow(NoteFilter.ALL)
    private val sortOrder = MutableStateFlow(NoteSortOrder.UPDATED_DESC)
    private val isGridView = MutableStateFlow(true)
    private val userId = MutableStateFlow(-1L)
    private val userName = MutableStateFlow("")
    private val userEmail = MutableStateFlow("")
    private val rawNotes = MutableStateFlow<List<NoteEntity>>(emptyList())
    private val noteCount = MutableStateFlow(0)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            sessionManager.sessionFlow.collect { session ->
                when (session) {
                    is SessionState.LoggedIn -> {
                        userId.value = session.userId
                        userName.value = session.fullName
                        userEmail.value = session.email
                    }
                    SessionState.LoggedOut -> {
                        _uiState.update { it.copy(logoutRequested = true) }
                    }
                }
            }
        }

        viewModelScope.launch {
            userId.flatMapLatest { id ->
                if (id <= 0) kotlinx.coroutines.flow.flowOf(0)
                else noteRepository.observeNoteCount(id)
            }.collect { count -> noteCount.value = count }
        }

        viewModelScope.launch {
            combine(userId, searchQuery) { id, query -> id to query }
                .flatMapLatest { (id, query) ->
                    if (id <= 0) {
                        kotlinx.coroutines.flow.flowOf(emptyList())
                    } else {
                        noteRepository.observeNotes(id, query)
                    }
                }
                .collect { notes -> rawNotes.value = notes }
        }

        viewModelScope.launch {
            combine(
                combine(rawNotes, filter, sortOrder, noteCount) { notes, f, s, count ->
                    Triple(notes, f, s) to count
                },
                combine(isGridView, searchQuery, userName, userEmail) { grid, query, name, email ->
                    listOf(grid, query, name, email)
                }
            ) { noteData, profileData ->
                val (triple, count) = noteData
                val (notes, f, s) = triple
                HomeUiState(
                    userName = profileData[2] as String,
                    userEmail = profileData[3] as String,
                    notes = noteRepository.applyFilterAndSort(notes, f, s),
                    noteCount = count,
                    searchQuery = profileData[1] as String,
                    filter = f,
                    sortOrder = s,
                    isGridView = profileData[0] as Boolean,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.update { current ->
                    state.copy(
                        showDeleteDialog = current.showDeleteDialog,
                        noteToDelete = current.noteToDelete,
                        pendingUndoNote = current.pendingUndoNote,
                        showUndoSnackbar = current.showUndoSnackbar,
                        showLimitSnackbar = current.showLimitSnackbar,
                        logoutRequested = current.logoutRequested
                    )
                }
            }
        }
    }

    fun onSearchChange(query: String) {
        searchQuery.value = query
    }

    fun onFilterChange(newFilter: NoteFilter) {
        filter.value = newFilter
    }

    fun onSortChange(order: NoteSortOrder) {
        sortOrder.value = order
    }

    fun toggleViewMode() {
        isGridView.value = !isGridView.value
    }

    fun onAddNoteClicked(onNavigate: () -> Unit) {
        if (_uiState.value.isLimitReached) {
            _uiState.update { it.copy(showLimitSnackbar = true) }
        } else {
            onNavigate()
        }
    }

    fun dismissLimitSnackbar() {
        _uiState.update { it.copy(showLimitSnackbar = false) }
    }

    fun requestDelete(note: NoteEntity) {
        _uiState.update { it.copy(showDeleteDialog = true, noteToDelete = note) }
    }

    fun dismissDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = false, noteToDelete = null) }
    }

    fun confirmDelete() {
        val note = _uiState.value.noteToDelete ?: return
        viewModelScope.launch {
            noteRepository.deleteNote(note)
            _uiState.update {
                it.copy(
                    showDeleteDialog = false,
                    noteToDelete = null,
                    pendingUndoNote = note,
                    showUndoSnackbar = true
                )
            }
        }
    }

    fun undoDelete() {
        val note = _uiState.value.pendingUndoNote ?: return
        viewModelScope.launch {
            val result = noteRepository.saveNote(note)
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(pendingUndoNote = null, showUndoSnackbar = false) }
                },
                onFailure = {
                    _uiState.update {
                        it.copy(
                            pendingUndoNote = null,
                            showUndoSnackbar = false,
                            showLimitSnackbar = true
                        )
                    }
                }
            )
        }
    }

    fun dismissUndoSnackbar() {
        _uiState.update { it.copy(pendingUndoNote = null, showUndoSnackbar = false) }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.update { it.copy(logoutRequested = true) }
        }
    }

    fun consumeLogout() = _uiState.update { it.copy(logoutRequested = false) }
}
