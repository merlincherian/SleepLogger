package com.noom.interview.fullstack.sleep.repository

import com.noom.interview.fullstack.sleep.entity.SleepLog
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Repository
class SleepLogRepository(private val jdbcTemplate: JdbcTemplate) {

    fun create(sleepLog: SleepLog): Int {
        val sql = """
            INSERT INTO sleep_logs (user_id, sleep_date, start_time, end_time, total_time_in_bed, feeling)
            VALUES (?, ?, ?, ?, ?::interval, ?)
        """
        val startDateTime = LocalDateTime.of(sleepLog.sleepDate, sleepLog.startTime)
        val endDateTime = LocalDateTime.of(sleepLog.sleepDate, sleepLog.endTime)
        val totalTimeInBed = sleepLog.totalTimeInBed.toString()

        return jdbcTemplate.update(sql, sleepLog.userId, sleepLog.sleepDate, startDateTime, endDateTime, totalTimeInBed, sleepLog.feeling)
    }

    fun findAll(): List<SleepLog> {
        val sql = "SELECT * FROM sleep_logs"
        return jdbcTemplate.query(sql, sleepLogRowMapper)
    }

    private val sleepLogRowMapper = RowMapper { rs: ResultSet, _: Int ->
        SleepLog(
            id = rs.getLong("id"),
            userId = rs.getLong("user_id"),
            sleepDate = rs.getObject("sleep_date", LocalDate::class.java),
            startTime = rs.getObject("start_time", LocalTime::class.java),
            endTime = rs.getObject("end_time", LocalTime::class.java),
            totalTimeInBed = parseIntervalToDuration(rs.getString("total_time_in_bed")).toString(),
            feeling = rs.getString("feeling")
        )
    }

    private fun parseIntervalToDuration(interval: String): Duration {
        val parts = interval.split(":")
        return if (parts.size == 3) {
            val hours = parts[0].toLong()
            val minutes = parts[1].toLong()
            val seconds = parts[2].toLong()
            Duration.ofHours(hours).plusMinutes(minutes).plusSeconds(seconds)
        } else {
            throw IllegalArgumentException("Invalid interval format: $interval")
        }
    }

    fun findLastNightSleepLog(userId: Long): SleepLog? {
        val lastNight = LocalDate.now().minusDays(1)
        val sql = """
            SELECT * FROM sleep_logs
            WHERE user_id = ? AND sleep_date = ?
            LIMIT 1
        """
        return jdbcTemplate.query(sql, arrayOf(userId, lastNight), sleepLogRowMapper).firstOrNull()
    }

    fun convertToAmPmFormat(avgBedTime: Double): String {
        val hours = avgBedTime.toInt()
        val minutes = ((avgBedTime - hours) * 60).toInt()
        val time = LocalTime.of(hours, minutes)
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
        return time.format(formatter)
    }

    fun getLast30DaysAverages(userId: Long): Map<String, Any> {
        val sql = """
            SELECT
                CURRENT_DATE - INTERVAL '30 days' AS start_date,
                CURRENT_DATE AS end_date,
                AVG(EXTRACT(EPOCH FROM total_time_in_bed) / 3600) AS avg_total_time_in_bed_hours,
                AVG(EXTRACT(HOUR FROM start_time) + EXTRACT(MINUTE FROM start_time) / 60.0) AS avg_bed_time,
                AVG(EXTRACT(HOUR FROM end_time) + EXTRACT(MINUTE FROM end_time) / 60.0) AS avg_wake_time,
                COUNT(CASE WHEN feeling = 'BAD' THEN 1 END) AS bad_feeling_count,
                COUNT(CASE WHEN feeling = 'OK' THEN 1 END) AS ok_feeling_count,
                COUNT(CASE WHEN feeling = 'GOOD' THEN 1 END) AS good_feeling_count
            FROM
                sleep_logs
            WHERE
                sleep_date >= CURRENT_DATE - INTERVAL '30 days'
                AND user_id = ?
                AND sleep_date IS NOT NULL
        """
        return jdbcTemplate.queryForObject(sql, arrayOf(userId), RowMapper { rs: ResultSet, _: Int ->
            val avgTotalTimeInBedHours = rs.getDouble("avg_total_time_in_bed_hours")
            val hours = avgTotalTimeInBedHours.toInt()
            val minutes = ((avgTotalTimeInBedHours - hours) * 60).toInt()
    
            mapOf(
                "start_date" to rs.getDate("start_date"),
                "end_date" to rs.getDate("end_date"),
                "avg_total_time_in_bed_hours" to String.format("%d h %d mins", hours, minutes),
                "avg_bed_time" to convertToAmPmFormat(rs.getDouble("avg_bed_time")),
                "avg_wake_time" to convertToAmPmFormat(rs.getDouble("avg_wake_time")),
                "bad_feeling_count" to rs.getInt("bad_feeling_count"),
                "ok_feeling_count" to rs.getInt("ok_feeling_count"),
                "good_feeling_count" to rs.getInt("good_feeling_count")
            )
        }) ?: emptyMap()
    }

    internal fun formatDuration(duration: Duration): String {
        val hours = duration.toHours()
        val minutes = duration.toMinutes() % 60
        return "${hours} hrs ${minutes} mins"
    }
}