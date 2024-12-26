package com.noom.interview.fullstack.sleep.controller

import com.noom.interview.fullstack.sleep.entity.SleepLog
import com.noom.interview.fullstack.sleep.repository.SleepLogRepository
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.sql.Timestamp
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.slf4j.LoggerFactory
import com.noom.interview.fullstack.sleep.dto.SleepLogDTO

@RestController
@RequestMapping("/api/sleep-logs")
class SleepLogController(private val sleepLogRepository: SleepLogRepository) {

    @PostMapping
    fun createSleepLog(@RequestBody request: SleepLogRequest): ResponseEntity<Void> {

        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
        val date = LocalDate.parse(request.sleepDate)
        var startTime = LocalTime.parse(request.startTime, formatter)
        var endTime = LocalTime.parse(request.endTime, formatter)


        var startDateTime = LocalDateTime.of(date, startTime)
        var endDateTime = LocalDateTime.of(date, endTime)
        if(endDateTime.isBefore(startDateTime)) {
            endDateTime = endDateTime.plusDays(1)
        }

        val totalTimeInBed = Duration.between(startDateTime, endDateTime).toString()

        val sleepLog = SleepLog(
            userId = request.userId,
            sleepDate = date,
            startTime = startTime,
            endTime = endTime,
            totalTimeInBed = totalTimeInBed,
            feeling = request.feeling
        )

        sleepLogRepository.create(sleepLog)
        return ResponseEntity.ok().build()

    }
    
    @GetMapping
    fun getAllSleepLogs(): ResponseEntity<List<SleepLog>> {
        val sleepLogs = sleepLogRepository.findAll()
        return ResponseEntity.ok(sleepLogs)
    }

    @GetMapping("users/{userId}/last-night")
    fun getLastNightSleepLog(@PathVariable userId: Long): SleepLogDTO? {
        val sleepLog = sleepLogRepository.findLastNightSleepLog(userId)
        return sleepLog?.let { SleepLogDTO.fromSleepLog(it) }
    }

    @GetMapping("/users/{userId}/last-30-days")
    fun getLast30DaysAverages(@PathVariable userId: Long): Map<String, Any> {
        return sleepLogRepository.getLast30DaysAverages(userId)
    }

}

data class SleepLogRequest(
    val userId: Long,
    val sleepDate: String,
    val startTime: String,
    val endTime: String,
    val feeling: String
)