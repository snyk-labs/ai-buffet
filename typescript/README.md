# TypeScript AI Buffet

TypeScript examples of AI usage.

## Setup

```bash
cd typescript-ai-buffet
npm install
```

## Examples

### LangChain (`langchain-example.ts`)

- **ChatOpenAI** from `@langchain/openai` with a custom model instance.
- **createOpenAIToolsAgent** + **AgentExecutor** from `langchain/agents` with a `get_weather` tool.
- Variants: agent with tools, minimal model-only usage.

Run (no invocation by default; set `OPENAI_API_KEY` and uncomment the `invoke` in the file to try):

```bash
npm run langchain
```

### Chatbot (`chatbot.ts`)

- Simple chat loop using **@anthropic-ai/sdk**.
- Model name is not passed directly to the client, to showcase detection of model usage via code flow.

Requires `ANTHROPIC_API_KEY`:

```bash
ANTHROPIC_API_KEY=sk-... npm run chatbot
```

### OpenAI raw fetch (`openai-raw.ts`)

- Calls **OpenAI Chat Completions** via raw `fetch` (no SDK).
- Model and endpoint are in constants; good for detecting HTTP-based model usage.

Requires `OPENAI_API_KEY`:

```bash
npm run openai-raw
```

### OpenAI SDK (`openai-sdk.ts`)

- One-shot chat completion using the official **openai** package.
- Exports a `chat()` helper; when run directly, sends a single prompt and prints the reply.

```bash
OPENAI_API_KEY=sk-... npm run openai-sdk
```

### OpenAI streaming (`openai-streaming.ts`)

- **Streaming** chat completion with the OpenAI SDK; prints tokens as they arrive.
- Optional first argument is the prompt (default: count 1 to 5).

```bash
OPENAI_API_KEY=sk-... npm run openai-streaming "Explain TypeScript in one sentence."
```

### OpenAI images (`openai-images.ts`)

- **DALL-E 3** image generation via the OpenAI Images API. Writes the image URL to `generated-image-url.txt`.
- Optional: pass a prompt as CLI args.

```bash
OPENAI_API_KEY=sk-... npm run openai-images "A cozy cabin in the snow"
```

### Agent with calculator (`agent-calculator.ts`)

- LangChain **agent** with a **calculator** tool.
- Default task: "Calculate the square root of 144 and then multiply the result by 5." Override with CLI args.

```bash
OPENAI_API_KEY=sk-... npm run agent-calculator "What is (20 + 4) * 2?"
```
