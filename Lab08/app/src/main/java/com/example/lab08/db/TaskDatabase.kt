package com.example.lab08.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.lab08.dao.TaskDao
import com.example.lab08.model.Task

@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun TaskDao(): TaskDao
}