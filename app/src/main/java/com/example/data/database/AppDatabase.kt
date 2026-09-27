package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.AnnouncementDao
import com.example.data.dao.ComplaintDao
import com.example.data.dao.UserDao
import com.example.data.model.AnnouncementEntity
import com.example.data.model.ComplaintEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [UserEntity::class, ComplaintEntity::class, AnnouncementEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun complaintDao(): ComplaintDao
    abstract fun announcementDao(): AnnouncementDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "chashma_goth_complaints.db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database)
                }
            }
        }

        private suspend fun populateInitialData(db: AppDatabase) {
            val userDao = db.userDao()
            val complaintDao = db.complaintDao()
            val announcementDao = db.announcementDao()

            // 1. Seed Default Admin
            val adminId = userDao.insertUser(
                UserEntity(
                    id = 1,
                    name = "Youth Admin Desk",
                    phone = "03001122334",
                    email = "admin@chashmagoth.org",
                    password = "admin",
                    area = "Central Youth Office, Chashma Goth",
                    role = "ADMIN"
                )
            )

            // 2. Seed Default Residents
            val residentId1 = userDao.insertUser(
                UserEntity(
                    id = 2,
                    name = "Kamran Ali",
                    phone = "03001234567",
                    email = "kamran@gmail.com",
                    password = "user123",
                    area = "Fishermen Colony",
                    role = "RESIDENT"
                )
            )

            val residentId2 = userDao.insertUser(
                UserEntity(
                    id = 3,
                    name = "Bilal Hussain",
                    phone = "03339876543",
                    email = "bilal@gmail.com",
                    password = "user123",
                    area = "Rehri Road Sector",
                    role = "RESIDENT"
                )
            )

            // 3. Seed Realistic Community Complaints
            val now = System.currentTimeMillis()
            complaintDao.insertComplaint(
                ComplaintEntity(
                    id = 1,
                    trackingCode = "CG-2026-001",
                    userId = residentId1,
                    userName = "Kamran Ali",
                    userPhone = "03001234567",
                    userArea = "Fishermen Colony",
                    category = "WATER",
                    title = "Main Water Supply Line Leakage",
                    description = "Pichlay 3 din se meethay paani ki main line phati hui hai. Sara paani gali mein zaya ho raha hai aur aage gharon mein pani ka dabao nahi aa raha.",
                    locationDetails = "Gali #4, Fishermen Colony, Near Purana Kuan",
                    priority = "HIGH",
                    status = "IN_PROGRESS",
                    adminRemarks = "Youth water team inspected the site. KWSB valve man notified; replacement 3-inch PVC joint arranged for today.",
                    assignedTo = "Water Supply Youth Wing",
                    createdAt = now - (36 * 3600 * 1000L),
                    updatedAt = now - (6 * 3600 * 1000L)
                )
            )

            complaintDao.insertComplaint(
                ComplaintEntity(
                    id = 2,
                    trackingCode = "CG-2026-002",
                    userId = residentId2,
                    userName = "Bilal Hussain",
                    userPhone = "03339876543",
                    userArea = "Rehri Road Sector",
                    category = "ELECTRICITY",
                    title = "PMT Sparking & Severe Low Voltage",
                    description = "Transformer se bar bar chingariyan nikal rahi hain aur raat ko voltage 140V tak gir jati hai jis se motor aur fridge chalna mushkil ho gaya hai.",
                    locationDetails = "Near Jamia Masjid Noor, Pole #14, Rehri Road Sector",
                    priority = "EMERGENCY",
                    status = "RESOLVED",
                    adminRemarks = "K-Electric bin Qasim sub-station escalated. Linemen repaired jumper wire and balanced phase load. Voltage restored to normal 220V.",
                    assignedTo = "Electricity Coordination Cell",
                    citizenRating = 5,
                    citizenFeedback = "Bohat shukriya Young Generation team! 4 ghantay ke andar KE team bula kar masla hal karwaya.",
                    createdAt = now - (72 * 3600 * 1000L),
                    updatedAt = now - (12 * 3600 * 1000L)
                )
            )

            complaintDao.insertComplaint(
                ComplaintEntity(
                    id = 3,
                    trackingCode = "CG-2026-003",
                    userId = residentId1,
                    userName = "Kamran Ali",
                    userPhone = "03001234567",
                    userArea = "Fishermen Colony",
                    category = "SANITATION",
                    title = "Gutter Overflow & Garbage Accumulation",
                    description = "Main school road par gutter ubal raha hai aur kachra jama ho gaya hai. Bachon ko school aane jane mein shaded pareshani hai.",
                    locationDetails = "Opposite Govt Primary School, Street #2",
                    priority = "HIGH",
                    status = "PENDING",
                    adminRemarks = "Complaint received and verified. Assigned to Youth volunteer squad for Friday cleanliness drive.",
                    assignedTo = "Sanitation Youth Squad",
                    createdAt = now - (5 * 3600 * 1000L),
                    updatedAt = now - (5 * 3600 * 1000L)
                )
            )

            complaintDao.insertComplaint(
                ComplaintEntity(
                    id = 4,
                    trackingCode = "CG-2026-004",
                    userId = residentId2,
                    userName = "Bilal Hussain",
                    userPhone = "03339876543",
                    userArea = "Rehri Road Sector",
                    category = "ROADS",
                    title = "Large Potholes near Main Entrance Gate",
                    description = "Chashma Goth main gate par barish ke baad gehre gaddhey pad gaye hain. Rikshaw aur motorcycle aksar phans jati hain.",
                    locationDetails = "Main Entrance Gate near Bus Stop",
                    priority = "MEDIUM",
                    status = "IN_PROGRESS",
                    adminRemarks = "Crushed stone (roari) and cement gravel arranged with union council fund. Levelling work scheduled.",
                    assignedTo = "Infrastructure & Roads Wing",
                    createdAt = now - (48 * 3600 * 1000L),
                    updatedAt = now - (18 * 3600 * 1000L)
                )
            )

            // 4. Seed Announcements
            announcementDao.insertAnnouncement(
                AnnouncementEntity(
                    id = 1,
                    title = "Free Medical & Eye Screening Camp",
                    content = "Chashma Goth Young Generation is organizing a free medical checkup & medicine distribution camp this Sunday from 10:00 AM to 4:00 PM at Community Hall.",
                    category = "NOTICE",
                    date = now - (10 * 3600 * 1000L),
                    author = "Youth Health Committee"
                )
            )

            announcementDao.insertAnnouncement(
                AnnouncementEntity(
                    id = 2,
                    title = "Meetha Paani Water Schedule Update",
                    content = "Water distribution will run on Tuesday & Friday for Fishermen Colony, and Monday & Thursday for Main Road / Bazaar. Please keep storage tanks clean.",
                    category = "WATER_SUPPLY",
                    date = now - (24 * 3600 * 1000L),
                    author = "Water Board Coordination Desk"
                )
            )
        }
    }
}
