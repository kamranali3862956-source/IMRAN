package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ComplaintEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ComplaintDao {
    @Query("SELECT * FROM complaints ORDER BY createdAt DESC")
    fun getAllComplaints(): Flow<List<ComplaintEntity>>

    @Query("SELECT * FROM complaints WHERE userId = :userId ORDER BY createdAt DESC")
    fun getComplaintsByUserId(userId: Long): Flow<List<ComplaintEntity>>

    @Query("SELECT * FROM complaints WHERE id = :id LIMIT 1")
    fun getComplaintById(id: Long): Flow<ComplaintEntity?>

    @Query("SELECT * FROM complaints WHERE trackingCode = :code LIMIT 1")
    suspend fun getComplaintByTrackingCode(code: String): ComplaintEntity?

    @Query("SELECT * FROM complaints WHERE status = :status ORDER BY createdAt DESC")
    fun getComplaintsByStatus(status: String): Flow<List<ComplaintEntity>>

    @Query("SELECT COUNT(*) FROM complaints")
    fun getTotalComplaintsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM complaints WHERE status = :status")
    fun getCountByStatus(status: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComplaint(complaint: ComplaintEntity): Long

    @Update
    suspend fun updateComplaint(complaint: ComplaintEntity)

    @Delete
    suspend fun deleteComplaint(complaint: ComplaintEntity)

    @Query("SELECT COUNT(*) FROM complaints")
    suspend fun getComplaintsCountSync(): Int
}
