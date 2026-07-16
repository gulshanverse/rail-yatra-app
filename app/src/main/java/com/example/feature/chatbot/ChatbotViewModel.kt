package com.example.feature.chatbot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.network.ChatMessage
import com.example.core.network.GeminiRepository
import com.example.core.network.VoiceAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatbotViewModel(
    private val geminiRepository: GeminiRepository
) : ViewModel() {

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(listOf(
        ChatMessage("AI", "Namaste! I am Yatri AI, your premium RailYatra assistant. How can I help you make your journey smoother today?")
    ))
    val chatMessages = _chatMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading = _isChatLoading.asStateFlow()

    private val _speechText = MutableStateFlow("")
    val speechText = _speechText.asStateFlow()

    fun sendChatMessage(msg: String) {
        if (msg.trim().isEmpty()) return
        val userMsg = ChatMessage("User", msg)
        _chatMessages.value = _chatMessages.value + userMsg
        _isChatLoading.value = true

        viewModelScope.launch {
            val aiResponseText = geminiRepository.getChatbotResponse(msg, _chatMessages.value)
            _chatMessages.value = _chatMessages.value + ChatMessage("AI", aiResponseText)
            _isChatLoading.value = false
        }
    }

    fun clearChat() {
        _chatMessages.value = listOf(ChatMessage("AI", "Namaste! I am Yatri AI, your premium RailYatra assistant. How can I help you make your journey smoother today?"))
    }

    fun processVoiceInput(text: String, onVoiceAction: (VoiceAction) -> Unit) {
        _speechText.value = text
        viewModelScope.launch {
            val action = geminiRepository.parseVoiceCommand(text)
            onVoiceAction(action)
        }
    }
}
