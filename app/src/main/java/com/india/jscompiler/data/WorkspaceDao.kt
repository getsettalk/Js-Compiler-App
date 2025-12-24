package com.india.jscompiler.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkspaceDao {
    @Query("SELECT * FROM workspaces ORDER BY lastUpdated DESC")
    fun getAllWorkspaces(): Flow<List<WorkspaceEntity>>

    @Query("SELECT * FROM workspaces WHERE id = :id")
    suspend fun getWorkspaceById(id: Long): WorkspaceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkspace(workspace: WorkspaceEntity): Long

    @Update
    suspend fun updateWorkspace(workspace: WorkspaceEntity)

    @Delete
    suspend fun deleteWorkspace(workspace: WorkspaceEntity)
}
