package com.example.ui

import android.app.Application
import android.provider.OpenableColumns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.User
import com.example.data.model.ResourceMaterial
import com.example.data.model.Gig
import com.example.data.model.FeedPost
import com.example.data.model.DirectMessage
import com.example.data.model.ChatMessage
import com.example.data.model.CommunityPost
import com.example.data.model.CommunityComment
import com.example.data.repository.CampusRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import android.net.Uri
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.CustomCredential
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.example.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

data class DirectMessageWithUser(
    val dm: DirectMessage,
    val otherUser: User?
)

class CampusViewModel(application: Application) : AndroidViewModel(application) {
    private val auth = FirebaseAuth.getInstance()
    private var authStateListener: FirebaseAuth.AuthStateListener? = null
    
    // Auth Loading State
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val db = AppDatabase.getDatabase(application)
    private val repository = CampusRepository(
        db.userDao(),
        db.resourceDao(),
        db.gigDao(),
        db.postDao(),
        db.messageDao(),
        db.communityDao()
    )

    // States
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _communityPosts = MutableStateFlow<List<CommunityPost>>(emptyList())
    val communityPosts: StateFlow<List<CommunityPost>> = _communityPosts.asStateFlow()

    private val _activeComments = MutableStateFlow<List<CommunityComment>>(emptyList())
    val activeComments: StateFlow<List<CommunityComment>> = _activeComments.asStateFlow()

    private val _directMessages = MutableStateFlow<List<DirectMessage>>(emptyList())
    val directMessages: StateFlow<List<DirectMessage>> = _directMessages.asStateFlow()

    private val _directMessagesWithUsers = MutableStateFlow<List<DirectMessageWithUser>>(emptyList())
    val directMessagesWithUsers: StateFlow<List<DirectMessageWithUser>> = _directMessagesWithUsers.asStateFlow()

    private val _currentChatOtherUser = MutableStateFlow<User?>(null)
    val currentChatOtherUser: StateFlow<User?> = _currentChatOtherUser.asStateFlow()

    private val _allColleges = MutableStateFlow<List<String>>(emptyList())
    val allColleges: StateFlow<List<String>> = _allColleges.asStateFlow()

    private val _allMajors = MutableStateFlow<List<String>>(emptyList())
    val allMajors: StateFlow<List<String>> = _allMajors.asStateFlow()

    private val _usersInSelectedCollege = MutableStateFlow<List<User>>(emptyList())
    val usersInSelectedCollege: StateFlow<List<User>> = _usersInSelectedCollege.asStateFlow()

    private val _usersInSelectedMajor = MutableStateFlow<List<User>>(emptyList())
    val usersInSelectedMajor: StateFlow<List<User>> = _usersInSelectedMajor.asStateFlow()

    private val _usersByName = MutableStateFlow<List<User>>(emptyList())
    val usersByName: StateFlow<List<User>> = _usersByName.asStateFlow()

    private val _currentChatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val currentChatMessages: StateFlow<List<ChatMessage>> = _currentChatMessages.asStateFlow()

    private val _resources = MutableStateFlow<List<ResourceMaterial>>(emptyList())
    val resources: StateFlow<List<ResourceMaterial>> = _resources.asStateFlow()

    private val _gigs = MutableStateFlow<List<Gig>>(emptyList())
    val gigs: StateFlow<List<Gig>> = _gigs.asStateFlow()

    private val _feedPosts = MutableStateFlow<List<FeedPost>>(emptyList())
    val feedPosts: StateFlow<List<FeedPost>> = _feedPosts.asStateFlow()

    private val _collegeNames = MutableStateFlow<List<String>>(emptyList())
    val collegeNames: StateFlow<List<String>> = _collegeNames.asStateFlow()

    private val _selectedCollegeResources = MutableStateFlow<List<ResourceMaterial>>(emptyList())
    val selectedCollegeResources: StateFlow<List<ResourceMaterial>> = _selectedCollegeResources.asStateFlow()

    private val _collegeSearchQuery = MutableStateFlow("")
    val collegeSearchQuery: StateFlow<String> = _collegeSearchQuery.asStateFlow()

    private val _unlockedResourceIds = MutableStateFlow<Set<Int>>(emptySet())
    val unlockedResourceIds: StateFlow<Set<Int>> = _unlockedResourceIds.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    private val _isRegistering = MutableStateFlow(false)
    val isRegistering: StateFlow<Boolean> = _isRegistering.asStateFlow()

    init {
        // Auth State Listener for persistence
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                viewModelScope.launch {
                    val uid = firebaseUser.uid
                    val email = firebaseUser.email ?: return@launch
                    
                    val firestoreUser = repository.getFirestoreUserProfile(uid)
                    if (firestoreUser != null) {
                        repository.insertUser(firestoreUser)
                        _currentUser.value = firestoreUser
                        observePostsForCollege(firestoreUser.collegeName)
                    } else {
                        // Offline fallback or missing profile
                        val localUser = repository.getUserByEmailSync(email)
                        if (localUser != null) {
                            _currentUser.value = localUser
                            observePostsForCollege(localUser.collegeName)
                        } else {
                            FirebaseAuth.getInstance().signOut()
                            _currentUser.value = null
                        }
                    }
                }
            } else {
                _currentUser.value = null
            }
        }
        auth.addAuthStateListener(authStateListener!!)

        // Observe search query and update resources dynamically
        viewModelScope.launch {
            _searchQuery.collect { query ->
                repository.searchResources(query).collect { list ->
                    _resources.value = list
                }
            }
        }

        // Observe gigs
        viewModelScope.launch {
            repository.getAllGigs().collect { list ->
                _gigs.value = list
            }
        }

        // Initialize college names
        viewModelScope.launch {
            repository.getUniqueColleges().collect { list ->
                _collegeNames.value = list
            }
        }

        viewModelScope.launch {
            repository.getAllCommunityPosts().collect { list ->
                _communityPosts.value = list
            }
        }

        viewModelScope.launch {
            repository.getAllColleges().collect { list ->
                _allColleges.value = list
            }
        }

        viewModelScope.launch {
            repository.getAllMajors().collect { list ->
                _allMajors.value = list
            }
        }

        // Check and seed DB if empty
        viewModelScope.launch {
            seedDatabaseIfEmpty()
        }
    }

    private suspend fun seedDatabaseIfEmpty() {
        val mockUsers = listOf(
            User("prince@kletech.ac.in", "Prince", "KLE Tech University Hubli", "S20201", "Computer Science", 150),
            User("trigun@kletech.ac.in", "Trigun", "KLE Tech University Hubli", "S20202", "Electronics & Communication", 140),
            User("shubh@sdm.edu", "Shubh", "SDM College", "S30201", "Information Technology", 130),
            User("krupa@sdm.edu", "Krupa", "SDM College", "S30202", "Data Science", 160),
            User("riya@kletech.ac.in", "Riya", "KLE Tech University Hubli", "S20203", "Computer Science", 125),
            User("rithika@sdm.edu", "Rithika", "SDM College", "S30203", "Business Administration", 135),
            User("aliya@kletech.ac.in", "Aliya", "KLE Tech University Hubli", "S20204", "Artificial Intelligence", 145),
            User("navneet@sdm.edu", "Navneet", "SDM College", "S30204", "Mechanical Engineering", 115),
            User("alice.adams@stateu.edu", "Alice Adams", "State University", "S10405", "Computer Science", 150),
            User("bob.baker@stateu.edu", "Bob Baker", "State University", "S10982", "Bioengineering", 120),
            User("charlie.chen@stateu.edu", "Charlie Chen", "State University", "S11342", "Mechanical Engineering", 80),
            User("rahul.patil@kletech.ac.in", "Rahul Patil", "KLE Tech University Hubli", "S20101", "Computer Science", 110),
            User("pria.kulkarni@kletech.ac.in", "Pria Kulkarni", "KLE Tech University Hubli", "S20102", "Electronics & Communication", 140),
            User("vijay.hegde@sdm.edu", "Vijay Hegde", "SDM College", "S30101", "Computer Science", 95),
            User("ananya.rao@sdm.edu", "Ananya Rao", "SDM College", "S30102", "Business Administration", 130),
            User("dean.office@stateu.edu", "Dean's Office", "State University", "FAC001", "Administration", 9999)
        )

        for (u in mockUsers) {
            if (repository.getUserByEmailSync(u.email) == null) {
                repository.insertUser(u)
            }
        }

        val existingCommunityPosts = repository.getAllCommunityPosts().first()
        if (existingCommunityPosts.isEmpty()) {
            repository.insertCommunityPost(
                CommunityPost(
                    collegeName = "KLE Technological University, Hubballi",
                    authorName = "Anonymous Student 🥷",
                    authorEmail = "anon1@kletech.ac.in",
                    isAnonymous = true,
                    title = "Is the Wi-Fi in the Library Basement completely down for everyone?",
                    content = "Trying to submit my CS201 lab before midnight but Eduroam keeps dropping connection near the study pods. Anyone else facing this issue?",
                    categoryTag = "Campus Issue",
                    upvotesCount = 18,
                    commentsCount = 4
                )
            )

            repository.insertCommunityPost(
                CommunityPost(
                    collegeName = "SDM College of Engineering & Tech",
                    authorName = "Anonymous Student 🥷",
                    authorEmail = "anon2@sdm.edu",
                    isAnonymous = true,
                    title = "Advice for surviving Dr. Kowalski's Linear Algebra Midterm?",
                    content = "Heard rumors that 40% of the midterm is conceptual proof derivations from Chapter 3. Are previous year question banks enough?",
                    categoryTag = "Advice",
                    upvotesCount = 32,
                    commentsCount = 7
                )
            )

            repository.insertCommunityPost(
                CommunityPost(
                    collegeName = "KLE Technological University, Hubballi",
                    authorName = "Prince",
                    authorEmail = "prince@kletech.ac.in",
                    isAnonymous = false,
                    title = "Who wants to team up for ACM CodeStorm '26 Hackathon?",
                    content = "Need 1 backend developer proficient in Kotlin/Python and Room/PostgreSQL. We have 1 frontend dev and 1 designer. DM or reply here!",
                    categoryTag = "Discussion",
                    upvotesCount = 25,
                    commentsCount = 5
                )
            )
        }

        val existingResources = repository.getAllResources().first()
        if (existingResources.isEmpty()) {
            // Seed resource materials
            repository.insertResource(
                ResourceMaterial(
                    title = "CS 201: Data Structures Exam Review Sheet",
                    courseCode = "CS201",
                    professor = "Dr. Evans",
                    semester = "Spring 2026",
                    description = "Comprehensive 4-page summary of Red-Black Trees, Heaps, and Graph algorithms with practice problems and code snippets.",
                    fileType = "PDF Study Guide",
                    uploaderEmail = "alice.adams@stateu.edu",
                    uploaderName = "Alice Adams",
                    collegeName = "State University",
                    priceInPoints = 15
                )
            )

            repository.insertResource(
                ResourceMaterial(
                    title = "MATH 302: Linear Algebra Final Summary Notes",
                    courseCode = "MATH302",
                    professor = "Prof. Kowalski",
                    semester = "Fall 2025",
                    description = "Extensive review notes covering Eigenvalues, Diagonalization, and Orthogonal projections. Highly detailed hand-drawn diagrams.",
                    fileType = "PDF Lecture Notes",
                    uploaderEmail = "bob.baker@stateu.edu",
                    uploaderName = "Bob Baker",
                    collegeName = "State University",
                    priceInPoints = 25
                )
            )

            repository.insertResource(
                ResourceMaterial(
                    title = "CHEM 101: Organic Chemistry Lab Techniques",
                    courseCode = "CHEM101",
                    professor = "Dr. Al-Hassan",
                    semester = "Spring 2026",
                    description = "Quick reference guide for distillation, extraction, and chromatography. Perfect for pre-lab quizzes.",
                    fileType = "Cheat Sheet",
                    uploaderEmail = "charlie.chen@stateu.edu",
                    uploaderName = "Charlie Chen",
                    collegeName = "State University",
                    priceInPoints = 10
                )
            )

            // Seed Gigs
            repository.insertGig(
                Gig(
                    title = "Need peer-tutoring for Calculus III",
                    category = "Tutoring",
                    description = "Struggling with partial derivatives and Lagrange multipliers before the midterm on Tuesday. Happy to meet at the library.",
                    rewardPoints = 30,
                    requesterEmail = "bob.baker@stateu.edu",
                    requesterName = "Bob Baker",
                    contactInfo = "Library Study Room 4, BobB on Discord",
                    status = "OPEN"
                )
            )

            repository.insertGig(
                Gig(
                    title = "Borrowing Arduino Uno & Breadboard",
                    category = "Lab Equipment",
                    description = "I need to test an embedded sensor layout for class today. Can return it by 5:00 PM tonight. Will credit 15 points!",
                    rewardPoints = 15,
                    requesterEmail = "charlie.chen@stateu.edu",
                    requesterName = "Charlie Chen",
                    contactInfo = "Engineering Block B, Room 204",
                    status = "OPEN"
                )
            )

            repository.insertGig(
                Gig(
                    title = "English 202 Research Paper Proofreading",
                    category = "Study Help",
                    description = "Need a quick read of my 8-page literature analysis on Shakespeare's Tempest. Grammatical and flow check only. Rewarding 20 points.",
                    rewardPoints = 20,
                    requesterEmail = "alice.adams@stateu.edu",
                    requesterName = "Alice Adams",
                    contactInfo = "Text or WhatsApp at 555-0199",
                    status = "OPEN"
                )
            )

            // Seed feed posts
            repository.insertPost(
                FeedPost(
                    collegeName = "State University",
                    authorName = "Dean's Office",
                    authorEmail = "dean.office@stateu.edu",
                    content = "Welcome to Campusdeck! This is your localized collegiate board. Reminder: annual Campus Hackathon registration closes tomorrow at 5:00 PM. Team up and register!",
                    likesCount = 24,
                    likedByEmails = "alice.adams@stateu.edu,bob.baker@stateu.edu"
                )
            )

            repository.insertPost(
                FeedPost(
                    collegeName = "State University",
                    authorName = "Alice Adams",
                    authorEmail = "alice.adams@stateu.edu",
                    content = "To whoever left their blue Hydroflask in the Student Union basement study room yesterday—I gave it to the front desk lost-and-found! Hope you find it.",
                    likesCount = 8,
                    likedByEmails = "bob.baker@stateu.edu"
                )
            )

            // Seed initial assignments
            repository.insertResource(
                ResourceMaterial(
                    title = "CS 102: Data Structures Lab 3 - Tree Traversal",
                    courseCode = "CS102",
                    professor = "Dr. Miller",
                    semester = "Spring 2026",
                    description = "Reference solution and explanatory notes for the binary search tree traversal lab. Includes edge case analysis.",
                    fileType = "Assignment",
                    uploaderEmail = "alice.adams@stateu.edu",
                    uploaderName = "Alice Adams",
                    collegeName = "State University",
                    priceInPoints = 20
                )
            )

            repository.insertResource(
                ResourceMaterial(
                    title = "ECON 101: Macroeconomics Problem Set #2",
                    courseCode = "ECON101",
                    professor = "Prof. Smith",
                    semester = "Spring 2026",
                    description = "Detailed breakdown of the IS-LM model problem set. Step-by-step derivation of equilibrium points.",
                    fileType = "Assignment",
                    uploaderEmail = "charlie.chen@stateu.edu",
                    uploaderName = "Charlie Chen",
                    collegeName = "State University",
                    priceInPoints = 15
                )
            )
        }
    }

    fun setRegistering(registering: Boolean) {
        _isRegistering.value = registering
        _loginError.value = null
    }

    // Auth actions
    fun login(email: String, password: String) {
        viewModelScope.launch {
            if (email.isBlank() || password.isBlank()) {
                _loginError.value = "Email and password cannot be empty"
                return@launch
            }
            if (!isValidCollegeEmail(email)) {
                _loginError.value = "Email must end with a college-issued domain (e.g., .edu, .ac.uk, or .college.org)"
                return@launch
            }

            _isLoading.value = true
            _loginError.value = null

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser = task.result?.user
                        if (firebaseUser != null && firebaseUser.email != null) {
                            viewModelScope.launch {
                                val firestoreUser = repository.getFirestoreUserProfile(firebaseUser.uid)
                                if (firestoreUser != null) {
                                    repository.insertUser(firestoreUser)
                                    _currentUser.value = firestoreUser
                                    observePostsForCollege(firestoreUser.collegeName)
                                } else {
                                    val localUser = repository.getUserByEmailSync(firebaseUser.email!!)
                                    if (localUser != null) {
                                        repository.createFirestoreUserProfile(firebaseUser.uid, localUser)
                                        _currentUser.value = localUser
                                        observePostsForCollege(localUser.collegeName)
                                    } else {
                                        _loginError.value = "Local profile not found. Please register."
                                        auth.signOut()
                                    }
                                }
                                _isLoading.value = false
                            }
                        } else {
                            _isLoading.value = false
                        }
                    } else {
                        _isLoading.value = false
                        _loginError.value = when (task.exception) {
                            is FirebaseAuthInvalidUserException -> "No account found with this email"
                            is FirebaseAuthInvalidCredentialsException -> "Incorrect email or password"
                            else -> task.exception?.localizedMessage ?: "Login failed. Please try again."
                        }
                    }
                }
        }
    }

    fun register(email: String, password: String, name: String, collegeName: String, studentId: String, major: String) {
        viewModelScope.launch {
            if (email.isBlank() || password.isBlank() || name.isBlank() || collegeName.isBlank() || studentId.isBlank() || major.isBlank()) {
                _loginError.value = "All fields are required"
                return@launch
            }
            if (!isValidCollegeEmail(email)) {
                _loginError.value = "Email must end with a college-issued domain (e.g., .edu, .ac.uk, or .college.org)"
                return@launch
            }

            // Check if Room already has it first before hitting Firebase
            val existing = repository.getUserByEmailSync(email)
            if (existing != null) {
                _loginError.value = "An account with this email already exists locally"
                return@launch
            }

            _isLoading.value = true
            _loginError.value = null

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser = task.result?.user
                        if (firebaseUser != null) {
                            viewModelScope.launch {
                                val newUser = User(
                                    email = email,
                                    name = name,
                                    collegeName = collegeName,
                                    studentId = studentId,
                                    major = major,
                                    points = 100 // Welcome points!
                                    // Room PK is still email in Phase 2
                                )
                                repository.createFirestoreUserProfile(firebaseUser.uid, newUser)
                                repository.insertUser(newUser)
                                _currentUser.value = newUser
                                _isLoading.value = false
                                observePostsForCollege(collegeName)
                            }
                        } else {
                            _isLoading.value = false
                        }
                    } else {
                        _isLoading.value = false
                        _loginError.value = when (task.exception) {
                            is FirebaseAuthUserCollisionException -> "An account with this email already exists"
                            is FirebaseAuthWeakPasswordException -> "Password is too weak"
                            else -> task.exception?.localizedMessage ?: "Registration failed"
                        }
                    }
                }
        }
    }

    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            _loginError.value = null
            try {
                val credentialManager = CredentialManager.create(context)
                val webClientId = context.getString(R.string.default_web_client_id)

                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(webClientId)
                    .setAutoSelectEnabled(false)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(
                    request = request,
                    context = context
                )

                val credential = result.credential
                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val idToken = googleIdTokenCredential.idToken

                    val authCredential = GoogleAuthProvider.getCredential(idToken, null)
                    auth.signInWithCredential(authCredential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val firebaseUser = task.result?.user
                                if (firebaseUser != null && firebaseUser.email != null) {
                                    viewModelScope.launch {
                                        val firestoreUser = repository.getFirestoreUserProfile(firebaseUser.uid)
                                        if (firestoreUser != null) {
                                            repository.insertUser(firestoreUser)
                                            _currentUser.value = firestoreUser
                                            observePostsForCollege(firestoreUser.collegeName)
                                        } else {
                                            val localUser = repository.getUserByEmailSync(firebaseUser.email!!)
                                            val name = firebaseUser.displayName ?: firebaseUser.email!!.substringBefore("@").replace(".", " ")
                                            val photoUrl = firebaseUser.photoUrl?.toString()

                                            val userToSave = localUser ?: User(
                                                email = firebaseUser.email!!,
                                                name = name,
                                                collegeName = "State University",
                                                studentId = "S${(10000..99999).random()}",
                                                major = "General Studies",
                                                points = 100,
                                                profilePictureUri = photoUrl
                                            )

                                            repository.createFirestoreUserProfile(firebaseUser.uid, userToSave)
                                            repository.insertUser(userToSave)
                                            _currentUser.value = userToSave
                                            observePostsForCollege(userToSave.collegeName)
                                        }
                                        _isLoading.value = false
                                    }
                                } else {
                                    _isLoading.value = false
                                }
                            } else {
                                _isLoading.value = false
                                _loginError.value = when (task.exception) {
                                    is FirebaseAuthUserCollisionException -> "An account with this email already exists with another provider."
                                    else -> task.exception?.localizedMessage ?: "Google Sign-In failed."
                                }
                            }
                        }
                } else {
                    _isLoading.value = false
                    _loginError.value = "Unrecognized credential type."
                }
            } catch (e: GetCredentialCancellationException) {
                _isLoading.value = false
                _loginError.value = "Google Sign-In was cancelled."
            } catch (e: GetCredentialException) {
                _isLoading.value = false
                _loginError.value = e.localizedMessage ?: "Google Sign-In failed."
            } catch (e: Exception) {
                _isLoading.value = false
                _loginError.value = e.localizedMessage ?: "An error occurred during Google Sign-In."
            }
        }
    }

    fun logout() {
        auth.signOut()
        _currentUser.value = null
        _feedPosts.value = emptyList()
        _unlockedResourceIds.value = emptySet()
    }

    private fun observePostsForCollege(collegeName: String) {
        viewModelScope.launch {
            repository.getPostsForCollege(collegeName).collect { posts ->
                _feedPosts.value = posts
            }
        }
    }

    // Messaging actions
    fun loadDirectMessages() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.getDirectMessagesForUser(user.email).collect { list ->
                _directMessages.value = list
                val listWithUsers = list.map { dm ->
                    val otherEmail = if (dm.participant1Email == user.email) dm.participant2Email else dm.participant1Email
                    val otherUser = repository.getUserByEmailSync(otherEmail)
                    DirectMessageWithUser(dm, otherUser)
                }
                _directMessagesWithUsers.value = listWithUsers
            }
        }
    }

    fun loadChatMessages(chatId: Int) {
        val currentUserVal = _currentUser.value
        viewModelScope.launch {
            repository.getChatMessages(chatId).collect { list ->
                _currentChatMessages.value = list
            }
        }
        viewModelScope.launch {
            val dm = repository.getDirectMessageById(chatId)
            if (dm != null && currentUserVal != null) {
                val otherEmail = if (dm.participant1Email == currentUserVal.email) dm.participant2Email else dm.participant1Email
                val otherUser = repository.getUserByEmailSync(otherEmail)
                _currentChatOtherUser.value = otherUser
            } else {
                _currentChatOtherUser.value = null
            }
        }
    }

    fun sendMessage(chatId: Int, content: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.sendChatMessage(chatId, user.email, content)
        }
    }

    fun startNewChat(theirEmail: String, onResult: (Boolean, String, Int?) -> Unit) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val existingChatId = repository.getDirectMessageId(user.email, theirEmail)
            if (existingChatId != null) {
                // Already have a chat, just open it, no charge
                onResult(true, "", existingChatId)
                return@launch
            }

            // New chat - charge 10 CC
            val cost = 10
            if (user.points < cost) {
                onResult(false, "Insufficient balance! You need $cost CC (Campus Credits) to start a new chat connection.", null)
                return@launch
            }

            val updatedUser = user.copy(points = user.points - cost)
            repository.insertUser(updatedUser)
            _currentUser.value = updatedUser

            val chatId = repository.createOrGetDirectMessage(user.email, theirEmail)
            onResult(true, "Chat connection started! -$cost CC", chatId)
        }
    }

    fun selectCollegeForChat(collegeName: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.getUsersInCollege(collegeName, user.email).collect {
                _usersInSelectedCollege.value = it
            }
        }
    }

    fun selectMajorForChat(major: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.getUsersInMajor(major, user.email).collect {
                _usersInSelectedMajor.value = it
            }
        }
    }

    fun searchUsersByName(name: String) {
        val user = _currentUser.value ?: return
        if (name.isBlank()) {
            _usersByName.value = emptyList()
            return
        }
        viewModelScope.launch {
            repository.searchUsersByName(name, user.email).collect {
                _usersByName.value = it
            }
        }
    }

    // Community Methods
    fun createCommunityPost(title: String, content: String, categoryTag: String, isAnonymous: Boolean) {
        val user = _currentUser.value ?: return
        if (title.isBlank() || content.isBlank()) return
        viewModelScope.launch {
            val author = if (isAnonymous) "Anonymous Student 🥷" else user.name
            val post = CommunityPost(
                collegeName = user.collegeName,
                authorName = author,
                authorEmail = user.email,
                isAnonymous = isAnonymous,
                title = title,
                content = content,
                categoryTag = categoryTag
            )
            repository.insertCommunityPost(post)
        }
    }

    fun loadCommentsForPost(postId: Int) {
        viewModelScope.launch {
            repository.getCommentsForPost(postId).collect { list ->
                _activeComments.value = list
            }
        }
    }

    fun addCommunityComment(postId: Int, content: String, isAnonymous: Boolean) {
        val user = _currentUser.value ?: return
        if (content.isBlank()) return
        viewModelScope.launch {
            val author = if (isAnonymous) "Anonymous Student 🥷" else user.name
            val comment = CommunityComment(
                postId = postId,
                authorName = author,
                authorEmail = user.email,
                isAnonymous = isAnonymous,
                content = content
            )
            repository.insertCommunityComment(comment)
            val currentComments = _activeComments.value.size + 1
            repository.updateCommentsCount(postId, currentComments)
        }
    }

    fun voteCommunityPost(post: CommunityPost, isUpvote: Boolean) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val upvotedList = post.upvotedByEmails.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toMutableList()
            val downvotedList = post.downvotedByEmails.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toMutableList()

            var newUpvotes = post.upvotesCount
            var newDownvotes = post.downvotesCount

            if (isUpvote) {
                if (upvotedList.contains(user.email)) {
                    upvotedList.remove(user.email)
                    newUpvotes = (newUpvotes - 1).coerceAtLeast(0)
                } else {
                    upvotedList.add(user.email)
                    newUpvotes += 1
                    if (downvotedList.contains(user.email)) {
                        downvotedList.remove(user.email)
                        newDownvotes = (newDownvotes - 1).coerceAtLeast(0)
                    }
                }
            } else {
                if (downvotedList.contains(user.email)) {
                    downvotedList.remove(user.email)
                    newDownvotes = (newDownvotes - 1).coerceAtLeast(0)
                } else {
                    downvotedList.add(user.email)
                    newDownvotes += 1
                    if (upvotedList.contains(user.email)) {
                        upvotedList.remove(user.email)
                        newUpvotes = (newUpvotes - 1).coerceAtLeast(0)
                    }
                }
            }

            repository.updatePostVotes(
                id = post.id,
                upvotesCount = newUpvotes,
                upvotedByEmails = upvotedList.joinToString(","),
                downvotesCount = newDownvotes,
                downvotedByEmails = downvotedList.joinToString(",")
            )
        }
    }

    // Marketplace actions
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCollegeSearchQuery(query: String) {
        _collegeSearchQuery.value = query
    }

    fun selectCollege(collegeName: String) {
        viewModelScope.launch {
            repository.getResourcesByCollege(collegeName).collect { list ->
                _selectedCollegeResources.value = list
            }
        }
    }

    private fun getFileSize(uri: Uri): Long {
        val context = getApplication<Application>().applicationContext
        var size = 0L
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (sizeIndex != -1) {
                    size = cursor.getLong(sizeIndex)
                }
            }
        }
        return size
    }

    fun updateUserProfile(user: User) {
        viewModelScope.launch {
            _isLoading.value = true
            var finalUser = user
            val firebaseUser = auth.currentUser
            
            if (firebaseUser != null) {
                // Check if profilePictureUri is a local content URI
                if (user.profilePictureUri != null && user.profilePictureUri.startsWith("content://")) {
                    val uri = Uri.parse(user.profilePictureUri)
                    
                    if (getFileSize(uri) > 5 * 1024 * 1024) {
                        _isLoading.value = false
                        return@launch
                    }

                    val uploadPath = repository.uploadProfileImage(getApplication<Application>().applicationContext, uri)
                    if (uploadPath != null) {
                        val success = repository.updateFirestoreUserProfile(firebaseUser.uid, finalUser, true, uploadPath)
                        if (success) {
                            val signedUrl = repository.getSignedUrl(uploadPath)
                            finalUser = finalUser.copy(profilePictureUri = signedUrl ?: uploadPath)
                            repository.updateUser(finalUser)
                            _currentUser.value = finalUser
                        } else {
                            // Fallback
                            val prevUrl = _currentUser.value?.profilePictureUri
                            finalUser = finalUser.copy(profilePictureUri = prevUrl)
                            repository.updateUser(finalUser)
                            _currentUser.value = finalUser
                        }
                    } else {
                        // Failed to upload
                        val prevUrl = _currentUser.value?.profilePictureUri
                        finalUser = finalUser.copy(profilePictureUri = prevUrl)
                        repository.updateUser(finalUser)
                        _currentUser.value = finalUser
                    }
                } else {
                    // No image change
                    repository.updateFirestoreUserProfile(firebaseUser.uid, finalUser, false, null)
                    repository.updateUser(finalUser)
                    _currentUser.value = finalUser
                }
            } else {
                repository.updateUser(finalUser)
                _currentUser.value = finalUser
            }
            _isLoading.value = false
        }
    }

    private val _uploadError = MutableStateFlow<String?>(null)
    val uploadError: StateFlow<String?> = _uploadError.asStateFlow()

    fun addResource(title: String, courseCode: String, professor: String, semester: String, description: String, fileType: String, priceInPoints: Int, fileUri: Uri?) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            _uploadError.value = null
            
            val firebaseUser = auth.currentUser
            if (firebaseUser == null) {
                _isLoading.value = false
                _uploadError.value = "You must be signed in to upload a resource."
                return@launch
            }
            
            var downloadUrl: String? = null
            if (fileUri != null) {
                // Validate file size (e.g. max 15 MB for PDFs)
                if (getFileSize(fileUri) > 15 * 1024 * 1024) {
                    _isLoading.value = false
                    _uploadError.value = "File is too large (Max 15MB)."
                    return@launch
                }
                
                downloadUrl = repository.uploadResourceFile(getApplication<Application>().applicationContext, fileUri)
                if (downloadUrl == null) {
                    _isLoading.value = false
                    _uploadError.value = "Failed to upload file. Please check your connection."
                    return@launch
                }
            } else {
                _isLoading.value = false
                _uploadError.value = "Please select a valid PDF file."
                return@launch
            }

            val material = ResourceMaterial(
                title = title,
                courseCode = courseCode.uppercase(),
                professor = professor,
                semester = semester,
                description = description,
                fileType = fileType,
                uploaderEmail = user.email,
                uploaderName = user.name,
                collegeName = user.collegeName,
                priceInPoints = priceInPoints,
                fileUrl = downloadUrl
            )
            repository.insertResource(material)
            _isLoading.value = false
        }
    }

    fun purchaseResource(material: ResourceMaterial, onResult: (Boolean, String) -> Unit) {
        val buyer = _currentUser.value ?: return
        viewModelScope.launch {
            if (buyer.email == material.uploaderEmail) {
                // Free for uploader
                _unlockedResourceIds.value = _unlockedResourceIds.value + material.id
                onResult(true, "You already own this document!")
                return@launch
            }

            if (_unlockedResourceIds.value.contains(material.id)) {
                onResult(true, "Document already unlocked!")
                return@launch
            }

            if (buyer.points < material.priceInPoints) {
                onResult(false, "Insufficient balance! You need ${material.priceInPoints} CC (Campus Credits). Completing gigs is a great way to earn points.")
                return@launch
            }

            // Perform transaction securely
            val updatedBuyerPoints = buyer.points - material.priceInPoints
            val updatedBuyer = buyer.copy(points = updatedBuyerPoints)
            repository.insertUser(updatedBuyer)
            _currentUser.value = updatedBuyer

            // Credit seller
            val seller = repository.getUserByEmailSync(material.uploaderEmail)
            if (seller != null) {
                repository.insertUser(seller.copy(points = seller.points + material.priceInPoints))
            }

            // Unlock resource in-memory
            _unlockedResourceIds.value = _unlockedResourceIds.value + material.id
            onResult(true, "Successfully unlocked '${material.title}'! Dedicated download simulator launched.")
        }
    }

    // Gig actions
    fun postGig(title: String, category: String, description: String, rewardPoints: Int, contactInfo: String, onResult: (Boolean, String) -> Unit) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            if (user.points < rewardPoints) {
                onResult(false, "Insufficient balance! Posting this gig requires $rewardPoints CC to be locked in escrow.")
                return@launch
            }

            // Deduct escrow points from user balance
            val updatedUser = user.copy(points = user.points - rewardPoints)
            repository.insertUser(updatedUser)
            _currentUser.value = updatedUser

            val newGig = Gig(
                title = title,
                category = category,
                description = description,
                rewardPoints = rewardPoints,
                requesterEmail = user.email,
                requesterName = user.name,
                contactInfo = contactInfo,
                status = "OPEN"
            )
            repository.insertGig(newGig)
            onResult(true, "Gig posted successfully! $rewardPoints CC locked in escrow.")
        }
    }

    fun acceptGig(gig: Gig) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.updateGigStatus(gig.id, "ACCEPTED", user.email, user.name)
        }
    }

    fun completeGig(gig: Gig) {
        viewModelScope.launch {
            // Update status
            repository.updateGigStatus(gig.id, "COMPLETED", gig.helperEmail, gig.helperName)

            // Transfer escrowed points to the helper
            val helperEmail = gig.helperEmail ?: return@launch
            val helper = repository.getUserByEmailSync(helperEmail)
            if (helper != null) {
                val updatedHelper = helper.copy(points = helper.points + gig.rewardPoints)
                repository.insertUser(updatedHelper)

                // If currently logged in user is the helper, update current user state
                val currentUserVal = _currentUser.value
                if (currentUserVal != null && currentUserVal.email == helper.email) {
                    _currentUser.value = updatedHelper
                }
            }
        }
    }

    fun cancelGig(gig: Gig) {
        viewModelScope.launch {
            // Delete gig and refund original requester
            repository.deleteGig(gig.id)

            val requester = repository.getUserByEmailSync(gig.requesterEmail)
            if (requester != null) {
                val updatedRequester = requester.copy(points = requester.points + gig.rewardPoints)
                repository.insertUser(updatedRequester)

                val currentUserVal = _currentUser.value
                if (currentUserVal != null && currentUserVal.email == requester.email) {
                    _currentUser.value = updatedRequester
                }
            }
        }
    }

    // Feed actions
    fun createPost(content: String) {
        val user = _currentUser.value ?: return
        if (content.isBlank()) return

        viewModelScope.launch {
            val newPost = FeedPost(
                collegeName = user.collegeName,
                authorName = user.name,
                authorEmail = user.email,
                content = content
            )
            repository.insertPost(newPost)
        }
    }

    fun toggleLikePost(post: FeedPost) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val likedEmailsList = post.likedByEmails.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toMutableList()
            val newLikesCount: Int
            val newLikedByEmails: String

            if (likedEmailsList.contains(user.email)) {
                likedEmailsList.remove(user.email)
                newLikesCount = (post.likesCount - 1).coerceAtLeast(0)
            } else {
                likedEmailsList.add(user.email)
                newLikesCount = post.likesCount + 1
            }
            newLikedByEmails = likedEmailsList.joinToString(",")

            repository.updatePostLikes(post.id, newLikesCount, newLikedByEmails)

            // Refresh current feed to reflect immediately
            observePostsForCollege(user.collegeName)
        }
    }

    // Utilities
    private fun isValidCollegeEmail(email: String): Boolean {
        val cleanEmail = email.trim().lowercase()
        return cleanEmail.contains("@") && cleanEmail.isNotEmpty()
    }

    override fun onCleared() {
        super.onCleared()
        authStateListener?.let { auth.removeAuthStateListener(it) }
    }

    private fun guessCollegeName(email: String): String {
        val domain = email.substringAfter("@").lowercase()
        val parts = domain.substringBeforeLast(".edu").split(".")
        val namePart = parts.lastOrNull() ?: "State University"
        return namePart.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } + " University"
    }

    private fun String.capitalizeWords(): String {
        return split(" ").joinToString(" ") { word ->
            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
    }
}
