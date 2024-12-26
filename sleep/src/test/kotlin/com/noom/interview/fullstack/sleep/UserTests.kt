package com.noom.interview.fullstack.sleep.controller

import com.noom.interview.fullstack.sleep.entity.User
import com.noom.interview.fullstack.sleep.repository.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

class UserControllerTest {

    private lateinit var userRepository: UserRepository
    private lateinit var userController: UserController

    @BeforeEach
    fun setUp() {
        userRepository = mock(UserRepository::class.java)
        userController = UserController(userRepository)
    }

    @Test
    fun `test getAllUsers`() {
        val users = listOf(User(1, "John Doe", "john.doe@example.com"))
        `when`(userRepository.findAll()).thenReturn(users)

        val result = userController.getAllUsers()
        assertEquals(1, result.size)
        assertEquals("John Doe", result[0].username)
    }

    @Test
    fun `test getUserById`() {
        val user = User(1, "John Doe", "john.doe@example.com")
        `when`(userRepository.findById(1)).thenReturn(user)

        val result = userController.getUserById(1)
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals("John Doe", result.body?.username)
    }

    @Test
    fun `test getUserById not found`() {
        `when`(userRepository.findById(1)).thenReturn(null)

        val result = userController.getUserById(1)
        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
    }

    @Test
    fun `test createUser`() {
        val user = User(1, "John Doe", "john.doe@example.com")
        `when`(userRepository.save(user)).thenReturn(1)

        val result = userController.createUser(user)
        assertEquals(HttpStatus.CREATED, result.statusCode)
        assertEquals("John Doe", result.body?.username)
    }

    @Test
    fun `test updateUser`() {
        val existingUser = User(1, "John Doe", "john.doe@example.com")
        val updatedUser = User(1, "Jane Doe", "jane.doe@example.com")
        `when`(userRepository.findById(1)).thenReturn(existingUser)
        `when`(userRepository.update(updatedUser)).thenReturn(1)

        val result = userController.updateUser(1, updatedUser)
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals("Jane Doe", result.body?.username)
    }

    @Test
    fun `test updateUser not found`() {
        val updatedUser = User(1, "Jane Doe", "jane.doe@example.com")
        `when`(userRepository.findById(1)).thenReturn(null)

        val result = userController.updateUser(1, updatedUser)
        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
    }

    @Test
    fun `test deleteUser`() {
        `when`(userRepository.deleteById(1)).thenReturn(1)

        val result = userController.deleteUser(1)
        assertEquals(HttpStatus.NO_CONTENT, result.statusCode)
    }

    @Test
    fun `test deleteUser not found`() {
        `when`(userRepository.deleteById(1)).thenReturn(0)

        val result = userController.deleteUser(1)
        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
    }
}