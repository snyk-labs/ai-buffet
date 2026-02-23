/**
 * Streaming chat completion using the OpenAI SDK.
 * Demonstrates token-by-token streaming and model usage.
 */

import OpenAI from "openai";

const MODEL = "gpt-4o-mini";

function getClient(): OpenAI {
  const apiKey = process.env.OPENAI_API_KEY;
  if (!apiKey) {
    throw new Error("OPENAI_API_KEY is not set");
  }
  return new OpenAI({ apiKey });
}

export async function streamChat(
  messages: OpenAI.Chat.ChatCompletionMessageParam[],
  onChunk: (text: string) => void
): Promise<void> {
  const client = getClient();
  const stream = await client.chat.completions.create({
    model: MODEL,
    messages,
    stream: true,
    max_tokens: 1024,
  });

  for await (const chunk of stream) {
    const delta = chunk.choices[0]?.delta?.content;
    if (typeof delta === "string") {
      onChunk(delta);
    }
  }
}

async function main(): Promise<void> {
  const prompt = process.argv[2] ?? "Count from 1 to 5, one number per line.";
  try {
    process.stdout.write("Assistant: ");
    await streamChat([{ role: "user", content: prompt }], (text) => {
      process.stdout.write(text);
    });
    process.stdout.write("\n");
  } catch (err) {
    console.error(err);
    process.exit(1);
  }
}

main();
