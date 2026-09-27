package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.AnnouncementEntity
import com.example.data.model.ComplaintEntity
import com.example.data.model.UserEntity
import com.example.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Random

enum class AppScreen {
    AUTH,
    HOME,
    REGISTER_COMPLAINT,
    COMPLAINT_DETAIL,
    ADMIN_DASHBOARD,
    ANNOUNCEMENTS,
    PROFILE
}

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = AppRepository(
            database.userDao(),
            database.complaintDao(),
            database.announcementDao()
        )
    }

    // Navigation State
    private val _currentScreen = MutableStateFlow(AppScreen.AUTH)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Auth State
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Complaints Data
    val allComplaints: StateFlow<List<ComplaintEntity>> = repository.allComplaints
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val announcements: StateFlow<List<AnnouncementEntity>> = repository.allAnnouncements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filters
    val statusFilter = MutableStateFlow("ALL")
    val categoryFilter = MutableStateFlow("ALL")
    val searchQuery = MutableStateFlow("")

    // Selected Complaint for Details
    private val _selectedComplaint = MutableStateFlow<ComplaintEntity?>(null)
    val selectedComplaint: StateFlow<ComplaintEntity?> = _selectedComplaint.asStateFlow()

    // Transient message (Snackbar / Toast alert)
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun viewComplaintDetails(complaint: ComplaintEntity) {
        _selectedComplaint.value = complaint
        _currentScreen.value = AppScreen.COMPLAINT_DETAIL
    }

    // Quick Login Helpers for testing & instant access
    fun quickLoginAsResident() {
        viewModelScope.launch {
            _isLoading.value = true
            val resident = repository.authenticate("03001234567", "user123")
                ?: repository.getUserByPhoneOrEmail("03001234567")
            if (resident != null) {
                _currentUser.value = resident
                _currentScreen.value = AppScreen.HOME
                _userMessage.value = "خوش آمدید! Logged in as ${resident.name}"
            } else {
                // If db not yet seeded, create on the fly
                val newResident = UserEntity(
                    name = "Kamran Ali",
                    phone = "03001234567",
                    email = "kamran@gmail.com",
                    password = "user123",
                    area = "Fishermen Colony",
                    role = "RESIDENT"
                )
                val id = repository.registerUser(newResident)
                _currentUser.value = newResident.copy(id = id)
                _currentScreen.value = AppScreen.HOME
                _userMessage.value = "خوش آمدید! Logged in as Kamran Ali"
            }
            _isLoading.value = false
        }
    }

    fun quickLoginAsAdmin() {
        viewModelScope.launch {
            _isLoading.value = true
            val admin = repository.authenticate("03001122334", "admin")
                ?: repository.getUserByPhoneOrEmail("03001122334")
            if (admin != null) {
                _currentUser.value = admin
                _currentScreen.value = AppScreen.HOME
                _userMessage.value = "Admin Access Granted: ${admin.name}"
            } else {
                val newAdmin = UserEntity(
                    name = "Youth Admin Desk",
                    phone = "03001122334",
                    email = "admin@chashmagoth.org",
                    password = "admin",
                    area = "Central Youth Office, Chashma Goth",
                    role = "ADMIN"
                )
                val id = repository.registerUser(newAdmin)
                _currentUser.value = newAdmin.copy(id = id)
                _currentScreen.value = AppScreen.HOME
                _userMessage.value = "Admin Access Granted: Youth Admin Desk"
            }
            _isLoading.value = false
        }
    }

    fun login(phoneOrEmail: String, pass: String) {
        if (phoneOrEmail.isBlank() || pass.isBlank()) {
            _authError.value = "براہ کرم فون/ای میل اور پاس ورڈ درج کریں (Please fill all fields)"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _authError.value = null
            val user = repository.authenticate(phoneOrEmail.trim(), pass.trim())
            if (user != null) {
                _currentUser.value = user
                _currentScreen.value = AppScreen.HOME
                _userMessage.value = "خوش آمدید ${user.name}!"
            } else {
                _authError.value = "غلط فون نمبر یا پاس ورڈ (Invalid credentials)"
            }
            _isLoading.value = false
        }
    }

    fun register(
        name: String,
        phone: String,
        email: String,
        password: String,
        area: String,
        role: String
    ) {
        if (name.isBlank() || phone.isBlank() || password.isBlank()) {
            _authError.value = "براہ کرم تمام لازمی فیلڈز مکمل کریں (Please fill required fields)"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _authError.value = null

            val existing = repository.getUserByPhoneOrEmail(phone.trim())
            if (existing != null) {
                _authError.value = "یہ فون نمبر پہلے سے رجسٹرڈ ہے (Phone already registered)"
                _isLoading.value = false
                return@launch
            }

            val newUser = UserEntity(
                name = name.trim(),
                phone = phone.trim(),
                email = email.trim(),
                password = password.trim(),
                area = area,
                role = role
            )
            val generatedId = repository.registerUser(newUser)
            _currentUser.value = newUser.copy(id = generatedId)
            _currentScreen.value = AppScreen.HOME
            _userMessage.value = "اکاؤنٹ کامیابی سے بن گیا ہے! Welcome, ${newUser.name}"
            _isLoading.value = false
        }
    }

    fun logout() {
        _currentUser.value = null
        _selectedComplaint.value = null
        _currentScreen.value = AppScreen.AUTH
        _userMessage.value = "لاگ آؤٹ ہو گیا (Logged out successfully)"
    }

    // Submit new Complaint
    fun submitComplaint(
        category: String,
        title: String,
        description: String,
        locationDetails: String,
        priority: String,
        photoUri: String?,
        onSuccess: (String) -> Unit
    ) {
        val user = _currentUser.value ?: return

        if (title.isBlank() || description.isBlank() || locationDetails.isBlank()) {
            _userMessage.value = "براہ کرم تمام تفصیلات درج کریں (Please fill all details)"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true

            // Generate unique tracking code e.g. CG-2026-XXX
            val randomDigits = (100..999).random()
            val trackingCode = "CG-2026-$randomDigits"

            val complaint = ComplaintEntity(
                trackingCode = trackingCode,
                userId = user.id,
                userName = user.name,
                userPhone = user.phone,
                userArea = user.area,
                category = category,
                title = title.trim(),
                description = description.trim(),
                locationDetails = locationDetails.trim(),
                priority = priority,
                status = "PENDING",
                photoUri = photoUri,
                adminRemarks = "شکایت موصول ہو گئی۔ نوجوان کمیٹی جلد معائنہ کرے گی (Complaint logged. Under initial review).",
                assignedTo = "Youth Committee Desk"
            )

            repository.registerComplaint(complaint)
            _isLoading.value = false
            _userMessage.value = "شکایت کامیابی سے درج ہو گئی! Tracking ID: $trackingCode"
            onSuccess(trackingCode)
        }
    }

    // Admin updates status, wing assignment, remarks
    fun updateComplaintByAdmin(
        complaintId: Long,
        newStatus: String,
        adminRemarks: String,
        assignedTo: String,
        priority: String,
        onSuccess: () -> Unit
    ) {
        val complaint = _selectedComplaint.value ?: return

        viewModelScope.launch {
            _isLoading.value = true
            val updated = complaint.copy(
                status = newStatus,
                adminRemarks = adminRemarks.trim(),
                assignedTo = assignedTo,
                priority = priority,
                updatedAt = System.currentTimeMillis()
            )
            repository.updateComplaint(updated)
            _selectedComplaint.value = updated
            _isLoading.value = false
            _userMessage.value = "شکایت کی تفصیلات اپ ڈیٹ کر دی گئیں (Updated successfully)"
            onSuccess()
        }
    }

    // Citizen rating and feedback for resolved complaints
    fun submitCitizenRating(
        complaintId: Long,
        rating: Int,
        feedback: String
    ) {
        val complaint = _selectedComplaint.value ?: return

        viewModelScope.launch {
            val updated = complaint.copy(
                citizenRating = rating,
                citizenFeedback = feedback.trim(),
                updatedAt = System.currentTimeMillis()
            )
            repository.updateComplaint(updated)
            _selectedComplaint.value = updated
            _userMessage.value = "آپ کے تاثرات کا شکریہ! (Thank you for your feedback)"
        }
    }

    fun deleteComplaint(complaint: ComplaintEntity) {
        viewModelScope.launch {
            repository.deleteComplaint(complaint)
            if (_selectedComplaint.value?.id == complaint.id) {
                _selectedComplaint.value = null
                _currentScreen.value = AppScreen.HOME
            }
            _userMessage.value = "شکایت خارج کر دی گئی (Complaint deleted)"
        }
    }

    fun addAnnouncement(title: String, content: String, category: String, onSuccess: () -> Unit) {
        if (title.isBlank() || content.isBlank()) {
            _userMessage.value = "Please enter title and content"
            return
        }

        viewModelScope.launch {
            repository.addAnnouncement(
                AnnouncementEntity(
                    title = title.trim(),
                    content = content.trim(),
                    category = category,
                    author = _currentUser.value?.name ?: "Chashma Goth Youth Desk"
                )
            )
            _userMessage.value = "اعلان شائع کر دیا گیا (Announcement posted)"
            onSuccess()
        }
    }
}
