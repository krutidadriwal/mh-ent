package com.example

import com.example.model.Screen
import com.example.viewmodel.MainViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testLoginAndLogout() {
        val viewModel = MainViewModel()
        viewModel.login("krutidadriwal1834@gmail.com", "Krutika")

        val stateAfterLogin = viewModel.uiState.value
        assertEquals("krutidadriwal1834@gmail.com", stateAfterLogin.currentUser?.email)
        assertEquals(Screen.HOME, stateAfterLogin.currentScreen)

        viewModel.logout()
        val stateAfterLogout = viewModel.uiState.value
        assertEquals(null, stateAfterLogout.currentUser)
        assertEquals(Screen.LOGIN, stateAfterLogout.currentScreen)
    }

    @Test
    fun testDarkModeToggle() {
        val viewModel = MainViewModel()
        val initialMode = viewModel.uiState.value.isDarkMode

        viewModel.toggleDarkMode()
        assertEquals(!initialMode, viewModel.uiState.value.isDarkMode)

        viewModel.setDarkMode(true)
        assertTrue(viewModel.uiState.value.isDarkMode)

        viewModel.setDarkMode(false)
        assertFalse(viewModel.uiState.value.isDarkMode)
    }

    @Test
    fun testNavigation() {
        val viewModel = MainViewModel()
        viewModel.navigateTo(Screen.OUTSTANDING)
        assertEquals(Screen.OUTSTANDING, viewModel.uiState.value.currentScreen)

        viewModel.navigateTo(Screen.PAYMENTS)
        assertEquals(Screen.PAYMENTS, viewModel.uiState.value.currentScreen)

        viewModel.navigateBack()
        assertEquals(Screen.HOME, viewModel.uiState.value.currentScreen)
    }

    @Test
    fun testToggleTask() {
        val viewModel = MainViewModel()
        val firstTask = viewModel.uiState.value.tasksList.first()
        val initialStatus = firstTask.isCompleted

        viewModel.toggleTask(firstTask.id)
        val updatedTask = viewModel.uiState.value.tasksList.first { it.id == firstTask.id }
        assertEquals(!initialStatus, updatedTask.isCompleted)
    }

    @Test
    fun testMarkNotificationsAsRead() {
        val viewModel = MainViewModel()
        viewModel.markNotificationsAsRead()
        assertTrue(viewModel.uiState.value.notificationsList.all { it.isRead })
    }
}
