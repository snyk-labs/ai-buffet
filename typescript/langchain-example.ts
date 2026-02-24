/**
 * ChatOpenAI and an OpenAI tools agent using LangChain.js.
 */

import { ChatOpenAI } from "@langchain/openai";
import { ChatPromptTemplate, MessagesPlaceholder } from "@langchain/core/prompts";
import { tool } from "@langchain/core/tools";
import { z } from "zod";
import { createOpenAIToolsAgent, AgentExecutor } from "langchain/agents";

// Chat model instance (e.g. for custom config like proxy via env)
const model = new ChatOpenAI({
  model: "gpt-4o",
  temperature: 0,
});

// Example tool for the agent
const getWeather = tool(
  ({ city }: { city: string }) => `It's always sunny in ${city}!`,
  {
    name: "get_weather",
    description: "Get the weather for a given city",
    schema: z.object({
      city: z.string(),
    }),
  }
);

const tools = [getWeather];

// Prompt must include agent_scratchpad for the OpenAI tools agent
const prompt = ChatPromptTemplate.fromMessages([
  ["system", "You are a helpful assistant."],
  ["human", "{input}"],
  new MessagesPlaceholder("agent_scratchpad"),
]);

// Agent with tools (createOpenAIToolsAgent is async)
async function createAgentWithTools() {
  const agent = await createOpenAIToolsAgent({
    llm: model,
    tools,
    prompt,
  });
  return new AgentExecutor({ agent, tools });
}

// Minimal chat model usage (no agent)
const agentMinimal = model;

// Run the agent
const executor = await createAgentWithTools();
const result = await executor.invoke({ input: "What's the weather in Tokyo?" });
console.log(result);

export { model, createAgentWithTools, agentMinimal, tools, prompt };
