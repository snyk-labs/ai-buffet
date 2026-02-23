/**
 * Simple chat completion using the official OpenAI Node SDK.
 * Showcases model usage via the openai package.
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

export async function chat(messages: OpenAI.Chat.ChatCompletionMessageParam[]): Promise<string> {
  const client = getClient();
  const completion = await client.chat.completions.create({
    model: MODEL,
    messages,
    max_tokens: 1024,
    temperature: 0.7,
  });
  const content = completion.choices[0]?.message?.content;
  if (content == null) {
    throw new Error("No content in completion");
  }
  return content;
}

// Run a single prompt when executed directly
async function main(): Promise<void> {
  try {
    const reply = await chat([{ role: "user", content: "Say hello in one sentence." }]);
    console.log(reply);
  } catch (err) {
    console.error(err);
    process.exit(1);
  }
}

main();
