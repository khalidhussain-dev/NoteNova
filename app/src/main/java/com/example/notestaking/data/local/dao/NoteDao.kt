package com.example.notestaking.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.notestaking.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes WHERE userId = :userId ORDER BY isPinned DESC, updatedAt DESC")
    fun observeNotes(userId: Long): Flow<List<NoteEntity>>

    @Query("SELECT COUNT(*) FROM notes WHERE userId = :userId")
    fun observeNoteCount(userId: Long): Flow<Int>

    @Query("SELECT * FROM notes WHERE id = :noteId AND userId = :userId LIMIT 1")
    suspend fun getNote(noteId: Long, userId: Long): NoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: NoteEntity): Long

    @Update
    suspend fun update(note: NoteEntity)

    @Delete
    suspend fun delete(note: NoteEntity)

    @Query(
        """
        SELECT * FROM notes WHERE userId = :userId
        AND (title LIKE '%' || :query || '%'
        OR content LIKE '%' || :query || '%'
        OR searchKeywords LIKE '%' || :query || '%')
        ORDER BY isPinned DESC, updatedAt DESC
        """
    )
    fun searchNotes(userId: Long, query: String): Flow<List<NoteEntity>>
}
