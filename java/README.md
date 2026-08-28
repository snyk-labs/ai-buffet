# Java AI Buffet

Java examples of AI usage.

## Setup

```bash
cd java
mvn -q compile
```

Requires Java 17+ and Maven.

## Examples

### LangChain4j (`LangChainExample.java`)

- **OpenAiChatModel** from `langchain4j-open-ai`.
- **AiServices** agent with a `get_weather` tool.

Requires `OPENAI_API_KEY`:

```bash
mvn -q exec:java -Dexec.mainClass=aibuffet.LangChainExample
```

### Chatbot (`Chatbot.java`)

- Simple chat loop using the **Anthropic Java SDK**.
- Model name is not passed directly to the client constructor, to showcase detection of model usage via code flow.

Requires `ANTHROPIC_API_KEY`:

```bash
ANTHROPIC_API_KEY=sk-... mvn -q exec:java -Dexec.mainClass=aibuffet.Chatbot
```

### OpenAI raw HTTP (`OpenAiRaw.java`)

- Calls **OpenAI Chat Completions** via `java.net.http.HttpClient` (no SDK).
- Model and endpoint are in constants; good for detecting HTTP-based model usage.

Requires `OPENAI_API_KEY`:

```bash
OPENAI_API_KEY=sk-... mvn -q exec:java -Dexec.mainClass=aibuffet.OpenAiRaw
```

### OpenAI SDK (`OpenAiSdk.java`)

- One-shot chat completion using the official **openai-java** package.
- Exports a `chat()` helper; when run directly, sends a single prompt and prints the reply.

```bash
OPENAI_API_KEY=sk-... mvn -q exec:java -Dexec.mainClass=aibuffet.OpenAiSdk
```

### OpenAI streaming (`OpenAiStreaming.java`)

- **Streaming** chat completion with the OpenAI Java SDK; prints tokens as they arrive.
- Optional first argument is the prompt (default: count 1 to 5).

```bash
OPENAI_API_KEY=sk-... mvn -q exec:java -Dexec.mainClass=aibuffet.OpenAiStreaming -Dexec.args="Explain Java in one sentence."
```

### OpenAI images (`OpenAiImages.java`)

- **DALL-E 3** image generation via the OpenAI Images API. Writes the image URL to `generated-image-url.txt`.
- Optional: pass a prompt as CLI args.

```bash
OPENAI_API_KEY=sk-... mvn -q exec:java -Dexec.mainClass=aibuffet.OpenAiImages -Dexec.args="A cozy cabin in the snow"
```

### Agent with calculator (`AgentCalculator.java`)

- LangChain4j **AiServices** agent with a **calculator** tool.
- Default task: "Calculate the square root of 144 and then multiply the result by 5." Override with CLI args.

```bash
OPENAI_API_KEY=sk-... mvn -q exec:java -Dexec.mainClass=aibuffet.AgentCalculator -Dexec.args="What is (20 + 4) * 2?"
```

### Real-world: customer support agent (`realworld/CustomerSupportAgentDemo.java`)

Adapted from the official [LangChain4j customer support agent example](https://github.com/langchain4j/langchain4j-examples/tree/main/customer-support-agent-example) — a car rental support bot ("Miles of Smiles") with:

- **Tools** for booking lookup and cancellation (`BookingTools`)
- **Chat memory** per session (`@MemoryId`)
- **RAG** over the bundled terms-of-use document (`miles-of-smiles-terms-of-use.txt`)

Requires `OPENAI_API_KEY`:

```bash
OPENAI_API_KEY=sk-... mvn -q exec:java -Dexec.mainClass=aibuffet.realworld.CustomerSupportAgentDemo
```

Example prompts:

- "What is your cancellation policy?"
- "My name is John Doe and my booking number is MS-777. What are my booking details?"
- "Please cancel booking MS-777 for John Doe"
