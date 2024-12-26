package com.noom.interview.fullstack.sleep.repository

import com.noom.interview.fullstack.sleep.entity.User
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

@Repository
class UserRepository(private val jdbcTemplate: JdbcTemplate) {

    private val rowMapper = RowMapper<User> { rs, _ ->
        User(
            id = rs.getLong("id"),
            username = rs.getString("username"),
            email = rs.getString("email")
        )
    }

    fun findAll(): List<User> {
        return jdbcTemplate.query("SELECT * FROM users", rowMapper)
    }

    fun findById(id: Long): User? {
        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE id = ?", rowMapper, id)
    }

    fun save(user: User): Int {
        return jdbcTemplate.update(
            "INSERT INTO users (username, email) VALUES (?, ?)",
            user.username, user.email
        )
    }

    fun update(user: User): Int {
        return jdbcTemplate.update(
            "UPDATE users SET username = ?, email = ? WHERE id = ?",
            user.username, user.email, user.id
        )
    }

    fun deleteById(id: Long): Int {
        return jdbcTemplate.update("DELETE FROM users WHERE id = ?", id)
    }
}