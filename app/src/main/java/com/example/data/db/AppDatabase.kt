package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.User
import com.example.data.model.ResourceMaterial
import com.example.data.model.Gig
import com.example.data.model.FeedPost
import com.example.data.model.DirectMessage
import com.example.data.model.ChatMessage
import com.example.data.model.CommunityPost
import com.example.data.model.CommunityComment

@Database(
    entities = [User::class, ResourceMaterial::class, Gig::class, FeedPost::class, DirectMessage::class, ChatMessage::class, CommunityPost::class, CommunityComment::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun resourceDao(): ResourceDao
    abstract fun gigDao(): GigDao
    abstract fun postDao(): PostDao
    abstract fun messageDao(): MessageDao
    abstract fun communityDao(): CommunityDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "campusdeck_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
