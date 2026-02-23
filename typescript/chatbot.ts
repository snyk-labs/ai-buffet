/**
 * Simple chat loop using the Anthropic SDK.
 * Model name is not passed directly to the client constructor, to showcase
 * detection of model usage via code flow.
 */

import Anthropic from "@anthropic-ai/sdk";

const MODEL_KEY = "claude-sonnet-4-20250514";

function init(): { client: Anthropic; model: string } {
  const key = "ANTHROPIC_API_KEY";
  const apiKey = process.env[key];
  if (!apiKey) {
    console.error(`${key} not set in environment.`);
    process.exit(1);
  }
  const client = new Anthropic({ apiKey });
  return { client, model: MODEL_KEY };
}

async function main(): Promise<void> {
  const { client, model } = init();

  console.log("Chat with Claude (type 'exit' to quit).\n");

  const readline = await import("readline");
  const rl = readline.createInterface({ input: process.stdin, output: process.stdout });

  const ask = (): void => {
    rl.question("> ", async (prompt) => {
      if (prompt === "exit") {
        rl.close();
        return;
      }
      try {
        const response = await client.messages.create({
          model,
          max_tokens: 1024,
          messages: [{ role: "user", content: prompt }],
        });
        const text = response.content.find((block) => block.type === "text");
        console.log(text && "text" in text ? text.text : "");
      } catch (err) {
        console.error(err);
      }
      ask();
    });
  };

  ask();
}

main();
