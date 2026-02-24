/**
 * LangChain agent with a calculator tool (createOpenAIToolsAgent + AgentExecutor).
 */

import { ChatOpenAI } from "@langchain/openai";
import { ChatPromptTemplate, MessagesPlaceholder } from "@langchain/core/prompts";
import { tool } from "@langchain/core/tools";
import { z } from "zod";
import { createOpenAIToolsAgent, AgentExecutor } from "langchain/agents";

const model = new ChatOpenAI({
  model: "gpt-4o",
  temperature: 0,
});

const calculator = tool(
  (input: { expression: string }) => {
    try {
      const expr = input.expression
        .replace(/\bsqrt\s*\(\s*([^)]+)\s*\)/g, "Math.sqrt($1)")
        .replace(/\b(\d+)\s*\/\s*(\d+)/g, "($1/$2)");
      const value = new Function(`return (${expr})`)();
      return String(value);
    } catch {
      return "Error: could not evaluate expression.";
    }
  },
  {
    name: "calculator",
    description: "Evaluate a numeric math expression. Supports +, -, *, /, and sqrt(x).",
    schema: z.object({
      expression: z.string(),
    }),
  }
);

const tools = [calculator];

const prompt = ChatPromptTemplate.fromMessages([
  ["system", "You are a helpful assistant. Use the calculator tool when you need to compute numbers."],
  ["human", "{input}"],
  new MessagesPlaceholder("agent_scratchpad"),
]);

async function runAgent(task: string): Promise<string> {
  const agent = await createOpenAIToolsAgent({
    llm: model,
    tools,
    prompt,
  });
  const executor = new AgentExecutor({ agent, tools });
  const result = await executor.invoke({ input: task });
  return (result.output as string) ?? String(result);
}

async function main(): Promise<void> {
  const task =
    process.argv.slice(2).join(" ") ||
    "Calculate the square root of 144 and then multiply the result by 5.";
  try {
    console.log("Task:", task);
    const result = await runAgent(task);
    console.log("\nResult:", result);
  } catch (err) {
    console.error(err);
    process.exit(1);
  }
}

main();
