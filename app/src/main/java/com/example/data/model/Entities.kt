package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val email: String,
    val name: String,
    val collegeName: String,
    val studentId: String,
    val major: String,
    val points: Int = 100, // Starting balance for localized token collaboration
    val bio: String = "",
    val profilePictureUri: String? = null,
    val appearance: String = "System",
    val collegeCourse: String = "",
    val phoneNumber: String = "",
    val socialLink: String = "",
    val role: String = "Student" // roles: Student, Faculty, Admin
)

@Entity(tableName = "direct_messages")
data class DirectMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val participant1Email: String,
    val participant2Email: String,
    val lastMessage: String = "",
    val lastMessageTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val chatId: Int,
    val senderEmail: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "resource_materials")
data class ResourceMaterial(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val courseCode: String, // CS101, MATH201
    val professor: String,
    val semester: String, // Fall 2026, Spring 2026
    val description: String,
    val fileType: String, // PDF, Study Guide, Exam Prep
    val uploaderEmail: String,
    val uploaderName: String,
    val collegeName: String,
    val priceInPoints: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "gigs")
data class Gig(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String, // Tutoring, Lab Equipment, Study Group, Help, Other
    val description: String,
    val rewardPoints: Int,
    val requesterEmail: String,
    val requesterName: String,
    val contactInfo: String,
    val status: String = "OPEN", // OPEN, ACCEPTED, COMPLETED
    val helperEmail: String? = null,
    val helperName: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "feed_posts")
data class FeedPost(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val collegeName: String,
    val authorName: String,
    val authorEmail: String,
    val content: String,
    val likesCount: Int = 0,
    val likedByEmails: String = "", // Comma-separated list of emails who liked
    val timestamp: Long = System.currentTimeMillis()
)

data class FriendSuggestion(
    val id: String,
    val name: String,
    val context: String, // e.g., "3 mutual friends", "Nearby"
    val avatarInitial: String
)

sealed class HomeFeedItem {
    data class Post(val post: FeedPost) : HomeFeedItem()
    data class Suggestions(val suggestions: List<FriendSuggestion>) : HomeFeedItem()
}

@Entity(tableName = "community_posts")
data class CommunityPost(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val collegeName: String,
    val authorName: String,
    val authorEmail: String,
    val isAnonymous: Boolean = true,
    val title: String,
    val content: String,
    val categoryTag: String = "Campus Discussion",
    val upvotesCount: Int = 1,
    val upvotedByEmails: String = "",
    val downvotesCount: Int = 0,
    val downvotedByEmails: String = "",
    val commentsCount: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "community_comments")
data class CommunityComment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val postId: Int,
    val authorName: String,
    val authorEmail: String,
    val isAnonymous: Boolean = true,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)
