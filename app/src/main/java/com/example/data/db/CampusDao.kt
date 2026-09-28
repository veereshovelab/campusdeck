package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.User
import com.example.data.model.ResourceMaterial
import com.example.data.model.Gig
import com.example.data.model.FeedPost
import com.example.data.model.DirectMessage
import com.example.data.model.ChatMessage
import com.example.data.model.CommunityPost
import com.example.data.model.CommunityComment
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM direct_messages WHERE participant1Email = :email OR participant2Email = :email ORDER BY lastMessageTimestamp DESC")
    fun getDirectMessagesForUser(email: String): Flow<List<DirectMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDirectMessage(message: DirectMessage): Long

    @Query("SELECT * FROM chat_messages WHERE chatId = :chatId ORDER BY timestamp ASC")
    fun getChatMessages(chatId: Int): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessage)

    @Query("UPDATE direct_messages SET lastMessage = :lastMessage, lastMessageTimestamp = :timestamp WHERE id = :chatId")
    suspend fun updateLastMessage(chatId: Int, lastMessage: String, timestamp: Long)

    @Query("SELECT id FROM direct_messages WHERE (participant1Email = :email1 AND participant2Email = :email2) OR (participant1Email = :email2 AND participant2Email = :email1) LIMIT 1")
    suspend fun getDirectMessageId(email1: String, email2: String): Int?

    @Query("SELECT * FROM direct_messages WHERE id = :chatId LIMIT 1")
    suspend fun getDirectMessageById(chatId: Int): DirectMessage?
}

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    fun getUserByEmail(email: String): Flow<User?>

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmailSync(email: String): User?

    @Query("UPDATE users SET points = :points WHERE email = :email")
    suspend fun updateUserPoints(email: String, points: Int)

    @Query("SELECT DISTINCT collegeName FROM users WHERE collegeName != '' ORDER BY collegeName ASC")
    fun getAllColleges(): Flow<List<String>>

    @Query("SELECT DISTINCT major FROM users WHERE major != '' ORDER BY major ASC")
    fun getAllMajors(): Flow<List<String>>

    @Query("SELECT * FROM users WHERE collegeName = :collegeName AND email != :myEmail ORDER BY name ASC")
    fun getUsersInCollege(collegeName: String, myEmail: String): Flow<List<User>>

    @Query("SELECT * FROM users WHERE major = :major AND email != :myEmail ORDER BY name ASC")
    fun getUsersInMajor(major: String, myEmail: String): Flow<List<User>>

    @Query("SELECT * FROM users WHERE name LIKE :nameQuery AND email != :myEmail ORDER BY name ASC")
    fun searchUsersByName(nameQuery: String, myEmail: String): Flow<List<User>>
}

@Dao
interface ResourceDao {
    @Query("SELECT * FROM resource_materials ORDER BY timestamp DESC")
    fun getAllResources(): Flow<List<ResourceMaterial>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResource(material: ResourceMaterial)

    @Query("SELECT * FROM resource_materials WHERE title LIKE :query OR courseCode LIKE :query OR professor LIKE :query ORDER BY timestamp DESC")
    fun searchResources(query: String): Flow<List<ResourceMaterial>>

    @Query("SELECT * FROM resource_materials WHERE collegeName = :collegeName ORDER BY timestamp DESC")
    fun getResourcesByCollege(collegeName: String): Flow<List<ResourceMaterial>>

    @Query("SELECT DISTINCT collegeName FROM resource_materials UNION SELECT DISTINCT collegeName FROM users")
    fun getUniqueColleges(): Flow<List<String>>

    @Query("DELETE FROM resource_materials WHERE id = :id")
    suspend fun deleteResource(id: Int)
}

@Dao
interface GigDao {
    @Query("SELECT * FROM gigs ORDER BY timestamp DESC")
    fun getAllGigs(): Flow<List<Gig>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGig(gig: Gig)

    @Query("UPDATE gigs SET status = :status, helperEmail = :helperEmail, helperName = :helperName WHERE id = :id")
    suspend fun updateGigStatus(id: Int, status: String, helperEmail: String?, helperName: String?)

    @Query("DELETE FROM gigs WHERE id = :id")
    suspend fun deleteGig(id: Int)
}

@Dao
interface PostDao {
    @Query("SELECT * FROM feed_posts WHERE collegeName = :collegeName ORDER BY timestamp DESC")
    fun getPostsForCollege(collegeName: String): Flow<List<FeedPost>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: FeedPost)

    @Query("UPDATE feed_posts SET likesCount = :likesCount, likedByEmails = :likedByEmails WHERE id = :id")
    suspend fun updatePostLikes(id: Int, likesCount: Int, likedByEmails: String)
}

@Dao
interface CommunityDao {
    @Query("SELECT * FROM community_posts ORDER BY timestamp DESC")
    fun getAllCommunityPosts(): Flow<List<CommunityPost>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommunityPost(post: CommunityPost): Long

    @Query("UPDATE community_posts SET upvotesCount = :upvotesCount, upvotedByEmails = :upvotedByEmails, downvotesCount = :downvotesCount, downvotedByEmails = :downvotedByEmails WHERE id = :id")
    suspend fun updatePostVotes(id: Int, upvotesCount: Int, upvotedByEmails: String, downvotesCount: Int, downvotedByEmails: String)

    @Query("UPDATE community_posts SET commentsCount = :commentsCount WHERE id = :id")
    suspend fun updateCommentsCount(id: Int, commentsCount: Int)

    @Query("SELECT * FROM community_comments WHERE postId = :postId ORDER BY timestamp ASC")
    fun getCommentsForPost(postId: Int): Flow<List<CommunityComment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommunityComment(comment: CommunityComment): Long
}
