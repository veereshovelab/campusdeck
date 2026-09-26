package com.example.data.repository

import com.example.data.db.UserDao
import com.example.data.db.ResourceDao
import com.example.data.db.GigDao
import com.example.data.db.PostDao
import com.example.data.db.MessageDao
import com.example.data.model.User
import com.example.data.model.ResourceMaterial
import com.example.data.model.Gig
import com.example.data.model.FeedPost
import com.example.data.model.DirectMessage
import com.example.data.model.ChatMessage
import kotlinx.coroutines.flow.Flow

class CampusRepository(
    private val userDao: UserDao,
    private val resourceDao: ResourceDao,
    private val gigDao: GigDao,
    private val postDao: PostDao,
    private val messageDao: MessageDao
) {
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
