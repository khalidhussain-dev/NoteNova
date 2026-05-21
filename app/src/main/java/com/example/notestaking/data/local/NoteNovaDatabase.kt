package com.example.notestaking.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.notestaking.data.local.dao.NoteDao
import com.example.notestaking.data.local.dao.UserDao
import com.example.notestaking.data.local.entity.NoteEntity
import com.example.notestaking.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class, NoteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class NoteNovaDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun noteDao(): NoteDao
}
