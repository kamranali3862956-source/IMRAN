package com.example.data.repository

import com.example.data.dao.AnnouncementDao
import com.example.data.dao.ComplaintDao
import com.example.data.dao.UserDao
import com.example.data.model.AnnouncementEntity
import com.example.data.model.ComplaintEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val userDao: UserDao,
    private val complaintDao: ComplaintDao,
    private val announcementDao: AnnouncementDao
) {
    // User operations
    val allUsers: Flow<List<UserEntity>> = userDao.getAllUsers()

    fun getUserById(id: Long): Flow<UserEntity?> = userDao.getUserById(id)

    suspend fun getUserByPhoneOrEmail(identifier: String): UserEntity? =
        userDao.getUserByPhoneOrEmail(identifier)

    suspend fun authenticate(identifier: String, pass: String): UserEntity? =
        userDao.authenticate(identifier, pass)

    suspend fun registerUser(user: UserEntity): Long =
        userDao.insertUser(user)

    suspend fun updateUser(user: UserEntity) =
        userDao.updateUser(user)

    // Complaint operations
    val allComplaints: Flow<List<ComplaintEntity>> = complaintDao.getAllComplaints()

    fun getComplaintsByUser(userId: Long): Flow<List<ComplaintEntity>> =
        complaintDao.getComplaintsByUserId(userId)

    fun getComplaintById(id: Long): Flow<ComplaintEntity?> =
        complaintDao.getComplaintById(id)

    fun getComplaintsByStatus(status: String): Flow<List<ComplaintEntity>> =
        complaintDao.getComplaintsByStatus(status)

    val totalComplaintsCount: Flow<Int> = complaintDao.getTotalComplaintsCount()

    fun getCountByStatus(status: String): Flow<Int> = complaintDao.getCountByStatus(status)

    suspend fun registerComplaint(complaint: ComplaintEntity): Long =
        complaintDao.insertComplaint(complaint)

    suspend fun updateComplaint(complaint: ComplaintEntity) =
        complaintDao.updateComplaint(complaint)

    suspend fun deleteComplaint(complaint: ComplaintEntity) =
        complaintDao.deleteComplaint(complaint)

    // Announcements
    val allAnnouncements: Flow<List<AnnouncementEntity>> = announcementDao.getAllAnnouncements()

    suspend fun addAnnouncement(announcement: AnnouncementEntity): Long =
        announcementDao.insertAnnouncement(announcement)
}
