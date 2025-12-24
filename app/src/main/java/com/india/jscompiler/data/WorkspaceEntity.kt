package com.india.jscompiler.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workspaces")
data class WorkspaceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val code: String,
    val jsVersion: String,
    val lastUpdated: Long = System.currentTimeMillis()
)
