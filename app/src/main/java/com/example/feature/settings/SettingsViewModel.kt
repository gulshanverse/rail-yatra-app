package com.example.feature.settings

import androidx.lifecycle.ViewModel
import com.example.core.preferences.PreferencesHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(
    private val preferencesHelper: PreferencesHelper
) : ViewModel() {

    val isDarkMode: StateFlow<Boolean> = preferencesHelper.isDarkMode
    val selectedLanguage: StateFlow<String> = preferencesHelper.selectedLanguage
    val notificationsEnabled: StateFlow<Boolean> = preferencesHelper.notificationsEnabled

    private val _adminNotificationTitle = MutableStateFlow("")
    val adminNotificationTitle = _adminNotificationTitle.asStateFlow()

    private val _adminNotificationMessage = MutableStateFlow("")
    val adminNotificationMessage = _adminNotificationMessage.asStateFlow()

    fun toggleDarkMode() {
        preferencesHelper.setDarkMode(!isDarkMode.value)
    }

    fun changeLanguage(language: String) {
        preferencesHelper.setLanguage(language)
    }

    fun toggleNotifications() {
        preferencesHelper.setNotificationsEnabled(!notificationsEnabled.value)
    }

    fun sendAdminAnnouncement(title: String, message: String, onBroadcast: (String, String) -> Unit) {
        _adminNotificationTitle.value = title
        _adminNotificationMessage.value = message
        onBroadcast(title, message)
    }
}
