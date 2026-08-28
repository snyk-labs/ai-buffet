package aibuffet;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

/**
 * OpenAiChatModel and a LangChain4j tools agent.
 */
public final class LangChainExample {

    static class WeatherTools {
        @Tool("Get the weather for a given city")
        String getWeather(String city) {
            return "It's always sunny in " + city + "!";
        }
    }

    interface WeatherAssistant {
        String chat(String userMessage);
    }

    public static void main(String[] args) {
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("OPENAI_API_KEY is not set");
            System.exit(1);
        }

        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gpt-4o")
                .temperature(0.0)
                .build();

        WeatherAssistant assistant = AiServices.builder(WeatherAssistant.class)
                .chatLanguageModel(model)
                .tools(new WeatherTools())
                .build();

        String result = assistant.chat("What's the weather in Tokyo?");
        System.out.println(result);
    }
}
