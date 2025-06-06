package com.hd.misale.Misale.langchain;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;



@Component
@Slf4j
public class ChatLanguageModelManager {


    public final ChatLanguageModel model;


    public ChatLanguageModelManager() {
        String apiKey = System.getenv("GEMINI_AI_KEY");
        if (apiKey == null || apiKey.trim().isEmpty()) {

            log.error("FATAL: GEMINI_AI_KEY environment variable is not set!");
            throw new IllegalStateException("GEMINI_AI_KEY environment variable must be set.");
        }


        String modelName = "gemini-2.0-flash";

        log.info("Initializing Google AI Gemini Chat Model ({})...", modelName);


        this.model = GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(0.3)
                .topK(1)
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
            return "";
        }


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

            return response.trim();
        } catch (Exception e) {
            log.error("Error during translation to Amharic for input '{}': {}", userMessage, e.getMessage(), e);

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

            return response.trim();
        } catch (Exception e) {
            log.error("Error during transliteration to Amharic Fidel for input '{}': {}", userMessage, e.getMessage(), e);

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


        String prompt = String.format(
                """
                        በአማርኛ ቋንቋ የሚከተለውን የአማርኛ አባባል ትርጉም አብራራ/ያስረዳ። ማብራሪያውን ብቻ መልስ።
                        
                        አባባል፦ "%s"
                        
                        ማብራሪያ፦""",
                userMessage
        );

        log.debug("Sending Amharic meaning prompt to Gemini:\n{}", prompt);

        try {

            String response = model.chat(prompt);
            log.debug("Received Amharic meaning response from Gemini: {}", response);
            return response.trim();
        } catch (Exception e) {

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


        String prompt = String.format(
                """
                        Explain the meaning of the following Amharic proverb or expression in English. \
                        Provide only the English explanation. Don't forget to give meaning before and then explanation.
                        
                        Amharic Proverb/Expression: "%s"
                        
                        English Explanation:""",
                userMessage
        );

        log.debug("Sending English meaning prompt to Gemini:\n{}", prompt);

        try {
            String response = model.chat(prompt);
            log.debug("Received English meaning response from Gemini: {}", response);
            return response.trim();
        } catch (Exception e) {

            log.error("Error getting English meaning for input '{}': {}", userMessage, e.getMessage(), e);
            return "[Meaning Explanation Error (English)]";
        }
    }


    public String processToAmharic(String userMessage) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            log.warn("processToAmharic called with null or empty message.");
            return "";
        }


        String prompt = String.format(
                """
                Carefully analyze the input text below. Your goal is to produce output *only* in standard Amharic Fidel script.

                1.  **Determine the input type:** Is it English text or Amharic text written phonetically in Latin letters?
                2.  **Perform the correct action:**
                    * **If English:** Translate the *meaning* of the English text into standard Amharic Fidel script. Use appropriate Amharic vocabulary. **Crucially, do *not* just represent the English sounds using Amharic letters (do not transliterate English words); provide the actual Amharic *translation* conveying the intended meaning.** Only transliterate proper nouns if they have no common Amharic equivalent.
                    * **If Latin-script Amharic:** Convert the text phonetically into its standard Amharic Fidel script representation (this is transliteration).

                Provide *only* the final Amharic Fidel script output, with no other text, labels, or explanations.

                Input Text: %s

                Amharic Fidel Output:""",
                userMessage
        );

        log.debug("Sending unified processing prompt to Gemini:\n{}", prompt);

        try {

            String response = model.chat(prompt); // Use your actual method to call the model
            log.debug("Received processing response from Gemini for input '{}': {}", userMessage, response);


            return response.trim();

        } catch (Exception e) {

            log.error("Error during unified processing to Amharic for input '{}': {}", userMessage, e.getMessage(), e);


            return "[Processing Error]";
        }
    }
}


