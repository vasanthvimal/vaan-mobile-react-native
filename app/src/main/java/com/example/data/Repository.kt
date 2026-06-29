package com.example.data

import kotlinx.coroutines.flow.Flow

class AppRepository(private val appDao: AppDao) {

    // Meetings
    val allMeetings: Flow<List<ClientMeeting>> = appDao.getAllMeetings()

    suspend fun insertMeeting(meeting: ClientMeeting): Long {
        return appDao.insertMeeting(meeting)
    }

    suspend fun updateMeeting(meeting: ClientMeeting) {
        appDao.updateMeeting(meeting)
    }

    suspend fun deleteMeeting(meeting: ClientMeeting) {
        appDao.deleteMeeting(meeting)
    }

    suspend fun deleteMeetingById(id: Int) {
        appDao.deleteMeetingById(id)
    }

    // Inquiries
    val allInquiries: Flow<List<ClientInquiry>> = appDao.getAllInquiries()

    suspend fun insertInquiry(inquiry: ClientInquiry): Long {
        return appDao.insertInquiry(inquiry)
    }

    suspend fun updateInquiry(inquiry: ClientInquiry) {
        appDao.updateInquiry(inquiry)
    }

    suspend fun deleteInquiry(inquiry: ClientInquiry) {
        appDao.deleteInquiry(inquiry)
    }

    // Appointments
    val allAppointments: Flow<List<Appointment>> = appDao.getAllAppointments()

    suspend fun insertAppointment(appointment: Appointment): Long {
        return appDao.insertAppointment(appointment)
    }

    suspend fun updateAppointment(appointment: Appointment) {
        appDao.updateAppointment(appointment)
    }

    suspend fun deleteAppointment(appointment: Appointment) {
        appDao.deleteAppointment(appointment)
    }

    // Email Logs
    val allEmailLogs: Flow<List<EmailLog>> = appDao.getAllEmailLogs()

    suspend fun insertEmailLog(emailLog: EmailLog): Long {
        return appDao.insertEmailLog(emailLog)
    }
}
