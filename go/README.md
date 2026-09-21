# Go AI Buffet

Go examples of AI usage, mirroring the TypeScript examples in this repo.

## Setup

Requires Go 1.24+.

```bash
cd go
go mod download
```

## Examples

Each example is a standalone command under `cmd/`.

### Chatbot (`cmd/chatbot`)

- Simple chat loop using **anthropic-sdk-go**.
- Model name is passed via a constant and code flow, not the client constructor.

Requires `ANTHROPIC_API_KEY`:

```bash
ANTHROPIC_API_KEY=sk-... go run ./cmd/chatbot
```

### OpenAI SDK (`cmd/openaisdk`)

- One-shot chat completion using **openai-go**.
- Model: `gpt-4o-mini`.

```bash
OPENAI_API_KEY=sk-... go run ./cmd/openaisdk
```

### OpenAI raw HTTP (`cmd/openairaw`)

- Calls **OpenAI Chat Completions** via raw `net/http` (no SDK).
- Model and endpoint are constants; good for detecting HTTP-based model usage.

```bash
OPENAI_API_KEY=sk-... go run ./cmd/openairaw
```

### OpenAI streaming (`cmd/openaistreaming`)

- **Streaming** chat completion with the OpenAI SDK; prints tokens as they arrive.
- Optional first argument is the prompt (default: count 1 to 5).

```bash
OPENAI_API_KEY=sk-... go run ./cmd/openaistreaming "Explain Go in one sentence."
```

### OpenAI images (`cmd/openaiimages`)

- **DALL-E 3** image generation via the OpenAI Images API. Writes the image URL to `generated-image-url.txt`.
- Optional: pass a prompt as CLI args.

```bash
OPENAI_API_KEY=sk-... go run ./cmd/openaiimages "A cozy cabin in the snow"
```

### LangChainGo (`cmd/langchainexample`)

- **langchaingo** OpenAI LLM with **NewOneShotAgent** and a custom `get_weather` tool.

```bash
OPENAI_API_KEY=sk-... go run ./cmd/langchainexample
```

### Agent with calculator (`cmd/agentcalculator`)

- LangChainGo **agent** with the built-in **Calculator** tool.
- Default task: "Calculate the square root of 144 and then multiply the result by 5." Override with CLI args.

```bash
OPENAI_API_KEY=sk-... go run ./cmd/agentcalculator "What is (20 + 4) * 2?"
```
