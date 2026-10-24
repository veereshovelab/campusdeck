package com.example.data.repository

import com.example.data.db.UserDao
import com.example.data.db.ResourceDao
import com.example.data.db.GigDao
import com.example.data.db.PostDao
import com.example.data.db.MessageDao
import com.example.data.db.CommunityDao
import com.example.data.model.User
import com.example.data.model.ResourceMaterial
import com.example.data.model.Gig
import com.example.data.model.FeedPost
import com.example.data.model.DirectMessage
import com.example.data.model.ChatMessage
import com.example.data.model.CommunityPost
import com.example.data.model.CommunityComment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.FieldValue

class CampusRepository(
    private val userDao: UserDao,
    private val resourceDao: ResourceDao,
    private val gigDao: GigDao,
    private val postDao: PostDao,
    private val messageDao: MessageDao,
    private val communityDao: CommunityDao
) {
    private val firestore = FirebaseFirestore.getInstance()

    // Firestore User Profile
    suspend fun createFirestoreUserProfile(uid: String, user: User): Boolean {
        return try {
            val userMap = hashMapOf(
                "uid" to uid,
                "email" to user.email,
                "name" to user.name,
                "collegeName" to user.collegeName,
                "studentId" to user.studentId,
                "major" to user.major,
                "points" to user.points,
                "bio" to user.bio,
                "profilePictureUri" to user.profilePictureUri,
                "appearance" to user.appearance,
                "collegeCourse" to user.collegeCourse,
                "phoneNumber" to user.phoneNumber,
                "socialLink" to user.socialLink,
                "role" to user.role,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            firestore.collection("users").document(uid).set(userMap).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getFirestoreUserProfile(uid: String): User? {
        return try {
            val document = firestore.collection("users").document(uid).get().await()
            if (document.exists()) {
                User(
                    email = document.getString("email") ?: "",
                    name = document.getString("name") ?: "",
                    collegeName = document.getString("collegeName") ?: "",
                    studentId = document.getString("studentId") ?: "",
                    major = document.getString("major") ?: "",
                    points = document.getLong("points")?.toInt() ?: 100,
                    bio = document.getString("bio") ?: "",
                    profilePictureUri = document.getString("profilePictureUri"),
                    appearance = document.getString("appearance") ?: "System",
                    collegeCourse = document.getString("collegeCourse") ?: "",
                    phoneNumber = document.getString("phoneNumber") ?: "",
                    socialLink = document.getString("socialLink") ?: "",
                    role = document.getString("role") ?: "Student"
                )
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun updateFirestoreUserProfile(uid: String, user: User): Boolean {
        return try {
            val userMap = hashMapOf<String, Any?>(
                "name" to user.name,
                "collegeName" to user.collegeName,
                "studentId" to user.studentId,
                "major" to user.major,
                "points" to user.points,
                "bio" to user.bio,
                "profilePictureUri" to user.profilePictureUri,
                "appearance" to user.appearance,
                "collegeCourse" to user.collegeCourse,
                "phoneNumber" to user.phoneNumber,
                "socialLink" to user.socialLink,
                "role" to user.role,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            firestore.collection("users").document(uid).set(userMap, SetOptions.merge()).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Community
    fun getAllCommunityPosts() = communityDao.getAllCommunityPosts()
    suspend fun insertCommunityPost(post: CommunityPost) = communityDao.insertCommunityPost(post)
    suspend fun updatePostVotes(id: Int, upvotesCount: Int, upvotedByEmails: String, downvotesCount: Int, downvotedByEmails: String) =
        communityDao.updatePostVotes(id, upvotesCount, upvotedByEmails, downvotesCount, downvotedByEmails)
    suspend fun updateCommentsCount(id: Int, count: Int) = communityDao.updateCommentsCount(id, count)
    fun getCommentsForPost(postId: Int) = communityDao.getCommentsForPost(postId)
    suspend fun insertCommunityComment(comment: CommunityComment) = communityDao.insertCommunityComment(comment)
    // Messaging
    fun getDirectMessagesForUser(email: String) = messageDao.getDirectMessagesForUser(email)

    suspend fun createOrGetDirectMessage(myEmail: String, theirEmail: String): Int {
        val newDm = DirectMessage(participant1Email = myEmail, participant2Email = theirEmail)
        return messageDao.insertDirectMessage(newDm).toInt()
    }

    suspend fun getDirectMessageId(email1: String, email2: String): Int? {
        return messageDao.getDirectMessageId(email1, email2)
    }

    suspend fun getDirectMessageById(chatId: Int): DirectMessage? {
        return messageDao.getDirectMessageById(chatId)
    }

    fun getChatMessages(chatId: Int) = messageDao.getChatMessages(chatId)

    suspend fun sendChatMessage(chatId: Int, senderEmail: String, content: String) {
        val timestamp = System.currentTimeMillis()
        messageDao.insertChatMessage(ChatMessage(chatId = chatId, senderEmail = senderEmail, content = content, timestamp = timestamp))
        messageDao.updateLastMessage(chatId, content, timestamp)
    }

    fun getUserByEmail(email: String): Flow<User?> = userDao.getUserByEmail(email)

    suspend fun getUserByEmailSync(email: String): User? = userDao.getUserByEmailSync(email)

    fun getAllColleges(): Flow<List<String>> = userDao.getAllColleges()

    fun getAllMajors(): Flow<List<String>> = userDao.getAllMajors()

    fun getUsersInCollege(collegeName: String, myEmail: String): Flow<List<User>> = userDao.getUsersInCollege(collegeName, myEmail)

    fun getUsersInMajor(major: String, myEmail: String): Flow<List<User>> = userDao.getUsersInMajor(major, myEmail)

    fun searchUsersByName(nameQuery: String, myEmail: String): Flow<List<User>> = userDao.searchUsersByName("%$nameQuery%", myEmail)

    suspend fun insertUser(user: User) = userDao.insertUser(user)

    suspend fun updateUser(user: User) = userDao.updateUser(user)

    suspend fun updateUserPoints(email: String, points: Int) = userDao.updateUserPoints(email, points)

    fun getAllResources(): Flow<List<ResourceMaterial>> = resourceDao.getAllResources()

    fun searchResources(query: String): Flow<List<ResourceMaterial>> {
        return if (query.isBlank()) {
            resourceDao.getAllResources()
        } else {
            resourceDao.searchResources("%$query%")
        }
    }

    fun getResourcesByCollege(collegeName: String): Flow<List<ResourceMaterial>> = resourceDao.getResourcesByCollege(collegeName)

    fun getUniqueColleges(): Flow<List<String>> = resourceDao.getUniqueColleges()

    suspend fun insertResource(material: ResourceMaterial) = resourceDao.insertResource(material)

    suspend fun deleteResource(id: Int) = resourceDao.deleteResource(id)

    fun getAllGigs(): Flow<List<Gig>> = gigDao.getAllGigs()

    suspend fun insertGig(gig: Gig) = gigDao.insertGig(gig)

    suspend fun updateGigStatus(id: Int, status: String, helperEmail: String?, helperName: String?) {
        gigDao.updateGigStatus(id, status, helperEmail, helperName)
    }

    suspend fun deleteGig(id: Int) = gigDao.deleteGig(id)

    fun getPostsForCollege(collegeName: String): Flow<List<FeedPost>> = postDao.getPostsForCollege(collegeName)

    suspend fun insertPost(post: FeedPost) = postDao.insertPost(post)

    suspend fun updatePostLikes(id: Int, likesCount: Int, likedByEmails: String) {
        postDao.updatePostLikes(id, likesCount, likedByEmails)
    }
}
