/**
 * Calls the OpenAI Chat Completions API via raw fetch (no SDK).
 * Showcases model usage through direct HTTP.
 */

const OPENAI_API_HOST = "https://api.openai.com";
const OPENAI_CHAT_PATH = "/v1/chat/completions";
const OPENAI_MODEL = "gpt-4o-mini";

async function getChatResponse(promptMessage: string): Promise<string> {
  const apiKey = process.env.OPENAI_API_KEY;
  if (!apiKey || apiKey === "YOUR_OPENAI_API_KEY") {
    return "Error: Set OPENAI_API_KEY in the environment.";
  }

  try {
    const response = await fetch(`${OPENAI_API_HOST}${OPENAI_CHAT_PATH}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${apiKey}`,
      },
      body: JSON.stringify({
        model: OPENAI_MODEL,
        messages: [{ role: "user" as const, content: promptMessage }],
        temperature: 0.7,
        max_tokens: 150,
      }),
    });

    if (!response.ok) {
      const text = await response.text();
      return `API Error: ${response.status} - ${response.statusText}. ${text}`;
    }

    const data = (await response.json()) as {
      choices?: Array<{ message?: { content?: string } }>;
    };
    const content = data.choices?.[0]?.message?.content;
    return content ?? "No response from chatbot.";
  } catch (err) {
    return `Error: ${err instanceof Error ? err.message : String(err)}`;
  }
}

async function main(): Promise<void> {
  console.log("OpenAI Chat (raw fetch). Type 'exit' to quit.\n");

  const readline = await import("readline");
  const rl = readline.createInterface({ input: process.stdin, output: process.stdout });

  const ask = (): void => {
    rl.question("You: ", async (userInput) => {
      if (userInput.trim().toLowerCase() === "exit") {
        console.log("Goodbye!");
        rl.close();
        return;
      }
      console.log("Chatbot: Thinking...");
      const reply = await getChatResponse(userInput);
      console.log(`Chatbot: ${reply}\n`);
      ask();
    });
  };

  ask();
}

main();
