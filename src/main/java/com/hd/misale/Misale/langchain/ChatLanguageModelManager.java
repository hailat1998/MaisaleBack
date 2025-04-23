package com.hd.misale.Misale.langchain;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;



@Component
@Slf4j
public class ChatLanguageModelManager {

    // Make model final as it's initialized in the constructor
    public final ChatLanguageModel model;

    public ChatLanguageModelManager() {
        String apiKey = System.getenv("GEMINI_AI_KEY");
        if (apiKey == null || apiKey.trim().isEmpty()) {
            // Log error and potentially throw exception if API key is essential for startup
            log.error("FATAL: GEMINI_AI_KEY environment variable is not set!");
            throw new IllegalStateException("GEMINI_AI_KEY environment variable must be set.");
        }

        // Verify the correct model name. "gemini-2.0-flash" might not be standard.
        // Common options: "gemini-1.5-flash-latest", "gemini-1.5-pro-latest", "gemini-1.0-pro"
        // Using "gemini-1.5-flash-latest" as a likely valid example.
        String modelName = "gemini-2.0-flash"; // <-- Adjust if needed

        log.info("Initializing Google AI Gemini Chat Model ({})...", modelName);

        // Assuming you are using the Google AI (MakerSuite/AI Studio) API key directly
        this.model = GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                // Optional parameters:
                .temperature(0.3) // Lower temperature for more deterministic translations/transliterations
                .topK(1)           // Consider setting topK/topP for focused results

                .build();

        log.info("Google AI Gemini Chat Model initialized successfully.");
    }

    /**
     * Translates the userMessage from English to Amharic using the configured Gemini model.
     * @param userMessage the message from the user in English.
     * @return the translated response in Amharic, or an error message if translation fails.
     */
    public String translateToAmharic(String userMessage) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            log.warn("translateToAmharic called with empty message.");
            return ""; // Return empty for empty input
        }

        // Construct a clear prompt for the LLM
        String prompt = String.format(
                """
                        Translate the following English text accurately into Amharic. \
                        Provide only the Amharic translation, without any explanations or labels.
                        
                        English: %s
                        
                        Amharic:""",
                userMessage
        );

        log.debug("Sending translation prompt to Gemini:\n{}", prompt); // Log the prompt for debugging

        try {
            String response = model.chat(prompt);
            log.debug("Received translation response from Gemini: {}", response);
            // Trim whitespace which models sometimes add
            return response.trim();
        } catch (Exception e) {
            log.error("Error during translation to Amharic for input '{}': {}", userMessage, e.getMessage(), e);
            // Return a meaningful error message or re-throw a custom exception
            return "[Translation Error]";
        }
    }

    /**
     * Transliterates Amharic text written phonetically in Latin letters to the standard Amharic Fidel script.
     * @param userMessage The Amharic message written using Latin letters (e.g., "selam new").
     * @return The transliterated word(s) in Amharic Fidel script, or an error message.
     */
    public String transliterationToAmharic(String userMessage) { // Renamed method for clarity
        if (userMessage == null || userMessage.trim().isEmpty()) {
            log.warn("transliterationToAmharic called with empty message.");
            return ""; // Return empty for empty input
        }

        // Construct a clear prompt for the LLM
        String prompt = String.format(
                """
                        Convert the following Amharic text, which is written phonetically using Latin letters, \
                        into the standard Amharic Fidel script. \
                        Provide only the Amharic script output, without any explanations or labels.
                        
                        Input (Latin Script Amharic): %s
                        
                        Output (Amharic Fidel Script):""",
                userMessage
        );

        log.debug("Sending transliteration prompt to Gemini:\n{}", prompt);

        try {
            String response = model.chat(prompt);
            log.debug("Received transliteration response from Gemini: {}", response);
            // Trim whitespace
            return response.trim();
        } catch (Exception e) {
            log.error("Error during transliteration to Amharic Fidel for input '{}': {}", userMessage, e.getMessage(), e);
            // Return a meaningful error message or re-throw a custom exception
            return "[Transliteration Error]";
        }
    }

    /**
     * Explains the meaning of an Amharic proverb or expression *in Amharic*.
     * Uses a prompt structure similar to the provided curl request.
     * @param userMessage The Amharic proverb or expression (e.g., "ዕንጨት አይሸከሙ ላመድ አሮጊት አያገቡ ላትወልድ").
     * @return The explanation of the meaning in Amharic, or an error message.
     */
    public String meaningInAmharic(String userMessage) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            log.warn("meaningInAmharic called with empty message.");
            return "";
        }

        // Prompt asking for explanation in Amharic, similar to the curl example structure.
        // "In the Amharic language, explain the following Amharic proverb/expression: '{proverb}'. Provide only the explanation."
        // Note: Ensure your source files are UTF-8 encoded to handle Amharic characters correctly.
        String prompt = String.format(
                """
                        በአማርኛ ቋንቋ የሚከተለውን የአማርኛ አባባል ትርጉም አብራራ/ያስረዳ። ማብራሪያውን ብቻ መልስ።
                        
                        አባባል፦ "%s"
                        
                        ማብራሪያ፦""",
                userMessage
        );

        log.debug("Sending Amharic meaning prompt to Gemini:\n{}", prompt);

        try {
            // LangChain4j handles the JSON structuring ("contents", "parts", "text") internally
            String response = model.chat(prompt);
            log.debug("Received Amharic meaning response from Gemini: {}", response);
            return response.trim();
        } catch (Exception e) {
            // Log the error with the original Amharic input for better context
            log.error("Error getting Amharic meaning for input '{}': {}", userMessage, e.getMessage(), e);
            return "[Meaning Explanation Error (Amharic)]"; // User-friendly error
        }
    }

    /**
     * Explains the meaning of an Amharic proverb or expression *in English*.
     * @param userMessage The Amharic proverb or expression (e.g., "ዕንጨት አይሸከሙ ላመድ አሮጊት አያገቡ ላትወልድ").
     * @return The explanation of the meaning in English, or an error message.
     */
    public String meaningInEnglish(String userMessage) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            log.warn("meaningInEnglish called with empty message.");
            return "";
        }

        // Prompt in English asking for an explanation in English of the Amharic text.
        String prompt = String.format(
                "Explain the meaning of the following Amharic proverb or expression in English. " +
                        "Provide only the English explanation.\n\n" +
                        "Amharic Proverb/Expression: \"%s\"\n\n" +
                        "English Explanation:",
                userMessage // Pass the Amharic text directly here
        );

        log.debug("Sending English meaning prompt to Gemini:\n{}", prompt);

        try {
            String response = model.chat(prompt);
            log.debug("Received English meaning response from Gemini: {}", response);
            return response.trim();
        } catch (Exception e) {
            // Log the error with the original Amharic input
            log.error("Error getting English meaning for input '{}': {}", userMessage, e.getMessage(), e);
            return "[Meaning Explanation Error (English)]"; // User-friendly error
        }
    }
}


