package com.example.notestaking.data.repository

import com.example.notestaking.data.local.dao.NoteDao
import com.example.notestaking.data.local.entity.NoteEntity
import com.example.notestaking.util.NoteLimits
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

enum class NoteSortOrder {
    UPDATED_DESC,
    UPDATED_ASC,
    TITLE_ASC,
    TITLE_DESC
}

enum class NoteFilter {
    ALL,
    PINNED,
    FAVORITES
}

class NoteRepository(private val noteDao: NoteDao) {

    fun observeNoteCount(userId: Long): Flow<Int> = noteDao.observeNoteCount(userId)

    fun canCreateNote(currentCount: Int): Boolean = currentCount < NoteLimits.MAX_NOTES_PER_USER

    fun observeNotes(userId: Long, searchQuery: String): Flow<List<NoteEntity>> {
        return if (searchQuery.isBlank()) {
            noteDao.observeNotes(userId)
        } else {
            noteDao.searchNotes(userId, searchQuery.trim())
        }
    }

    suspend fun getNote(noteId: Long, userId: Long): NoteEntity? =
        noteDao.getNote(noteId, userId)

    suspend fun saveNote(note: NoteEntity): Result<Long> {
        val keywords = buildSearchKeywords(note.title, note.content)
        val enriched = note.copy(searchKeywords = keywords)
        return if (note.id == 0L) {
            val count = noteDao.observeNoteCount(note.userId).first()
            if (!canCreateNote(count)) {
                Result.failure(Exception("Note limit reached (${NoteLimits.MAX_NOTES_PER_USER} max)"))
            } else {
                Result.success(noteDao.insert(enriched))
            }
        } else {
            noteDao.update(enriched)
            Result.success(note.id)
        }
    }

    suspend fun deleteNote(note: NoteEntity) {
        noteDao.delete(note)
    }

    fun applyFilterAndSort(
        notes: List<NoteEntity>,
        filter: NoteFilter,
        sortOrder: NoteSortOrder
    ): List<NoteEntity> {
        val filtered = when (filter) {
            NoteFilter.ALL -> notes
            NoteFilter.PINNED -> notes.filter { it.isPinned }
            NoteFilter.FAVORITES -> notes.filter { it.isFavorite }
        }
        return when (sortOrder) {
            NoteSortOrder.UPDATED_DESC -> filtered.sortedWith(
                compareByDescending<NoteEntity> { it.isPinned }
                    .thenByDescending { it.updatedAt }
            )
            NoteSortOrder.UPDATED_ASC -> filtered.sortedWith(
                compareByDescending<NoteEntity> { it.isPinned }
                    .thenBy { it.updatedAt }
            )
            NoteSortOrder.TITLE_ASC -> filtered.sortedWith(
                compareByDescending<NoteEntity> { it.isPinned }
                    .thenBy { it.title.lowercase() }
            )
            NoteSortOrder.TITLE_DESC -> filtered.sortedWith(
                compareByDescending<NoteEntity> { it.isPinned }
                    .thenByDescending { it.title.lowercase() }
            )
        }
    }

    private fun buildSearchKeywords(title: String, content: String): String {
        return "$title $content".lowercase()
            .replace(Regex("\\s+"), " ")
            .trim()
    }
}
