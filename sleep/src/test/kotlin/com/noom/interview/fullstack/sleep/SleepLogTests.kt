import com.noom.interview.fullstack.sleep.entity.SleepLog
import com.noom.interview.fullstack.sleep.repository.SleepLogRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class SleepLogRepositoryTest {

    @Mock
    private lateinit var jdbcTemplate: JdbcTemplate

    @InjectMocks
    private lateinit var sleepLogRepository: SleepLogRepository

    @Test
    fun `test create`() {
        val sleepLog = SleepLog(
            id = 1L,
            userId = 1L,
            sleepDate = LocalDate.now(),
            startTime = LocalTime.of(22, 0),
            endTime = LocalTime.of(6, 0),
            totalTimeInBed = Duration.ofHours(8).toString(),
            feeling = "GOOD"
        )

        val sql = """
            INSERT INTO sleep_logs (user_id, sleep_date, start_time, end_time, total_time_in_bed, feeling)
            VALUES (?, ?, ?, ?, ?::interval, ?)
        """

        `when`(jdbcTemplate.update(sql, sleepLog.userId, sleepLog.sleepDate, LocalDateTime.of(sleepLog.sleepDate, sleepLog.startTime), LocalDateTime.of(sleepLog.sleepDate, sleepLog.endTime), sleepLog.totalTimeInBed.toString(), sleepLog.feeling))
            .thenReturn(1)

        val result = sleepLogRepository.create(sleepLog)
        assertEquals(1, result)
    }

    @Test
    fun `test findAll`() {
        val sql = "SELECT * FROM sleep_logs"
        val sleepLog = SleepLog(
            id = 1L,
            userId = 1L,
            sleepDate = LocalDate.now(),
            startTime = LocalTime.of(22, 0),
            endTime = LocalTime.of(6, 0),
            totalTimeInBed = Duration.ofHours(8).toString(),
            feeling = "GOOD"
        )

        `when`(jdbcTemplate.query(eq(sql), any(RowMapper::class.java)))
            .thenReturn(listOf(sleepLog))

        val result = sleepLogRepository.findAll()
        assertEquals(1, result.size)
        assertEquals(sleepLog, result[0])
    }

    @Test
    fun `test findLastNightSleepLog`() {
        val userId = 1L
        val lastNight = LocalDate.now().minusDays(1)
        val sql = """
            SELECT * FROM sleep_logs
            WHERE user_id = ? AND sleep_date = ?
            LIMIT 1
        """
        val sleepLog = SleepLog(
            id = 1L,
            userId = userId,
            sleepDate = lastNight,
            startTime = LocalTime.of(22, 0),
            endTime = LocalTime.of(6, 0),
            totalTimeInBed = Duration.ofHours(8).toString(),
            feeling = "GOOD"
        )

        `when`(jdbcTemplate.query(eq(sql), eq(arrayOf(userId, lastNight)), any(RowMapper::class.java)))
            .thenReturn(listOf(sleepLog))

        val result = sleepLogRepository.findLastNightSleepLog(userId)
        assertNotNull(result)
        assertEquals(sleepLog, result)
    }

    @Test
    fun `test getLast30DaysAverages`() {
        val userId = 1L
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

        val resultSet = mock(ResultSet::class.java)
        `when`(resultSet.getDate("start_date")).thenReturn(java.sql.Date.valueOf(LocalDate.now().minusDays(30)))
        `when`(resultSet.getDate("end_date")).thenReturn(java.sql.Date.valueOf(LocalDate.now()))
        `when`(resultSet.getDouble("avg_total_time_in_bed_hours")).thenReturn(8.0)
        `when`(resultSet.getDouble("avg_bed_time")).thenReturn(22.0)
        `when`(resultSet.getDouble("avg_wake_time")).thenReturn(6.0)
        `when`(resultSet.getInt("bad_feeling_count")).thenReturn(2)
        `when`(resultSet.getInt("ok_feeling_count")).thenReturn(5)
        `when`(resultSet.getInt("good_feeling_count")).thenReturn(23)

        val rowMapper = RowMapper<Map<String, Any>> { rs, _ ->
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
        }

        `when`(jdbcTemplate.queryForObject(eq(sql), eq(arrayOf<Any>(userId)), any<RowMapper<Map<String, Any>>>()))
            .thenAnswer { invocation ->
                rowMapper.mapRow(resultSet, 1)
            }

        val result = sleepLogRepository.getLast30DaysAverages(userId)
        assertEquals("8 h 0 mins", result["avg_total_time_in_bed_hours"])
        assertEquals("10:00 PM", result["avg_bed_time"])
        assertEquals("06:00 AM", result["avg_wake_time"])
        assertEquals(2, result["bad_feeling_count"])
        assertEquals(5, result["ok_feeling_count"])
        assertEquals(23, result["good_feeling_count"])
    }

    @Test
    fun `test convertToAmPmFormat`() {
        assertEquals("12:00 AM", convertToAmPmFormat(0.0))
        assertEquals("01:30 AM", convertToAmPmFormat(1.5))
        assertEquals("12:00 PM", convertToAmPmFormat(12.0))
        assertEquals("01:45 PM", convertToAmPmFormat(13.75))
    }

    private fun convertToAmPmFormat(time: Double): String {
        val hours = time.toInt()
        val minutes = ((time - hours) * 60).toInt()
        val amPm = if (hours >= 12) "PM" else "AM"
        val formattedHours = if (hours % 12 == 0) 12 else hours % 12
        return String.format("%02d:%02d %s", formattedHours, minutes, amPm)
    }
}