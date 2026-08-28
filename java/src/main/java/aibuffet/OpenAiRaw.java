package aibuffet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Calls the OpenAI Chat Completions API via raw HTTP (no SDK).
 * Showcases model usage through direct HTTP.
 */
public final class OpenAiRaw {

    private static final String OPENAI_API_HOST = "https://api.openai.com";
    private static final String OPENAI_CHAT_PATH = "/v1/chat/completions";
    private static final String OPENAI_MODEL = "gpt-4o-mini";

    private static String getChatResponse(String promptMessage) {
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isBlank() || "YOUR_OPENAI_API_KEY".equals(apiKey)) {
            return "Error: Set OPENAI_API_KEY in the environment.";
        }

        try {
            String body = "{"
                    + "\"model\": \"" + OPENAI_MODEL + "\","
                    + "\"messages\": [{\"role\": \"user\", \"content\": " + jsonString(promptMessage) + "}],"
                    + "\"temperature\": 0.7,"
                    + "\"max_tokens\": 150"
                    + "}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(OPENAI_API_HOST + OPENAI_CHAT_PATH))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                return "API Error: " + response.statusCode() + " - " + response.body();
            }

            return extractContent(response.body());
        } catch (Exception err) {
            return "Error: " + err.getMessage();
        }
    }

    private static String jsonString(String value) {
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    private static String extractContent(String json) {
        String marker = "\"content\":\"";
        int start = json.indexOf(marker);
        if (start < 0) {
            return "No response from chatbot.";
        }
        start += marker.length();
        StringBuilder content = new StringBuilder();
        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '\\' && i + 1 < json.length()) {
                content.append(json.charAt(i + 1));
                i++;
                continue;
            }
            if (c == '"') {
                break;
            }
            content.append(c);
        }
        return content.length() == 0 ? "No response from chatbot." : content.toString();
    }

    public static void main(String[] args) {
        System.out.println("OpenAI Chat (raw HTTP). Type 'exit' to quit.\n");

        Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(System.in)));
        while (true) {
            System.out.print("You: ");
            if (!scanner.hasNextLine()) {
                break;
            }
            String userInput = scanner.nextLine();
            if ("exit".equalsIgnoreCase(userInput.trim())) {
                System.out.println("Goodbye!");
                break;
            }

            System.out.println("Chatbot: Thinking...");
            String reply = getChatResponse(userInput);
            System.out.println("Chatbot: " + reply + "\n");
        }
    }
}
