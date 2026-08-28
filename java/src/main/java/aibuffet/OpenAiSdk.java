package aibuffet;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

/**
 * Simple chat completion using the official OpenAI Java SDK.
 * Showcases model usage via the openai-java package.
 */
public final class OpenAiSdk {

    private static final String MODEL = "gpt-4o-mini";

    private static OpenAIClient getClient() {
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("OPENAI_API_KEY is not set");
        }
        return OpenAIOkHttpClient.builder().apiKey(apiKey).build();
    }

    public static String chat(String userMessage) {
        OpenAIClient client = getClient();
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(MODEL)
                .maxCompletionTokens(1024L)
                .addUserMessage(userMessage)
                .temperature(0.7)
                .build();

        ChatCompletion completion = client.chat().completions().create(params);
        return completion.choices().stream()
                .findFirst()
                .flatMap(choice -> choice.message().content())
                .orElseThrow(() -> new IllegalStateException("No content in completion"));
    }

    public static void main(String[] args) {
        try {
            System.out.println(chat("Say hello in one sentence."));
        } catch (Exception err) {
            System.err.println(err.getMessage());
            System.exit(1);
        }
    }
}
