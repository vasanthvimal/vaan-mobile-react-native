package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ClientMeeting::class,
        ClientInquiry::class,
        Appointment::class,
        EmailLog::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val isTest = try {
                System.getProperty("java.class.path")?.contains("junit") == true ||
                System.getProperty("java.class.path")?.contains("robolectric") == true
            } catch (e: Exception) {
                false
            }

            if (isTest) {
                return Room.inMemoryDatabaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java
                )
                .allowMainThreadQueries()
                .setQueryExecutor { it.run() }
                .setTransactionExecutor { it.run() }
                .build()
            }

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vaan_consulting_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
