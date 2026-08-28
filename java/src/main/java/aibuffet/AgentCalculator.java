package aibuffet;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

/**
 * LangChain4j agent with a calculator tool (AiServices + @Tool).
 */
public final class AgentCalculator {

    static class CalculatorTools {
        @Tool("Evaluate a numeric math expression. Supports +, -, *, /, and sqrt(x).")
        String calculator(String expression) {
            try {
                String expr = expression
                        .replaceAll("(?i)sqrt\\s*\\(\\s*([^)]+)\\s*\\)", "Math.sqrt($1)")
                        .replaceAll("(\\d+)\\s*/\\s*(\\d+)", "($1/$2)");
                Object value = new javax.script.ScriptEngineManager()
                        .getEngineByName("JavaScript")
                        .eval(expr);
                return String.valueOf(value);
            } catch (Exception err) {
                return "Error: could not evaluate expression.";
            }
        }
    }

    interface Assistant {
        String chat(String userMessage);
    }

    public static void main(String[] args) {
        String task = args.length > 0
                ? String.join(" ", args)
                : "Calculate the square root of 144 and then multiply the result by 5.";

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

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(model)
                .tools(new CalculatorTools())
                .build();

        try {
            System.out.println("Task: " + task);
            String result = assistant.chat(task);
            System.out.println("\nResult: " + result);
        } catch (Exception err) {
            System.err.println(err.getMessage());
            System.exit(1);
        }
    }
}
