package com.example.notestaking.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notestaking.data.local.entity.NoteEntity
import com.example.notestaking.data.repository.NoteFilter
import com.example.notestaking.data.repository.NoteSortOrder
import com.example.notestaking.di.ViewModelFactory
import com.example.notestaking.ui.components.EmptyState
import com.example.notestaking.ui.components.NoteCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    factory: ViewModelFactory,
    onNavigateToEditor: (Long) -> Unit,
    onLogout: () -> Unit
) {
    val viewModel: HomeViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showFilterMenu by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    var showProfileMenu by remember { mutableStateOf(false) }
    val darkTheme = isSystemInDarkTheme()

    LaunchedEffect(uiState.logoutRequested) {
        if (uiState.logoutRequested) {
            viewModel.consumeLogout()
            onLogout()
        }
    }

    LaunchedEffect(uiState.showUndoSnackbar) {
        if (uiState.showUndoSnackbar) {
            val result = snackbarHostState.showSnackbar(
                message = "Note deleted",
                actionLabel = "Undo"
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.undoDelete()
            } else {
                viewModel.dismissUndoSnackbar()
            }
        }
    }

    LaunchedEffect(uiState.showLimitSnackbar) {
        if (uiState.showLimitSnackbar) {
            snackbarHostState.showSnackbar(
                message = "Note limit reached (${uiState.maxNotes} max). Delete a note to add more."
            )
            viewModel.dismissLimitSnackbar()
        }
    }

    if (uiState.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = viewModel::dismissDeleteDialog,
            title = { Text("Delete note?") },
            text = { Text("This action can be undone from the snackbar.") },
            confirmButton = {
                TextButton(onClick = viewModel::confirmDelete) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissDeleteDialog) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("NoteNova", style = MaterialTheme.typography.titleLarge)
                        Text(
                            uiState.userName.ifBlank { "Your notes" },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterMenu = true }) {
                        Icon(Icons.Outlined.FilterList, contentDescription = "Filter")
                    }
                    DropdownMenu(expanded = showFilterMenu, onDismissRequest = { showFilterMenu = false }) {
                        NoteFilter.entries.forEach { filter ->
                            DropdownMenuItem(
                                text = { Text(filter.label()) },
                                onClick = {
                                    viewModel.onFilterChange(filter)
                                    showFilterMenu = false
                                }
                            )
                        }
                    }
                    IconButton(onClick = { showSortMenu = true }) {
                        Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort")
                    }
                    DropdownMenu(expanded = showSortMenu, onDismissRequest = { showSortMenu = false }) {
                        NoteSortOrder.entries.forEach { order ->
                            DropdownMenuItem(
                                text = { Text(order.label()) },
                                onClick = {
                                    viewModel.onSortChange(order)
                                    showSortMenu = false
                                }
                            )
                        }
                    }
                    IconButton(onClick = viewModel::toggleViewMode) {
                        Icon(
                            if (uiState.isGridView) Icons.AutoMirrored.Filled.ViewList else Icons.Filled.GridView,
                            contentDescription = "Toggle view"
                        )
                    }
                    IconButton(onClick = { showProfileMenu = true }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Profile menu")
                    }
                    DropdownMenu(expanded = showProfileMenu, onDismissRequest = { showProfileMenu = false }) {
                        DropdownMenuItem(
                            text = { Text(uiState.userEmail) },
                            onClick = {},
                            enabled = false
                        )
                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = {
                                showProfileMenu = false
                                viewModel.logout()
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onAddNoteClicked { onNavigateToEditor(-1L) } },
                containerColor = if (uiState.isLimitReached) {
                    MaterialTheme.colorScheme.surfaceVariant
                } else {
                    MaterialTheme.colorScheme.primary
                }
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add note",
                    tint = if (uiState.isLimitReached) {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    } else {
                        MaterialTheme.colorScheme.onPrimary
                    }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            NotesQuotaCard(uiState = uiState)

            androidx.compose.material3.OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search notes...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
                shape = MaterialTheme.shapes.large
            )

            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                uiState.notes.isEmpty() -> {
                    EmptyState(
                        title = when {
                            uiState.isLimitReached && uiState.searchQuery.isBlank() ->
                                "Note limit reached"
                            uiState.searchQuery.isBlank() -> "No notes yet"
                            else -> "No results"
                        },
                        subtitle = when {
                            uiState.isLimitReached && uiState.searchQuery.isBlank() ->
                                "Delete a note to free up space for new ones"
                            uiState.searchQuery.isBlank() -> "Tap + to capture your first idea"
                            else -> "Try a different search term"
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else -> {
                    AnimatedContent(
                        targetState = uiState.isGridView,
                        label = "notes_layout"
                    ) { grid ->
                        if (grid) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(uiState.notes, key = { it.id }) { note ->
                                    NoteItem(
                                        note = note,
                                        darkTheme = darkTheme,
                                        onClick = { onNavigateToEditor(note.id) },
                                        onDelete = { viewModel.requestDelete(note) }
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(uiState.notes, key = { it.id }) { note ->
                                    NoteItem(
                                        note = note,
                                        darkTheme = darkTheme,
                                        onClick = { onNavigateToEditor(note.id) },
                                        onDelete = { viewModel.requestDelete(note) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotesQuotaCard(uiState: HomeUiState) {
    val progress = (uiState.noteCount.toFloat() / uiState.maxNotes).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "quota_progress")
    val containerColor = when {
        uiState.isLimitReached -> MaterialTheme.colorScheme.errorContainer
        uiState.isNearLimit -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.primaryContainer
    }
    val contentColor = when {
        uiState.isLimitReached -> MaterialTheme.colorScheme.onErrorContainer
        uiState.isNearLimit -> MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.onPrimaryContainer
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${uiState.remainingNotes} / ${uiState.maxNotes} Notes Remaining",
                    style = MaterialTheme.typography.titleMedium,
                    color = contentColor
                )
                if (uiState.isNearLimit || uiState.isLimitReached) {
                    Icon(
                        Icons.Outlined.Warning,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.fillMaxWidth(),
                color = if (uiState.isLimitReached) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                },
                trackColor = contentColor.copy(alpha = 0.25f)
            )
            Text(
                text = when {
                    uiState.isLimitReached ->
                        "You've reached your note limit. Delete a note to create more."
                    uiState.isNearLimit ->
                        "You're almost at your limit — ${uiState.remainingNotes} slots left."
                    else ->
                        "${uiState.noteCount} of ${uiState.maxNotes} notes used"
                },
                style = MaterialTheme.typography.bodySmall,
                color = contentColor.copy(alpha = 0.85f)
            )
        }
    }
}

@Composable
private fun NoteItem(
    note: NoteEntity,
    darkTheme: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    NoteCard(
        note = note,
        darkTheme = darkTheme,
        onClick = onClick,
        onDelete = onDelete
    )
}

private fun NoteFilter.label(): String = when (this) {
    NoteFilter.ALL -> "All notes"
    NoteFilter.PINNED -> "Pinned"
    NoteFilter.FAVORITES -> "Favorites"
}

private fun NoteSortOrder.label(): String = when (this) {
    NoteSortOrder.UPDATED_DESC -> "Recently updated"
    NoteSortOrder.UPDATED_ASC -> "Oldest first"
    NoteSortOrder.TITLE_ASC -> "Title A–Z"
    NoteSortOrder.TITLE_DESC -> "Title Z–A"
}
