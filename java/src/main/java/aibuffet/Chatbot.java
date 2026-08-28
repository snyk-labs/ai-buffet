package aibuffet;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.Model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * Simple chat loop using the Anthropic Java SDK.
 * Model name is not passed directly to the client constructor, to showcase
 * detection of model usage via code flow.
 */
public final class Chatbot {

    private static final String ENV_KEY = "ANTHROPIC_API_KEY";

    private static AnthropicClient initClient() {
        String apiKey = System.getenv(ENV_KEY);
        if (apiKey == null || apiKey.isBlank()) {
            System.err.println(ENV_KEY + " not set in environment.");
            System.exit(1);
        }
        return AnthropicOkHttpClient.fromEnv();
    }

    private static Model resolveModel() {
        return Model.CLAUDE_3_5_SONNET_20240620;
    }

    public static void main(String[] args) {
        AnthropicClient client = initClient();
        Model model = resolveModel();
        System.out.println("Chat with Claude (type 'exit' to quit).\n");

        Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(System.in)));
        while (true) {
            System.out.print("> ");
            if (!scanner.hasNextLine()) {
                break;
            }
            String prompt = scanner.nextLine();
            if ("exit".equals(prompt)) {
                break;
            }

            try {
                MessageCreateParams params = MessageCreateParams.builder()
                        .model(model)
                        .maxTokens(1024L)
                        .addUserMessage(prompt)
                        .build();
                Message response = client.messages().create(params);
                response.content().stream()
                        .flatMap(block -> block.text().stream())
                        .forEach(text -> System.out.println(text.text()));
            } catch (Exception err) {
                System.err.println(err.getMessage());
            }
        }
    }
}
