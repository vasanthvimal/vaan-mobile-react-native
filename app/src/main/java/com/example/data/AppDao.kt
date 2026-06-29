package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Client Meetings
    @Query("SELECT * FROM meetings ORDER BY dateTime ASC")
    fun getAllMeetings(): Flow<List<ClientMeeting>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeeting(meeting: ClientMeeting): Long

    @Update
    suspend fun updateMeeting(meeting: ClientMeeting)

    @Delete
    suspend fun deleteMeeting(meeting: ClientMeeting)

    @Query("DELETE FROM meetings WHERE id = :id")
    suspend fun deleteMeetingById(id: Int)

    // Client Inquiries
    @Query("SELECT * FROM inquiries ORDER BY receivedTime DESC")
    fun getAllInquiries(): Flow<List<ClientInquiry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInquiry(inquiry: ClientInquiry): Long

    @Update
    suspend fun updateInquiry(inquiry: ClientInquiry)

    @Delete
    suspend fun deleteInquiry(inquiry: ClientInquiry)

    // Appointments
    @Query("SELECT * FROM appointments ORDER BY dateTime ASC")
    fun getAllAppointments(): Flow<List<Appointment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: Appointment): Long

    @Update
    suspend fun updateAppointment(appointment: Appointment)

    @Delete
    suspend fun deleteAppointment(appointment: Appointment)

    // Email Logs
    @Query("SELECT * FROM email_logs ORDER BY sentTime DESC")
    fun getAllEmailLogs(): Flow<List<EmailLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmailLog(emailLog: EmailLog): Long
}
