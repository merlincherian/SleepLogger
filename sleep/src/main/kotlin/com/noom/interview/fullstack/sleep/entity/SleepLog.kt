package com.noom.interview.fullstack.sleep.entity

import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.sql.Timestamp

data class SleepLog(
    val id: Long = 0,
    val userId: Long,
    val sleepDate: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val totalTimeInBed: String,
    val feeling: String
)