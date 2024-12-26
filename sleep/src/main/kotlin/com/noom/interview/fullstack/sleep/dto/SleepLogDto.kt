package com.noom.interview.fullstack.sleep.dto

import com.noom.interview.fullstack.sleep.entity.SleepLog
import java.time.format.DateTimeFormatter
import java.time.Duration

data class SleepLogDTO(
    val id: Long,
    val userId: Long,
    val sleepDate: String,
    val startTime: String,
    val endTime: String,
    val totalTimeInBed: String,
    val feeling: String,
    // val formattedStartTime: String
) {
    companion object {
        fun fromSleepLog(sleepLog: SleepLog): SleepLogDTO {
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
            val twelveHourFormatter = DateTimeFormatter.ofPattern("h:mm a")

            return SleepLogDTO(
                id = sleepLog.id,
                userId = sleepLog.userId,
                sleepDate = sleepLog.sleepDate.format(dateFormatter),
                totalTimeInBed = formatDuration(Duration.parse(sleepLog.totalTimeInBed)),
                feeling = sleepLog.feeling,
                startTime = sleepLog.startTime.format(twelveHourFormatter),
                endTime = sleepLog.endTime.format(twelveHourFormatter)

            )
        }
        private fun formatDuration(duration: Duration): String {
            val hours = duration.toHours()
            val minutes = duration.minusHours(hours).toMinutes()
            return "${hours}H ${minutes} min"
        }
    }
}