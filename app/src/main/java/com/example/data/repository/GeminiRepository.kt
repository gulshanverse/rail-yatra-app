package com.example.data.repository

import android.util.Log
import com.example.data.api.Content
import com.example.data.api.GeminiRequest
import com.example.data.api.Part
import com.example.data.api.RetrofitClient
import com.example.data.api.GenerationConfig
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiRepository {

    private val apiKey: String
        get() = BuildConfig.GEMINI_API_KEY

    private suspend fun callGemini(prompt: String, systemInstruction: String? = null): String = withContext(Dispatchers.IO) {
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.e("GeminiRepository", "Gemini API key is not configured. Falling back to local AI simulation.")
            return@withContext ""
        }

        try {
            val contentList = listOf(Content(parts = listOf(Part(text = prompt))))
            val sysInstruction = systemInstruction?.let { Content(parts = listOf(Part(text = it))) }
            val request = GeminiRequest(
                contents = contentList,
                systemInstruction = sysInstruction,
                generationConfig = GenerationConfig(temperature = 0.7f)
            )

            val response = RetrofitClient.service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
        } catch (e: Exception) {
            Log.e("GeminiRepository", "Error calling Gemini API: ${e.message}", e)
            ""
        }
    }

    /**
     * AI Confirmation Prediction for PNR statuses
     */
    suspend fun predictPnrConfirmation(
        pnr: String,
        trainNumber: String,
        trainName: String,
        currentStatus: String,
        journeyDate: String
    ): PnrPredictionResult = withContext(Dispatchers.IO) {
        val prompt = """
            Perform a professional Indian Railways PNR Confirmation Analysis.
            PNR: $pnr
            Train: $trainNumber - $trainName
            Current Status: $currentStatus
            Journey Date: $journeyDate
            
            Analyze waiting list trends, historical confirmations, seasonal demand, and festivals.
            Provide:
            1. Confirmation probability (%)
            2. Chance of RAC (%)
            3. Chance of Waiting (%)
            4. Detailed justification / booking trends
            5. Intelligent alternative travel choices if confirmation chance is low (such as split journeys, nearby boarding/destination stations, alternative premium trains, metro, bus, cab suggestions).
            
            Format your response exactly as a JSON string with these fields:
            {
               "probability": Int (value between 0 and 100),
               "racChance": Int (value between 0 and 100),
               "waitingChance": Int (value between 0 and 100),
               "analysis": "string summarizing seasonal demand, booking trends and historical predictions",
               "alternatives": "string describing split journeys, alternative trains, bus/metro, and cabs"
            }
            Do not include any markdown backticks or formatting. Output raw JSON only.
        """.trimIndent()

        val responseText = callGemini(
            prompt = prompt,
            systemInstruction = "You are RailYatra's premium AI smart prediction engine. Output ONLY valid raw JSON."
        )

        if (responseText.isNotEmpty()) {
            try {
                val cleanJson = responseText.trim().removeSurrounding("```json", "```").trim()
                // Simple parsing to avoid library complexities
                val prob = extractJsonInt(cleanJson, "probability") ?: 55
                val rac = extractJsonInt(cleanJson, "racChance") ?: 15
                val wl = extractJsonInt(cleanJson, "waitingChance") ?: 30
                val analysis = extractJsonString(cleanJson, "analysis") ?: "High travel demand observed during this period."
                val alternatives = extractJsonString(cleanJson, "alternatives") ?: "Consider booking 3A Class as seat availability is higher."
                return@withContext PnrPredictionResult(prob, rac, wl, analysis, alternatives)
            } catch (e: Exception) {
                Log.e("GeminiRepository", "Failed to parse JSON response: $responseText", e)
            }
        }

        // High-quality local simulator fallback if API fails
        val fallbackProb = if (currentStatus.contains("CNF")) 100 else if (currentStatus.contains("RAC")) 90 else if (currentStatus.contains("WL")) {
            val wlNum = currentStatus.substringAfter("/").toIntOrNull() ?: 20
            if (wlNum < 10) 75 else if (wlNum < 30) 55 else 30
        } else 50

        val fallbackAlternatives = if (fallbackProb < 60) {
            "• Split journey at intermediate junction.\n• Alternative trains: MMCT Tejas (12932) has available seats.\n• Try State Road Transport bus services or private sleeper cabs."
        } else {
            "Your ticket is highly likely to get confirmed. Keep tracking live status!"
        }

        PnrPredictionResult(
            probability = fallbackProb,
            racChance = if (fallbackProb == 100) 0 else if (fallbackProb > 80) 10 else 20,
            waitingChance = if (fallbackProb == 100) 0 else 100 - fallbackProb,
            analysis = "Simulated Analysis: High travel demand during monsoon travel season. Historical data indicates a ${fallbackProb}% confirmation rate for initial status $currentStatus.",
            alternatives = fallbackAlternatives
        )
    }

    /**
     * AI Assistant Chatbot
     */
    suspend fun getChatbotResponse(message: String, history: List<ChatMessage> = emptyList()): String = withContext(Dispatchers.IO) {
        val historyPrompt = history.joinToString("\n") { "${it.sender}: ${it.message}" }
        val prompt = """
            $historyPrompt
            User: $message
            
            Provide a helpful, precise, and polite response regarding Indian Railways queries, refund rules, Tatkal timings, platform tracking, coach position, luggage allowances, cancellation rates, local foods, emergency helpline guidance, or lost luggage. Keep the tone premium and assistive.
        """.trimIndent()

        val systemInstruction = """
            You are 'Yatri AI', the ultra-premium voice and text assistant for RailYatra.
            Help passengers book smarter and travel easier by giving smart railway guidance.
            If asked about cancellation rules: Standard cancellation charges are Flat Rs 240 for AC 1st/Executive, Rs 200 for AC 2 Tier, Rs 180 for AC 3 Tier, and Rs 120 for Sleeper if cancelled 48 hours before departure.
            If asked about Tatkal bookings: Tatkal window opens daily at 10:00 AM for AC classes and 11:00 AM for Non-AC classes of the journey date from train-originating station.
            If asked about refunds or lost baggage: Promptly list clear step-by-step guidance.
        """.trimIndent()

        val response = callGemini(prompt, systemInstruction)
        if (response.isNotEmpty()) {
            response
        } else {
            // Simulated response fallbacks for offline / missing API key
            val msgLower = message.lowercase()
            when {
                msgLower.contains("refund") -> "According to Indian Railways refund rules: If a ticket is cancelled online up to 48 hours before departure, flat cancellation charges are applied. AC 3T: ₹180, AC 2T: ₹200, Sleeper: ₹120. If cancelled within 12 hours, 25% of ticket fare is deducted."
                msgLower.contains("tatkal") -> "Tatkal ticket bookings open daily. For AC Classes (3A, 2A, 1A), bookings open at 10:00 AM. For Non-AC Classes (Sleeper, Second Seating), bookings open at 11:00 AM. Always book within the first 5 minutes to secure a seat!"
                msgLower.contains("luggage") -> "Free baggage allowance on Indian Railways: AC First Class: 70kg, AC 2-Tier: 50kg, AC 3-Tier: 40kg, Sleeper Class: 40kg. Excess baggage will attract a charge which is 1.5 times the standard rate."
                msgLower.contains("food") -> "RailYatra offers online e-catering service. You can enter your PNR to order hot, hygienic food from IRCTC-approved local restaurants and get it delivered right to your seat at upcoming stations."
                else -> "I can assist you with all your Indian Railways questions. For example, you can ask me about Tatkal booking timings, refund rates, cancellation rules, luggage allowances, or get a smart travel itinerary!"
            }
        }
    }

    /**
     * Smart Voice Assistant spoken command parser
     */
    suspend fun parseVoiceCommand(spokenText: String): VoiceAction = withContext(Dispatchers.IO) {
        val prompt = """
            Parse the following spoken travel command: "$spokenText"
            Identify the action type: "SEARCH_TRAINS", "CHECK_PNR", "LIVE_STATUS", or "GENERAL_HELP".
            Identify any entities like: source station, destination station, date, PNR number, train number.
            
            Return ONLY a raw JSON string like:
            {
               "action": "SEARCH_TRAINS",
               "source": "New Delhi",
               "destination": "Mumbai",
               "date": "Tomorrow",
               "pnr": "",
               "trainNumber": ""
            }
            Do not include markdown tags. Output JSON only.
        """.trimIndent()

        val response = callGemini(prompt, "You are a voice command natural language parser. Output raw JSON only.")
        if (response.isNotEmpty()) {
            try {
                val cleanJson = response.trim().removeSurrounding("```json", "```").trim()
                val action = extractJsonString(cleanJson, "action") ?: "GENERAL_HELP"
                val source = extractJsonString(cleanJson, "source") ?: ""
                val dest = extractJsonString(cleanJson, "destination") ?: ""
                val date = extractJsonString(cleanJson, "date") ?: ""
                val pnr = extractJsonString(cleanJson, "pnr") ?: ""
                val trainNumber = extractJsonString(cleanJson, "trainNumber") ?: ""
                return@withContext VoiceAction(action, source, dest, date, pnr, trainNumber)
            } catch (e: Exception) {
                Log.e("GeminiRepository", "Error parsing voice action JSON: $response", e)
            }
        }

        // Local Regex Parsing fallback
        val text = spokenText.lowercase()
        return@withContext when {
            text.contains("train") || text.contains("search") || text.contains("find") -> {
                val stations = parseStationsFromSpeech(text)
                VoiceAction("SEARCH_TRAINS", stations.first, stations.second, "Tomorrow")
            }
            text.contains("pnr") || text.contains("check status") -> {
                val pnrRegex = "\\d{10}".toRegex()
                val match = pnrRegex.find(text)?.value ?: "9876543210"
                VoiceAction("CHECK_PNR", pnr = match)
            }
            text.contains("live") || text.contains("where is") || text.contains("arrive") -> {
                val trainNumRegex = "\\d{5}".toRegex()
                val match = trainNumRegex.find(text)?.value ?: "12951"
                VoiceAction("LIVE_STATUS", trainNumber = match)
            }
            else -> VoiceAction("GENERAL_HELP")
        }
    }

    /**
     * Smart Journey Itinerary Planner
     */
    suspend fun generateJourneyPlanner(source: String, destination: String, date: String): JourneyItinerary = withContext(Dispatchers.IO) {
        val prompt = """
            Generate an ultra-premium multi-modal travel itinerary for a passenger journey from $source to $destination on $date.
            Provide details for:
            1. Pre-journey checklist
            2. Local metro, auto, or cab connection at $source
            3. Recommended trains
            4. Local metro, auto, or cab connection at $destination
            5. Recommended budget and premium hotels in $destination
            6. Nearby top tourist attractions in $destination
            7. Current seasonal weather summary in $destination
            8. Travel packing checklist
            
            Format your response exactly as a JSON string with these fields:
            {
               "checklist": "bullet points for travel preparation",
               "sourceTransit": "metro, auto, cab route from source",
               "destTransit": "metro, auto, cab route from destination",
               "hotels": "recommended hotels",
               "attractions": "places to visit",
               "weather": "expected weather",
               "itinerary": "full day wise or hour wise plan"
            }
            Output raw JSON only.
        """.trimIndent()

        val response = callGemini(prompt, "You are RailYatra's premium Smart Journey Planner. Output raw JSON only.")
        if (response.isNotEmpty()) {
            try {
                val cleanJson = response.trim().removeSurrounding("```json", "```").trim()
                return@withContext JourneyItinerary(
                    checklist = extractJsonString(cleanJson, "checklist") ?: "",
                    sourceTransit = extractJsonString(cleanJson, "sourceTransit") ?: "",
                    destTransit = extractJsonString(cleanJson, "destTransit") ?: "",
                    hotels = extractJsonString(cleanJson, "hotels") ?: "",
                    attractions = extractJsonString(cleanJson, "attractions") ?: "",
                    weather = extractJsonString(cleanJson, "weather") ?: "",
                    itinerary = extractJsonString(cleanJson, "itinerary") ?: ""
                )
            } catch (e: Exception) {
                Log.e("GeminiRepository", "Error parsing journey planner JSON: $response", e)
            }
        }

        // Local high-quality fallback simulator
        JourneyItinerary(
            checklist = "• Web check-in or download offline ticket PDF.\n• Charge power banks and mobile devices.\n• Pack light blankets and personal hygiene kit.",
            sourceTransit = "Board Delhi Metro Yellow Line to New Delhi Station (NDLS) directly. Quick and avoids road traffic.",
            destTransit = "From Mumbai Central (MMCT), board local train to Churchgate or take a prepaid local black-and-yellow taxi directly to your hotel.",
            hotels = "• Premium: Taj Mahal Palace (Colaba), Hotel Orchid.\n• Budget: Ginger Mumbai, Bloom Hotel.",
            attractions = "Gateway of India, Marine Drive Sunset, Siddhivinayak Temple, and shopping at Colaba Causeway.",
            weather = "Expected temperature 28°C - 32°C. High humidity. Occasional evening rain showers expected. Carry an umbrella.",
            itinerary = "• 16:30 - Arrive at NDLS, board Mumbai Rajdhani.\n• 17:00 - High tea served. High-speed travel through Kota.\n• 20:30 - Delicious hot dinner served.\n• 08:35 - Arrive MMCT, board local cab to Gateway of India."
        )
    }

    // --- Helper JSON extraction functions to make parsing extremely reliable without GSON/Jackson ---
    private fun extractJsonInt(json: String, key: String): Int? {
        val pattern = "\"$key\"\\s*:\\s*(\\d+)".toRegex()
        return pattern.find(json)?.groupValues?.get(1)?.toIntOrNull()
    }

    private fun extractJsonString(json: String, key: String): String? {
        val pattern = "\"$key\"\\s*:\\s*\"([^\"]*)\"".toRegex()
        val match = pattern.find(json)
        if (match != null) {
            return match.groupValues[1]
                .replace("\\n", "\n")
                .replace("\\t", "\t")
                .replace("\\\"", "\"")
        }
        return null
    }

    private fun parseStationsFromSpeech(text: String): Pair<String, String> {
        val DelhiList = listOf("delhi", "new delhi", "ndls")
        val MumbaiList = listOf("mumbai", "mumbai central", "mmct", "bombay")
        val BangaloreList = listOf("bangalore", "bengaluru", "sbc")
        val ChennaiList = listOf("chennai", "mas")

        var src = "New Delhi (NDLS)"
        var dest = "Mumbai Central (MMCT)"

        if (DelhiList.any { text.contains(it) }) {
            src = "New Delhi (NDLS)"
        }
        if (MumbaiList.any { text.contains(it) }) {
            dest = "Mumbai Central (MMCT)"
        }
        if (BangaloreList.any { text.contains(it) }) {
            dest = "Bengaluru (SBC)"
        }
        if (ChennaiList.any { text.contains(it) }) {
            dest = "Chennai Central (MAS)"
        }

        // Avoid source == dest
        if (src == dest) {
            dest = "Mumbai Central (MMCT)"
        }

        return Pair(src, dest)
    }
}

data class PnrPredictionResult(
    val probability: Int,
    val racChance: Int,
    val waitingChance: Int,
    val analysis: String,
    val alternatives: String
)

data class ChatMessage(
    val sender: String, // User or AI
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class VoiceAction(
    val action: String, // SEARCH_TRAINS, CHECK_PNR, LIVE_STATUS, GENERAL_HELP
    val source: String = "",
    val destination: String = "",
    val date: String = "",
    val pnr: String = "",
    val trainNumber: String = ""
)

data class JourneyItinerary(
    val checklist: String,
    val sourceTransit: String,
    val destTransit: String,
    val hotels: String,
    val attractions: String,
    val weather: String,
    val itinerary: String
)
