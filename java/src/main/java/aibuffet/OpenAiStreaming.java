package aibuffet;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.core.http.StreamResponse;
import com.openai.models.chat.completions.ChatCompletionChunk;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

/**
 * Streaming chat completion using the OpenAI Java SDK.
 * Demonstrates token-by-token streaming and model usage.
 */
public final class OpenAiStreaming {

    private static final String MODEL = "gpt-4o-mini";

    private static OpenAIClient getClient() {
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("OPENAI_API_KEY is not set");
        }
        return OpenAIOkHttpClient.builder().apiKey(apiKey).build();
    }

    public static void streamChat(String userMessage) {
        OpenAIClient client = getClient();
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(MODEL)
                .maxCompletionTokens(1024L)
                .addUserMessage(userMessage)
                .build();

        try (StreamResponse<ChatCompletionChunk> streamResponse =
                     client.chat().completions().createStreaming(params)) {
            streamResponse.stream()
                    .flatMap(chunk -> chunk.choices().stream())
                    .flatMap(choice -> choice.delta().content().stream())
                    .forEach(System.out::print);
        }
        System.out.println();
    }

    public static void main(String[] args) {
        String prompt = args.length > 0
                ? String.join(" ", args)
                : "Count from 1 to 5, one number per line.";
        try {
            System.out.print("Assistant: ");
            streamChat(prompt);
        } catch (Exception err) {
            System.err.println(err.getMessage());
            System.exit(1);
        }
    }
}
